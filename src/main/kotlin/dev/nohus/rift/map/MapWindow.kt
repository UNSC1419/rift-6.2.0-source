package dev.nohus.rift.map

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.nohus.rift.EventEffect
import dev.nohus.rift.utils.entitylinks.EntityInteractionProvider
import dev.nohus.rift.compose.KeyName
import dev.nohus.rift.compose.RiftContextMenuPopup
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftSearchField
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.TitleBarStyle
import dev.nohus.rift.compose.animatedcontentfixed.AnimatedContentFixed
import dev.nohus.rift.compose.animatedcontentfixed.AnimatedVisibility
import dev.nohus.rift.compose.animatedcontentfixed.ContentTransform
import dev.nohus.rift.compose.animatedcontentfixed.fadeIn
import dev.nohus.rift.compose.animatedcontentfixed.fadeOut
import dev.nohus.rift.compose.animatedcontentfixed.scaleIn
import dev.nohus.rift.compose.animatedcontentfixed.scaleOut
import dev.nohus.rift.compose.animatedcontentfixed.togetherWith
import dev.nohus.rift.compose.onKeyPress
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.abstract_layout_32px
import dev.nohus.rift.generated.resources.focus_viewfinder_32px
import dev.nohus.rift.generated.resources.map_shortcut_focus_current_location
import dev.nohus.rift.generated.resources.map_action_fit_to_window
import dev.nohus.rift.generated.resources.map_action_focus_current_location
import dev.nohus.rift.generated.resources.map_shortcut_fit_to_window
import dev.nohus.rift.generated.resources.map_action_toggle_2d_layout
import dev.nohus.rift.generated.resources.map_fit_view_32px
import dev.nohus.rift.generated.resources.map_window_title
import dev.nohus.rift.generated.resources.window_map
import dev.nohus.rift.intel.state.getTotalIntelEntityCount
import dev.nohus.rift.map.MapViewModel.MapType
import dev.nohus.rift.map.MapViewModel.MapType.ClusterRegionsMap
import dev.nohus.rift.map.MapViewModel.MapType.ClusterSystemsMap
import dev.nohus.rift.map.MapViewModel.MapType.DistanceMap
import dev.nohus.rift.map.MapViewModel.MapType.RegionMap
import dev.nohus.rift.map.MapViewModel.Transform
import dev.nohus.rift.map.MapViewModel.UiState
import dev.nohus.rift.map.MapViewModel.VoronoiLayout
import dev.nohus.rift.map.painter.MapPainter
import dev.nohus.rift.map.painter.RegionsMapPainter
import dev.nohus.rift.map.painter.SystemsMapPainter
import dev.nohus.rift.map.systemcolor.SystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.AnsiblexZonesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.AssetSafetySystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.AssetsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.AsteroidBeltsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.ClonesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.ColoniesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.FactionWarfareSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.HostileEntitiesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.IceFieldsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.IncursionsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.IndustryIndicesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.JoveObservatorySystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.JumpRangeSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.JumpsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.KillsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.MetaliminalStormsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.NpcKillsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.NullSecuritySystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.RaidableSkyhooksColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.RatsTypeSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.SecuritySystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.SovereigntyIndicesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.SovereigntyIndicesSystemColorStrategy.SovereigntyIndex
import dev.nohus.rift.map.systemcolor.strategies.SovereigntySystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.SovereigntyUpgradesSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.StandingsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.StarColorSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.StationsSystemColorStrategy
import dev.nohus.rift.map.systemcolor.strategies.WormholesSystemColorStrategy
import dev.nohus.rift.network.esi.models.IndustryActivity
import dev.nohus.rift.repositories.PlanetTypes.PlanetType
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.settings.persistence.MapSystemInfoType
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import dev.nohus.rift.settings.persistence.MapType as SettingsMapType

@Composable
fun MapWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: MapViewModel = viewModel(windowState.uuid)
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.map_window_title),
        icon = Res.drawable.window_map,
        state = windowState,
        onCloseClick = onCloseRequest,
        titleBarStyle = if (state.settings.isUsingCompactMode) TitleBarStyle.Small else TitleBarStyle.Full,
        titleBarContent = { height ->
            ToolbarRow(
                state = state,
                fixedHeight = height,
                onTabSelect = viewModel::onTabSelect,
                onTabClose = viewModel::onTabClose,
                onSearchChange = viewModel::onSearchChange,
                onSearchSubmit = viewModel::onSearchSubmit,
            )
        },
        withContentPadding = !state.settings.isUsingCompactMode,
        isMaximizeButtonShown = true,
    ) {
        MapWindowContent(
            state = state,
            onMapHover = viewModel::onMapHover,
            onRegionPointerEnter = viewModel::onRegionPointerEnter,
            onRegionPointerExit = viewModel::onRegionPointerExit,
            onRegionClick = viewModel::onRegionClick,
            onMapClick = viewModel::onMapClick,
            onContextMenuDismiss = viewModel::onContextMenuDismiss,
            onMapTransformChanged = viewModel::onMapTransformChanged,
            onSystemColorChange = viewModel::onSystemColorChange,
            onSystemColorHover = viewModel::onSystemColorHover,
            onCellColorChange = viewModel::onCellColorChange,
            onCellColorHover = viewModel::onCellColorHover,
            onIndicatorChange = viewModel::onIndicatorChange,
            onInfoBoxChange = viewModel::onInfoBoxChange,
            onAnsiblexCapitalSystemUpdate = viewModel::onAnsiblexCapitalSystemUpdate,
            onJumpRangeTargetUpdate = viewModel::onJumpRangeTargetUpdate,
            onJumpRangeDistanceUpdate = viewModel::onJumpRangeDistanceUpdate,
            onPlanetTypesUpdate = viewModel::onPlanetTypesUpdate,
            onSovereigntyUpgradeTypesUpdate = viewModel::onSovereigntyUpgradeTypesUpdate,
            onLayoutSelected = viewModel::onLayoutSelected,
            onDistanceMapCenterUpdate = viewModel::onDistanceMapCenterUpdate,
            onDistanceMapRangeUpdate = viewModel::onDistanceMapRangeUpdate,
            onFocusCurrentClick = viewModel::onFocusCurrentClick,
            onFitMapClick = viewModel::onFitMapClick,
            onToggle2dLayoutClick = viewModel::onToggle2dLayoutClick,
        )
    }
}

data class DoubleOffset(val x: Double, val y: Double) {
    operator fun plus(other: DoubleOffset) = DoubleOffset(x + other.x, y + other.y)
}

