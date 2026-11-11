package dev.nohus.rift.killboard

import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.repositories.CelestialsRepository
import dev.nohus.rift.repositories.Position
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.StarGatesRepository
import dev.nohus.rift.types.TypesRepository
import org.koin.core.annotation.Single
import kotlin.math.roundToInt

@Single
class KillmailLocationResolver(
    private val celestialsRepository: CelestialsRepository,
    private val starGatesRepository: StarGatesRepository,
    private val solarSystemsRepository: SolarSystemsRepository,
    private val typesRepository: TypesRepository,
) {
    fun resolve(systemId: Int, position: Position?, victimShipTypeId: Int?): SystemEntity? {
        position ?: return null
        val closest = celestialsRepository.getClosestCelestial(systemId, position) ?: return null
        val shipRadius = victimShipTypeId?.let(typesRepository::getType)?.radius ?: 0f
        val distanceKm = ((closest.distance - shipRadius).coerceAtLeast(0.0) / 1000).roundToInt()
        if (distanceKm >= 1000) return null

        val stargateSystem = starGatesRepository.getStargates(systemId)
            .filter { it.second.typeId == closest.celestial.type.id }
            .mapNotNull { solarSystemsRepository.getSystem(it.first) }
            .singleOrNull { it.name in closest.celestial.name }
        return if (stargateSystem != null) {
            SystemEntity.Gate(stargateSystem, isAnsiblex = false, distanceKm = distanceKm)
        } else {
            SystemEntity.Celestial(closest.celestial, distanceKm)
        }
    }
}
