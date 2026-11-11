package dev.nohus.rift.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCharacterPortrait
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.ClickableCharacter
import dev.nohus.rift.compose.IntelTimer
import dev.nohus.rift.compose.LocalNow
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.SystemEntities
import dev.nohus.rift.compose.SystemEntityInfoRow
import dev.nohus.rift.compose.getNow
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitParallax
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.bonusshieldcapital16
import dev.nohus.rift.generated.resources.bonusshieldfill24
import dev.nohus.rift.generated.resources.defensebonussimple
import dev.nohus.rift.generated.resources.indicator_asset_safety_source
import dev.nohus.rift.generated.resources.indicator_asset_safety_target
import dev.nohus.rift.generated.resources.indicator_assets
import dev.nohus.rift.generated.resources.indicator_asteroid_belt
import dev.nohus.rift.generated.resources.indicator_clones
import dev.nohus.rift.generated.resources.indicator_colony
import dev.nohus.rift.generated.resources.indicator_ice_field
import dev.nohus.rift.generated.resources.indicator_incursion
import dev.nohus.rift.generated.resources.indicator_jove
import dev.nohus.rift.generated.resources.indicator_jump_drive
import dev.nohus.rift.generated.resources.indicator_jump_drive_no
import dev.nohus.rift.generated.resources.indicator_jump_gate
import dev.nohus.rift.generated.resources.indicator_jumps
import dev.nohus.rift.generated.resources.indicator_kills
import dev.nohus.rift.generated.resources.indicator_npc_kills
import dev.nohus.rift.generated.resources.indicator_pod
import dev.nohus.rift.generated.resources.indicator_stations
import dev.nohus.rift.generated.resources.indicator_storm
import dev.nohus.rift.generated.resources.indicator_wormhole
import dev.nohus.rift.generated.resources.industryindex_w
import dev.nohus.rift.generated.resources.map_system_info_activity_defense_multiplier_name
import dev.nohus.rift.generated.resources.map_system_info_ansiblex_zone_tooltip
import dev.nohus.rift.generated.resources.map_system_info_asset_safety_source_count
import dev.nohus.rift.generated.resources.map_system_info_asset_safety_source_tooltip
import dev.nohus.rift.generated.resources.map_system_info_asset_safety_target_tooltip
import dev.nohus.rift.generated.resources.map_system_info_assets_tooltip
import dev.nohus.rift.generated.resources.map_system_info_asteroid_belt_tooltip
import dev.nohus.rift.generated.resources.map_system_info_capital_name
import dev.nohus.rift.generated.resources.map_system_info_clone_count
import dev.nohus.rift.generated.resources.map_system_info_clone_total
import dev.nohus.rift.generated.resources.map_system_info_colonies_tooltip
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_occupier
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_owner
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_points
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_state_captured
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_state_contested
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_state_uncontested
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_state_vulnerable
import dev.nohus.rift.generated.resources.map_system_info_faction_warfare_status
import dev.nohus.rift.generated.resources.map_system_info_ice_field_tooltip
import dev.nohus.rift.generated.resources.map_system_info_incursion_state_established
import dev.nohus.rift.generated.resources.map_system_info_incursion_state_mobilizing
import dev.nohus.rift.generated.resources.map_system_info_incursion_state_withdrawing
import dev.nohus.rift.generated.resources.map_system_info_incursion_summary
import dev.nohus.rift.generated.resources.map_system_info_industry_copying_index
import dev.nohus.rift.generated.resources.map_system_info_industry_index_name
import dev.nohus.rift.generated.resources.map_system_info_industry_invention_index
import dev.nohus.rift.generated.resources.map_system_info_industry_manufacturing_index
import dev.nohus.rift.generated.resources.map_system_info_industry_material_efficiency_index
import dev.nohus.rift.generated.resources.map_system_info_industry_reactions_index
import dev.nohus.rift.generated.resources.map_system_info_industry_time_efficiency_index
import dev.nohus.rift.generated.resources.map_system_info_intel_cleared
import dev.nohus.rift.generated.resources.map_system_info_jove_observatory_tooltip
import dev.nohus.rift.generated.resources.map_system_info_jump_range_in_range_tooltip
import dev.nohus.rift.generated.resources.map_system_info_jump_range_out_of_range_tooltip
import dev.nohus.rift.generated.resources.map_system_info_jumps_tooltip
import dev.nohus.rift.generated.resources.map_system_info_military_index_name
import dev.nohus.rift.generated.resources.map_system_info_npc_kills_tooltip
import dev.nohus.rift.generated.resources.map_system_info_online_character_count
import dev.nohus.rift.generated.resources.map_system_info_planet_summary
import dev.nohus.rift.generated.resources.map_system_info_pod_kills_tooltip
import dev.nohus.rift.generated.resources.map_system_info_rats_summary
import dev.nohus.rift.generated.resources.map_system_info_ship_kills_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_bad_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_excellent_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_good_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_high_sec_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_low_sec_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_neutral_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_no_sovereignty_tooltip
import dev.nohus.rift.generated.resources.map_system_info_standing_terrible_tooltip
import dev.nohus.rift.generated.resources.map_system_info_stations_tooltip
import dev.nohus.rift.generated.resources.map_system_info_storm_strong_electric
import dev.nohus.rift.generated.resources.map_system_info_storm_strong_exotic
import dev.nohus.rift.generated.resources.map_system_info_storm_strong_gamma
import dev.nohus.rift.generated.resources.map_system_info_storm_strong_plasma
import dev.nohus.rift.generated.resources.map_system_info_storm_weak_electric
import dev.nohus.rift.generated.resources.map_system_info_storm_weak_exotic
import dev.nohus.rift.generated.resources.map_system_info_storm_weak_gamma
import dev.nohus.rift.generated.resources.map_system_info_storm_weak_plasma
import dev.nohus.rift.generated.resources.map_system_info_strategic_index_name
import dev.nohus.rift.generated.resources.map_system_info_unknown_name
import dev.nohus.rift.generated.resources.map_system_info_upgrade_count
import dev.nohus.rift.generated.resources.map_system_info_upgrade_count_compact
import dev.nohus.rift.generated.resources.map_system_info_upgrade_effect
import dev.nohus.rift.generated.resources.map_system_info_upgrade_effect_count
import dev.nohus.rift.generated.resources.map_system_info_wormhole_destination_suffix
import dev.nohus.rift.generated.resources.map_system_info_wormhole_origin_suffix
import dev.nohus.rift.generated.resources.map_system_info_wormhole_size_capital
import dev.nohus.rift.generated.resources.map_system_info_wormhole_size_large
import dev.nohus.rift.generated.resources.map_system_info_wormhole_size_medium
import dev.nohus.rift.generated.resources.map_system_info_wormhole_size_small
import dev.nohus.rift.generated.resources.map_system_info_wormhole_size_suffix
import dev.nohus.rift.generated.resources.map_system_info_wormhole_size_very_large
import dev.nohus.rift.generated.resources.map_system_info_wormhole_type_suffix
import dev.nohus.rift.generated.resources.map_system_info_wormhole_unknown_size
import dev.nohus.rift.generated.resources.militaryindex_w
import dev.nohus.rift.generated.resources.strategicindex_w
import dev.nohus.rift.intel.state.IntelStateController.Dated
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.location.GetOnlineCharactersLocationUseCase
import dev.nohus.rift.network.esi.models.Contested
import dev.nohus.rift.network.esi.models.IncursionState
import dev.nohus.rift.network.esi.models.IndustryActivity
import dev.nohus.rift.network.esi.models.SovereigntySystemClaim
import dev.nohus.rift.network.evescout.GetMetaliminalStormsUseCase.Storm
import dev.nohus.rift.network.evescout.GetMetaliminalStormsUseCase.StormStrength
import dev.nohus.rift.network.evescout.GetMetaliminalStormsUseCase.StormType
import dev.nohus.rift.network.evescout.GetPublicWormholesUseCase.WormholeSize
import dev.nohus.rift.repositories.MapStatusRepository.SolarSystemStatus
import dev.nohus.rift.repositories.NamesRepository
import dev.nohus.rift.repositories.RatsRepository.RatType
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.settings.persistence.MapSystemInfoType
import dev.nohus.rift.sovupgrades.MapSovereigntyUpgradesController.SovereigntyUpgrade
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.StandingsRepository
import dev.nohus.rift.standings.getSystemColor
import dev.nohus.rift.utils.roundSecurity
import dev.nohus.rift.utils.withColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import java.time.Duration
import java.time.Instant
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompactSystemInfoBox(
    system: MapSolarSystem,
    isShowingSecurity: Boolean,
    modifier: Modifier,
) {
    val text = buildAnnotatedString {
        append(system.name)
        if (isShowingSecurity) {
            append(" ")
            withStyle(SpanStyle(color = SecurityColors[system.security])) {
                append(system.security.roundSecurity().toString())
            }
        }
    }
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.8f))
            .border(1.dp, RiftTheme.colors.borderGrey)
            .padding(horizontal = 2.dp, vertical = 1.dp),
    ) {
        Text(
            text = text,
            style = RiftTheme.typography.detailPrimary.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SystemInfoBox(
    system: MapSolarSystem,
    isRegionNameForced: Boolean,
    isHighlightedOrHovered: Boolean,
    intel: List<Dated<SystemEntity>>?,
    hasIntelPopup: Boolean,
    onlineCharacters: List<GetOnlineCharactersLocationUseCase.OnlineCharacterLocation>,
    systemStatus: SolarSystemStatus?,
    infoTypes: List<MapSystemInfoType>,
    indicatorsInfoTypes: List<MapSystemInfoType>,
    onRegionClick: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        val isExpanded = hasIntelPopup || isHighlightedOrHovered
        val intelInPopup = if (isExpanded) intel else null
        val borderColor = if (isHighlightedOrHovered) RiftTheme.colors.borderGreyLight else RiftTheme.colors.borderGrey
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.8f))
                .border(1.dp, borderColor)
                .padding(horizontal = 2.dp, vertical = 1.dp),
        ) {
            ScrollbarColumn(
                isScrollbarConditional = true,
                isFillWidth = false,
                modifier = Modifier.width(IntrinsicSize.Max),
            ) {
                Column {
                    val intelGroups = if (intelInPopup != null) groupIntelByTime(intelInPopup) else null
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val isShowingSecurity = (isExpanded && MapSystemInfoType.Security in infoTypes) || (!isExpanded && MapSystemInfoType.Security in indicatorsInfoTypes)
                        val securityColor = SecurityColors[system.security]
                        val systemNameText = buildAnnotatedString {
                            append(system.name)
                            if (isShowingSecurity) {
                                append(" ")
                                withStyle(SpanStyle(color = securityColor)) {
                                    append(system.security.roundSecurity().toString())
                                }
                            }
                        }
                        val systemNameStyle = RiftTheme.typography.detailPrimary.copy(fontWeight = FontWeight.Bold)
                        val highlightedSystemNameStyle = RiftTheme.typography.detailHighlighted.copy(fontWeight = FontWeight.Bold)
                        val style = if (isHighlightedOrHovered) highlightedSystemNameStyle else systemNameStyle
                        Text(
                            text = systemNameText,
                            style = style,
                        )

                        val isShowingStandings = (isExpanded && MapSystemInfoType.Standings in infoTypes) || (!isExpanded && MapSystemInfoType.Standings in indicatorsInfoTypes)
                        if (isShowingStandings) {
                            StandingsIndicator(system.security, systemStatus)
                        }
                    }

                    if (isRegionNameForced || isExpanded && MapSystemInfoType.Region in infoTypes || !isExpanded && MapSystemInfoType.Region in indicatorsInfoTypes) {
                        systemStatus?.regionName?.let { name ->
                            Text(
                                text = name,
                                style = RiftTheme.typography.detailSecondary,
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                                    .onClick { onRegionClick() },
                            )
                        }
                    }

                    if (isExpanded && MapSystemInfoType.Constellation in infoTypes || !isExpanded && MapSystemInfoType.Constellation in indicatorsInfoTypes) {
                        systemStatus?.constellationName?.let { name ->
                            Text(
                                text = name,
                                style = RiftTheme.typography.detailSecondary,
                            )
                        }
                    }

                    if (isExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                            modifier = Modifier.modifyIf(intelGroups != null) { padding(bottom = 1.dp) },
                        ) {
                            SystemInfoTypes(system, infoTypes, systemStatus)
                            onlineCharacters.singleOrNull()?.let { onlineCharacterLocation ->
                                ClickableCharacter(onlineCharacterLocation.id) {
                                    SystemEntityInfoRow(32.dp, hasBorder = false) {
                                        DynamicCharacterPortraitParallax(
                                            characterId = onlineCharacterLocation.id,
                                            size = 32.dp,
                                            enterTimestamp = null,
                                            pointerInteractionStateHolder = null,
                                        )
                                        Text(
                                            text = onlineCharacterLocation.name,
                                            style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.successGreen),
                                            modifier = Modifier.padding(4.dp),
                                        )
                                    }
                                }
                            }
                            if (onlineCharacters.size > 1) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    onlineCharacters.forEach { onlineCharacterLocation ->
                                        RiftTooltipArea(
                                            text = onlineCharacterLocation.name,
                                        ) {
                                            ClickableCharacter(onlineCharacterLocation.id) {
                                                DynamicCharacterPortraitParallax(
                                                    characterId = onlineCharacterLocation.id,
                                                    size = 32.dp,
                                                    enterTimestamp = null,
                                                    pointerInteractionStateHolder = null,
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = pluralStringResource(Res.plurals.map_system_info_online_character_count, onlineCharacters.size, onlineCharacters.size),
                                        style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.successGreen),
                                        modifier = Modifier.padding(end = 4.dp),
                                    )
                                }
                            }
                        }
                    }

                    if (intelGroups != null) {
                        Divider(
                            color = RiftTheme.colors.divider,
                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                        )
                        SystemIntelContents(intelGroups, system)
                    }
                }
            }
        }
        Column(
            modifier = Modifier.padding(start = 2.dp, top = Spacing.verySmall),
        ) {
            val shownIndicatorsInfoTypes = if (isExpanded) indicatorsInfoTypes - infoTypes.toSet() else indicatorsInfoTypes
            SystemInfoTypesIndicators(system, shownIndicatorsInfoTypes, systemStatus, isExpanded)
        }
    }
}

