package dev.nohus.rift.map

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.RequirementIcon
import dev.nohus.rift.compose.RiftAutocompleteTextField
import dev.nohus.rift.compose.RiftDropdownWithLabel
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftPill
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.backicon
import dev.nohus.rift.generated.resources.expand_more_16px
import dev.nohus.rift.generated.resources.map_alternative_layouts_label
import dev.nohus.rift.generated.resources.map_ansiblex_capital_system_valid
import dev.nohus.rift.generated.resources.map_ansiblex_zones_title
import dev.nohus.rift.generated.resources.map_ansiblex_zones_capital_label
import dev.nohus.rift.generated.resources.map_distance_center_label
import dev.nohus.rift.generated.resources.map_distance_title
import dev.nohus.rift.generated.resources.map_enabled_count
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_activity_defense_multiplier_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_actual_color_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_ansiblex_zones_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_asset_safety_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_assets_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_asteroid_belts_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_clones_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_activity_defense_multiplier_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_ansiblex_zones_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_rats_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_faction_warfare_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_incursions_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_copying_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_invention_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_manufacturing_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_reactions_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_material_efficiency_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_time_efficiency_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_sovereignty_upgrades_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_jump_range_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_null_sec_status_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_jumps_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_npc_kills_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_assets_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_hostiles_count_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_kills_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_stations_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_asset_safety_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_metaliminal_storms_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_security_status_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_sovereignty_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_industrial_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_military_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_strategic_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_actual_color_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_standings_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_asteroid_belts_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_clones_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_pi_colonies_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_ice_fields_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_jove_observatories_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_raidable_skyhooks_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_wormholes_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_copying_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_faction_warfare_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_hostiles_count_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_ice_fields_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_incursions_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_industrial_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_invention_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_jove_observatories_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_jump_range_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_jumps_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_kills_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_manufacturing_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_material_efficiency_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_metaliminal_storms_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_military_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_none_description
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_npc_kills_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_null_sec_status_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_pi_colonies_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_raidable_skyhooks_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_rats_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_reactions_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_security_status_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_sovereignty_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_sovereignty_upgrades_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_standings_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_stations_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_strategic_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_time_efficiency_index_name
import dev.nohus.rift.generated.resources.map_star_info_type_color_name_wormholes_name
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_activity_defense_multiplier_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_capital_system_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_ansiblex_zones_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_asset_safety_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_capital_system_name
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_constellation_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_constellation_name
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_incursions_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_jump_range_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_metaliminal_storms_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_asteroid_belts_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_ice_fields_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_sovereignty_upgrades_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_jove_observatories_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_clones_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_pi_colonies_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_planets_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_raidable_skyhooks_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_wormholes_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_copying_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_invention_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_manufacturing_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_reactions_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_material_efficiency_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_time_efficiency_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_jumps_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_npc_kills_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_assets_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_kills_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_stations_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_planets_name
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_region_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_region_name
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_security_status_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_security_status_name
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_sovereignty_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_industrial_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_military_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_strategic_index_description
import dev.nohus.rift.generated.resources.map_star_info_type_indicator_name_standings_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_asteroid_belts_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_rats_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_faction_warfare_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_ice_fields_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_incursions_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_sovereignty_upgrades_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_jove_observatories_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_clones_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_jump_range_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_metaliminal_storms_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_pi_colonies_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_planets_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_raidable_skyhooks_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_sovereignty_description
import dev.nohus.rift.generated.resources.map_star_info_type_info_box_name_wormholes_description
import dev.nohus.rift.generated.resources.map_jump_count
import dev.nohus.rift.generated.resources.map_jump_range_source_label
import dev.nohus.rift.generated.resources.map_jump_range_title
import dev.nohus.rift.generated.resources.map_jump_range_distance_label
import dev.nohus.rift.generated.resources.map_jump_range_source_not_found
import dev.nohus.rift.generated.resources.map_jump_range_source_placeholder
import dev.nohus.rift.generated.resources.map_jump_range_source_valid_character
import dev.nohus.rift.generated.resources.map_settings_background_color_label
import dev.nohus.rift.generated.resources.map_settings_indicators_title
import dev.nohus.rift.generated.resources.map_settings_indicators_label
import dev.nohus.rift.generated.resources.map_settings_info_box_title
import dev.nohus.rift.generated.resources.map_settings_info_box_label
import dev.nohus.rift.generated.resources.map_settings_none
import dev.nohus.rift.generated.resources.map_settings_background_color_title
import dev.nohus.rift.generated.resources.map_settings_system_color_title
import dev.nohus.rift.generated.resources.map_settings_system_color_label
import dev.nohus.rift.generated.resources.map_planet_types_title
import dev.nohus.rift.generated.resources.map_sovereignty_upgrade_types_title
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.map.DistanceMapController.DistanceMapState
import dev.nohus.rift.map.MapAnsiblexZonesController.MapAnsiblexZonesState
import dev.nohus.rift.map.MapJumpRangeController.MapJumpRangeState
import dev.nohus.rift.map.MapLayoutRepository.Layout
import dev.nohus.rift.map.MapPlanetsController.MapPlanetsState
import dev.nohus.rift.map.MapViewModel.MapType
import dev.nohus.rift.map.MapViewModel.MapType.ClusterRegionsMap
import dev.nohus.rift.map.MapViewModel.MapType.ClusterSystemsMap
import dev.nohus.rift.map.MapViewModel.MapType.DistanceMap
import dev.nohus.rift.map.MapViewModel.MapType.RegionMap
import dev.nohus.rift.map.MapViewModel.SystemInfoTypes
import dev.nohus.rift.map.PanelState.AnsiblexZones
import dev.nohus.rift.map.PanelState.CellColor
import dev.nohus.rift.map.PanelState.Collapsed
import dev.nohus.rift.map.PanelState.DistanceMapCenter
import dev.nohus.rift.map.PanelState.Expanded
import dev.nohus.rift.map.PanelState.Indicators
import dev.nohus.rift.map.PanelState.InfoBox
import dev.nohus.rift.map.PanelState.JumpRange
import dev.nohus.rift.map.PanelState.Planets
import dev.nohus.rift.map.PanelState.SovereigntyUpgrades
import dev.nohus.rift.map.PanelState.StarColor
import dev.nohus.rift.repositories.PlanetTypes
import dev.nohus.rift.repositories.PlanetTypes.PlanetType
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.settings.persistence.MapSystemInfoType
import dev.nohus.rift.sovupgrades.MapSovereigntyUpgradesController.MapSovereigntyUpgradesState
import dev.nohus.rift.sovupgrades.SovereigntyUpgradesRepository
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import dev.nohus.rift.settings.persistence.MapType as SettingsMapType