@Composable
private fun MapWindowContent(
    state: UiState,
    onMapHover: (Offset, mapScale: Float) -> Unit,
    onRegionPointerEnter: (Int) -> Unit,
    onRegionPointerExit: (Int) -> Unit,
    onRegionClick: (regionId: Int, systemId: Int) -> Unit,
    onMapClick: (button: Int) -> Unit,
    onContextMenuDismiss: () -> Unit,
    onMapTransformChanged: (MapType, Transform) -> Unit,
    onSystemColorChange: (SettingsMapType, MapSystemInfoType) -> Unit,
    onSystemColorHover: (SettingsMapType, MapSystemInfoType, Boolean) -> Unit,
    onCellColorChange: (SettingsMapType, MapSystemInfoType?) -> Unit,
    onCellColorHover: (SettingsMapType, MapSystemInfoType?, Boolean) -> Unit,
    onIndicatorChange: (SettingsMapType, MapSystemInfoType) -> Unit,
    onInfoBoxChange: (SettingsMapType, MapSystemInfoType) -> Unit,
    onAnsiblexCapitalSystemUpdate: (String) -> Unit,
    onJumpRangeTargetUpdate: (String) -> Unit,
    onJumpRangeDistanceUpdate: (Double) -> Unit,
    onPlanetTypesUpdate: (List<PlanetType>) -> Unit,
    onSovereigntyUpgradeTypesUpdate: (List<Type>) -> Unit,
    onLayoutSelected: (Int) -> Unit,
    onDistanceMapCenterUpdate: (String) -> Unit,
    onDistanceMapRangeUpdate: (Int) -> Unit,
    onFocusCurrentClick: () -> Unit,
    onFitMapClick: () -> Unit,
    onToggle2dLayoutClick: () -> Unit,
) {
    Box {
        val hazeState = rememberHazeState()
        AnimatedContentFixed(
            targetState = state,
            contentKey = { it.mapType },
            transitionSpec = { getMapChangeTransition(initialState, targetState) },
            modifier = Modifier
                .background(RiftTheme.colors.mapBackground)
                .border(1.dp, RiftTheme.colors.borderGrey)
                .hazeSource(state = hazeState),
        ) { state ->
            Map(
                state = state,
                onMapHover = onMapHover,
                onRegionPointerEnter = onRegionPointerEnter,
                onRegionPointerExit = onRegionPointerExit,
                onRegionClick = onRegionClick,
                onMapClick = onMapClick,
                onContextMenuDismiss = onContextMenuDismiss,
                onMapTransformChanged = { onMapTransformChanged(state.mapType, it) },
                onFocusCurrentClick = onFocusCurrentClick,
                onFitMapClick = onFitMapClick,
            )
        }
        MapSettingsPanel(
            hazeState = hazeState,
            mapType = state.mapType,
            systemInfoTypes = state.systemInfoTypes,
            mapAnsiblexZonesState = state.mapAnsiblexZonesState,
            mapJumpRangeState = state.mapJumpRangeState,
            mapPlanetsState = state.mapPlanetsState,
            mapSovereigntyUpgradesState = state.mapSovereigntyUpgradesState,
            distanceMapState = state.distanceMapState,
            alternativeLayouts = state.alternativeLayouts,
            onSystemColorChange = onSystemColorChange,
            onSystemColorHover = onSystemColorHover,
            onCellColorChange = onCellColorChange,
            onCellColorHover = onCellColorHover,
            onIndicatorChange = onIndicatorChange,
            onInfoBoxChange = onInfoBoxChange,
            onAnsiblexCapitalSystemUpdate = onAnsiblexCapitalSystemUpdate,
            onJumpRangeTargetUpdate = onJumpRangeTargetUpdate,
            onJumpRangeDistanceUpdate = onJumpRangeDistanceUpdate,
            onPlanetTypesUpdate = onPlanetTypesUpdate,
            onSovereigntyUpgradeTypesUpdate = onSovereigntyUpgradeTypesUpdate,
            onLayoutSelected = onLayoutSelected,
            onDistanceMapCenterUpdate = onDistanceMapCenterUpdate,
            onDistanceMapRangeUpdate = onDistanceMapRangeUpdate,
        )
        BottomControlButtons(
            mapType = state.mapType,
            onFocusCurrentClick = onFocusCurrentClick,
            onFitMapClick = onFitMapClick,
            onToggle2dLayoutClick = onToggle2dLayoutClick,
        )
    }
}

@Composable
private fun BoxScope.BottomControlButtons(
    mapType: MapType,
    onFocusCurrentClick: () -> Unit,
    onFitMapClick: () -> Unit,
    onToggle2dLayoutClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier
            .padding(Spacing.medium)
            .align(Alignment.BottomEnd),
    ) {
        AnimatedVisibility(mapType != DistanceMap) {
            RiftTooltipArea(
                tooltip = {
                    KeyName(stringResource(Res.string.map_shortcut_focus_current_location), stringResource(Res.string.map_action_focus_current_location), modifier = Modifier.padding(Spacing.large))
                },
            ) {
                RiftImageButton(
                    resource = Res.drawable.focus_viewfinder_32px,
                    size = 32.dp,
                    onClick = onFocusCurrentClick,
                )
            }
        }
        RiftTooltipArea(
            tooltip = {
                KeyName(stringResource(Res.string.map_shortcut_fit_to_window), stringResource(Res.string.map_action_fit_to_window), modifier = Modifier.padding(Spacing.large))
            },
        ) {
            RiftImageButton(
                resource = Res.drawable.map_fit_view_32px,
                size = 32.dp,
                onClick = onFitMapClick,
            )
        }
        AnimatedVisibility(mapType is ClusterSystemsMap) {
            RiftTooltipArea(
                text = stringResource(Res.string.map_action_toggle_2d_layout),
            ) {
                RiftImageButton(
                    resource = Res.drawable.abstract_layout_32px,
                    size = 32.dp,
                    onClick = onToggle2dLayoutClick,
                )
            }
        }
    }
}

