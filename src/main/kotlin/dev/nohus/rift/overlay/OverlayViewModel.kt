package dev.nohus.rift.overlay

import dev.nohus.rift.ViewModel
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.characters.repositories.OnlineCharactersRepository
import dev.nohus.rift.charactersettings.io.ReadCharacterSettingsUseCase
import dev.nohus.rift.game.AutopilotController
import dev.nohus.rift.intel.state.IntelStateController
import dev.nohus.rift.intel.state.IntelStateController.Dated
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.intel.state.getHostileIntelEntityCount
import dev.nohus.rift.location.CharacterLocationRepository
import dev.nohus.rift.map.MapExternalControl
import dev.nohus.rift.repositories.GetSystemsInRangeBandsUseCase
import dev.nohus.rift.repositories.MapStatusRepository
import dev.nohus.rift.repositories.MapStatusRepository.SolarSystemStatus
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.settings.persistence.OverlayInfoType
import dev.nohus.rift.settings.persistence.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

@Factory
class OverlayViewModel(
    characterLocationRepository: CharacterLocationRepository,
    private val solarSystemsRepository: SolarSystemsRepository,
    private val getSystemsInRangeBandsUseCase: GetSystemsInRangeBandsUseCase,
    private val routeKillmailsRepository: RouteKillmailsRepository,
    mapStatusRepository: MapStatusRepository,
    autopilotController: AutopilotController,
    intelStateController: IntelStateController,
    localCharactersRepository: LocalCharactersRepository,
    onlineCharactersRepository: OnlineCharactersRepository,
    private val readCharacterSettingsUseCase: ReadCharacterSettingsUseCase,
    private val settings: Settings,
) : ViewModel() {

    data class State(
        val characterContent: Map<Int, CharacterContent> = emptyMap(),
        val characterSystemIds: Map<Int, Int> = emptyMap(),
        val enabledInfoTypes: Set<OverlayInfoType> = OverlayInfoType.entries.toSet(),
        val isSpaceTypeIconOnly: Boolean = false,
        val isSovereigntyIconOnly: Boolean = false,
        val isDistanceIntelCountAlwaysShown: Boolean = true,
        val neocomWidthsPx: Map<Int, Int> = emptyMap(),
        val routeKillsBySystemId: Map<Int, RouteKillmailsRepository.SystemKills> = emptyMap(),
    )

    data class CharacterContent(
        val system: MapSolarSystem,
        val systemStatus: SolarSystemStatus?,
        val distanceBands: List<DistanceBand>,
        val routeIntel: RouteIntel? = null,
    )

    data class RouteIntel(
        val stops: List<RouteStop>,
    )

    data class RouteStop(
        val position: Int,
        val systemIntel: SystemIntel,
        val hostileCount: Int,
    )

    data class DistanceBand(
        val distance: Int,
        val systems: List<SystemIntel>,
        val hostileCount: Int,
    )

    data class SystemIntel(
        val system: MapSolarSystem,
        val intel: List<Dated<SystemEntity>>,
        val systemStatus: SolarSystemStatus?,
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            routeKillmailsRepository.state.collect { kills ->
                _state.update { it.copy(routeKillsBySystemId = kills) }
            }
        }
        viewModelScope.launch {
            settings.updateFlow.map { it.overlayEnabledCharacterIds }.distinctUntilChanged().collectLatest { enabledCharacterIds ->
                _state.update { it.copy(neocomWidthsPx = it.neocomWidthsPx.filterKeys { id -> id in enabledCharacterIds }) }
                if (enabledCharacterIds.isEmpty()) {
                    _state.update { it.copy(neocomWidthsPx = emptyMap()) }
                    return@collectLatest
                }
                var previousOnlineIds = emptySet<Int>()
                combine(
                    localCharactersRepository.allCharacters,
                    onlineCharactersRepository.onlineCharacters,
                ) { characters, onlineIds -> characters to onlineIds.filter { it in enabledCharacterIds }.toSet() }.collect { [characters, onlineIds] ->
                    val idsToRead = (onlineIds - previousOnlineIds) + (onlineIds - _state.value.neocomWidthsPx.keys)
                    previousOnlineIds = onlineIds
                    if (idsToRead.isEmpty()) return@collect
                    val widths = withContext(Dispatchers.IO) {
                        characters.filter { it.characterId in idsToRead }.mapNotNull { character ->
                            val settingsFile = character.settingsFiles.values.firstOrNull()
                            readCharacterSettingsUseCase(settingsFile)?.neocomWidthPx?.let { width ->
                                character.characterId to width
                            }
                        }.toMap()
                    }
                    if (widths.isNotEmpty()) {
                        _state.update { it.copy(neocomWidthsPx = it.neocomWidthsPx + widths) }
                    }
                }
            }
        }
        viewModelScope.launch {
            settings.updateFlow.map { it.overlayEnabledCharacterIds }.distinctUntilChanged().collectLatest { enabledCharacterIds ->
                if (enabledCharacterIds.isEmpty()) {
                    routeKillmailsRepository.watchSystems(emptyList())
                    _state.update { it.copy(characterContent = emptyMap(), characterSystemIds = emptyMap()) }
                    return@collectLatest
                }
                combine(
                    characterLocationRepository.locations,
                    mapStatusRepository.status,
                    intelStateController.state,
                    settings.updateFlow,
                    autopilotController.activeRoutes,
                ) { locations, systemStatuses, intel, settingsModel, routes ->
                    val enabledLocations = locations.filterKeys { it in enabledCharacterIds }
                    val contentBySystemId = enabledLocations.values.map { it.solarSystemId }
                        .distinct()
                        .mapNotNull { systemId ->
                            val system = solarSystemsRepository.getSystem(systemId) ?: return@mapNotNull null
                            val distanceBands = getDistanceIntelBands(system, intel, systemStatuses, settingsModel.overlayDistanceIntelMaxJumps)
                            systemId to CharacterContent(system, systemStatuses[systemId], distanceBands)
                        }.toMap()
                    State(
                        characterContent = enabledLocations.mapNotNull { [characterId, location] ->
                            val content = contentBySystemId[location.solarSystemId] ?: return@mapNotNull null
                            val routeIntel = getRouteIntel(routes[characterId]?.systems, location.solarSystemId, intel, systemStatuses)
                            characterId to content.copy(routeIntel = routeIntel)
                        }.toMap(),
                        characterSystemIds = enabledLocations.mapValues { [_, location] -> location.solarSystemId },
                        enabledInfoTypes = settingsModel.overlayInfoTypes,
                        isSpaceTypeIconOnly = settingsModel.isOverlaySpaceTypeIconOnly,
                        isSovereigntyIconOnly = settingsModel.isOverlaySovereigntyIconOnly,
                        isDistanceIntelCountAlwaysShown = settingsModel.isOverlayDistanceIntelCountAlwaysShown,
                    )
                }.collect { state ->
                    val systemIds = state.characterContent.values.flatMap { content ->
                        content.routeIntel?.stops.orEmpty().map { it.systemIntel.system.id }
                    }
                    routeKillmailsRepository.watchSystems(systemIds)
                    _state.update { state.copy(neocomWidthsPx = it.neocomWidthsPx, routeKillsBySystemId = it.routeKillsBySystemId) }
                }
            }
        }
    }

    private fun getRouteIntel(
        route: List<Int>?,
        currentSystemId: Int,
        intel: Map<MapSolarSystem, List<Dated<SystemEntity>>>,
        systemStatuses: Map<Int, SolarSystemStatus>,
    ): RouteIntel? {
        val currentIndex = route?.indexOf(currentSystemId) ?: return null
        if (currentIndex < 0 || currentIndex == route.lastIndex) return null
        val remainingRoute = route.drop(currentIndex + 1)
        val stops = remainingRoute.mapIndexed { index, systemId ->
            val system = solarSystemsRepository.getSystem(systemId) ?: return null
            val systemIntel = SystemIntel(system, intel[system].orEmpty(), systemStatuses[systemId])
            RouteStop(index + 1, systemIntel, getHostileIntelEntityCount(systemIntel.intel))
        }
        return RouteIntel(stops)
    }

    private fun getDistanceIntelBands(
        origin: MapSolarSystem,
        intel: Map<MapSolarSystem, List<Dated<SystemEntity>>>,
        systemStatuses: Map<Int, SolarSystemStatus>,
        maxJumps: Int,
    ): List<DistanceBand> {
        return getSystemsInRangeBandsUseCase(origin.id, maxJumps).mapIndexed { distance, systemIds ->
            val systems = systemIds.mapNotNull(solarSystemsRepository::getSystem).map { system ->
                SystemIntel(system, intel[system].orEmpty(), systemStatuses[system.id])
            }
            DistanceBand(
                distance = distance,
                systems = systems,
                hostileCount = systems.sumOf { getHostileIntelEntityCount(it.intel) },
            )
        }
    }
}