enum class PanelState {
    Collapsed,
    Expanded,
    StarColor,
    CellColor,
    Indicators,
    InfoBox,
    AnsiblexZones,
    JumpRange,
    Planets,
    SovereigntyUpgrades,
    DistanceMapCenter,
}

private val editableInfoTypes = mapOf(
    MapSystemInfoType.AnsiblexZones to AnsiblexZones,
    MapSystemInfoType.JumpRange to JumpRange,
    MapSystemInfoType.Planets to Planets,
    MapSystemInfoType.SovereigntyUpgrades to SovereigntyUpgrades,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapSettingsPanel(
    hazeState: HazeState,
    mapType: MapType,
    systemInfoTypes: SystemInfoTypes,
    mapAnsiblexZonesState: MapAnsiblexZonesState,
    mapJumpRangeState: MapJumpRangeState,
    mapPlanetsState: MapPlanetsState,
    mapSovereigntyUpgradesState: MapSovereigntyUpgradesState,
    distanceMapState: DistanceMapState,
    alternativeLayouts: List<Layout>,
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
) {
    val settingsMapType = when (mapType) {
        ClusterRegionsMap -> null
        is ClusterSystemsMap -> SettingsMapType.NewEden
        is RegionMap -> SettingsMapType.Region
        is DistanceMap -> SettingsMapType.Distance
    } ?: return
    Column(
        modifier = Modifier.padding(1.dp),
    ) {
        var previousPanelState: PanelState by remember { mutableStateOf(Collapsed) }
        var panelState: PanelState by remember { mutableStateOf(Collapsed) }
        val player = rememberUiSoundPlayer()
        val backgroundColor = RiftTheme.colors.backgroundPrimaryDark
        ScrollbarColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            isScrollbarConditional = true,
            hasScrollbarBackground = false,
            modifier = Modifier
                .heightIn(max = 200.dp)
                .onPointerEvent(PointerEventType.Enter) {
                    if (panelState == Collapsed) panelState = Expanded
                }
                .onPointerEvent(PointerEventType.Exit) {
                    if (panelState == Expanded) panelState = Collapsed
                }
                .hazeEffect(hazeState) {
                    blurEffect {
                        this.backgroundColor = backgroundColor
                        blurRadius = 16.dp
                    }
                },
        ) {
            AnimatedContent(targetState = panelState) { state ->
                when (state) {
                    Collapsed -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.expand_more_16px),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(horizontal = Spacing.small)
                                    .size(16.dp),
                            )
                        }
                    }
                    Expanded -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier
                                .padding(Spacing.medium)
                                .fillMaxWidth(),
                        ) {
                            FlowRow(
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                maxItemsInEachRow = 2,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.map_settings_system_color_label),
                                        style = RiftTheme.typography.headerPrimary,
                                    )
                                    SystemColorPills(
                                        isExpanded = false,
                                        isCellColor = false,
                                        selected = systemInfoTypes.starSelected[settingsMapType],
                                        onPillClick = {
                                            panelState = StarColor
                                        },
                                        onPillEditClick = {
                                            previousPanelState = panelState
                                            panelState = editableInfoTypes[it]!!
                                        },
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.map_settings_background_color_label),
                                        style = RiftTheme.typography.headerPrimary,
                                    )
                                    SystemColorPills(
                                        isExpanded = false,
                                        isCellColor = true,
                                        selected = systemInfoTypes.cellSelected[settingsMapType],
                                        onPillClick = {
                                            panelState = CellColor
                                        },
                                        onPillEditClick = {
                                            previousPanelState = panelState
                                            panelState = editableInfoTypes[it]!!
                                        },
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.map_settings_indicators_label),
                                        style = RiftTheme.typography.headerPrimary,
                                    )
                                    val text = systemInfoTypes.indicators[settingsMapType].orEmpty().let {
                                        if (it.isEmpty()) stringResource(Res.string.map_settings_none) else Res.plurals.map_enabled_count.toUiText(it.size, it.size.toUiTextArgument()).resolve()
                                    }
                                    RiftPill(
                                        text = text,
                                        onClick = {
                                            player.play(UiSound.ButtonClick)
                                            panelState = Indicators
                                        },
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.map_settings_info_box_label),
                                        style = RiftTheme.typography.headerPrimary,
                                    )
                                    val text = systemInfoTypes.infoBox[settingsMapType].orEmpty().let {
                                        if (it.isEmpty()) stringResource(Res.string.map_settings_none) else Res.plurals.map_enabled_count.toUiText(it.size, it.size.toUiTextArgument()).resolve()
                                    }
                                    RiftPill(
                                        text = text,
                                        onClick = {
                                            player.play(UiSound.ButtonClick)
                                            panelState = InfoBox
                                        },
                                    )
                                }
                            }
                            if (mapType is RegionMap) {
                                AlternativeLayoutsPills(
                                    alternativeLayouts = alternativeLayouts,
                                    selectedLayoutId = mapType.layoutId,
                                    onLayoutSelected = onLayoutSelected,
                                )
                            } else if (mapType is DistanceMap) {
                                DistanceMapPills(
                                    state = distanceMapState,
                                    onDistanceMapCenterClick = {
                                        panelState = DistanceMapCenter
                                    },
                                    onDistanceMapRangeClick = {
                                        panelState = DistanceMapCenter
                                    },
                                )
                            }
                        }
                    }
                    StarColor -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier.padding(Spacing.medium),
                        ) {
                            SettingsPanelTitle(
                                title = stringResource(Res.string.map_settings_system_color_title),
                                onBack = { panelState = Expanded },
                            )
                            SystemColorPills(
                                isExpanded = true,
                                isCellColor = false,
                                selected = systemInfoTypes.starSelected[settingsMapType],
                                onPillClick = {
                                    onSystemColorChange(settingsMapType, it!!)
                                    panelState = Expanded
                                },
                                onPillEditClick = {
                                    previousPanelState = panelState
                                    panelState = editableInfoTypes[it]!!
                                },
                                onPillHover = { color, isHovered ->
                                    onSystemColorHover(settingsMapType, color!!, isHovered)
                                },
                            )
                        }
                    }
                    CellColor -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier.padding(Spacing.medium),
                        ) {
                            SettingsPanelTitle(
                                title = stringResource(Res.string.map_settings_background_color_title),
                                onBack = { panelState = Expanded },
                            )
                            SystemColorPills(
                                isExpanded = true,
                                isCellColor = true,
                                selected = systemInfoTypes.cellSelected[settingsMapType],
                                onPillClick = {
                                    onCellColorChange(settingsMapType, it)
                                    panelState = Expanded
                                },
                                onPillEditClick = {
                                    previousPanelState = panelState
                                    panelState = editableInfoTypes[it]!!
                                },
                                onPillHover = { color, isHovered ->
                                    onCellColorHover(settingsMapType, color, isHovered)
                                },
                            )
                        }
                    }
                    Indicators -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier.padding(Spacing.medium),
                        ) {
                            SettingsPanelTitle(
                                title = stringResource(Res.string.map_settings_indicators_title),
                                onBack = { panelState = Expanded },
                            )
                            SystemIndicatorsPills(
                                hidden = setOf(
                                    MapSystemInfoType.StarColor,
                                    MapSystemInfoType.NullSecurity,
                                    MapSystemInfoType.IntelHostiles,
                                    MapSystemInfoType.FactionWarfare,
                                    MapSystemInfoType.RatsType,
                                ),
                                getInfoTypeNames = { getMapStarInfoTypeIndicatorName(it) },
                                selected = systemInfoTypes.indicators[settingsMapType].orEmpty(),
                                onPillClick = { onIndicatorChange(settingsMapType, it) },
                                onPillEditClick = {
                                    previousPanelState = panelState
                                    panelState = editableInfoTypes[it]!!
                                },
                            )
                        }
                    }
                    InfoBox -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier.padding(Spacing.medium),
                        ) {
                            SettingsPanelTitle(
                                title = stringResource(Res.string.map_settings_info_box_title),
                                onBack = { panelState = Expanded },
                            )
                            SystemIndicatorsPills(
                                hidden = setOf(
                                    MapSystemInfoType.StarColor,
                                    MapSystemInfoType.NullSecurity,
                                    MapSystemInfoType.IntelHostiles,
                                ),
                                getInfoTypeNames = { getMapStarInfoTypeInfoBoxName(it) },
                                selected = systemInfoTypes.infoBox[settingsMapType].orEmpty(),
                                onPillClick = { onInfoBoxChange(settingsMapType, it) },
                                onPillEditClick = {
                                    previousPanelState = panelState
                                    panelState = editableInfoTypes[it]!!
                                },
                            )
                        }
                    }
                    JumpRange -> {
                        JumpRangePanel(
                            mapJumpRangeState = mapJumpRangeState,
                            onBack = { panelState = previousPanelState },
                            onJumpRangeTargetUpdate = onJumpRangeTargetUpdate,
                            onJumpRangeDistanceUpdate = onJumpRangeDistanceUpdate,
                        )
                    }
                    AnsiblexZones -> {
                        AnsiblexZonesPanel(
                            state = mapAnsiblexZonesState,
                            onBack = { panelState = previousPanelState },
                            onCapitalSystemUpdate = onAnsiblexCapitalSystemUpdate,
                        )
                    }
                    Planets -> {
                        PlanetsPanel(
                            mapPlanetsState = mapPlanetsState,
                            onBack = { panelState = previousPanelState },
                            onPlanetTypesUpdate = onPlanetTypesUpdate,
                        )
                    }
                    SovereigntyUpgrades -> {
                        SovereigntyUpgradesPanel(
                            mapSovereigntyUpgradesState = mapSovereigntyUpgradesState,
                            onBack = { panelState = previousPanelState },
                            onSovereigntyUpgradeTypesUpdate = onSovereigntyUpgradeTypesUpdate,
                        )
                    }
                    DistanceMapCenter -> {
                        DistanceMapPanel(
                            state = distanceMapState,
                            onBack = { panelState = Expanded },
                            onDistanceMapCenterUpdate = onDistanceMapCenterUpdate,
                            onDistanceMapRangeUpdate = onDistanceMapRangeUpdate,
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(1.dp).background(RiftTheme.colors.borderGrey),
        )
    }
}