@Composable
private fun ToolbarRow(
    state: UiState,
    fixedHeight: Dp,
    onTabSelect: (Int) -> Unit,
    onTabClose: (Int) -> Unit,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RiftTabBar(
            tabs = state.tabs,
            selectedTab = state.selectedTab,
            onTabSelected = onTabSelect,
            onTabClosed = onTabClose,
            withUnderline = false,
            isShowingIcons = state.settings.isUsingCompactMode,
            fixedHeight = fixedHeight,
            modifier = Modifier.weight(1f),
        )

        val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
        val suggestions by derivedStateOf {
            solarSystemsRepository.getSystems()
                .map { it.name }
                .filter { it.lowercase().startsWith(state.search?.lowercase() ?: "") }
        }
        Spacer(Modifier.width(Spacing.medium))
        RiftSearchField(
            search = state.search,
            suggestions = suggestions.take(5),
            isCompact = state.settings.isUsingCompactMode,
            onSearchChange = {
                onSearchChange(it)
            },
            onSearchConfirm = onSearchSubmit,
            modifier = Modifier
                .width(100.dp),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun Map(
    state: UiState,
    onMapHover: (Offset, mapScale: Float) -> Unit,
    onRegionPointerEnter: (Int) -> Unit,
    onRegionPointerExit: (Int) -> Unit,
    onRegionClick: (regionId: Int, systemId: Int) -> Unit,
    onMapClick: (button: Int) -> Unit,
    onContextMenuDismiss: () -> Unit,
    onMapTransformChanged: (Transform) -> Unit,
    onFocusCurrentClick: () -> Unit,
    onFitMapClick: () -> Unit,
) {
    val layoutBounds by remember(state.layout) { mutableStateOf(getMapLayoutBounds(state.layout)) }
    val zoomRange = remember(state.mapType) {
        when (state.mapType) {
            ClusterRegionsMap -> 1.0..2.0
            is ClusterSystemsMap -> 0.2..20.0
            is RegionMap -> 0.12..2.0
            is DistanceMap -> 0.2..2.0
        }
    }
    var zoom by remember {
        mutableStateOf(
            state.mapState.initialTransform?.zoom ?: if (state.mapType is ClusterSystemsMap) 0.2 else 1.0,
        )
    }
    val animatedZoom by animateFloatAsState(zoom.toFloat(), spring(stiffness = Spring.StiffnessLow))
    var center by remember {
        mutableStateOf(
            if (state.mapState.centeredSystem != null) {
                state.layout[state.mapState.centeredSystem.value]
                    ?.let { Offset(it.position.x.toFloat(), it.position.y.toFloat()) }
                    ?: getMapLayoutCenter(layoutBounds)
            } else {
                state.mapState.initialTransform?.center ?: getMapLayoutCenter(layoutBounds)
            },
        )
    }
    LaunchedEffect(center, zoom) {
        onMapTransformChanged(Transform(center, zoom))
    }

    val springSpec = remember { spring<Offset>(stiffness = Spring.StiffnessLow) }
    val snapSpec = remember { snap<Offset>() }
    var translationAnimationSpec by remember { mutableStateOf<AnimationSpec<Offset>>(springSpec) }
    val animatedCenter by animateOffsetAsState(Offset(center.x, center.y), translationAnimationSpec)
    if (translationAnimationSpec != springSpec && animatedCenter == center) {
        translationAnimationSpec = springSpec
    }
    val dragMatcher =
        remember { PointerMatcher.mouse(PointerButton.Primary) + PointerMatcher.mouse(PointerButton.Secondary) }

    var mapScale by remember { mutableStateOf(0.0f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val hostileEntitiesSystemColorStrategy = remember(state.mapState.intel) {
        HostileEntitiesSystemColorStrategy { system -> state.mapState.intel[system].orEmpty() }
    }
    val systemStatusColorStrategies = remember(state.mapState.systemStatus) {
        SystemStatusColorStrategies(
            jumps = JumpsSystemColorStrategy(state.mapState.systemStatus),
            kills = KillsSystemColorStrategy(state.mapState.systemStatus),
            npcKills = NpcKillsSystemColorStrategy(state.mapState.systemStatus),
            assets = AssetsSystemColorStrategy(state.mapState.systemStatus),
            assetSafety = AssetSafetySystemColorStrategy(state.mapState.systemStatus),
            incursions = IncursionsSystemColorStrategy(state.mapState.systemStatus),
            stations = StationsSystemColorStrategy(state.mapState.systemStatus),
            factionWarfare = FactionWarfareSystemColorStrategy(state.mapState.systemStatus),
            sovereignty = koin.get { parametersOf(state.mapState.systemStatus) },
            sovereigntyUpgrades = SovereigntyUpgradesSystemColorStrategy(state.mapState.systemStatus),
            sovereigntyStrategicIndex = SovereigntyIndicesSystemColorStrategy(state.mapState.systemStatus, SovereigntyIndex.Strategic),
            sovereigntyMilitaryIndex = SovereigntyIndicesSystemColorStrategy(state.mapState.systemStatus, SovereigntyIndex.Military),
            sovereigntyIndustrialIndex = SovereigntyIndicesSystemColorStrategy(state.mapState.systemStatus, SovereigntyIndex.Industrial),
            sovereigntyActivityDefenseMultiplier = SovereigntyIndicesSystemColorStrategy(state.mapState.systemStatus, SovereigntyIndex.ActivityDefenseMultiplier),
            raidableSkyhooks = RaidableSkyhooksColorStrategy(state.mapState.systemStatus),
            storms = MetaliminalStormsSystemColorStrategy(state.mapState.systemStatus),
            wormholes = WormholesSystemColorStrategy(state.mapState.systemStatus),
            ansiblexZones = AnsiblexZonesSystemColorStrategy(state.mapState.systemStatus),
            jumpRange = JumpRangeSystemColorStrategy(state.mapState.systemStatus),
            colonies = ColoniesSystemColorStrategy(state.mapState.systemStatus),
            clones = ClonesSystemColorStrategy(state.mapState.systemStatus),
            standings = koin.get { parametersOf(state.mapState.systemStatus) },
            rats = RatsTypeSystemColorStrategy(state.mapState.systemStatus),
            industryCopying = IndustryIndicesSystemColorStrategy(state.mapState.systemStatus, IndustryActivity.Copying),
            industryInvention = IndustryIndicesSystemColorStrategy(state.mapState.systemStatus, IndustryActivity.Invention),
            industryManufacturing = IndustryIndicesSystemColorStrategy(state.mapState.systemStatus, IndustryActivity.Manufacturing),
            industryReaction = IndustryIndicesSystemColorStrategy(state.mapState.systemStatus, IndustryActivity.Reaction),
            industryMaterialEfficiency = IndustryIndicesSystemColorStrategy(state.mapState.systemStatus, IndustryActivity.ResearchingMaterialEfficiency),
            industryTimeEfficiency = IndustryIndicesSystemColorStrategy(state.mapState.systemStatus, IndustryActivity.ResearchingTimeEfficiency),
        )
    }

    val mapPainter: MapPainter = remember(
        state.cluster,
        state.layout,
        state.jumpBridgeAdditionalSystems,
        state.mapType,
        state.settings.isJumpBridgeNetworkShown,
        state.settings.jumpBridgeNetworkOpacity,
        state.mapState.autopilotConnections,
    ) {
        if (state.mapType is ClusterRegionsMap) {
            RegionsMapPainter(state.cluster, state.layout)
        } else {
            SystemsMapPainter(
                cluster = state.cluster,
                layout = state.layout,
                jumpBridgeAdditionalSystems = state.jumpBridgeAdditionalSystems,
                mapType = state.mapType,
                isJumpBridgeNetworkShown = state.settings.isJumpBridgeNetworkShown,
                jumpBridgeNetworkOpacity = state.settings.jumpBridgeNetworkOpacity,
                autopilotConnections = state.mapState.autopilotConnections,
            )
        }
    }.apply { initializeComposed() }

    EventEffect(state.mapState.centeredSystem) { centeredSystem ->
        val selectedPosition = state.layout[centeredSystem]?.position ?: return@EventEffect
        center = Offset(selectedPosition.x.toFloat(), selectedPosition.y.toFloat())
    }

    val density = LocalDensity.current.density
    EventEffect(state.fitMapEvent) {
        center = getMapLayoutCenter(layoutBounds)
        zoom = (zoom * getIdealZoomMultiplier(state.mapType, layoutBounds, canvasSize, mapScale, density)).coerceIn(zoomRange)
    }

    val transition = rememberInfiniteTransition()
    val animationPercentage = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10_000, easing = LinearEasing)),
    )

    Crossfade(targetState = state.systemInfoTypes) { systemInfoTypes ->
        val solarSystemColorStrategy = remember(
            state.mapType,
            systemInfoTypes.starApplied,
            hostileEntitiesSystemColorStrategy,
            systemStatusColorStrategies,
        ) {
            getSolarSystemColorStrategy(
                state.mapType,
                systemInfoTypes.starApplied,
                hostileEntitiesSystemColorStrategy,
                systemStatusColorStrategies,
            )!!
        }
        val cellColorStrategy = remember(
            state.mapType,
            systemInfoTypes.cellApplied,
            hostileEntitiesSystemColorStrategy,
            systemStatusColorStrategies,
        ) {
            getSolarSystemColorStrategy(
                state.mapType,
                systemInfoTypes.cellApplied,
                hostileEntitiesSystemColorStrategy,
                systemStatusColorStrategies,
            )
        }
        Box(modifier = Modifier.clipToBounds()) {
            val baseScale = when (state.mapType) {
                ClusterRegionsMap -> 0.7f
                is ClusterSystemsMap -> 2.0f
                is RegionMap -> 0.6f
                is DistanceMap -> 0.6f
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
            ) {
                val scale = baseScale / animatedZoom / density
                mapScale = scale
                canvasSize = size
                mapPainter.drawStatic(
                    scope = this,
                    center = DoubleOffset(animatedCenter.x.toDouble(), animatedCenter.y.toDouble()),
                    scale = scale,
                    systemColorStrategy = solarSystemColorStrategy,
                    cellColorStrategy = cellColorStrategy,
                    jumpBands = state.distanceMapState.distance + 1,
                )
            }
            Canvas(
                modifier = Modifier.fillMaxSize(),
            ) {
                val scale = baseScale / animatedZoom / density
                mapPainter.drawAnimated(
                    scope = this,
                    center = DoubleOffset(animatedCenter.x.toDouble(), animatedCenter.y.toDouble()),
                    scale = scale,
                    zoom = animatedZoom,
                    animationPercentage = animationPercentage.value,
                    systemColorStrategy = solarSystemColorStrategy,
                )
            }
            val focusRequester = remember { FocusRequester() }

            var isKeyboardZooming by remember { mutableStateOf(0) }
            LaunchedEffect(isKeyboardZooming) {
                while (true) {
                    zoom = (zoom * 1.2.pow(isKeyboardZooming)).coerceIn(zoomRange)
                    delay(100.milliseconds)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onPointerEvent(PointerEventType.Scroll) { event ->
                        event.changes.forEach { change ->
                            var scrollDelta = change.scrollDelta.y
                            if (state.settings.isInvertZoom) scrollDelta = -scrollDelta
                            val zoomPercentDelta = 1.3.pow(scrollDelta.toDouble())
                            zoom = (zoom * zoomPercentDelta).coerceIn(zoomRange)
                        }
                    }
                    .onDrag(matcher = dragMatcher) {
                        if (translationAnimationSpec != snapSpec) {
                            translationAnimationSpec = snapSpec
                        }
                        center = Offset(
                            x = (center.x + (-it.x * mapScale)).coerceIn(layoutBounds.minX.toFloat()..layoutBounds.maxX.toFloat()),
                            y = (center.y + (-it.y * mapScale)).coerceIn(layoutBounds.minY.toFloat()..layoutBounds.maxY.toFloat()),
                        )
                        focusRequester.requestFocus()
                    }
                    .onPointerEvent(PointerEventType.Move) { event ->
                        event.changes.forEach { change ->
                            val centerOffset = Offset(
                                change.position.x - canvasSize.width / 2,
                                change.position.y - canvasSize.height / 2,
                            )
                            val layoutOffset = center.copy(
                                x = center.x + centerOffset.x * mapScale,
                                y = center.y + centerOffset.y * mapScale,
                            )
                            onMapHover(layoutOffset, mapScale)
                        }
                    }
                    .onPointerEvent(PointerEventType.Release) { event ->
                        val awtEvent = event.awtEventOrNull ?: return@onPointerEvent
                        if (awtEvent.button == RIGHT_BUTTON) onMapClick(awtEvent.button)
                    }
                    .focusRequester(focusRequester)
                    .focusable()
                    .onClick {
                        onMapClick(LEFT_BUTTON)
                        focusRequester.requestFocus()
                    }
                    .onKeyPress(Key.Spacebar) {
                        onFitMapClick()
                    }
                    .onKeyPress(Key.Enter) {
                        onFocusCurrentClick()
                    }
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown) {
                            isKeyboardZooming = when (it.key) {
                                Key.PageUp -> 1
                                Key.PageDown -> -1
                                else -> return@onKeyEvent false
                            }
                            return@onKeyEvent true
                        } else if (it.type == KeyEventType.KeyUp) {
                            isKeyboardZooming = 0
                        }
                        false
                    },
            )
            if (mapScale != 0.0f && canvasSize != Size.Zero) {
                when (state.mapType) {
                    ClusterRegionsMap -> {
                        RegionsLayer(
                            state,
                            animatedCenter,
                            mapScale,
                            canvasSize,
                            onRegionPointerEnter,
                            onRegionPointerExit,
                            onClick = { onMapClick(LEFT_BUTTON) },
                        )
                    }
                    is ClusterSystemsMap, is RegionMap, is DistanceMap -> {
                        val nodeSizes = NodeSizes(
                            margin = 12.dp,
                            marginPx = LocalDensity.current.run { 12.dp.toPx() },
                            radius = 8.dp,
                            radiusPx = LocalDensity.current.run { 8.dp.toPx() },
                        )
                        val systemsById = remember(state.cluster.systems) { state.cluster.systems.associateBy { it.id } }
                        val spatialIndex = remember(state.layout) { LayoutSpatialIndex(state.layout) }
                        val visibleLayoutEntries = remember(spatialIndex, animatedCenter, mapScale, canvasSize) {
                            spatialIndex.query(animatedCenter, mapScale, canvasSize)
                        }
                        val visibleSystems = remember(state, animatedCenter, mapScale, canvasSize, systemsById) {
                            getVisibleSystems(state, visibleLayoutEntries, animatedCenter, mapScale, canvasSize, systemsById, forceDrawWithIntel = true)
                        }
                        val visibleInfoSystems = remember(state, animatedCenter, mapScale, canvasSize, systemsById) {
                            getVisibleSystems(state, visibleLayoutEntries, animatedCenter, mapScale, canvasSize, systemsById, forceDrawWithIntel = false)
                        }
                        SolarSystemsLayer(state, solarSystemColorStrategy, visibleSystems, mapScale, nodeSizes, animationPercentage)
                        SystemInfoBoxesLayer(state, visibleInfoSystems, mapScale, canvasSize, nodeSizes, onRegionClick)
                        SystemContextMenu(state, animatedCenter, mapScale, canvasSize, onContextMenuDismiss)
                    }
                }
            }
        }
    }
}

