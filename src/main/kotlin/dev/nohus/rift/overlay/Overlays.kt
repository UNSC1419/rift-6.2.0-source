package dev.nohus.rift.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.ProvideTooltipDismissalEpoch
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.animatedcontentfixed.AnimatedVisibility
import dev.nohus.rift.compose.animatedcontentfixed.fadeIn
import dev.nohus.rift.compose.animatedcontentfixed.fadeOut
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.eye_16px
import dev.nohus.rift.generated.resources.indicator_assets
import dev.nohus.rift.generated.resources.indicator_asteroid_belt
import dev.nohus.rift.generated.resources.indicator_colony
import dev.nohus.rift.generated.resources.indicator_ice_field
import dev.nohus.rift.generated.resources.indicator_jove
import dev.nohus.rift.generated.resources.indicator_jump_drive
import dev.nohus.rift.generated.resources.indicator_jump_gate
import dev.nohus.rift.generated.resources.indicator_jumps
import dev.nohus.rift.generated.resources.indicator_kills
import dev.nohus.rift.generated.resources.indicator_npc_kills
import dev.nohus.rift.generated.resources.indicator_pod
import dev.nohus.rift.generated.resources.indicator_stations
import dev.nohus.rift.generated.resources.locationinfo
import dev.nohus.rift.generated.resources.overlay_assets_tooltip
import dev.nohus.rift.generated.resources.overlay_asteroid_belts
import dev.nohus.rift.generated.resources.overlay_display_overlay_content_nearby_intel_text
import dev.nohus.rift.generated.resources.overlay_display_overlay_content_route_intel_text
import dev.nohus.rift.generated.resources.overlay_display_overlay_content_system_info_text
import dev.nohus.rift.generated.resources.overlay_jumps_tooltip
import dev.nohus.rift.generated.resources.overlay_light_years
import dev.nohus.rift.generated.resources.overlay_npckills_tooltip
import dev.nohus.rift.generated.resources.overlay_podkills_tooltip
import dev.nohus.rift.generated.resources.overlay_shipkills_tooltip
import dev.nohus.rift.generated.resources.overlay_show_sovereignty_unclaimed_text
import dev.nohus.rift.generated.resources.overlay_stations_tooltip
import dev.nohus.rift.generated.resources.overlay_system_info_overlay_content_ansiblex_cost_zone_tooltip
import dev.nohus.rift.generated.resources.overlay_system_info_overlay_content_ice_field_system_tooltip
import dev.nohus.rift.generated.resources.overlay_system_info_overlay_content_jump_range_text
import dev.nohus.rift.generated.resources.overlay_system_info_overlay_content_out_jump_range_text
import dev.nohus.rift.generated.resources.route
import dev.nohus.rift.generated.resources.window_sovereignty
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.map.ClonesIndicators
import dev.nohus.rift.map.RaidableSkyhooksInfo
import dev.nohus.rift.map.SovereigntyUpgradesIndicators
import dev.nohus.rift.network.esi.models.SovereigntySystemClaim
import dev.nohus.rift.overlay.compose.DistanceIntelIndicator
import dev.nohus.rift.overlay.compose.InfoTypeIndicator
import dev.nohus.rift.overlay.compose.LocalRats
import dev.nohus.rift.overlay.compose.MovableOverlayContent
import dev.nohus.rift.overlay.compose.PlanetsInSystem
import dev.nohus.rift.overlay.compose.RouteIntelIndicator
import dev.nohus.rift.overlay.compose.SpaceType
import dev.nohus.rift.overlay.compose.SystemName
import dev.nohus.rift.overlay.window.OverlayWindowFailureReason
import dev.nohus.rift.overlay.window.RiftOverlayWindow
import dev.nohus.rift.repositories.NamesRepository
import dev.nohus.rift.settings.persistence.InfoPanelSection
import dev.nohus.rift.settings.persistence.OverlayInfoType
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.Pos
import dev.nohus.rift.utils.evewindows.EveWindowId
import dev.nohus.rift.utils.evewindows.EveWindowsRepository
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

private const val STALE_OVERLAY_GEOMETRY_MILLIS = 2_000L
private const val NATIVE_OVERLAY_RETRY_MILLIS = 5_000L