@Composable
private fun AnsiblexZonesPanel(
    state: MapAnsiblexZonesState,
    onBack: () -> Unit,
    onCapitalSystemUpdate: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        SettingsPanelTitle(
            title = stringResource(Res.string.map_ansiblex_zones_title),
            onBack = onBack,
        )
        AnsiblexCapitalSystemField(
            state = state,
            label = stringResource(Res.string.map_ansiblex_zones_capital_label),
            onCapitalSystemUpdate = onCapitalSystemUpdate,
        )
    }
}

@Composable
private fun JumpRangePanel(
    mapJumpRangeState: MapJumpRangeState,
    onBack: () -> Unit,
    onJumpRangeTargetUpdate: (String) -> Unit,
    onJumpRangeDistanceUpdate: (Double) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        SettingsPanelTitle(
            title = stringResource(Res.string.map_jump_range_title),
            onBack = onBack,
        )
        JumpRangeSourceField(
            state = mapJumpRangeState,
            label = stringResource(Res.string.map_jump_range_source_label),
            onTargetUpdate = onJumpRangeTargetUpdate,
        )
        JumpRangeDistanceField(
            state = mapJumpRangeState,
            label = stringResource(Res.string.map_jump_range_distance_label),
            onRangeUpdate = onJumpRangeDistanceUpdate,
        )
    }
}

@Composable
private fun PlanetsPanel(
    mapPlanetsState: MapPlanetsState,
    onBack: () -> Unit,
    onPlanetTypesUpdate: (List<PlanetType>) -> Unit,
) {
    val player = rememberUiSoundPlayer()
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        SettingsPanelTitle(
            title = stringResource(Res.string.map_planet_types_title),
            onBack = onBack,
        )
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            PlanetTypes.types.forEach { type ->
                val isSelected = type in mapPlanetsState.selectedTypes
                RiftPill(
                    text = type.name,
                    icon = type.icon,
                    isIconColor = true,
                    isSelected = isSelected,
                    onClick = {
                        player.playSelect(isSelected)
                        val new = if (isSelected) {
                            mapPlanetsState.selectedTypes - type
                        } else {
                            mapPlanetsState.selectedTypes + type
                        }
                        onPlanetTypesUpdate(new)
                    },
                )
            }
        }
    }
}