/**
 * These show when the system info box is expanded
 */
@Suppress("UnusedReceiverParameter")
@Composable
private fun ColumnScope.SystemInfoTypes(
    system: MapSolarSystem,
    infoTypes: List<MapSystemInfoType>,
    systemStatus: SolarSystemStatus?,
) {
    val namesRepository: NamesRepository = remember { koin.get() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        SovereigntyDevelopmentIndices(systemStatus, infoTypes)
        infoTypes.distinct()
            .filterNot { it in sovereigntyDevelopmentInfoTypes }
            .forEach { color ->
                when (color) {
                    MapSystemInfoType.StarColor -> {}
                    MapSystemInfoType.Security -> {}
                    MapSystemInfoType.NullSecurity -> {}
                    MapSystemInfoType.IntelHostiles -> {}
                    MapSystemInfoType.Jumps -> {
                        val jumps = systemStatus?.shipJumps?.takeIf { it > 0 }?.toString()
                        InfoTypeIndicator(jumps, Res.drawable.indicator_jumps, stringResource(Res.string.map_system_info_jumps_tooltip, jumps.toString()))
                    }
                    MapSystemInfoType.Kills -> {
                        val podKills = systemStatus?.podKills?.takeIf { it > 0 }?.toString()
                        InfoTypeIndicator(podKills, Res.drawable.indicator_pod, stringResource(Res.string.map_system_info_pod_kills_tooltip, podKills.toString()))
                        val shipKills = systemStatus?.shipKills?.takeIf { it > 0 }?.toString()
                        InfoTypeIndicator(shipKills, Res.drawable.indicator_kills, stringResource(Res.string.map_system_info_ship_kills_tooltip, shipKills.toString()))
                    }
                    MapSystemInfoType.NpcKills -> {
                        val npcKills = systemStatus?.npcKills?.takeIf { it > 0 }?.toString()
                        InfoTypeIndicator(npcKills, Res.drawable.indicator_npc_kills, stringResource(Res.string.map_system_info_npc_kills_tooltip, npcKills.toString()))
                    }
                    MapSystemInfoType.Assets -> {
                        val assets = systemStatus?.assetCount?.takeIf { it > 0 }?.toString()
                        InfoTypeIndicator(assets, Res.drawable.indicator_assets, stringResource(Res.string.map_system_info_assets_tooltip, assets.toString()))
                    }
                    MapSystemInfoType.AssetSafety -> {} // In column
                    MapSystemInfoType.Incursions -> {} // In column
                    MapSystemInfoType.Stations -> {
                        val stations = systemStatus?.stations?.size?.takeIf { it > 0 }?.toString()
                        InfoTypeIndicator(stations, Res.drawable.indicator_stations, stringResource(Res.string.map_system_info_stations_tooltip, stations.toString()))
                    }
                    MapSystemInfoType.FactionWarfare -> {} // In column
                    MapSystemInfoType.Sovereignty -> {} // In column
                    MapSystemInfoType.SovereigntyUpgrades -> {} // In column
                    MapSystemInfoType.RaidableSkyhooks -> {} // In column
                    MapSystemInfoType.MetaliminalStorms -> {} // In column
                    MapSystemInfoType.AnsiblexZones -> {
                        systemStatus?.ansiblexZone?.let { zone ->
                            InfoTypeIndicator(
                                text = zone.displayText.resolve(),
                                icon = Res.drawable.indicator_jump_gate,
                                tooltip = stringResource(Res.string.map_system_info_ansiblex_zone_tooltip),
                            )
                        }
                    }
                    MapSystemInfoType.Planets -> {} // In column
                    MapSystemInfoType.JoveObservatories -> {
                        InfoTypeIndicator("".takeIf { system.hasJoveObservatory }, Res.drawable.indicator_jove, stringResource(Res.string.map_system_info_jove_observatory_tooltip))
                    }
                    MapSystemInfoType.Wormholes -> {} // In column
                    MapSystemInfoType.JumpRange -> {
                        systemStatus?.distance?.let {
                            val lightYears = String.format("%.1fly", it.distanceLy)
                            if (it.isInJumpRange) {
                                InfoTypeIndicator(lightYears, Res.drawable.indicator_jump_drive, stringResource(Res.string.map_system_info_jump_range_in_range_tooltip))
                            } else {
                                InfoTypeIndicator(lightYears, Res.drawable.indicator_jump_drive_no, stringResource(Res.string.map_system_info_jump_range_out_of_range_tooltip))
                            }
                        }
                    }
                    MapSystemInfoType.Colonies -> {
                        val colonies = systemStatus?.colonies?.takeIf { it > 0 }
                        InfoTypeIndicator("$colonies".takeIf { colonies != null }, Res.drawable.indicator_colony, stringResource(Res.string.map_system_info_colonies_tooltip, colonies.toString()))
                    }
                    MapSystemInfoType.Clones -> {} // In column
                    MapSystemInfoType.Standings -> {} // In system name row
                    MapSystemInfoType.RatsType -> {} // In column
                    MapSystemInfoType.AsteroidBelts -> {
                        InfoTypeIndicator(
                            text = system.asteroidBeltCount.takeIf { it > 0 }?.toString(),
                            icon = Res.drawable.indicator_asteroid_belt,
                            tooltip = pluralStringResource(Res.plurals.map_system_info_asteroid_belt_tooltip, system.asteroidBeltCount, system.asteroidBeltCount),
                        )
                    }
                    MapSystemInfoType.IceFields -> {
                        InfoTypeIndicator(
                            text = "".takeIf { system.iceFieldCount > 0 },
                            icon = Res.drawable.indicator_ice_field,
                            tooltip = stringResource(Res.string.map_system_info_ice_field_tooltip),
                        )
                    }
                    MapSystemInfoType.Region -> {} // With system name
                    MapSystemInfoType.Constellation -> {} // With system name
                    MapSystemInfoType.IndustryIndexCopying -> {} // In column
                    MapSystemInfoType.IndustryIndexInvention -> {} // In column
                    MapSystemInfoType.IndustryIndexManufacturing -> {} // In column
                    MapSystemInfoType.IndustryIndexReaction -> {} // In column
                    MapSystemInfoType.IndustryIndexMaterialEfficiency -> {} // In column
                    MapSystemInfoType.IndustryIndexTimeEfficiency -> {} // In column
                    MapSystemInfoType.SovereigntyStrategicIndex -> {} // Grouped above
                    MapSystemInfoType.SovereigntyMilitaryIndex -> {} // Grouped above
                    MapSystemInfoType.SovereigntyIndustrialIndex -> {} // Grouped above
                    MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> {} // Grouped above
                    MapSystemInfoType.SovereigntyCapitalSystem -> {} // Grouped above
                }
            }
    }
    infoTypes.distinct()
        .sortedBy { listOf(MapSystemInfoType.Sovereignty).indexOf(it) }
        .forEach { color ->
            when (color) {
                MapSystemInfoType.StarColor -> {}
                MapSystemInfoType.Security -> {}
                MapSystemInfoType.NullSecurity -> {}
                MapSystemInfoType.IntelHostiles -> {}
                MapSystemInfoType.Jumps -> {} // In icon row
                MapSystemInfoType.Kills -> {} // In icon row
                MapSystemInfoType.NpcKills -> {} // In icon row
                MapSystemInfoType.Assets -> {} // In icon row
                MapSystemInfoType.AssetSafety -> {
                    AssetSafetyIndicators(system, systemStatus, isShowingDetail = true)
                }
                MapSystemInfoType.Incursions -> {
                    systemStatus?.incursion?.let { incursion ->
                        Text(
                            text = stringResource(Res.string.map_system_info_incursion_summary, incursion.type, getIncursionStateText(incursion.state)),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                    }
                }
                MapSystemInfoType.Stations -> {} // In icon row
                MapSystemInfoType.FactionWarfare -> {
                    systemStatus?.factionWarfare?.let { factionWarfare ->
                        val owner = namesRepository.getName(factionWarfare.ownerFactionId) ?: stringResource(Res.string.map_system_info_unknown_name)
                        val occupier = namesRepository.getName(factionWarfare.occupierFactionId) ?: stringResource(Res.string.map_system_info_unknown_name)
                        val text = buildString {
                            appendLine(stringResource(Res.string.map_system_info_faction_warfare_status, getContestedStateText(factionWarfare.contested)))
                            appendLine(stringResource(Res.string.map_system_info_faction_warfare_owner, owner))
                            if (occupier != owner) {
                                appendLine(stringResource(Res.string.map_system_info_faction_warfare_occupier, occupier))
                            }
                            if (factionWarfare.victoryPoints != 0) {
                                appendLine(stringResource(Res.string.map_system_info_faction_warfare_points, factionWarfare.victoryPoints, factionWarfare.victoryPointsThreshold))
                            }
                        }.trim()
                        Text(
                            text = text,
                            style = RiftTheme.typography.bodyPrimary,
                        )
                    }
                }
                MapSystemInfoType.Sovereignty -> {
                    systemStatus?.sovereignty?.let {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val id = it.alliance?.allianceId ?: it.faction?.factionId
                            if (id != null) {
                                SovereigntyLogo(it)
                                val name = namesRepository.getName(id) ?: stringResource(Res.string.map_system_info_unknown_name)
                                Text(
                                    text = name,
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                            }
                        }
                    }
                }
                MapSystemInfoType.SovereigntyUpgrades -> {
                    systemStatus?.sovereigntyUpgrades?.takeIf { it.isNotEmpty() }?.let {
                        SovereigntyUpgradesIndicators(it, isExpanded = true)
                    }
                }
                MapSystemInfoType.SovereigntyStrategicIndex -> {} // In icon row
                MapSystemInfoType.SovereigntyMilitaryIndex -> {} // In icon row
                MapSystemInfoType.SovereigntyIndustrialIndex -> {} // In icon row
                MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> {} // In icon row
                MapSystemInfoType.SovereigntyCapitalSystem -> {} // In icon row
                MapSystemInfoType.RaidableSkyhooks -> {
                    RaidableSkyhooksInfo(systemStatus, isShowingDetail = true)
                }
                MapSystemInfoType.MetaliminalStorms -> {
                    systemStatus?.storms?.let {
                        it.forEach { storm ->
                            Text(
                                text = getStormSummary(storm),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                        }
                    }
                }
                MapSystemInfoType.AnsiblexZones -> {} // In indicator row
                MapSystemInfoType.JumpRange -> {} // In icon row
                MapSystemInfoType.Planets -> {
                    systemStatus?.planets?.let {
                        FlowRow(
                            maxItemsInEachRow = 5,
                        ) {
                            it.sortedWith(compareBy({ it.type.typeId }, { it.name })).forEach { planet ->
                                RiftTooltipArea(
                                    text = stringResource(Res.string.map_system_info_planet_summary, planet.name, planet.type.name),
                                ) {
                                    Image(
                                        painter = painterResource(planet.type.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                MapSystemInfoType.JoveObservatories -> {} // In icon row
                MapSystemInfoType.Wormholes -> {
                    WormholesInfo(systemStatus, isFull = true)
                }
                MapSystemInfoType.Colonies -> {} // In icon row
                MapSystemInfoType.Clones -> {
                    val clones = systemStatus?.clones?.takeIf { it.isNotEmpty() }
                    if (clones != null) {
                        ClonesIndicators(clones, true)
                    }
                }
                MapSystemInfoType.Standings -> {} // In system name row
                MapSystemInfoType.RatsType -> {
                    val text = when (systemStatus?.ratType) {
                        RatType.BloodRaiders -> "Blood Raiders"
                        RatType.Guristas -> "Guristas"
                        RatType.SanshasNation -> "Sansha's Nation"
                        RatType.Serpentis -> "Serpentis"
                        RatType.AngelCartel -> "Angel Cartel"
                        RatType.RogueDrones -> "Rogue Drones"
                        RatType.TriglavianCollective -> "Triglavians"
                        null -> null
                    }
                    if (text != null) {
                        Text(
                            text = stringResource(Res.string.map_system_info_rats_summary, text),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                    }
                }
                MapSystemInfoType.AsteroidBelts -> {} // In icon row
                MapSystemInfoType.IceFields -> {} // In icon row
                MapSystemInfoType.Region -> {} // With system name
                MapSystemInfoType.Constellation -> {} // With system name
                MapSystemInfoType.IndustryIndexCopying -> IndustryActivityIndex(systemStatus, IndustryActivity.Copying, Res.string.map_system_info_industry_copying_index)
                MapSystemInfoType.IndustryIndexInvention -> IndustryActivityIndex(systemStatus, IndustryActivity.Invention, Res.string.map_system_info_industry_invention_index)
                MapSystemInfoType.IndustryIndexManufacturing -> IndustryActivityIndex(systemStatus, IndustryActivity.Manufacturing, Res.string.map_system_info_industry_manufacturing_index)
                MapSystemInfoType.IndustryIndexReaction -> IndustryActivityIndex(systemStatus, IndustryActivity.Reaction, Res.string.map_system_info_industry_reactions_index)
                MapSystemInfoType.IndustryIndexMaterialEfficiency -> IndustryActivityIndex(systemStatus, IndustryActivity.ResearchingMaterialEfficiency, Res.string.map_system_info_industry_material_efficiency_index)
                MapSystemInfoType.IndustryIndexTimeEfficiency -> IndustryActivityIndex(systemStatus, IndustryActivity.ResearchingTimeEfficiency, Res.string.map_system_info_industry_time_efficiency_index)
            }
        }
    systemStatus?.markers?.forEach { marker ->
        InfoTypeIndicator(marker.label, marker.icon, tint = marker.color)
    }
}

/**
 * These show when the system info box is collapsed
 */
@Composable
private fun SystemInfoTypesIndicators(
    system: MapSolarSystem,
    infoTypes: List<MapSystemInfoType>,
    systemStatus: SolarSystemStatus?,
    isExpanded: Boolean,
) {
    val namesRepository: NamesRepository = remember { koin.get() }
    SovereigntyDevelopmentIndices(systemStatus, infoTypes)
    infoTypes.distinct()
        .filterNot { it in sovereigntyDevelopmentInfoTypes }
        .sortedBy { listOf(MapSystemInfoType.Incursions, MapSystemInfoType.Sovereignty).indexOf(it) }
        .forEach { color ->
            when (color) {
                MapSystemInfoType.StarColor -> {}
                MapSystemInfoType.Security -> {}
                MapSystemInfoType.NullSecurity -> {}
                MapSystemInfoType.IntelHostiles -> {}
                MapSystemInfoType.Jumps -> {
                    val jumps = systemStatus?.shipJumps?.takeIf { it > 0 }?.toString()
                    InfoTypeIndicator(jumps, Res.drawable.indicator_jumps)
                }
                MapSystemInfoType.Kills -> {
                    val podKills = systemStatus?.podKills ?: 0
                    val shipKills = systemStatus?.shipKills ?: 0
                    val kills = (podKills + shipKills).takeIf { it > 0 }?.toString()
                    InfoTypeIndicator(kills, Res.drawable.indicator_kills)
                }

                MapSystemInfoType.NpcKills -> {
                    val npcKills = systemStatus?.npcKills?.takeIf { it > 0 }?.toString()
                    InfoTypeIndicator(npcKills, Res.drawable.indicator_npc_kills)
                }

                MapSystemInfoType.Assets -> {
                    val assets = systemStatus?.assetCount?.takeIf { it > 0 }?.toString()
                    InfoTypeIndicator(assets, Res.drawable.indicator_assets)
                }
                MapSystemInfoType.AssetSafety -> {
                    AssetSafetyIndicators(system, systemStatus, isShowingDetail = false)
                }
                MapSystemInfoType.Incursions -> {
                    systemStatus?.incursion?.let {
                        InfoTypeIndicator("", Res.drawable.indicator_incursion)
                    }
                }
                MapSystemInfoType.Stations -> {
                    val stations = systemStatus?.stations?.size?.takeIf { it > 0 }?.toString()
                    InfoTypeIndicator(stations, Res.drawable.indicator_stations)
                }
                MapSystemInfoType.FactionWarfare -> {}
                MapSystemInfoType.Sovereignty -> {
                    systemStatus?.sovereignty?.let {
                        val id = it.alliance?.allianceId ?: it.faction?.factionId
                        if (id != null) {
                            val name = namesRepository.getName(id) ?: stringResource(Res.string.map_system_info_unknown_name)
                            RiftTooltipArea(
                                text = name,
                            ) {
                                SovereigntyLogo(it)
                            }
                        }
                    }
                }
                MapSystemInfoType.SovereigntyUpgrades -> {
                    systemStatus?.sovereigntyUpgrades?.takeIf { it.isNotEmpty() }?.let {
                        SovereigntyUpgradesIndicators(it, isExpanded = false)
                    }
                }
                MapSystemInfoType.RaidableSkyhooks -> {
                    RaidableSkyhooksInfo(systemStatus, isShowingDetail = false)
                }
                MapSystemInfoType.MetaliminalStorms -> {
                    systemStatus?.storms?.takeIf { it.isNotEmpty() }?.let { storms ->
                        InfoTypeIndicator(
                            text = "",
                            icon = Res.drawable.indicator_storm,
                            tooltip = buildString {
                                storms.forEach { storm ->
                                    appendLine(getStormSummary(storm))
                                }
                            }.trim(),
                        )
                    }
                }
                MapSystemInfoType.AnsiblexZones -> {
                    systemStatus?.ansiblexZone?.let { zone ->
                        InfoTypeIndicator(
                            text = zone.displayText.resolve(),
                            icon = Res.drawable.indicator_jump_gate,
                            tooltip = stringResource(Res.string.map_system_info_ansiblex_zone_tooltip),
                        )
                    }
                }
                MapSystemInfoType.JumpRange ->
                    systemStatus?.distance?.let {
                        if (it.isInJumpRange) {
                            val lightYears = String.format("%.1fly", it.distanceLy)
                            InfoTypeIndicator(lightYears, Res.drawable.indicator_jump_drive)
                        }
                    }
                MapSystemInfoType.Planets -> {
                    systemStatus?.planets?.let {
                        FlowRow(
                            maxItemsInEachRow = 5,
                        ) {
                            it.sortedWith(compareBy({ it.type.typeId }, { it.name })).forEach { planet ->
                                RiftTooltipArea(
                                    text = stringResource(Res.string.map_system_info_planet_summary, planet.name, planet.type.name),
                                ) {
                                    Image(
                                        painter = painterResource(planet.type.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                MapSystemInfoType.JoveObservatories -> {
                    if (system.hasJoveObservatory) {
                        InfoTypeIndicator("", Res.drawable.indicator_jove)
                    }
                }
                MapSystemInfoType.Wormholes -> {
                    WormholesInfo(systemStatus, isFull = false)
                }
                MapSystemInfoType.Colonies -> {
                    val colonies = systemStatus?.colonies?.takeIf { it > 0 }
                    if (colonies != null) {
                        InfoTypeIndicator("$colonies", Res.drawable.indicator_colony)
                    }
                }
                MapSystemInfoType.Clones -> {
                    val clones = systemStatus?.clones?.takeIf { it.isNotEmpty() }
                    if (clones != null) {
                        ClonesIndicators(clones, false)
                    }
                }
                MapSystemInfoType.Standings -> {} // In system name row
                MapSystemInfoType.RatsType -> {}
                MapSystemInfoType.AsteroidBelts -> {
                    InfoTypeIndicator(
                        text = "".takeIf { system.asteroidBeltCount > 0 },
                        icon = Res.drawable.indicator_asteroid_belt,
                        tooltip = pluralStringResource(Res.plurals.map_system_info_asteroid_belt_tooltip, system.asteroidBeltCount, system.asteroidBeltCount),
                    )
                }
                MapSystemInfoType.IceFields -> {
                    InfoTypeIndicator(
                        text = "".takeIf { system.iceFieldCount > 0 },
                        icon = Res.drawable.indicator_ice_field,
                        tooltip = stringResource(Res.string.map_system_info_ice_field_tooltip),
                    )
                }
                MapSystemInfoType.Region -> {} // With system name
                MapSystemInfoType.Constellation -> {} // With system name
                MapSystemInfoType.IndustryIndexCopying -> IndustryActivityIndex(systemStatus, IndustryActivity.Copying, Res.string.map_system_info_industry_copying_index)
                MapSystemInfoType.IndustryIndexInvention -> IndustryActivityIndex(systemStatus, IndustryActivity.Invention, Res.string.map_system_info_industry_invention_index)
                MapSystemInfoType.IndustryIndexManufacturing -> IndustryActivityIndex(systemStatus, IndustryActivity.Manufacturing, Res.string.map_system_info_industry_manufacturing_index)
                MapSystemInfoType.IndustryIndexReaction -> IndustryActivityIndex(systemStatus, IndustryActivity.Reaction, Res.string.map_system_info_industry_reactions_index)
                MapSystemInfoType.IndustryIndexMaterialEfficiency -> IndustryActivityIndex(systemStatus, IndustryActivity.ResearchingMaterialEfficiency, Res.string.map_system_info_industry_material_efficiency_index)
                MapSystemInfoType.IndustryIndexTimeEfficiency -> IndustryActivityIndex(systemStatus, IndustryActivity.ResearchingTimeEfficiency, Res.string.map_system_info_industry_time_efficiency_index)
                MapSystemInfoType.SovereigntyStrategicIndex -> {} // Grouped above
                MapSystemInfoType.SovereigntyMilitaryIndex -> {} // Grouped above
                MapSystemInfoType.SovereigntyIndustrialIndex -> {} // Grouped above
                MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> {} // Grouped above
                MapSystemInfoType.SovereigntyCapitalSystem -> {} // Grouped above
            }
        }
    if (!isExpanded) {
        systemStatus?.markers?.forEach { marker ->
            InfoTypeIndicator(marker.label, marker.icon, tint = marker.color)
        }
    }
}

@Composable
fun RaidableSkyhooksInfo(systemStatus: SolarSystemStatus?, isShowingDetail: Boolean) {
    systemStatus?.raidableSkyhooks?.takeIf { it.isNotEmpty() }?.let {
        val now = getNow()
        Column {
            it.filter { it.vulnerableTo > now }.forEach { skyhook ->
                RaidableSkyhookIndicator(skyhook, now, isShowingDetail = isShowingDetail)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WormholesInfo(systemStatus: SolarSystemStatus?, isFull: Boolean) {
    val wormholes = systemStatus?.wormholes ?: emptyList()
    for (wormhole in wormholes.sortedBy { it.outRegionName == null }) {
        val (_, size) = when (wormhole.maxShipSize) {
            WormholeSize.Small -> "S" to stringResource(Res.string.map_system_info_wormhole_size_small)
            WormholeSize.Medium -> "M" to stringResource(Res.string.map_system_info_wormhole_size_medium)
            WormholeSize.Large -> "L" to stringResource(Res.string.map_system_info_wormhole_size_large)
            WormholeSize.XLarge -> "XL" to stringResource(Res.string.map_system_info_wormhole_size_very_large)
            WormholeSize.Capital -> "C" to stringResource(Res.string.map_system_info_wormhole_size_capital)
            null -> "?" to null
        }
        RiftTooltipArea(
            text = buildAnnotatedString {
                withColor(RiftTheme.colors.textHighlighted) {
                    append(wormhole.inType)
                }
                append(stringResource(Res.string.map_system_info_wormhole_type_suffix))
                if (wormhole.inType == "K162") appendLine(" (${wormhole.outType})") else appendLine()
                if (size != null) {
                    withColor(RiftTheme.colors.textHighlighted) {
                        append(size)
                    }
                    appendLine(stringResource(Res.string.map_system_info_wormhole_size_suffix))
                } else {
                    appendLine(stringResource(Res.string.map_system_info_wormhole_unknown_size))
                }
                withColor(RiftTheme.colors.textHighlighted) {
                    append(wormhole.inSignature)
                }
                appendLine(stringResource(Res.string.map_system_info_wormhole_origin_suffix, wormhole.inSystemName, wormhole.inRegionName.toString()))
                withColor(RiftTheme.colors.textHighlighted) {
                    append(wormhole.outSignature)
                }
                append(stringResource(Res.string.map_system_info_wormhole_destination_suffix, wormhole.outSystemName))
                if (wormhole.outRegionName != null) {
                    append(" (${wormhole.outRegionName})")
                }
            },
        ) {
            val mapExternalControl: MapExternalControl = remember { koin.get() }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                    .onClick { mapExternalControl.showSystemOnMap(wormhole.outSystemId) },
            ) {
                Image(
                    painter = painterResource(Res.drawable.indicator_wormhole),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                val text = buildString {
                    append(wormhole.outSystemName)
                    if (wormhole.outRegionName != null) {
                        append(" (${wormhole.outRegionName})")
                    }
                    if (isFull) {
                        append(" ${wormhole.inSignature}")
                    }
                }
                Text(
                    text = text,
                    style = RiftTheme.typography.bodyPrimary,
                )
            }
        }
    }
}

@Composable
private fun IndustryActivityIndex(systemStatus: SolarSystemStatus?, activity: IndustryActivity, text: StringResource) {
    val index = systemStatus?.industryIndices?.get(activity)
    if (index != null) {
        Text(
            text = stringResource(text, String.format("%.1f", index * 100)),
            style = RiftTheme.typography.bodyPrimary,
        )
    }
}

private val sovereigntyDevelopmentInfoTypes = listOf(
    MapSystemInfoType.SovereigntyStrategicIndex,
    MapSystemInfoType.SovereigntyMilitaryIndex,
    MapSystemInfoType.SovereigntyIndustrialIndex,
    MapSystemInfoType.SovereigntyActivityDefenseMultiplier,
    MapSystemInfoType.SovereigntyCapitalSystem,
)

@Composable
private fun SovereigntyDevelopmentIndices(systemStatus: SolarSystemStatus?, infoTypes: List<MapSystemInfoType>) {
    val alliance = systemStatus?.sovereignty?.alliance ?: return
    val development = alliance.development
    val visibleTypes = sovereigntyDevelopmentInfoTypes
        .filter { it in infoTypes }
        .filter { it != MapSystemInfoType.SovereigntyCapitalSystem || alliance.isCapitalSystem }
    if (visibleTypes.isEmpty()) return
    val activityDefenseMultiplier = String.format("%.1f", development.activityDefenseMultiplier)
    val namesRepository: NamesRepository = remember { koin.get() }
    val allianceName = namesRepository.getName(alliance.allianceId) ?: stringResource(Res.string.map_system_info_unknown_name)
    RiftTooltipArea(
        tooltip = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier.width(280.dp).padding(Spacing.medium),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ActivityDefenseMultiplierIcon(development.activityDefenseMultiplier, 16.dp)
                    Text(
                        text = stringResource(Res.string.map_system_info_activity_defense_multiplier_name),
                        style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = Spacing.small),
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${activityDefenseMultiplier}x",
                        style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
                    )
                }
                SovereigntyIndexTooltipRow(Res.drawable.strategicindex_w, stringResource(Res.string.map_system_info_strategic_index_name), development.strategicLevel)
                SovereigntyIndexTooltipRow(Res.drawable.militaryindex_w, stringResource(Res.string.map_system_info_military_index_name), development.militaryLevel)
                SovereigntyIndexTooltipRow(Res.drawable.industryindex_w, stringResource(Res.string.map_system_info_industry_index_name), development.industrialLevel)
                if (alliance.isCapitalSystem) {
                    Divider(
                        color = RiftTheme.colors.divider,
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.verySmall),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(Res.drawable.bonusshieldcapital16),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = stringResource(Res.string.map_system_info_capital_name),
                            style = RiftTheme.typography.bodyPrimary,
                            modifier = Modifier.padding(start = Spacing.small),
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = allianceName,
                            style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            visibleTypes.forEach { type ->
                when (type) {
                    MapSystemInfoType.SovereigntyStrategicIndex -> InfoTypeIndicator(development.strategicLevel.toString(), Res.drawable.strategicindex_w)
                    MapSystemInfoType.SovereigntyMilitaryIndex -> InfoTypeIndicator(development.militaryLevel.toString(), Res.drawable.militaryindex_w)
                    MapSystemInfoType.SovereigntyIndustrialIndex -> InfoTypeIndicator(development.industrialLevel.toString(), Res.drawable.industryindex_w)
                    MapSystemInfoType.SovereigntyActivityDefenseMultiplier -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                    ) {
                        ActivityDefenseMultiplierIcon(development.activityDefenseMultiplier, 16.dp)
                        Text(activityDefenseMultiplier, style = RiftTheme.typography.bodyPrimary)
                    }
                    MapSystemInfoType.SovereigntyCapitalSystem -> InfoTypeIndicator("", Res.drawable.bonusshieldcapital16)
                    else -> error("Not a sovereignty development index: $type")
                }
            }
        }
    }
}

@Composable
private fun ActivityDefenseMultiplierIcon(multiplier: Double, size: Dp) {
    val fill = imageResource(Res.drawable.bonusshieldfill24)
    val mask = imageResource(Res.drawable.defensebonussimple)
    val percentage = (multiplier / 6.0).coerceIn(0.0, 1.0)
    Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
    ) {
        val destinationSize = IntSize(this.size.width.toInt(), this.size.height.toInt())
        val sourceSize = IntSize(fill.width, fill.width)
        val sourceOffset = IntOffset(0, ((fill.height - sourceSize.height) * percentage).toInt())
        drawImage(
            image = fill,
            srcOffset = sourceOffset,
            srcSize = sourceSize,
            dstSize = destinationSize,
        )
        drawImage(
            image = mask,
            dstSize = destinationSize,
            blendMode = BlendMode.DstIn,
        )
    }
}

@Composable
private fun SovereigntyIndexTooltipRow(icon: DrawableResource, name: String, level: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = name,
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.padding(start = Spacing.small),
        )
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(8.dp)
                        .background(
                            if (index < level) RiftTheme.colors.textPrimary else RiftTheme.colors.windowBackgroundSecondary,
                        ),
                )
            }
        }
    }
}

@Composable
fun StandingsIndicator(security: Double, systemStatus: SolarSystemStatus?) {
    val roundedSecurity = security.roundSecurity()
    val standingsRepository: StandingsRepository = remember { koin.get() }
    val allianceId = systemStatus?.sovereignty?.alliance?.allianceId
    val standing = standingsRepository.getStandingLevel(allianceId, null, null, includeWars = false)
    val [tooltip, color] = if (roundedSecurity >= 0.5) {
        stringResource(Res.string.map_system_info_standing_high_sec_tooltip) to Color(0xFF71E754)
    } else if (roundedSecurity > 0.0) {
        stringResource(Res.string.map_system_info_standing_low_sec_tooltip) to Color(0xFFF5FF83)
    } else if (allianceId == null) {
        stringResource(Res.string.map_system_info_standing_no_sovereignty_tooltip) to Color(0xFF7D7E7E)
    } else {
        when (standing) {
            Standing.WarTarget -> error("Not used")
            Standing.Terrible -> stringResource(Res.string.map_system_info_standing_terrible_tooltip) to standing.getSystemColor()
            Standing.Bad -> stringResource(Res.string.map_system_info_standing_bad_tooltip) to standing.getSystemColor()
            Standing.Neutral -> stringResource(Res.string.map_system_info_standing_neutral_tooltip) to standing.getSystemColor()
            Standing.Good -> stringResource(Res.string.map_system_info_standing_good_tooltip) to standing.getSystemColor()
            Standing.Excellent -> stringResource(Res.string.map_system_info_standing_excellent_tooltip) to standing.getSystemColor()
            Standing.Self, Standing.Corporation, Standing.Alliance -> stringResource(Res.string.map_system_info_standing_excellent_tooltip) to standing.getSystemColor()
        }
    }

    RiftTooltipArea(text = tooltip) {
        Box(
            modifier = Modifier
                .padding(bottom = 0.5.dp, start = 2.dp)
                .clip(CircleShape)
                .background(color)
                .size(8.dp),
        )
    }
}

@Composable
fun ClonesIndicators(clones: Map<Int, Int>, withDetails: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
    ) {
        Image(
            painter = painterResource(Res.drawable.indicator_clones),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        val localCharactersRepository: LocalCharactersRepository = remember { koin.get() }
        clones.forEach { (characterId, count) ->
            val characterName = localCharactersRepository.characters.value
                .find { it.characterId == characterId }
                ?.info?.name
            RiftTooltipArea(
                text = buildString {
                    if (characterName != null) appendLine(characterName)
                    append(pluralStringResource(Res.plurals.map_system_info_clone_count, count, count))
                },
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(RiftTheme.colors.windowBackgroundActive.copy(alpha = 0.3f)),
                ) {
                    AsyncCharacterPortrait(
                        characterId = characterId,
                        size = 32,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        if (withDetails) {
            val totalCount = clones.entries.sumOf { it.value }
            Text(
                text = pluralStringResource(Res.plurals.map_system_info_clone_total, totalCount, totalCount),
                style = RiftTheme.typography.bodyPrimary,
            )
        }
    }
}

@Composable
fun SovereigntyUpgradesIndicators(upgrades: List<SovereigntyUpgrade>, isExpanded: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
    ) {
        for ((upgrade, grouped) in upgrades.groupBy { it }) {
            val count = grouped.size
            RiftTooltipArea(
                text = when {
                    upgrade.isEffect && count > 1 -> stringResource(Res.string.map_system_info_upgrade_effect_count, count, upgrade.type.name)
                    upgrade.isEffect -> stringResource(Res.string.map_system_info_upgrade_effect, upgrade.type.name)
                    count > 1 -> stringResource(Res.string.map_system_info_upgrade_count, count, upgrade.type.name)
                    else -> upgrade.type.name
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (count > 1) {
                        Text(
                            text = stringResource(Res.string.map_system_info_upgrade_count_compact, count),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                    }
                    val size = if (isExpanded) 32.dp else 24.dp
                    AsyncTypeIcon(
                        type = upgrade.type,
                        modifier = Modifier
                            .alpha(if (upgrade.isEffect) 0.5f else 1f)
                            .size(size),
                    )
                }
            }
        }
    }
}

@Composable
private fun SovereigntyLogo(claim: SovereigntySystemClaim) {
    val requestSize = 32
    val size = 24
    if (claim.alliance?.allianceId != null) {
        AsyncAllianceLogo(
            allianceId = claim.alliance.allianceId,
            size = requestSize,
            modifier = Modifier.size(size.dp),
        )
    } else if (claim.faction?.factionId != null) {
        AsyncCorporationLogo(
            corporationId = claim.faction.factionId,
            size = requestSize,
            modifier = Modifier.size(size.dp),
        )
    }
}

@Composable
private fun AssetSafetyIndicators(
    system: MapSolarSystem,
    systemStatus: SolarSystemStatus?,
    isShowingDetail: Boolean,
) {
    val assetSafety = systemStatus?.assetSafety ?: return
    val mapExternalControl: MapExternalControl = remember { koin.get() }
    InfoTypeIndicator(
        text = assetSafety.targetSystemName,
        icon = Res.drawable.indicator_asset_safety_target,
        tooltip = stringResource(Res.string.map_system_info_asset_safety_target_tooltip, assetSafety.targetSystemName),
        onClick = { mapExternalControl.showSystemOnMap(assetSafety.targetSystemId) },
    )
    assetSafety.sourceSystemCount.takeIf { it > 0 }?.let { sourceSystemCount ->
        InfoTypeIndicator(
            text = if (isShowingDetail) {
                pluralStringResource(Res.plurals.map_system_info_asset_safety_source_count, sourceSystemCount, sourceSystemCount)
            } else {
                sourceSystemCount.toString()
            },
            icon = Res.drawable.indicator_asset_safety_source,
            tooltip = pluralStringResource(Res.plurals.map_system_info_asset_safety_source_tooltip, sourceSystemCount, system.name, sourceSystemCount),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InfoTypeIndicator(
    text: String?,
    icon: DrawableResource,
    tooltip: String? = null,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
) {
    if (text == null) return
    val content = movableContentOf {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
            modifier = Modifier.modifyIf(onClick != null) {
                pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                    .onClick { onClick?.invoke() }
            },
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = tint?.let { ColorFilter.tint(it) },
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = text,
                style = RiftTheme.typography.bodyPrimary,
            )
        }
    }
    if (tooltip != null) {
        RiftTooltipArea(
            text = tooltip,
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
fun SystemIntelContents(
    groups: List<IntelGroup>,
    system: MapSolarSystem,
) {
    CompositionLocalProvider(LocalNow provides getNow()) {
        Column {
            val isCompact = groups.hasAtLeast(8)
            for ((index, group) in groups.sortedByDescending { it.timestamp }.withIndex()) {
                if (index > 0) {
                    Divider(
                        color = RiftTheme.colors.divider,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        IntelTimer(
                            timestamp = group.timestamp,
                            style = RiftTheme.typography.detailBoldPrimary,
                        )
                        if (group.clearedAt != null) {
                            Text(
                                text = stringResource(Res.string.map_system_info_intel_cleared),
                                style = RiftTheme.typography.detailSecondary,
                            )
                            IntelTimer(
                                timestamp = group.clearedAt,
                                style = RiftTheme.typography.detailBoldPrimary,
                            )
                        }
                    }
                    SystemEntities(
                        entities = group.entities,
                        system = system,
                        rowHeight = if (isCompact) 24.dp else 32.dp,
                        isGroupingCharacters = isCompact,
                        isCleared = group.clearedAt != null,
                    )
                }
            }
        }
    }
}

private fun List<IntelGroup>.hasAtLeast(count: Int): Boolean {
    var sum = 0
    for (group in this) {
        sum += group.entities.size
        if (sum >= count) return true
    }
    return false
}

data class IntelGroup(
    val timestamp: Instant,
    val clearedAt: Instant?,
    val entities: List<SystemEntity>,
)

fun groupIntelByTime(intel: List<Dated<SystemEntity>>): List<IntelGroup> {
    // Group entities by when they were reported, so they can be displayed with a single timer by group
    val groups = mutableListOf<IntelGroup>()
    intel.forEach { item ->
        val timestamp = item.timestamp
        val clearedAt = item.clearedAt
        val groupIndex = groups.indexOfFirst { group ->
            val isSameReportGroup = Duration.between(timestamp, group.timestamp).abs() < Duration.ofSeconds(30)
            val isSameClearState = (clearedAt == null) == (group.clearedAt == null)
            val isSameClearGroup = !(clearedAt != null && group.clearedAt != null) || Duration.between(clearedAt, group.clearedAt).abs() < Duration.ofSeconds(30)
            isSameReportGroup && isSameClearState && isSameClearGroup
        }
        if (groupIndex != -1) {
            val group = groups[groupIndex]
            groups[groupIndex] = group.copy(entities = group.entities + item.item)
        } else {
            groups += IntelGroup(timestamp, clearedAt, listOf(item.item))
        }
    }
    return groups
}

@Composable
private fun getIncursionStateText(state: IncursionState): String {
    return stringResource(
        when (state) {
            IncursionState.Withdrawing -> Res.string.map_system_info_incursion_state_withdrawing
            IncursionState.Mobilizing -> Res.string.map_system_info_incursion_state_mobilizing
            IncursionState.Established -> Res.string.map_system_info_incursion_state_established
        },
    )
}

@Composable
private fun getContestedStateText(state: Contested): String {
    return stringResource(
        when (state) {
            Contested.Captured -> Res.string.map_system_info_faction_warfare_state_captured
            Contested.Contested -> Res.string.map_system_info_faction_warfare_state_contested
            Contested.Uncontested -> Res.string.map_system_info_faction_warfare_state_uncontested
            Contested.Vulnerable -> Res.string.map_system_info_faction_warfare_state_vulnerable
        },
    )
}

@Composable
private fun getStormSummary(storm: Storm): String {
    return stringResource(
        when (storm.strength) {
            StormStrength.Strong -> when (storm.type) {
                StormType.Gamma -> Res.string.map_system_info_storm_strong_gamma
                StormType.Electric -> Res.string.map_system_info_storm_strong_electric
                StormType.Plasma -> Res.string.map_system_info_storm_strong_plasma
                StormType.Exotic -> Res.string.map_system_info_storm_strong_exotic
            }
            StormStrength.Weak -> when (storm.type) {
                StormType.Gamma -> Res.string.map_system_info_storm_weak_gamma
                StormType.Electric -> Res.string.map_system_info_storm_weak_electric
                StormType.Plasma -> Res.string.map_system_info_storm_weak_plasma
                StormType.Exotic -> Res.string.map_system_info_storm_weak_exotic
            }
        },
    )
}
