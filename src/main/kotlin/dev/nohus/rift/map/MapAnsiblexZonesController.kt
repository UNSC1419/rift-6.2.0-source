package dev.nohus.rift.map

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.map_ansiblex_zone
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.repositories.IdRanges
import dev.nohus.rift.repositories.METERS_IN_LIGHT_YEAR
import dev.nohus.rift.repositories.RatsRepository
import dev.nohus.rift.repositories.RatsRepository.RatType.TriglavianCollective
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.repositories.distanceTo
import dev.nohus.rift.settings.persistence.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class MapAnsiblexZonesController(
    private val solarSystemsRepository: SolarSystemsRepository,
    private val ratsRepository: RatsRepository,
    private val settings: Settings,
) {

    data class MapAnsiblexZonesState(
        val capitalSystem: CapitalSystem? = null,
        val systemZones: Map<Int, AnsiblexZone> = emptyMap(),
    )

    data class CapitalSystem(
        val id: Int,
        val name: String,
    )

    enum class AnsiblexZone(val displayText: UiText) {
        Zone1(Res.string.map_ansiblex_zone.toUiText(1.toUiTextArgument())),
        Zone2(Res.string.map_ansiblex_zone.toUiText(2.toUiTextArgument())),
        Zone3(Res.string.map_ansiblex_zone.toUiText(3.toUiTextArgument())),
        Zone4(Res.string.map_ansiblex_zone.toUiText(4.toUiTextArgument())),
        Zone5(Res.string.map_ansiblex_zone.toUiText(5.toUiTextArgument())),
    }

    private val _state = MutableStateFlow(MapAnsiblexZonesState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onCapitalSystemUpdate(name: String) {
        val system = solarSystemsRepository.getSystem(name)
        val capitalSystem = system?.let { CapitalSystem(id = it.id, name = it.name) }
        _state.update { it.copy(capitalSystem = capitalSystem) }
        settings.ansiblexCapitalSystemId = capitalSystem?.id
        calculateSystemZones()
    }

    private fun loadSettings() {
        val capitalSystem = settings.ansiblexCapitalSystemId
            ?.let(solarSystemsRepository::getSystem)
            ?.let { CapitalSystem(id = it.id, name = it.name) }
        _state.update { it.copy(capitalSystem = capitalSystem) }
        calculateSystemZones()
    }

    private fun calculateSystemZones() {
        val capitalSystemId = _state.value.capitalSystem?.id ?: run {
            _state.update { it.copy(systemZones = emptyMap()) }
            return
        }
        val capitalSystem = solarSystemsRepository.getSystem(capitalSystemId) ?: run {
            _state.update { it.copy(systemZones = emptyMap()) }
            return
        }
        val systemZones = solarSystemsRepository
            .getSovSystems()
            .filter { isSovSystemValidAnsiblexTarget(it) }
            .associate { system ->
                val distanceLy = capitalSystem.distanceTo(system) / METERS_IN_LIGHT_YEAR
                val zone = when {
                    distanceLy <= 5.0 -> AnsiblexZone.Zone1
                    distanceLy <= 10.0 -> AnsiblexZone.Zone2
                    distanceLy <= 15.0 -> AnsiblexZone.Zone3
                    distanceLy <= 20.0 -> AnsiblexZone.Zone4
                    else -> AnsiblexZone.Zone5
                }
                system.id to zone
            }
        _state.update { it.copy(systemZones = systemZones) }
    }

    private fun isSovSystemValidAnsiblexTarget(system: MapSolarSystem): Boolean {
        if (ratsRepository.getRats(system.id) == TriglavianCollective) return false
        if (IdRanges.isJoveRegion(system.regionId)) return false
        return true
    }
}