@Composable
private fun SovereigntyUpgradesPanel(
    mapSovereigntyUpgradesState: MapSovereigntyUpgradesState,
    onBack: () -> Unit,
    onSovereigntyUpgradeTypesUpdate: (List<Type>) -> Unit,
) {
    val player = rememberUiSoundPlayer()
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        SettingsPanelTitle(
            title = stringResource(Res.string.map_sovereignty_upgrade_types_title),
            onBack = onBack,
        )
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            val sovereigntyUpgradesRepository: SovereigntyUpgradesRepository = remember { koin.get() }
            sovereigntyUpgradesRepository.groupedUpgradeTypes.forEach { group ->
                val isSelected = group.any { it in mapSovereigntyUpgradesState.selectedTypes }
                val name = group.first().name.replace(Regex("\\s+\\d+$"), "")
                RiftPill(
                    text = name,
                    icon = {
                        Row {
                            for (type in group) {
                                AsyncTypeIcon(
                                    type = type,
                                    modifier = Modifier
                                        .padding(end = Spacing.small)
                                        .size(32.dp),
                                )
                            }
                        }
                    },
                    isSelected = isSelected,
                    onClick = {
                        player.playSelect(isSelected)
                        val new = if (isSelected) {
                            mapSovereigntyUpgradesState.selectedTypes - group.toSet()
                        } else {
                            mapSovereigntyUpgradesState.selectedTypes + group
                        }
                        onSovereigntyUpgradeTypesUpdate(new)
                    },
                )
            }
        }
    }
}

@Composable
private fun DistanceMapPanel(
    state: DistanceMapState,
    onBack: () -> Unit,
    onDistanceMapCenterUpdate: (String) -> Unit,
    onDistanceMapRangeUpdate: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        SettingsPanelTitle(
            title = stringResource(Res.string.map_distance_title),
            onBack = onBack,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.heightIn(min = 36.dp),
        ) {
            val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
            val charactersRepository: LocalCharactersRepository = remember { koin.get() }
            var targetText by remember { mutableStateOf("") }
            var isEdited by remember { mutableStateOf(false) }

            val suggestions by derivedStateOf {
                val possibleCharacters = charactersRepository.characters.value
                    .mapNotNull { it.info?.name }
                val possibleSystems = solarSystemsRepository.getSystems()
                    .map { it.name }
                (possibleCharacters + possibleSystems)
                    .filter { it.lowercase().startsWith(targetText.lowercase()) }
                    .filter { it.lowercase() != targetText.lowercase() }
            }

            LaunchedEffect(state.followingCharacterId, state.followingCharacterName, state.centerSystemId, state.centerSystemName) {
                targetText = when {
                    state.followingCharacterId != null -> state.followingCharacterName ?: state.followingCharacterId.toString()
                    else -> state.centerSystemName ?: state.centerSystemId.toString()
                }
            }

            Text(
                text = stringResource(Res.string.map_distance_center_label),
                style = RiftTheme.typography.bodyPrimary,
                modifier = Modifier.padding(end = Spacing.small),
            )
            RiftAutocompleteTextField(
                text = targetText,
                suggestions = suggestions.take(5),
                placeholder = stringResource(Res.string.map_jump_range_source_placeholder),
                onTextChanged = {
                    targetText = it
                    isEdited = true
                    onDistanceMapCenterUpdate(it)
                },
                modifier = Modifier
                    .width(150.dp),
            )
            AnimatedVisibility(targetText.isNotBlank()) {
                RequirementIcon(
                    isFulfilled = state.isEditedCenterValid || !isEdited,
                    fulfilledTooltip = when {
                        state.followingCharacterId != null -> stringResource(Res.string.map_jump_range_source_valid_character)
                        else -> stringResource(Res.string.map_ansiblex_capital_system_valid)
                    },
                    notFulfilledTooltip = stringResource(Res.string.map_jump_range_source_not_found),
                )
            }
        }
        val ranges = (1..5).associateWith { range ->
            Res.plurals.map_jump_count.toUiText(range, range.toUiTextArgument()).resolve()
        }
        RiftDropdownWithLabel(
            label = stringResource(Res.string.map_jump_range_distance_label),
            items = ranges.keys.toList(),
            selectedItem = state.distance.takeIf { it in ranges } ?: ranges.keys.first(),
            onItemSelected = onDistanceMapRangeUpdate,
            getItemName = { ranges.getValue(it) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsPanelTitle(
    title: String,
    onBack: () -> Unit,
) {
    val player = rememberUiSoundPlayer()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.onClick {
            player.play(UiSound.ButtonClick)
            onBack()
        },
    ) {
        RiftImageButton(
            resource = Res.drawable.backicon,
            size = 20.dp,
            onClick = onBack,
        )
        Text(
            text = title,
            style = RiftTheme.typography.headerPrimary,
        )
    }
}

@Composable
private fun AlternativeLayoutsPills(
    alternativeLayouts: List<Layout>,
    selectedLayoutId: Int,
    onLayoutSelected: (Int) -> Unit,
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.map_alternative_layouts_label),
            style = RiftTheme.typography.headerPrimary,
        )
        alternativeLayouts.forEach { layout ->
            RiftPill(
                text = layout.name,
                isSelected = layout.layoutId == selectedLayoutId,
                onClick = {
                    onLayoutSelected(layout.layoutId)
                },
            )
        }
    }
}

@Composable
private fun DistanceMapPills(
    state: DistanceMapState,
    onDistanceMapCenterClick: () -> Unit,
    onDistanceMapRangeClick: () -> Unit,
) {
    val player = rememberUiSoundPlayer()
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.map_distance_center_label),
                style = RiftTheme.typography.headerPrimary,
            )
            if (state.followingCharacterId != null) {
                RiftPill(
                    text = state.followingCharacterName ?: state.followingCharacterId.toString(),
                    onClick = {
                        player.play(UiSound.ButtonClick)
                        onDistanceMapCenterClick()
                    },
                )
            } else {
                RiftPill(
                    text = state.centerSystemName ?: state.centerSystemId.toString(),
                    onClick = {
                        player.play(UiSound.ButtonClick)
                        onDistanceMapCenterClick()
                    },
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.map_jump_range_distance_label),
                style = RiftTheme.typography.headerPrimary,
            )
            RiftPill(
                text = Res.plurals.map_jump_count.toUiText(state.distance, state.distance.toUiTextArgument()).resolve(),
                onClick = {
                    player.play(UiSound.ButtonClick)
                    onDistanceMapRangeClick()
                },
            )
        }
    }
}

