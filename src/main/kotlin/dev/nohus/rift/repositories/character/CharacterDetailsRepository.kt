package dev.nohus.rift.repositories.character

import dev.nohus.rift.contacts.ContactsRepository
import dev.nohus.rift.network.Result
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.esi.models.AlliancesIdAlliance
import dev.nohus.rift.network.esi.models.CorporationPalette
import dev.nohus.rift.network.esi.models.CorporationsIdCorporation
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.repositories.CharacterTitlesRepository
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.StandingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import java.time.Instant

@Single
class CharacterDetailsRepository(
    private val esiApi: EsiApi,
    private val standingsRepository: StandingsRepository,
    private val contactsRepository: ContactsRepository,
    private val characterTitlesRepository: CharacterTitlesRepository,
) {
    private val scope = CoroutineScope(SupervisorJob())
    private val pendingRequestsMutex = Mutex()
    private val pendingCorporationRequests = mutableMapOf<Int, Deferred<CorporationsIdCorporation?>>()
    private val pendingAllianceRequests = mutableMapOf<Int, Deferred<AlliancesIdAlliance?>>()

    data class CharacterDetails(
        val characterId: Int,
        val name: String,
        val corporationId: Int,
        val corporationName: String,
        val corporationTicker: String,
        val corporationRoles: List<String>,
        val corporationPalette: CorporationPalette?,
        val allianceId: Int?,
        val allianceName: String?,
        val allianceTicker: String?,
        val standing: Float,
        val standingLevel: Standing,
        val corporationTitle: String?,
        val title: String?,
        val achievementScore: Int,
        val characterLabels: List<String>,
        val corporationLabels: List<String>,
        val allianceLabels: List<String>,
        val birthday: Instant,
        val factionId: Int?,
        val securityStatus: Double?,
    )

    data class CorporationDetails(
        val corporationId: Int,
        val corporationName: String,
        val corporationTicker: String,
        val allianceId: Int?,
        val allianceName: String?,
        val allianceTicker: String?,
        val standing: Float,
        val standingLevel: Standing,
        val corporationLabels: List<String>,
        val allianceLabels: List<String>,
    )

    data class AllianceDetails(
        val allianceId: Int,
        val allianceName: String,
        val allianceTicker: String,
        val standing: Float,
        val standingLevel: Standing,
        val allianceLabels: List<String>,
    )

    // TODO: Check if batch version can be applied anywhere else
    suspend fun getCharacterDetails(originator: Originator, characterIds: Collection<Int>): Map<Int, CharacterDetails?> = coroutineScope {
        val distinct = characterIds.distinct()
        distinct.map {
            async {
                it to getCharacterDetails(originator, it)
            }
        }.awaitAll().toMap()
    }

    suspend fun getCharacterDetails(originator: Originator, characterId: Int): CharacterDetails? = coroutineScope {
        val characterDeferred = async { esiApi.getCharactersId(originator, characterId).success }
        val character = characterDeferred.await() ?: return@coroutineScope null
        val corporationId = character.corporationId
        val allianceId = character.allianceId
        val deferredCorporation = async { getCorporation(originator, corporationId) }
        val deferredAlliance = async { allianceId?.let { getAlliance(originator, it) } }
        val corporation = deferredCorporation.await() ?: return@coroutineScope null
        val alliance = deferredAlliance.await()
        val standing = standingsRepository.getStanding(allianceId, corporationId, characterId) ?: 0f
        val standingLevel = standingsRepository.getStandingLevel(allianceId, corporationId, characterId, includeWars = true)
        val characterLabels = contactsRepository.getLabels(listOf(characterId)).map { it.name }.distinct()
        val corporationLabels = contactsRepository.getLabels(listOf(corporationId)).map { it.name }.distinct()
        val allianceLabels = allianceId
            ?.let { contactsRepository.getLabels(listOf(allianceId)).map { it.name }.distinct() }
            ?: emptyList()
        CharacterDetails(
            characterId = characterId,
            name = character.name,
            corporationId = corporationId,
            corporationName = corporation.name,
            corporationTicker = corporation.ticker,
            corporationRoles = emptyList(),
            corporationPalette = corporation.palette,
            allianceId = allianceId,
            allianceName = alliance?.name,
            allianceTicker = alliance?.ticker,
            standing = standing,
            standingLevel = standingLevel,
            corporationTitle = character.corporationTitle,
            title = character.characterTitleId?.let { characterTitlesRepository.getTitle(it) },
            achievementScore = character.achievementScore,
            characterLabels = characterLabels,
            corporationLabels = corporationLabels,
            allianceLabels = allianceLabels,
            birthday = character.birthday,
            factionId = character.factionId,
            securityStatus = character.securityStatus,
        )
    }

    suspend fun getCorporationDetails(originator: Originator, corporationId: Int): CorporationDetails? = coroutineScope {
        val corporation = getCorporation(originator, corporationId) ?: return@coroutineScope null
        val alliance = corporation.allianceId?.let { getAlliance(originator, it) }
        val standing = standingsRepository.getStanding(corporation.allianceId, corporationId, null) ?: 0f
        val standingLevel = standingsRepository.getStandingLevel(corporation.allianceId, corporationId, null, includeWars = true)
        val corporationLabels = contactsRepository.getLabels(listOf(corporationId)).map { it.name }.distinct()
        val allianceLabels = corporation.allianceId
            ?.let { contactsRepository.getLabels(listOf(corporation.allianceId)).map { it.name }.distinct() }
            ?: emptyList()
        CorporationDetails(
            corporationId = corporationId,
            corporationName = corporation.name,
            corporationTicker = corporation.ticker,
            allianceId = corporation.allianceId,
            allianceName = alliance?.name,
            allianceTicker = alliance?.ticker,
            standing = standing,
            standingLevel = standingLevel,
            corporationLabels = corporationLabels,
            allianceLabels = allianceLabels,
        )
    }

    suspend fun getAllianceDetails(originator: Originator, allianceId: Int): AllianceDetails? = coroutineScope {
        val alliance = getAlliance(originator, allianceId) ?: return@coroutineScope null
        val standing = standingsRepository.getStanding(allianceId, null, null) ?: 0f
        val standingLevel = standingsRepository.getStandingLevel(allianceId, null, null, includeWars = true)
        val allianceLabels = contactsRepository.getLabels(listOf(allianceId)).map { it.name }.distinct()
        AllianceDetails(
            allianceId = allianceId,
            allianceName = alliance.name,
            allianceTicker = alliance.ticker,
            standing = standing,
            standingLevel = standingLevel,
            allianceLabels = allianceLabels,
        )
    }

    suspend fun getCorporationName(originator: Originator, corporationId: Int): Result<CorporationsIdCorporation> {
        return esiApi.getCorporationsId(originator, corporationId)
    }

    suspend fun getAllianceName(originator: Originator, allianceId: Int): Result<AlliancesIdAlliance> {
        return esiApi.getAlliancesId(originator, allianceId)
    }

    private suspend fun getCorporation(originator: Originator, corporationId: Int): CorporationsIdCorporation? {
        return getPendingRequest(pendingCorporationRequests, corporationId) {
            esiApi.getCorporationsId(originator, corporationId).success
        }
    }

    private suspend fun getAlliance(originator: Originator, allianceId: Int): AlliancesIdAlliance? {
        return getPendingRequest(pendingAllianceRequests, allianceId) {
            esiApi.getAlliancesId(originator, allianceId).success
        }
    }

    private suspend fun <T> getPendingRequest(
        pendingRequests: MutableMap<Int, Deferred<T?>>,
        id: Int,
        request: suspend () -> T?,
    ): T? {
        val deferred = pendingRequestsMutex.withLock {
            pendingRequests[id] ?: scope.async {
                try {
                    request()
                } finally {
                    pendingRequestsMutex.withLock {
                        pendingRequests -= id
                    }
                }
            }.also {
                pendingRequests[id] = it
            }
        }
        return deferred.await()
    }
}