/**
 * Returns a value by which the zoom level needs to be multiplied to fit the map layout perfectly on canvas
 */
private fun getIdealZoomMultiplier(
    mapType: MapType,
    layoutBounds: MapLayoutBounds,
    canvasSize: Size,
    mapScale: Float,
    density: Float,
): Float {
    val margin = when (mapType) {
        ClusterRegionsMap, is ClusterSystemsMap, is RegionMap -> 50
        DistanceMap -> 120
    } * density
    val idealMapScaleX = (layoutBounds.maxX - layoutBounds.minX) / (canvasSize.width - margin)
    val idealMapScaleY = (layoutBounds.maxY - layoutBounds.minY) / (canvasSize.height - margin)
    val idealMapScale = maxOf(idealMapScaleX, idealMapScaleY)
    return mapScale / idealMapScale
}

data class SystemStatusColorStrategies(
    val jumps: JumpsSystemColorStrategy,
    val kills: KillsSystemColorStrategy,
    val npcKills: NpcKillsSystemColorStrategy,
    val assets: AssetsSystemColorStrategy,
    val assetSafety: AssetSafetySystemColorStrategy,
    val incursions: IncursionsSystemColorStrategy,
    val stations: StationsSystemColorStrategy,
    val factionWarfare: FactionWarfareSystemColorStrategy,
    val sovereignty: SovereigntySystemColorStrategy,
    val sovereigntyUpgrades: SovereigntyUpgradesSystemColorStrategy,
    val sovereigntyStrategicIndex: SovereigntyIndicesSystemColorStrategy,
    val sovereigntyMilitaryIndex: SovereigntyIndicesSystemColorStrategy,
    val sovereigntyIndustrialIndex: SovereigntyIndicesSystemColorStrategy,
    val sovereigntyActivityDefenseMultiplier: SovereigntyIndicesSystemColorStrategy,
    val raidableSkyhooks: RaidableSkyhooksColorStrategy,
    val storms: MetaliminalStormsSystemColorStrategy,
    val wormholes: WormholesSystemColorStrategy,
    val ansiblexZones: AnsiblexZonesSystemColorStrategy,
    val jumpRange: JumpRangeSystemColorStrategy,
    val colonies: ColoniesSystemColorStrategy,
    val clones: ClonesSystemColorStrategy,
    val standings: StandingsSystemColorStrategy,
    val rats: RatsTypeSystemColorStrategy,
    val industryCopying: IndustryIndicesSystemColorStrategy,
    val industryInvention: IndustryIndicesSystemColorStrategy,
    val industryManufacturing: IndustryIndicesSystemColorStrategy,
    val industryReaction: IndustryIndicesSystemColorStrategy,
    val industryMaterialEfficiency: IndustryIndicesSystemColorStrategy,
    val industryTimeEfficiency: IndustryIndicesSystemColorStrategy,
)