@Composable
private fun SystemColorPills(
    isExpanded: Boolean,
    isCellColor: Boolean,
    selected: MapSystemInfoType?,
    onPillClick: (MapSystemInfoType?) -> Unit,
    onPillEditClick: (MapSystemInfoType?) -> Unit,
    onPillHover: (MapSystemInfoType?, Boolean) -> Unit = { _, _ -> },
) {
    val player = rememberUiSoundPlayer()
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        val colorEntries = MapSystemInfoType.entries - setOf(
            MapSystemInfoType.Planets,
            MapSystemInfoType.Region,
            MapSystemInfoType.Constellation,
            MapSystemInfoType.SovereigntyCapitalSystem,
        )
        val pills = if (isCellColor) colorEntries + null else colorEntries
        pills.filter { isExpanded || selected == it }
            .forEach { type ->
                val [text, tooltip] = getMapStarInfoTypeColorName(type)
                RiftTooltipArea(
                    text = tooltip,
                ) {
                    val isSelected = isExpanded && selected == type
                    RiftPill(
                        text = text,
                        isSelected = isSelected,
                        onClick = {
                            player.playSelect(isSelected)
                            onPillClick(type)
                        },
                        onEditClick = editableInfoTypes[type]?.let { { onPillEditClick(type) } },
                        onHoverChange = {
                            onPillHover(type, it)
                        },
                    )
                }
            }
    }
}

@Composable
private fun SystemIndicatorsPills(
    hidden: Set<MapSystemInfoType>,
    getInfoTypeNames: @Composable (color: MapSystemInfoType?) -> Pair<String, String>,
    selected: List<MapSystemInfoType>,
    onPillClick: (MapSystemInfoType) -> Unit,
    onPillEditClick: (MapSystemInfoType) -> Unit,
) {
    val player = rememberUiSoundPlayer()
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier,
    ) {
        val pills = MapSystemInfoType.entries - hidden
        pills.forEach { type ->
            val [text, tooltip] = getInfoTypeNames(type)
            RiftTooltipArea(
                text = tooltip,
            ) {
                val isSelected = type in selected
                RiftPill(
                    text = text,
                    isSelected = type in selected,
                    onClick = {
                        player.playSelect(isSelected)
                        onPillClick(type)
                    },
                    onEditClick = editableInfoTypes[type]?.let { { onPillEditClick(type) } },
                )
            }
        }
    }
}

/**
 * System coloring
 */