@Composable
fun Overlays() {
    val viewModel: OverlayViewModel = viewModel()
    val contentState by viewModel.state.collectAsState()
    val repository: EveWindowsRepository = remember { koin.get() }
    val settings: Settings = remember { koin.get() }
    val settingsModel by settings.updateFlow.collectAsState()
    val localCharactersRepository: LocalCharactersRepository = remember { koin.get() }
    val localCharacters by localCharactersRepository.characters.collectAsState()
    val runtimeStatus: OverlayRuntimeStatusRepository = remember { koin.get() }
    val placementController: OverlayPlacementController = remember { koin.get() }
    val isPositioning by placementController.isPositioning.collectAsState()
    val failures by runtimeStatus.failures.collectAsState()
    val targets = remember { mutableStateMapOf<EveWindowId, OverlayTarget>() }
    val nextRetryNanos = failures.values.filterIsInstance<OverlayWindowFailure.RetryLater>()
        .minOfOrNull { it.retryAfterNanos }
    LaunchedEffect(runtimeStatus, nextRetryNanos) {
        if (nextRetryNanos != null) {
            val remainingNanos = (nextRetryNanos - System.nanoTime()).coerceAtLeast(0)
            delay(((remainingNanos + 999_999) / 1_000_000).coerceAtLeast(1).milliseconds)
            runtimeStatus.clearExpiredRetries()
        }
    }
    LaunchedEffect(repository, viewModel, settingsModel.overlayEnabledCharacterIds, failures) {
        combine(repository.state, localCharactersRepository.characters, viewModel.state) { windowsState, characters, overlayState ->
            Triple(windowsState, characters, overlayState)
        }.collectLatest { [repositoryState, characters, overlayState] ->
            if (settingsModel.overlayEnabledCharacterIds.isEmpty()) {
                targets.clear()
                runtimeStatus.retainWindows(emptySet())
                return@collectLatest
            }
            when (repositoryState.status) {
                EveWindowsRepository.Status.Available -> Unit
                is EveWindowsRepository.Status.Failed -> {
                    val ageMillis = repositoryState.updatedAt?.let {
                        Duration.between(it, Instant.now()).toMillis().coerceAtLeast(0)
                    } ?: STALE_OVERLAY_GEOMETRY_MILLIS
                    val remainingMillis = (STALE_OVERLAY_GEOMETRY_MILLIS - ageMillis).coerceAtLeast(0)
                    if (remainingMillis > 0) delay(remainingMillis.milliseconds)
                    if (repository.state.value.status is EveWindowsRepository.Status.Failed) targets.clear()
                    return@collectLatest
                }
                else -> {
                    targets.clear()
                    return@collectLatest
                }
            }
            runtimeStatus.retainWindows(repositoryState.windows.mapTo(mutableSetOf()) { it.id })
            val windows = repositoryState.windows.filter { eveWindow ->
                val geometryKey = eveWindow.getOverlayWindowGeometryKey()
                val failure = failures[eveWindow.id]
                if (failure != null && failure.geometry != geometryKey) runtimeStatus.clearFailure(eveWindow.id)
                val character = characters.firstOrNull { it.info?.name == eveWindow.characterName }
                val characterId = character?.characterId
                getOverlayStatus(
                    window = eveWindow,
                    isOverlayEnabled = characterId in settingsModel.overlayEnabledCharacterIds,
                    character = character,
                    systemId = characterId?.let { overlayState.characterSystemIds[it] },
                    isSystemAvailable = characterId?.let { it in overlayState.characterContent } == true,
                    runtimeFailure = failure?.takeIf { it.geometry == geometryKey },
                ) == OverlayStatus.Ready
            }
            val visibleIds = windows.mapTo(mutableSetOf()) { it.id }
            targets.keys.toList().filter { it !in visibleIds }.forEach { targets.remove(it) }

            windows.forEach { eveWindow ->
                val bounds = eveWindow.geometry.client.overlayBounds ?: return@forEach
                val geometryKey = eveWindow.getOverlayWindowGeometryKey() ?: return@forEach
                val size = DpSize(bounds.width.dp, bounds.height.dp)
                val position = WindowPosition.Absolute(bounds.x.dp, bounds.y.dp)
                val currentTarget = targets[eveWindow.id]
                if (currentTarget == null) {
                    targets[eveWindow.id] = OverlayTarget(
                        id = eveWindow.id,
                        characterName = eveWindow.characterName,
                        isActive = eveWindow.isActive,
                        processId = eveWindow.processId,
                        macWindowLevel = eveWindow.macWindowLevel,
                        geometryKey = geometryKey,
                        state = RiftWindowState(
                            windowState = WindowState(
                                size = size,
                                position = position,
                            ),
                            minimumSize = null to null,
                        ),
                    )
                } else {
                    if (currentTarget.isActive != eveWindow.isActive || currentTarget.characterName != eveWindow.characterName ||
                        currentTarget.processId != eveWindow.processId || currentTarget.macWindowLevel != eveWindow.macWindowLevel ||
                        currentTarget.geometryKey != geometryKey
                    ) {
                        targets[eveWindow.id] = currentTarget.copy(
                            characterName = eveWindow.characterName,
                            isActive = eveWindow.isActive,
                            processId = eveWindow.processId,
                            macWindowLevel = eveWindow.macWindowLevel,
                            geometryKey = geometryKey,
                        )
                    }
                    val windowState = currentTarget.state.windowState
                    if (windowState.size != size) windowState.size = size
                    if (windowState.position != position) windowState.position = position
                }
            }
        }
    }
    targets.values.forEach { overlayTarget ->
        key(overlayTarget.id) {
            val character = localCharacters.firstOrNull { it.info?.name == overlayTarget.characterName }
            val characterContent = character?.characterId?.let { contentState.characterContent[it] }
            val tooltipDismissalEpoch = remember { mutableStateOf(0) }
            if (characterContent != null) {
                RiftOverlayWindow(
                    windowState = overlayTarget.state,
                    eveWindowId = overlayTarget.id,
                    isEveActive = overlayTarget.isActive,
                    eveProcessId = overlayTarget.processId,
                    eveWindowLevel = overlayTarget.macWindowLevel,
                    onMouseExited = { tooltipDismissalEpoch.value++ },
                    onNativeWindowInvalidated = { reason ->
                        val currentTarget = targets[overlayTarget.id]
                        if (currentTarget?.state === overlayTarget.state) {
                            if (currentTarget.geometryKey == overlayTarget.geometryKey) {
                                val failure = when (reason) {
                                    OverlayWindowFailureReason.FrameTooLarge ->
                                        OverlayWindowFailure.TooLarge(overlayTarget.geometryKey)
                                    OverlayWindowFailureReason.NativeFailure -> OverlayWindowFailure.RetryLater(
                                        overlayTarget.geometryKey,
                                        System.nanoTime() + NATIVE_OVERLAY_RETRY_MILLIS * 1_000_000,
                                    )
                                }
                                runtimeStatus.reportFailure(overlayTarget.id, failure)
                            }
                            targets.remove(overlayTarget.id)
                        }
                    },
                ) {
                    RiftTheme {
                        ProvideTooltipDismissalEpoch(tooltipDismissalEpoch.value) {
                            DisplayOverlayContent(
                                state = contentState,
                                characterContent = characterContent,
                                characterAllianceId = character.info?.allianceId,
                                neocomWidthPx = contentState.neocomWidthsPx[character.characterId] ?: 48,
                                savedPosition = settingsModel.overlayPosition,
                                isPositioning = isPositioning,
                                hiddenOverlaySections = settingsModel.hiddenOverlaySections,
                                onHiddenOverlaySectionsChanged = { settings.hiddenOverlaySections = it },
                                onPositionChanged = { settings.overlayPosition = it },
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class OverlayTarget(
    val id: EveWindowId,
    val characterName: String,
    val isActive: Boolean,
    val processId: Int?,
    val macWindowLevel: Int?,
    val geometryKey: OverlayWindowGeometryKey,
    val state: RiftWindowState,
)

@Composable
private fun DisplayOverlayContent(
    state: OverlayViewModel.State,
    characterContent: OverlayViewModel.CharacterContent,
    characterAllianceId: Int?,
    neocomWidthPx: Int,
    savedPosition: Pos?,
    isPositioning: Boolean,
    hiddenOverlaySections: Set<InfoPanelSection>,
    onHiddenOverlaySectionsChanged: (Set<InfoPanelSection>) -> Unit,
    onPositionChanged: (Pos) -> Unit,
) {
    var selectedDistance by remember { mutableStateOf<Int?>(null) }
    var selectedDistanceAtPress by remember { mutableStateOf<Int?>(null) }
    var selectedRoutePosition by remember { mutableStateOf<Int?>(null) }
    var selectedRoutePositionAtPress by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    val distanceBands = characterContent.distanceBands
    LaunchedEffect(characterContent.system.id) {
        selectedDistance = null
        selectedRoutePosition = null
    }
    LaunchedEffect(characterContent.routeIntel?.stops?.map { it.systemIntel.system.id }) {
        selectedRoutePosition = null
    }
    LaunchedEffect(distanceBands.map { it.distance }) {
        if (selectedDistance != null && distanceBands.none { it.distance == selectedDistance }) {
            selectedDistance = null
        }
    }
    MovableOverlayContent(
        neocomWidthPx = neocomWidthPx,
        savedPosition = savedPosition,
        isPositioning = isPositioning,
        onPositionChanged = onPositionChanged,
        onPress = {
            selectedDistanceAtPress = selectedDistance
            selectedDistance = null
            selectedRoutePositionAtPress = selectedRoutePosition
            selectedRoutePosition = null
        },
    ) {
        Column {
            val isRouteAvailable = characterContent.routeIntel != null
            var isRouteAutoHidden by remember(isRouteAvailable) { mutableStateOf(!isRouteAvailable) }
            val hiddenSections = hiddenOverlaySections + if (isRouteAutoHidden) setOf(InfoPanelSection.RouteIntel) else emptySet()

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier.padding(start = 16.dp + Spacing.small, bottom = Spacing.medium)
            ) {
                InfoPanelSection.entries.forEach { entry ->
                    RiftTooltipArea(entry.tooltip) {
                        RiftImageButton(
                            resource = entry.icon,
                            size = 16.dp,
                            tint = RiftTheme.colors.textHighlighted,
                            isFullAlpha = true,
                            highlightModifier = 2f,
                            onClick = {
                                val updatedHiddenSections = if (entry in hiddenSections) {
                                    hiddenOverlaySections - entry
                                } else {
                                    hiddenOverlaySections + entry
                                }
                                onHiddenOverlaySectionsChanged(updatedHiddenSections)
                                if (entry == InfoPanelSection.RouteIntel) {
                                    isRouteAutoHidden = false
                                }
                            }
                        )
                    }
                }
            }

            InfoPanelSection(
                section = InfoPanelSection.SystemInfo,
                hiddenSections = hiddenSections,
            ) {
                SystemInfoOverlayContent(state, characterContent, characterAllianceId)
            }

            InfoPanelSection(
                section = InfoPanelSection.DistanceIntel,
                hiddenSections = hiddenSections,
            ) {
                DistanceIntelIndicator(
                    bands = distanceBands,
                    isCountAlwaysShown = state.isDistanceIntelCountAlwaysShown,
                    selectedDistance = selectedDistance,
                    onDistanceSelected = { distance ->
                        selectedDistance = if (selectedDistanceAtPress == distance) null else distance
                    },
                    onSelectionDismissed = { distance ->
                        scope.launch {
                            withFrameNanos { }
                            if (selectedDistance == distance) selectedDistance = null
                        }
                    },
                )
            }

            InfoPanelSection(
                section = InfoPanelSection.RouteIntel,
                hiddenSections = hiddenSections,
            ) {
                RouteIntelIndicator(
                    route = characterContent.routeIntel,
                    killsBySystemId = state.routeKillsBySystemId,
                    characterAllianceId = characterAllianceId,
                    selectedPosition = selectedRoutePosition,
                    onPositionSelected = { position ->
                        selectedRoutePosition = if (selectedRoutePositionAtPress == position) null else position
                    },
                    onSelectionDismissed = { position ->
                        scope.launch {
                            withFrameNanos { }
                            if (selectedRoutePosition == position) selectedRoutePosition = null
                        }
                    },
                )
            }
        }
    }
}

private val InfoPanelSection.icon: DrawableResource get() = when (this) {
    InfoPanelSection.SystemInfo -> Res.drawable.locationinfo
    InfoPanelSection.DistanceIntel -> Res.drawable.eye_16px
    InfoPanelSection.RouteIntel -> Res.drawable.route
}

private val InfoPanelSection.tooltip: String @Composable get() = when (this) {
    InfoPanelSection.SystemInfo -> stringResource(Res.string.overlay_display_overlay_content_system_info_text)
    InfoPanelSection.DistanceIntel -> stringResource(Res.string.overlay_display_overlay_content_nearby_intel_text)
    InfoPanelSection.RouteIntel -> stringResource(Res.string.overlay_display_overlay_content_route_intel_text)
}

@Composable
private fun InfoPanelSection(
    section: InfoPanelSection,
    hiddenSections: Set<InfoPanelSection>,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = section !in hiddenSections,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.padding(bottom = Spacing.medium)
        ) {
            RiftTooltipArea(section.tooltip) {
                Image(
                    painter = painterResource(section.icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(RiftTheme.colors.textHighlighted),
                    modifier = Modifier
                        .offset(y = 3.dp)
                        .size(16.dp)
                )
            }
            content()
        }
    }
}

@Composable
private fun SystemInfoOverlayContent(
    state: OverlayViewModel.State,
    characterContent: OverlayViewModel.CharacterContent,
    characterAllianceId: Int?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        val system = characterContent.system
        val systemStatus = characterContent.systemStatus
        val enabled = state.enabledInfoTypes
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (OverlayInfoType.System in enabled || OverlayInfoType.Constellation in enabled || OverlayInfoType.Region in enabled) {
                SystemName(
                    system = system,
                    showSystem = OverlayInfoType.System in enabled,
                    showConstellation = OverlayInfoType.Constellation in enabled,
                    showRegion = OverlayInfoType.Region in enabled,
                    securityTrailingContent = if (OverlayInfoType.System in enabled && OverlayInfoType.SpaceType in enabled && state.isSpaceTypeIconOnly) {
                        { SpaceType(system, systemStatus, characterAllianceId, iconOnly = true) }
                    } else {
                        null
                    },
                )
            }
            if (OverlayInfoType.System !in enabled && OverlayInfoType.SpaceType in enabled && state.isSpaceTypeIconOnly) {
                SpaceType(system, systemStatus, characterAllianceId, iconOnly = true)
            }
            if (OverlayInfoType.Sovereignty in enabled && state.isSovereigntyIconOnly) {
                ShowSovereignty(systemStatus?.sovereignty, iconOnly = true)
            }
            if (OverlayInfoType.LocalRats in enabled) LocalRats(systemStatus)
        }
        if (OverlayInfoType.SpaceType in enabled && !state.isSpaceTypeIconOnly) {
            SpaceType(system, systemStatus, characterAllianceId)
        }
        if (OverlayInfoType.Sovereignty in enabled && !state.isSovereigntyIconOnly) {
            ShowSovereignty(systemStatus?.sovereignty, iconOnly = false)
        }
        if (OverlayInfoType.MapMarkers in enabled && systemStatus?.markers?.isNotEmpty() == true) {
            Column {
                systemStatus.markers.forEach { marker ->
                    InfoTypeIndicator(marker.label, marker.icon, tint = marker.color)
                }
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            if (OverlayInfoType.Jumps in enabled) {
                val jumps = systemStatus?.shipJumps?.takeIf { it > 0 }?.toString()
                InfoTypeIndicator(jumps, Res.drawable.indicator_jumps, stringResource(Res.string.overlay_jumps_tooltip, jumps.toString()))
            }
            if (OverlayInfoType.Kills in enabled) {
                val podKills = systemStatus?.podKills?.takeIf { it > 0 }?.toString()
                InfoTypeIndicator(podKills, Res.drawable.indicator_pod, stringResource(Res.string.overlay_podkills_tooltip, podKills.toString()))
                val shipKills = systemStatus?.shipKills?.takeIf { it > 0 }?.toString()
                InfoTypeIndicator(shipKills, Res.drawable.indicator_kills, stringResource(Res.string.overlay_shipkills_tooltip, shipKills.toString()))
            }
            if (OverlayInfoType.NpcKills in enabled) {
                val npcKills = systemStatus?.npcKills?.takeIf { it > 0 }?.toString()
                InfoTypeIndicator(npcKills, Res.drawable.indicator_npc_kills, stringResource(Res.string.overlay_npckills_tooltip, npcKills.toString()))
            }
            if (OverlayInfoType.Assets in enabled) {
                val assets = systemStatus?.assetCount?.takeIf { it > 0 }?.toString()
                InfoTypeIndicator(assets, Res.drawable.indicator_assets, stringResource(Res.string.overlay_assets_tooltip, assets.toString()))
            }
            if (OverlayInfoType.Stations in enabled) {
                val stations = systemStatus?.stations?.size?.takeIf { it > 0 }?.toString()
                InfoTypeIndicator(stations, Res.drawable.indicator_stations, stringResource(Res.string.overlay_stations_tooltip, stations.toString()))
            }
            if (OverlayInfoType.AsteroidBelts in enabled) {
                InfoTypeIndicator(
                    text = system.asteroidBeltCount.takeIf { it > 0 }?.toString(),
                    icon = Res.drawable.indicator_asteroid_belt,
                    tooltip = Res.plurals.overlay_asteroid_belts.toUiText(system.asteroidBeltCount, system.asteroidBeltCount.toUiTextArgument()).resolve(),
                )
            }
            if (OverlayInfoType.IceFields in enabled) {
                InfoTypeIndicator(
                    text = "".takeIf { system.iceFieldCount > 0 },
                    icon = Res.drawable.indicator_ice_field,
                    tooltip = stringResource(Res.string.overlay_system_info_overlay_content_ice_field_system_tooltip),
                )
            }
            if (OverlayInfoType.Colonies in enabled) {
                val colonies = systemStatus?.colonies?.takeIf { it > 0 }
                if (colonies != null) InfoTypeIndicator("$colonies", Res.drawable.indicator_colony)
            }
            if (OverlayInfoType.JoveObservatories in enabled && system.hasJoveObservatory) {
                InfoTypeIndicator("", Res.drawable.indicator_jove)
            }
            if (OverlayInfoType.AnsiblexZone in enabled) {
                systemStatus?.ansiblexZone?.let { zone ->
                    InfoTypeIndicator(
                        text = zone.displayText.resolve(),
                        icon = Res.drawable.indicator_jump_gate,
                        tooltip = stringResource(Res.string.overlay_system_info_overlay_content_ansiblex_cost_zone_tooltip),
                    )
                }
            }
            if (OverlayInfoType.JumpRange in enabled) {
                systemStatus?.distance?.let {
                    val lightYears = Res.string.overlay_light_years.toUiText(it.distanceLy.toUiTextArgument(fractionDigits = 1)).resolve()
                    val tooltip = if (it.isInJumpRange) stringResource(Res.string.overlay_system_info_overlay_content_jump_range_text) else stringResource(Res.string.overlay_system_info_overlay_content_out_jump_range_text)
                    val tint = if (it.isInJumpRange) RiftTheme.colors.successGreen else RiftTheme.colors.hotRed
                    InfoTypeIndicator(lightYears, Res.drawable.indicator_jump_drive, tooltip, tint)
                }
            }
            if (OverlayInfoType.Clones in enabled) {
                val clones = systemStatus?.clones?.takeIf { it.isNotEmpty() }
                if (clones != null) ClonesIndicators(clones, false)
            }
        }

        if (OverlayInfoType.Planets in enabled) PlanetsInSystem(systemStatus)

        systemStatus?.sovereigntyUpgrades?.takeIf { OverlayInfoType.SovereigntyUpgrades in enabled && it.isNotEmpty() }?.let {
            SovereigntyUpgradesIndicators(it, isExpanded = true)
        }

        if (OverlayInfoType.RaidableSkyhooks in enabled) RaidableSkyhooksInfo(systemStatus, isShowingDetail = false)
    }
}

@Composable
private fun ShowSovereignty(claim: SovereigntySystemClaim?, iconOnly: Boolean) {
    if (claim == null) return
    val allianceId = claim.alliance?.allianceId
    val factionId = claim.faction?.factionId
    if (allianceId == null && factionId == null && claim.unclaimed != true) return

    val namesRepository: NamesRepository = remember { koin.get() }
    val name = when {
        allianceId != null -> namesRepository.getName(allianceId) ?: "?"
        factionId != null -> namesRepository.getName(factionId) ?: "?"
        else -> stringResource(Res.string.overlay_show_sovereignty_unclaimed_text)
    }
    val icon: @Composable () -> Unit = {
        when {
            allianceId != null -> AsyncAllianceLogo(allianceId = allianceId, size = 32, modifier = Modifier.size(32.dp))
            factionId != null -> AsyncCorporationLogo(corporationId = factionId, size = 32, modifier = Modifier.size(32.dp))
            else -> Image(
                painter = painterResource(Res.drawable.window_sovereignty),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
        }
    }
    if (iconOnly) {
        RiftTooltipArea(text = name) { icon() }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Text(
                text = name,
                style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}