fun getSolarSystemColorStrategy(
    mapType: MapType,
    color: Map<SettingsMapType, MapSystemInfoType?>,
    hostileEntitiesSystemColorStrategy: HostileEntitiesSystemColorStrategy,
    systemStatusColorStrategies: SystemStatusColorStrategies,
): SystemColorStrategy? {
    val color = when (mapType) {
        is ClusterSystemsMap -> color[SettingsMapType.NewEden]
        is ClusterRegionsMap -> color[SettingsMapType.NewEden]
        is RegionMap -> color[SettingsMapType.Region]
        is DistanceMap -> color[SettingsMapType.Distance]
    } ?: return null
    return when (color) {
        MapSystemInfoType.StarColor -> koin.get<StarColorSystemColorStrategy>()
        MapSystemInfoType.Security -> koin.get<SecuritySystemColorStrategy>()
        MapSystemInfoType.NullSecurity -> koin.get<NullSecuritySystemColorStrategy>()
        MapSystemInfoType.IntelHostiles -> hostileEntitiesSystemColorStrategy
        MapSystemInfoType.Jumps -> systemStatusColorStrategies.jumps
        MapSystemInfoType.Kills -> systemStatusColorStrategies.kills
        MapSystemInfoType.NpcKills -> systemStatusColorStrategies.npcKills
        MapSystemInfoType.Assets -> systemStatusColorStrategies.assets
        MapSystemInfoType.AssetSafety -> systemStatusColorStrategies.assetSafety
        MapSystemInfoType.Incursions -> systemStatusColorStrategies.incursions
        MapSystemInfoType.Stations -> systemStatusColorStrategies.stations
        MapSystemInfoType.FactionWarfare -> systemStatusColorStrategies.factionWarfare
        MapSystemInfoType.Sovereignty -> systemStatusColorStrategies.sovereignty
        MapSystemInfoType.SovereigntyUpgrades -> systemStatusColorStrategies.sovereigntyUpgrades
        MapSystemInfoType.SovereigntyStrategicIndex -> systemStatusColorStrategies.sovereigntyStrategicIndex
        MapSystemInfoType.SovereigntyMilitaryIndex -> systemStatusColorStrategies.sovereigntyMilitaryIndex
        MapSystemInfoType.SovereigntyIndustrialIndex -> systemStatusColorStrategies.sovereigntyIndustrialIndex
        MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> systemStatusColorStrategies.sovereigntyActivityDefenseMultiplier
        MapSystemInfoType.SovereigntyCapitalSystem -> throw IllegalArgumentException("Not used for coloring")
        MapSystemInfoType.RaidableSkyhooks -> systemStatusColorStrategies.raidableSkyhooks
        MapSystemInfoType.MetaliminalStorms -> systemStatusColorStrategies.storms
        MapSystemInfoType.AnsiblexZones -> systemStatusColorStrategies.ansiblexZones
        MapSystemInfoType.JumpRange -> systemStatusColorStrategies.jumpRange
        MapSystemInfoType.Planets -> throw IllegalArgumentException("Not used for coloring")
        MapSystemInfoType.JoveObservatories -> koin.get<JoveObservatorySystemColorStrategy>()
        MapSystemInfoType.Wormholes -> systemStatusColorStrategies.wormholes
        MapSystemInfoType.Colonies -> systemStatusColorStrategies.colonies
        MapSystemInfoType.Clones -> systemStatusColorStrategies.clones
        MapSystemInfoType.Standings -> systemStatusColorStrategies.standings
        MapSystemInfoType.RatsType -> systemStatusColorStrategies.rats
        MapSystemInfoType.AsteroidBelts -> koin.get<AsteroidBeltsSystemColorStrategy>()
        MapSystemInfoType.IceFields -> koin.get<IceFieldsSystemColorStrategy>()
        MapSystemInfoType.Region -> throw IllegalArgumentException("Not used for coloring")
        MapSystemInfoType.Constellation -> throw IllegalArgumentException("Not used for coloring")
        MapSystemInfoType.IndustryIndexCopying -> systemStatusColorStrategies.industryCopying
        MapSystemInfoType.IndustryIndexInvention -> systemStatusColorStrategies.industryInvention
        MapSystemInfoType.IndustryIndexManufacturing -> systemStatusColorStrategies.industryManufacturing
        MapSystemInfoType.IndustryIndexReaction -> systemStatusColorStrategies.industryReaction
        MapSystemInfoType.IndustryIndexMaterialEfficiency -> systemStatusColorStrategies.industryMaterialEfficiency
        MapSystemInfoType.IndustryIndexTimeEfficiency -> systemStatusColorStrategies.industryTimeEfficiency
    }
}