@Composable
private fun getMapStarInfoTypeColorName(color: MapSystemInfoType?): Pair<String, String> {
    return when (color) {
        MapSystemInfoType.StarColor -> stringResource(Res.string.map_star_info_type_color_name_actual_color_name) to stringResource(Res.string.map_star_info_type_color_name_actual_color_description)
        MapSystemInfoType.Security -> stringResource(Res.string.map_star_info_type_color_name_security_status_name) to stringResource(Res.string.map_star_info_type_color_name_security_status_description)
        MapSystemInfoType.NullSecurity -> stringResource(Res.string.map_star_info_type_color_name_null_sec_status_name) to stringResource(Res.string.map_star_info_type_color_name_null_sec_status_description)
        MapSystemInfoType.IntelHostiles -> stringResource(Res.string.map_star_info_type_color_name_hostiles_count_name) to stringResource(Res.string.map_star_info_type_color_name_hostiles_count_description)
        MapSystemInfoType.Jumps -> stringResource(Res.string.map_star_info_type_color_name_jumps_name) to stringResource(Res.string.map_star_info_type_color_name_jumps_description)
        MapSystemInfoType.Kills -> stringResource(Res.string.map_star_info_type_color_name_kills_name) to stringResource(Res.string.map_star_info_type_color_name_kills_description)
        MapSystemInfoType.NpcKills -> stringResource(Res.string.map_star_info_type_color_name_npc_kills_name) to stringResource(Res.string.map_star_info_type_color_name_npc_kills_description)
        MapSystemInfoType.Assets -> stringResource(Res.string.map_star_info_type_color_name_assets_name) to stringResource(Res.string.map_star_info_type_color_name_assets_description)
        MapSystemInfoType.AssetSafety -> stringResource(Res.string.map_star_info_type_color_name_asset_safety_name) to stringResource(Res.string.map_star_info_type_color_name_asset_safety_description)
        MapSystemInfoType.Incursions -> stringResource(Res.string.map_star_info_type_color_name_incursions_name) to stringResource(Res.string.map_star_info_type_color_name_incursions_description)
        MapSystemInfoType.Stations -> stringResource(Res.string.map_star_info_type_color_name_stations_name) to stringResource(Res.string.map_star_info_type_color_name_stations_description)
        MapSystemInfoType.FactionWarfare -> stringResource(Res.string.map_star_info_type_color_name_faction_warfare_name) to stringResource(Res.string.map_star_info_type_color_name_faction_warfare_description)
        MapSystemInfoType.Sovereignty -> stringResource(Res.string.map_star_info_type_color_name_sovereignty_name) to stringResource(Res.string.map_star_info_type_color_name_sovereignty_description)
        MapSystemInfoType.SovereigntyUpgrades -> stringResource(Res.string.map_star_info_type_color_name_sovereignty_upgrades_name) to stringResource(Res.string.map_star_info_type_color_name_sovereignty_upgrades_description)
        MapSystemInfoType.SovereigntyStrategicIndex -> stringResource(Res.string.map_star_info_type_color_name_strategic_index_name) to stringResource(Res.string.map_star_info_type_color_name_strategic_index_description)
        MapSystemInfoType.SovereigntyMilitaryIndex -> stringResource(Res.string.map_star_info_type_color_name_military_index_name) to stringResource(Res.string.map_star_info_type_color_name_military_index_description)
        MapSystemInfoType.SovereigntyIndustrialIndex -> stringResource(Res.string.map_star_info_type_color_name_industrial_index_name) to stringResource(Res.string.map_star_info_type_color_name_industrial_index_description)
        MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> stringResource(Res.string.map_star_info_type_color_name_activity_defense_multiplier_name) to stringResource(Res.string.map_star_info_type_color_name_activity_defense_multiplier_description)
        MapSystemInfoType.SovereigntyCapitalSystem -> throw IllegalArgumentException("Not used for colors")
        MapSystemInfoType.RaidableSkyhooks -> stringResource(Res.string.map_star_info_type_color_name_raidable_skyhooks_name) to stringResource(Res.string.map_star_info_type_color_name_raidable_skyhooks_description)
        MapSystemInfoType.MetaliminalStorms -> stringResource(Res.string.map_star_info_type_color_name_metaliminal_storms_name) to stringResource(Res.string.map_star_info_type_color_name_metaliminal_storms_description)
        MapSystemInfoType.AnsiblexZones -> stringResource(Res.string.map_star_info_type_color_name_ansiblex_zones_name) to stringResource(Res.string.map_star_info_type_color_name_ansiblex_zones_description)
        MapSystemInfoType.JumpRange -> stringResource(Res.string.map_star_info_type_color_name_jump_range_name) to stringResource(Res.string.map_star_info_type_color_name_jump_range_description)
        MapSystemInfoType.Planets -> throw IllegalArgumentException("Not used for colors")
        MapSystemInfoType.JoveObservatories -> stringResource(Res.string.map_star_info_type_color_name_jove_observatories_name) to stringResource(Res.string.map_star_info_type_color_name_jove_observatories_description)
        MapSystemInfoType.Wormholes -> stringResource(Res.string.map_star_info_type_color_name_wormholes_name) to stringResource(Res.string.map_star_info_type_color_name_wormholes_description)
        MapSystemInfoType.Colonies -> stringResource(Res.string.map_star_info_type_color_name_pi_colonies_name) to stringResource(Res.string.map_star_info_type_color_name_pi_colonies_description)
        MapSystemInfoType.Clones -> stringResource(Res.string.map_star_info_type_color_name_clones_name) to stringResource(Res.string.map_star_info_type_color_name_clones_description)
        MapSystemInfoType.Standings -> stringResource(Res.string.map_star_info_type_color_name_standings_name) to stringResource(Res.string.map_star_info_type_color_name_standings_description)
        MapSystemInfoType.RatsType -> stringResource(Res.string.map_star_info_type_color_name_rats_name) to stringResource(Res.string.map_star_info_type_color_name_rats_description)
        MapSystemInfoType.AsteroidBelts -> stringResource(Res.string.map_star_info_type_color_name_asteroid_belts_name) to stringResource(Res.string.map_star_info_type_color_name_asteroid_belts_description)
        MapSystemInfoType.IceFields -> stringResource(Res.string.map_star_info_type_color_name_ice_fields_name) to stringResource(Res.string.map_star_info_type_color_name_ice_fields_description)
        MapSystemInfoType.Region -> throw IllegalArgumentException("Not used for colors")
        MapSystemInfoType.Constellation -> throw IllegalArgumentException("Not used for colors")
        MapSystemInfoType.IndustryIndexCopying -> stringResource(Res.string.map_star_info_type_color_name_copying_index_name) to stringResource(Res.string.map_star_info_type_color_name_copying_index_description)
        MapSystemInfoType.IndustryIndexInvention -> stringResource(Res.string.map_star_info_type_color_name_invention_index_name) to stringResource(Res.string.map_star_info_type_color_name_invention_index_description)
        MapSystemInfoType.IndustryIndexManufacturing -> stringResource(Res.string.map_star_info_type_color_name_manufacturing_index_name) to stringResource(Res.string.map_star_info_type_color_name_manufacturing_index_description)
        MapSystemInfoType.IndustryIndexReaction -> stringResource(Res.string.map_star_info_type_color_name_reactions_index_name) to stringResource(Res.string.map_star_info_type_color_name_reactions_index_description)
        MapSystemInfoType.IndustryIndexMaterialEfficiency -> stringResource(Res.string.map_star_info_type_color_name_material_efficiency_index_name) to stringResource(Res.string.map_star_info_type_color_name_material_efficiency_index_description)
        MapSystemInfoType.IndustryIndexTimeEfficiency -> stringResource(Res.string.map_star_info_type_color_name_time_efficiency_index_name) to stringResource(Res.string.map_star_info_type_color_name_time_efficiency_index_description)
        null -> stringResource(Res.string.map_settings_none) to stringResource(Res.string.map_star_info_type_color_name_none_description)
    }
}

/**
 * System indicators
 */
