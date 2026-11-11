package dev.nohus.rift.killboard

import dev.nohus.rift.alerts.AlertsTriggerController
import dev.nohus.rift.intel.state.IntelStateController
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.repositories.character.CharacterDetailsRepository
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.StandingsRepository
import dev.nohus.rift.types.TypesRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import java.time.Duration
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Single
class KillmailProcessor(
    private val solarSystemsRepository: SolarSystemsRepository,
    private val intelStateController: IntelStateController,
    private val typeRepository: TypesRepository,
    private val characterDetailsRepository: CharacterDetailsRepository,
    private val standingsRepository: StandingsRepository,
    private val killmailLocationResolver: KillmailLocationResolver,
    private val alertsTriggerController: AlertsTriggerController,
) {

    data class ProcessedKillmail(
        val system: MapSolarSystem,
        val ships: List<SystemEntity.Ship>,
        val victim: SystemEntity.Character?,
        val attackers: List<SystemEntity.Character>,
        val killmail: SystemEntity.Killmail,
        val celestial: SystemEntity?,
        val timestamp: Instant,
    ) {
        val entities get() = listOf(killmail) + ships + attackers + listOfNotNull(celestial)
    }

    private val seenKillmails = mutableSetOf<Long>()
    private val mutex = Mutex()

    suspend fun submit(message: Killmail) = coroutineScope {
        val ago = Duration.between(message.killmailTime, Instant.now())
        if (ago > Duration.ofMinutes(15)) {
            logger.debug { "Ignoring old killmail, ${ago.toSeconds()}s ago" }
            return@coroutineScope
        }

        val system = solarSystemsRepository.getSystem(message.solarSystemId) ?: return@coroutineScope

        val deferredVictim = message.victim.characterId?.let {
            async { characterDetailsRepository.getCharacterDetails(Originator.Killmails, it) }
        }

        // Corporation and alliance are only loaded if there is no character, otherwise they are included with the character
        val deferredVictimCorporation = if (message.victim.characterId == null && message.victim.corporationId != null) {
            async { characterDetailsRepository.getCorporationName(Originator.Killmails, message.victim.corporationId).success }
        } else {
            null
        }
        val deferredVictimAlliance = if (message.victim.characterId == null && message.victim.allianceId != null) {
            async { characterDetailsRepository.getAllianceName(Originator.Killmails, message.victim.allianceId).success }
        } else {
            null
        }

        val deferredAttackers = message.attackers.mapNotNull {
            if (it.characterId != null) {
                async { characterDetailsRepository.getCharacterDetails(Originator.Killmails, it.characterId) }
            } else {
                null
            }
        }

        val victim = deferredVictim?.await()?.let {
            SystemEntity.Character(it.name, it.characterId, it)
        }
        val attackers = deferredAttackers.awaitAll().filterNotNull().map {
            SystemEntity.Character(it.name, it.characterId, it)
        }
        val ships = message.attackers
            .mapNotNull { attacker ->
                val ship = attacker.shipTypeId?.let { typeRepository.getType(it) } ?: return@mapNotNull null
                val standing = attackers.firstOrNull { it.characterId == attacker.characterId }?.details?.standingLevel ?: Standing.Neutral
                standing to ship
            }
            .groupBy { it.first }
            .mapValues { (standing, ships) ->
                ships
                    .map { it.second }
                    .groupBy { it }
                    .map { (ship, ships) -> SystemEntity.Ship(ship, ships.size, standing = standing) }
            }
            .flatMap { it.value }
        val standingLevel = standingsRepository.getStandingLevel(message.victim.allianceId, message.victim.corporationId, message.victim.characterId, includeWars = true)

        val killmailVictim = SystemEntity.KillmailVictim(
            characterId = message.victim.characterId,
            details = victim?.details,
            corporationId = message.victim.corporationId ?: victim?.details?.corporationId,
            corporationName = victim?.details?.corporationName ?: deferredVictimCorporation?.await()?.name,
            corporationTicker = victim?.details?.corporationTicker ?: deferredVictimCorporation?.await()?.ticker,
            allianceId = message.victim.allianceId ?: victim?.details?.allianceId,
            allianceName = victim?.details?.allianceName ?: deferredVictimAlliance?.await()?.name,
            allianceTicker = victim?.details?.allianceTicker ?: deferredVictimAlliance?.await()?.ticker,
            standing = standingLevel,
        )
        val victimShipType = message.victim.shipTypeId?.let { typeRepository.getType(it) }
        val killmail = SystemEntity.Killmail(
            url = message.url,
            ship = message.victim.shipTypeId?.let { typeRepository.getType(it) },
            typeName = victimShipType?.name,
            victim = killmailVictim,
        )
        val celestial = killmailLocationResolver.resolve(message.solarSystemId, message.position, message.victim.shipTypeId)

        val processedKillmail = ProcessedKillmail(
            system = system,
            ships = ships,
            victim = victim,
            attackers = attackers,
            killmail = killmail,
            celestial = celestial,
            timestamp = message.killmailTime,
        )

        mutex.withLock {
            if (message.killmailId !in seenKillmails) {
                seenKillmails += message.killmailId
                logger.debug { "Killmail: ${killmail.ship?.name ?: "Unknown ship"} killed by ${ships.joinToString { it.type.name }} in ${processedKillmail.system.name}, ${ago.toSeconds()}s ago" }
                intelStateController.submitKillmail(processedKillmail)
                alertsTriggerController.onNewKillmail(processedKillmail)
            } else {
                logger.debug { "Ignoring killmail, already seen" }
            }
        }
    }
}