@Composable
private fun SolarSystemsLayer(
    state: UiState,
    systemColorStrategy: SystemColorStrategy,
    visibleSystems: List<VisibleSystem>,
    mapScale: Float,
    nodeSizes: NodeSizes,
    animationPercentage: State<Float>,
) {
    val systemRadialGradients = remember {
        mutableMapOf<Pair<Color, NodeSizes>, Brush>()
    }
    val scaledNodeSizes = remember {
        mutableMapOf<Pair<NodeSizes, Float>, NodeSizes>()
    }
    val hostileOrbitPainter = remember { HostileOrbitPainter() }
    val orbitSystems = mutableListOf<HostileOrbitSystem>()

    visibleSystems.forEach { visibleSystem ->
        val system = visibleSystem.system
        val isHighlightedOrHovered = visibleSystem.isHighlightedOrHovered
        val dpCoordinates = with(LocalDensity.current) { visibleSystem.coordinates.x.toDp() to visibleSystem.coordinates.y.toDp() }
        val nodeScale = visibleSystem.nodeScale
        key(system.id) {
            val hasIntelPopup = system.id in state.mapState.intelPopupSystems
            val zIndex = if (hasIntelPopup || isHighlightedOrHovered) {
                HIGHLIGHTED_SYSTEM_NODE_Z_INDEX
            } else {
                SYSTEM_NODE_Z_INDEX
            }

            val effectiveNodeSizes = if (isHighlightedOrHovered) {
                nodeSizes
            } else {
                val roundedNodeScale = (nodeScale * 100).roundToInt() / 100f // For more efficient caching
                scaledNodeSizes.getOrPut(nodeSizes to roundedNodeScale) {
                    NodeSizes(
                        margin = nodeSizes.margin * roundedNodeScale,
                        marginPx = nodeSizes.marginPx * roundedNodeScale,
                        radius = nodeSizes.radius * roundedNodeScale,
                        radiusPx = nodeSizes.radiusPx * roundedNodeScale,
                    )
                }
            }

            SolarSystemNode(
                system = system,
                mapType = state.mapType,
                mapScale = mapScale,
                onlineCharacters = state.mapState.onlineCharacterLocations[system.id].orEmpty(),
                systemColorStrategy = systemColorStrategy,
                systemRadialGradients = systemRadialGradients,
                nodeSizes = effectiveNodeSizes,
                modifier = Modifier
                    .offset(dpCoordinates.first, dpCoordinates.second)
                    .zIndex(zIndex),
            )

            state.mapState.intel[system.id]?.takeIf { it.isNotEmpty() }?.let { intel ->
                orbitSystems += HostileOrbitSystem(
                    center = Offset(
                        x = dpCoordinates.first.value * LocalDensity.current.density,
                        y = dpCoordinates.second.value * LocalDensity.current.density,
                    ),
                    nodeSizes = effectiveNodeSizes,
                    hostileCount = getTotalIntelEntityCount(intel),
                    entityIcons = hostileOrbitPainter.getEntityIcons(intel).takeIf { effectiveNodeSizes == nodeSizes }.orEmpty(),
                )
            }
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            // Keep orbiting hostile icons above the system nodes, while allowing
            // info boxes to cover them when they overlap.
            .zIndex(HOSTILE_ORBIT_Z_INDEX),
    ) {
        orbitSystems.forEach { orbitSystem ->
            hostileOrbitPainter.draw(
                scope = this,
                nodeSizes = orbitSystem.nodeSizes,
                hasIntel = true,
                hostileCount = orbitSystem.hostileCount,
                entityIcons = orbitSystem.entityIcons,
                animationPercentage = animationPercentage.value,
                center = orbitSystem.center,
            )
        }
    }
}

private data class HostileOrbitSystem(
    val center: Offset,
    val nodeSizes: NodeSizes,
    val hostileCount: Int,
    val entityIcons: List<HostileOrbitPainter.EntityIcon>,
)

@Composable
private fun SystemInfoBoxesLayer(
    state: UiState,
    visibleSystems: List<VisibleSystem>,
    mapScale: Float,
    canvasSize: Size,
    nodeSizes: NodeSizes,
    onRegionClick: (regionId: Int, systemId: Int) -> Unit,
) {
    val indicatorsInfoTypes = getIndicatorsInfoTypes(state)
    val infoBoxInfoTypes = getInfoBoxInfoTypes(state)
    visibleSystems.forEach { visibleSystem ->
        val system = visibleSystem.system
        val isHighlightedOrHovered = visibleSystem.isHighlightedOrHovered
        val dpCoordinates = with(LocalDensity.current) { visibleSystem.coordinates.x.toDp() to visibleSystem.coordinates.y.toDp() }
        key(system.id) {
            val hasIntelPopup = system.id in state.mapState.intelPopupSystems
            val hasPinnedMarker = visibleSystem.hasPinnedMarker
            val zIndex = if (hasIntelPopup || isHighlightedOrHovered) {
                HIGHLIGHTED_INFO_BOX_Z_INDEX
            } else {
                INFO_BOX_Z_INDEX
            }
            val isRegionNameForced = state.mapType is RegionMap && system.regionId !in state.mapType.regionIds

            val isZoomEnough = (state.settings.isAlwaysShowingSystems || mapScale <= (0.9f / LocalDensity.current.density))
            val isShowingSystemInfoBox = isHighlightedOrHovered || when (state.mapType) {
                ClusterRegionsMap -> false
                is ClusterSystemsMap -> true
                is DistanceMap -> isZoomEnough || hasPinnedMarker
                is RegionMap -> isZoomEnough || hasPinnedMarker
            }
            if (isShowingSystemInfoBox) {
                val maxHeight = with(LocalDensity.current) { canvasSize.height.toDp() } - (dpCoordinates.second + nodeSizes.radius) - Spacing.medium
                val intel = state.mapState.intel[system.id]
                val onlineCharacters = state.mapState.onlineCharacterLocations[system.id].orEmpty()
                val systemStatus = state.mapState.systemStatus[system.id]
                val modifier = Modifier
                    .offset(dpCoordinates.first, dpCoordinates.second)
                    .zIndex(zIndex)
                    .offset(x = nodeSizes.radiusWithMargin, y = nodeSizes.radius)
                    .heightIn(max = maxHeight.coerceAtLeast(50.dp))
                if (!isHighlightedOrHovered && !hasIntelPopup && intel == null && onlineCharacters.isEmpty() && systemStatus == null) {
                    CompactSystemInfoBox(
                        system = system,
                        isShowingSecurity = MapSystemInfoType.Security in indicatorsInfoTypes,
                        modifier = modifier,
                    )
                } else {
                    SystemInfoBox(
                        system = system,
                        isRegionNameForced = isRegionNameForced,
                        isHighlightedOrHovered = isHighlightedOrHovered,
                        intel = intel,
                        hasIntelPopup = hasIntelPopup,
                        onlineCharacters = onlineCharacters,
                        systemStatus = systemStatus,
                        infoTypes = infoBoxInfoTypes,
                        indicatorsInfoTypes = indicatorsInfoTypes,
                        onRegionClick = { onRegionClick(system.regionId, system.id) },
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

private fun getIndicatorsInfoTypes(state: UiState): List<MapSystemInfoType> {
    val settingsMapType = getSettingsMapType(state.mapType)
    return state.systemInfoTypes.indicators[settingsMapType].orEmpty()
}

private fun getInfoBoxInfoTypes(state: UiState): List<MapSystemInfoType> {
    val settingsMapType = getSettingsMapType(state.mapType)
    return state.systemInfoTypes.infoBox[settingsMapType].orEmpty()
}

private fun getSettingsMapType(mapType: MapType): SettingsMapType {
    return when (mapType) {
        ClusterRegionsMap -> SettingsMapType.NewEden
        is ClusterSystemsMap -> SettingsMapType.NewEden
        is RegionMap -> SettingsMapType.Region
        is DistanceMap -> SettingsMapType.Distance
    }
}

private data class VisibleSystem(
    val system: SolarSystemsRepository.MapSolarSystem,
    val coordinates: Offset,
    val isHighlightedOrHovered: Boolean,
    val hasPinnedMarker: Boolean,
    val nodeScale: Float,
)

private fun getVisibleSystems(
    state: UiState,
    layoutEntries: List<Map.Entry<Int, VoronoiLayout>>,
    animatedCenter: Offset,
    mapScale: Float,
    canvasSize: Size,
    systemsById: Map<Int, SolarSystemsRepository.MapSolarSystem>,
    forceDrawWithIntel: Boolean,
): List<VisibleSystem> {
    return buildList {
        layoutEntries.forEach { (systemId, layout) ->
            if (!state.settings.isJumpBridgeNetworkShown && systemId in state.jumpBridgeAdditionalSystems) return@forEach
            val hasPinnedMarker = state.mapState.systemStatus[systemId]?.markers?.any { it.isPinned } == true
            val isHighlightedOrHovered = systemId == state.mapState.hoveredSystem ||
                systemId == state.mapState.selectedSystem ||
                systemId in state.mapState.searchResults
            val nodeScale = getNodeScale(mapScale)

            val isDrawn = when (state.mapType) {
                ClusterRegionsMap -> false // N/A
                is ClusterSystemsMap -> {
                    val isZoomedInEnough = if (state.mapType.is2D) mapScale <= 0.8 else mapScale <= 0.5
                    isZoomedInEnough || isHighlightedOrHovered || hasPinnedMarker || forceDrawWithIntel && (state.mapState.intel[systemId] != null || systemId in state.mapState.onlineCharacterLocations)
                }
                is DistanceMap -> true // Always draw
                is RegionMap -> true // Always draw
            }

            if (isDrawn) {
                val coordinates = getCanvasCoordinates(layout.position.x, layout.position.y, animatedCenter, mapScale, canvasSize)
                if (!isOnCanvas(coordinates, canvasSize, 100)) return@forEach
                val system = systemsById[systemId] ?: return@forEach
                add(VisibleSystem(system, coordinates, isHighlightedOrHovered, hasPinnedMarker, nodeScale))
            }
        }
    }
}

private class LayoutSpatialIndex(layout: Map<Int, VoronoiLayout>) {
    private companion object { const val CellSize = 500 }
    private val buckets = layout.entries.groupBy { entry -> bucketKey(entry.value.position.x, entry.value.position.y) }

    fun query(center: Offset, scale: Float, canvasSize: Size): List<Map.Entry<Int, VoronoiLayout>> = buildList {
        val margin = 100f * scale
        val minX = (center.x - canvasSize.width * scale / 2 - margin).toInt()
        val maxX = (center.x + canvasSize.width * scale / 2 + margin).toInt()
        val minY = (center.y - canvasSize.height * scale / 2 - margin).toInt()
        val maxY = (center.y + canvasSize.height * scale / 2 + margin).toInt()
        for (x in Math.floorDiv(minX, CellSize)..Math.floorDiv(maxX, CellSize)) {
            for (y in Math.floorDiv(minY, CellSize)..Math.floorDiv(maxY, CellSize)) {
                buckets[bucketKey(x * CellSize, y * CellSize)]?.let(::addAll)
            }
        }
    }

    private fun bucketKey(x: Int, y: Int): Long =
        (Math.floorDiv(x, CellSize).toLong() shl 32) xor (Math.floorDiv(y, CellSize).toLong() and 0xffffffffL)
}

private fun getNodeScale(mapScale: Float): Float {
    val nodeScaleStart = 1f // Normal node scale
    val nodeScaleEnd = 0.25f // Smallest scale nodes will reach
    val mapScaleStart = 2f // Map scale at which nodes will start shrinking
    val mapScaleEnd = 6f // Map scale at which nodes will reach the smallest size
    val mapScaleRange = mapScaleEnd - mapScaleStart
    return lerp(nodeScaleStart, nodeScaleEnd, (mapScale - mapScaleStart) / mapScaleRange)
        .coerceIn(nodeScaleEnd..nodeScaleStart)
}

@Composable
private fun RegionsLayer(
    state: UiState,
    animatedCenter: Offset,
    mapScale: Float,
    canvasSize: Size,
    onPointerEnter: (Int) -> Unit,
    onPointerExit: (Int) -> Unit,
    onClick: () -> Unit,
) {
    state.layout.forEach { (regionId, layout) ->
        val isHighlightedOrHovered = regionId == state.mapState.hoveredSystem || regionId == state.mapState.selectedSystem
        val coordinates = getCanvasCoordinates(layout.position.x, layout.position.y, animatedCenter, mapScale, canvasSize)
        val dpCoordinates = with(LocalDensity.current) { coordinates.x.toDp() to coordinates.y.toDp() }
        key(regionId) {
            val region = state.cluster.regions.firstOrNull { it.id == regionId } ?: return@forEach
            RegionNode(
                region = region,
                mapScale = mapScale,
                onlineCharacters = state.mapState.onlineCharacterLocations.values.flatten(),
                isHighlightedOrHovered = isHighlightedOrHovered,
                onPointerEnter = { onPointerEnter(region.id) },
                onPointerExit = { onPointerExit(region.id) },
                onClick = onClick,
                modifier = Modifier
                    .offset(dpCoordinates.first, dpCoordinates.second),
            )
        }
    }
}

@Composable
private fun SystemContextMenu(
    state: UiState,
    animatedCenter: Offset,
    mapScale: Float,
    canvasSize: Size,
    onDismissRequest: () -> Unit,
) {
    state.mapState.contextMenuSystem?.let { systemId ->
        val position = state.layout[systemId]?.position ?: return
        val coordinates = getCanvasCoordinates(position.x, position.y, animatedCenter, mapScale, canvasSize)
        val interactionProvider: EntityInteractionProvider = remember { koin.get() }
        key(systemId) {
            RiftContextMenuPopup(
                items = interactionProvider.getLocation(systemId, mapType = state.mapType).contextMenuItems,
                offset = IntOffset(coordinates.x.toInt(), coordinates.y.toInt()),
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

private fun getMapChangeTransition(from: UiState, to: UiState): ContentTransform {
    val fromType = from.mapType
    val toType = to.mapType
    val duration = 500
    return when {
        fromType is ClusterRegionsMap && toType is RegionMap || fromType is ClusterSystemsMap && toType is RegionMap -> {
            // Zoom in
            (fadeIn(animationSpec = tween(duration)) + scaleIn(initialScale = 0.7f, animationSpec = tween(duration)))
                .togetherWith(fadeOut(animationSpec = tween(duration / 2)) + scaleOut(targetScale = 1.3f, animationSpec = tween(duration / 2)))
        }
        fromType is RegionMap && toType is ClusterRegionsMap || fromType is RegionMap && toType is ClusterSystemsMap -> {
            // Zoom out
            (fadeIn(animationSpec = tween(duration)) + scaleIn(initialScale = 1.3f, animationSpec = tween(duration)))
                .togetherWith(fadeOut(animationSpec = tween(duration / 2)) + scaleOut(targetScale = 0.7f, animationSpec = tween(duration / 2)))
        }
        else -> {
            // Crossfade
            (fadeIn(animationSpec = tween(duration, delayMillis = (duration * 0.4).roundToInt())) + scaleIn(initialScale = 0.92f, animationSpec = tween(duration, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween((duration * 0.4).roundToInt())))
        }
    }
}

data class MapLayoutBounds(
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
)

private fun getMapLayoutBounds(layout: Map<Int, VoronoiLayout>): MapLayoutBounds {
    return MapLayoutBounds(
        minX = layout.minOf { it.value.position.x },
        maxX = layout.maxOf { it.value.position.x },
        minY = layout.minOf { it.value.position.y },
        maxY = layout.maxOf { it.value.position.y },
    )
}

private fun getMapLayoutCenter(bounds: MapLayoutBounds): Offset {
    val centerX = (bounds.maxX + bounds.minX) / 2
    val centerY = (bounds.maxY + bounds.minY) / 2
    return Offset(centerX.toFloat(), centerY.toFloat())
}

private fun getCanvasCoordinates(x: Int, y: Int, center: Offset, scale: Float, canvasSize: Size): Offset {
    val canvasX = (x - center.x) / scale + canvasSize.center.x
    val canvasY = (y - center.y) / scale + canvasSize.center.y
    return Offset(canvasX, canvasY)
}

private fun isOnCanvas(offset: Offset, canvasSize: Size, margin: Int = 0): Boolean {
    return offset.x >= -margin && offset.y >= -margin && offset.x < canvasSize.width + margin && offset.y < canvasSize.height + margin
}

private const val LEFT_BUTTON = 1
private const val RIGHT_BUTTON = 3

private const val SYSTEM_NODE_Z_INDEX = 0f
private const val HIGHLIGHTED_SYSTEM_NODE_Z_INDEX = 1f
private const val HOSTILE_ORBIT_Z_INDEX = 2f
private const val INFO_BOX_Z_INDEX = 3f
private const val HIGHLIGHTED_INFO_BOX_Z_INDEX = 4f