@Composable
private fun getMapStarInfoTypeIndicatorName(color: MapSystemInfoType?): Pair<String, String> {
    return when (color) {
        MapSystemInfoType.StarColor -> "" to ""
        MapSystemInfoType.Security -> stringResource(Res.string.map_star_info_type_indicator_name_security_status_name) to stringResource(Res.string.map_star_info_type_indicator_name_security_status_description)
        MapSystemInfoType.NullSecurity -> "" to ""
        MapSystemInfoType.IntelHostiles -> "" to ""
        MapSystemInfoType.Jumps -> stringResource(Res.string.map_star_info_type_color_name_jumps_name) to stringResource(Res.string.map_star_info_type_indicator_name_jumps_description)
        MapSystemInfoType.Kills -> stringResource(Res.string.map_star_info_type_color_name_kills_name) to stringResource(Res.string.map_star_info_type_indicator_name_kills_description)
        MapSystemInfoType.NpcKills -> stringResource(Res.string.map_star_info_type_color_name_npc_kills_name) to stringResource(Res.string.map_star_info_type_indicator_name_npc_kills_description)
        MapSystemInfoType.Assets -> stringResource(Res.string.map_star_info_type_color_name_assets_name) to stringResource(Res.string.map_star_info_type_indicator_name_assets_description)
        MapSystemInfoType.AssetSafety -> stringResource(Res.string.map_star_info_type_color_name_asset_safety_name) to stringResource(Res.string.map_star_info_type_indicator_name_asset_safety_description)
        MapSystemInfoType.Incursions -> stringResource(Res.string.map_star_info_type_color_name_incursions_name) to stringResource(Res.string.map_star_info_type_indicator_name_incursions_description)
        MapSystemInfoType.Stations -> stringResource(Res.string.map_star_info_type_color_name_stations_name) to stringResource(Res.string.map_star_info_type_indicator_name_stations_description)
        MapSystemInfoType.FactionWarfare -> "" to ""
        MapSystemInfoType.Sovereignty -> stringResource(Res.string.map_star_info_type_color_name_sovereignty_name) to stringResource(Res.string.map_star_info_type_indicator_name_sovereignty_description)
        MapSystemInfoType.SovereigntyUpgrades -> stringResource(Res.string.map_star_info_type_color_name_sovereignty_upgrades_name) to stringResource(Res.string.map_star_info_type_indicator_name_sovereignty_upgrades_description)
        MapSystemInfoType.SovereigntyStrategicIndex -> stringResource(Res.string.map_star_info_type_color_name_strategic_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_strategic_index_description)
        MapSystemInfoType.SovereigntyMilitaryIndex -> stringResource(Res.string.map_star_info_type_color_name_military_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_military_index_description)
        MapSystemInfoType.SovereigntyIndustrialIndex -> stringResource(Res.string.map_star_info_type_color_name_industrial_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_industrial_index_description)
        MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> stringResource(Res.string.map_star_info_type_color_name_activity_defense_multiplier_name) to stringResource(Res.string.map_star_info_type_indicator_name_activity_defense_multiplier_description)
        MapSystemInfoType.SovereigntyCapitalSystem -> stringResource(Res.string.map_star_info_type_indicator_name_capital_system_name) to stringResource(Res.string.map_star_info_type_indicator_name_capital_system_description)
        MapSystemInfoType.RaidableSkyhooks -> stringResource(Res.string.map_star_info_type_color_name_raidable_skyhooks_name) to stringResource(Res.string.map_star_info_type_indicator_name_raidable_skyhooks_description)
        MapSystemInfoType.MetaliminalStorms -> stringResource(Res.string.map_star_info_type_color_name_metaliminal_storms_name) to stringResource(Res.string.map_star_info_type_indicator_name_metaliminal_storms_description)
        MapSystemInfoType.AnsiblexZones -> stringResource(Res.string.map_star_info_type_color_name_ansiblex_zones_name) to stringResource(Res.string.map_star_info_type_indicator_name_ansiblex_zones_description)
        MapSystemInfoType.JumpRange -> stringResource(Res.string.map_star_info_type_color_name_jump_range_name) to stringResource(Res.string.map_star_info_type_indicator_name_jump_range_description)
        MapSystemInfoType.Planets -> stringResource(Res.string.map_star_info_type_indicator_name_planets_name) to stringResource(Res.string.map_star_info_type_indicator_name_planets_description)
        MapSystemInfoType.JoveObservatories -> stringResource(Res.string.map_star_info_type_color_name_jove_observatories_name) to stringResource(Res.string.map_star_info_type_indicator_name_jove_observatories_description)
        MapSystemInfoType.Wormholes -> stringResource(Res.string.map_star_info_type_color_name_wormholes_name) to stringResource(Res.string.map_star_info_type_indicator_name_wormholes_description)
        MapSystemInfoType.Colonies -> stringResource(Res.string.map_star_info_type_color_name_pi_colonies_name) to stringResource(Res.string.map_star_info_type_indicator_name_pi_colonies_description)
        MapSystemInfoType.Clones -> stringResource(Res.string.map_star_info_type_color_name_clones_name) to stringResource(Res.string.map_star_info_type_indicator_name_clones_description)
        MapSystemInfoType.Standings -> stringResource(Res.string.map_star_info_type_color_name_standings_name) to stringResource(Res.string.map_star_info_type_indicator_name_standings_description)
        MapSystemInfoType.RatsType -> "" to ""
        MapSystemInfoType.AsteroidBelts -> stringResource(Res.string.map_star_info_type_color_name_asteroid_belts_name) to stringResource(Res.string.map_star_info_type_indicator_name_asteroid_belts_description)
        MapSystemInfoType.IceFields -> stringResource(Res.string.map_star_info_type_color_name_ice_fields_name) to stringResource(Res.string.map_star_info_type_indicator_name_ice_fields_description)
        MapSystemInfoType.Region -> stringResource(Res.string.map_star_info_type_indicator_name_region_name) to stringResource(Res.string.map_star_info_type_indicator_name_region_description)
        MapSystemInfoType.Constellation -> stringResource(Res.string.map_star_info_type_indicator_name_constellation_name) to stringResource(Res.string.map_star_info_type_indicator_name_constellation_description)
        MapSystemInfoType.IndustryIndexCopying -> stringResource(Res.string.map_star_info_type_color_name_copying_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_copying_index_description)
        MapSystemInfoType.IndustryIndexInvention -> stringResource(Res.string.map_star_info_type_color_name_invention_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_invention_index_description)
        MapSystemInfoType.IndustryIndexManufacturing -> stringResource(Res.string.map_star_info_type_color_name_manufacturing_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_manufacturing_index_description)
        MapSystemInfoType.IndustryIndexReaction -> stringResource(Res.string.map_star_info_type_color_name_reactions_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_reactions_index_description)
        MapSystemInfoType.IndustryIndexMaterialEfficiency -> stringResource(Res.string.map_star_info_type_color_name_material_efficiency_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_material_efficiency_index_description)
        MapSystemInfoType.IndustryIndexTimeEfficiency -> stringResource(Res.string.map_star_info_type_color_name_time_efficiency_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_time_efficiency_index_description)
        null -> stringResource(Res.string.map_settings_none) to stringResource(Res.string.map_star_info_type_color_name_none_description)
    }
}

/**
 * System info box indicators
 */
@Composable
private fun getMapStarInfoTypeInfoBoxName(color: MapSystemInfoType?): Pair<String, String> {
    return when (color) {
        MapSystemInfoType.StarColor -> "" to ""
        MapSystemInfoType.Security -> stringResource(Res.string.map_star_info_type_indicator_name_security_status_name) to stringResource(Res.string.map_star_info_type_indicator_name_security_status_description)
        MapSystemInfoType.NullSecurity -> "" to ""
        MapSystemInfoType.IntelHostiles -> "" to ""
        MapSystemInfoType.Jumps -> stringResource(Res.string.map_star_info_type_color_name_jumps_name) to stringResource(Res.string.map_star_info_type_indicator_name_jumps_description)
        MapSystemInfoType.Kills -> stringResource(Res.string.map_star_info_type_color_name_kills_name) to stringResource(Res.string.map_star_info_type_indicator_name_kills_description)
        MapSystemInfoType.NpcKills -> stringResource(Res.string.map_star_info_type_color_name_npc_kills_name) to stringResource(Res.string.map_star_info_type_indicator_name_npc_kills_description)
        MapSystemInfoType.Assets -> stringResource(Res.string.map_star_info_type_color_name_assets_name) to stringResource(Res.string.map_star_info_type_indicator_name_assets_description)
        MapSystemInfoType.AssetSafety -> stringResource(Res.string.map_star_info_type_color_name_asset_safety_name) to stringResource(Res.string.map_star_info_type_indicator_name_asset_safety_description)
        MapSystemInfoType.Incursions -> stringResource(Res.string.map_star_info_type_color_name_incursions_name) to stringResource(Res.string.map_star_info_type_info_box_name_incursions_description)
        MapSystemInfoType.Stations -> stringResource(Res.string.map_star_info_type_color_name_stations_name) to stringResource(Res.string.map_star_info_type_indicator_name_stations_description)
        MapSystemInfoType.FactionWarfare -> stringResource(Res.string.map_star_info_type_color_name_faction_warfare_name) to stringResource(Res.string.map_star_info_type_info_box_name_faction_warfare_description)
        MapSystemInfoType.Sovereignty -> stringResource(Res.string.map_star_info_type_color_name_sovereignty_name) to stringResource(Res.string.map_star_info_type_info_box_name_sovereignty_description)
        MapSystemInfoType.SovereigntyUpgrades -> stringResource(Res.string.map_star_info_type_color_name_sovereignty_upgrades_name) to stringResource(Res.string.map_star_info_type_info_box_name_sovereignty_upgrades_description)
        MapSystemInfoType.SovereigntyStrategicIndex -> stringResource(Res.string.map_star_info_type_color_name_strategic_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_strategic_index_description)
        MapSystemInfoType.SovereigntyMilitaryIndex -> stringResource(Res.string.map_star_info_type_color_name_military_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_military_index_description)
        MapSystemInfoType.SovereigntyIndustrialIndex -> stringResource(Res.string.map_star_info_type_color_name_industrial_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_industrial_index_description)
        MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> stringResource(Res.string.map_star_info_type_color_name_activity_defense_multiplier_name) to stringResource(Res.string.map_star_info_type_indicator_name_activity_defense_multiplier_description)
        MapSystemInfoType.SovereigntyCapitalSystem -> stringResource(Res.string.map_star_info_type_indicator_name_capital_system_name) to stringResource(Res.string.map_star_info_type_indicator_name_capital_system_description)
        MapSystemInfoType.RaidableSkyhooks -> stringResource(Res.string.map_star_info_type_color_name_raidable_skyhooks_name) to stringResource(Res.string.map_star_info_type_info_box_name_raidable_skyhooks_description)
        MapSystemInfoType.MetaliminalStorms -> stringResource(Res.string.map_star_info_type_color_name_metaliminal_storms_name) to stringResource(Res.string.map_star_info_type_info_box_name_metaliminal_storms_description)
        MapSystemInfoType.AnsiblexZones -> stringResource(Res.string.map_star_info_type_color_name_ansiblex_zones_name) to stringResource(Res.string.map_star_info_type_indicator_name_ansiblex_zones_description)
        MapSystemInfoType.JumpRange -> stringResource(Res.string.map_star_info_type_color_name_jump_range_name) to stringResource(Res.string.map_star_info_type_info_box_name_jump_range_description)
        MapSystemInfoType.Planets -> stringResource(Res.string.map_star_info_type_indicator_name_planets_name) to stringResource(Res.string.map_star_info_type_info_box_name_planets_description)
        MapSystemInfoType.JoveObservatories -> stringResource(Res.string.map_star_info_type_color_name_jove_observatories_name) to stringResource(Res.string.map_star_info_type_info_box_name_jove_observatories_description)
        MapSystemInfoType.Wormholes -> stringResource(Res.string.map_star_info_type_color_name_wormholes_name) to stringResource(Res.string.map_star_info_type_info_box_name_wormholes_description)
        MapSystemInfoType.Colonies -> stringResource(Res.string.map_star_info_type_color_name_pi_colonies_name) to stringResource(Res.string.map_star_info_type_info_box_name_pi_colonies_description)
        MapSystemInfoType.Clones -> stringResource(Res.string.map_star_info_type_color_name_clones_name) to stringResource(Res.string.map_star_info_type_info_box_name_clones_description)
        MapSystemInfoType.Standings -> stringResource(Res.string.map_star_info_type_color_name_standings_name) to stringResource(Res.string.map_star_info_type_indicator_name_standings_description)
        MapSystemInfoType.RatsType -> stringResource(Res.string.map_star_info_type_color_name_rats_name) to stringResource(Res.string.map_star_info_type_info_box_name_rats_description)
        MapSystemInfoType.AsteroidBelts -> stringResource(Res.string.map_star_info_type_color_name_asteroid_belts_name) to stringResource(Res.string.map_star_info_type_info_box_name_asteroid_belts_description)
        MapSystemInfoType.IceFields -> stringResource(Res.string.map_star_info_type_color_name_ice_fields_name) to stringResource(Res.string.map_star_info_type_info_box_name_ice_fields_description)
        MapSystemInfoType.Region -> stringResource(Res.string.map_star_info_type_indicator_name_region_name) to stringResource(Res.string.map_star_info_type_indicator_name_region_description)
        MapSystemInfoType.Constellation -> stringResource(Res.string.map_star_info_type_indicator_name_constellation_name) to stringResource(Res.string.map_star_info_type_indicator_name_constellation_description)
        MapSystemInfoType.IndustryIndexCopying -> stringResource(Res.string.map_star_info_type_color_name_copying_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_copying_index_description)
        MapSystemInfoType.IndustryIndexInvention -> stringResource(Res.string.map_star_info_type_color_name_invention_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_invention_index_description)
        MapSystemInfoType.IndustryIndexManufacturing -> stringResource(Res.string.map_star_info_type_color_name_manufacturing_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_manufacturing_index_description)
        MapSystemInfoType.IndustryIndexReaction -> stringResource(Res.string.map_star_info_type_color_name_reactions_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_reactions_index_description)
        MapSystemInfoType.IndustryIndexMaterialEfficiency -> stringResource(Res.string.map_star_info_type_color_name_material_efficiency_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_material_efficiency_index_description)
        MapSystemInfoType.IndustryIndexTimeEfficiency -> stringResource(Res.string.map_star_info_type_color_name_time_efficiency_index_name) to stringResource(Res.string.map_star_info_type_indicator_name_time_efficiency_index_description)
        null -> stringResource(Res.string.map_settings_none) to stringResource(Res.string.map_star_info_type_color_name_none_description)
    }
}
