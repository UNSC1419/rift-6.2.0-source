package dev.nohus.rift.intelscanner.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.ClickableShip
import dev.nohus.rift.compose.ClickableType
import dev.nohus.rift.compose.ExpandChevron
import dev.nohus.rift.compose.RiftSolarSystemChip
import dev.nohus.rift.compose.RiftToggleButton
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ToggleButtonType
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.rememberPointerInteractionStateHolder
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.compose.unboundedWithFadingRightEdge
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_scanner_dscan_grid_filter_all
import dev.nohus.rift.generated.resources.intel_scanner_dscan_grid_filter_off_grid
import dev.nohus.rift.generated.resources.intel_scanner_dscan_grid_filter_on_grid
import dev.nohus.rift.generated.resources.intel_scanner_dscan_item_count
import dev.nohus.rift.generated.resources.intel_scanner_dscan_off_grid_count
import dev.nohus.rift.generated.resources.intel_scanner_dscan_on_grid_count
import dev.nohus.rift.generated.resources.intel_scanner_dscan_ship_count_compact
import dev.nohus.rift.generated.resources.intel_scanner_dscan_total_mass
import dev.nohus.rift.intelscanner.IntelScannerViewModel
import dev.nohus.rift.intelscanner.IntelScannerViewModel.Scan
import dev.nohus.rift.intelscanner.IntelScannerViewModel.UiState
import dev.nohus.rift.intelscanner.LoadDScanIntelUseCase.DScanItem
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.settings.persistence.IntelScannerDScanSection
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.get
import dev.nohus.rift.utils.parseRomanNumerals
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val MAX_GRID_SIZE = 8_000_000

@Composable
fun DScanContent(state: UiState, scan: Scan.DScan) {
    var filter by rememberSaveable { mutableStateOf(DScanFilter.All) }
    val items = scan.dscan.items.filter {
        when (filter) {
            DScanFilter.All -> true
            DScanFilter.OnGrid -> it.isOnGrid
            DScanFilter.OffGrid -> !it.isOnGrid
        }
    }
    var hoveredShipGroup: String? by remember { mutableStateOf(null) }
    val panes = remember(items, state.settings.enabledDScanSections) {
        getDScanPanes(items, state.settings.enabledDScanSections)
    }

    Column {
        Spacer(Modifier.height(Spacing.medium))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                RiftToggleButton(
                    text = stringResource(Res.string.intel_scanner_dscan_grid_filter_all),
                    isSelected = filter == DScanFilter.All,
                    type = ToggleButtonType.Left,
                    onClick = { filter = DScanFilter.All },
                )
                RiftToggleButton(
                    text = stringResource(Res.string.intel_scanner_dscan_grid_filter_on_grid),
                    isSelected = filter == DScanFilter.OnGrid,
                    type = ToggleButtonType.Middle,
                    onClick = { filter = DScanFilter.OnGrid },
                )
                RiftToggleButton(
                    text = stringResource(Res.string.intel_scanner_dscan_grid_filter_off_grid),
                    isSelected = filter == DScanFilter.OffGrid,
                    type = ToggleButtonType.Right,
                    onClick = { filter = DScanFilter.OffGrid },
                )
            }
            scan.dscan.systemChip?.let {
                RiftSolarSystemChip(it)
            }
        }
        Spacer(Modifier.height(Spacing.medium))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(320.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalItemSpacing = Spacing.medium,
        ) {
            panes.forEach { pane ->
                paneItem(pane.title, pane.section, state.settings, pane.id) {
                    DScanPaneContent(
                        pane = pane,
                        state = state,
                        system = scan.dscan.systemChip?.name,
                        hoveredShipGroup = hoveredShipGroup,
                        onShipGroupHover = { hoveredShipGroup = it },
                    )
                }
            }
        }
    }
}

@Composable
fun DScanPaneContent(
    pane: DScanPaneModel,
    state: UiState,
    system: String?,
    hoveredShipGroup: String?,
    onShipGroupHover: (String?) -> Unit,
) {
    val isCompact = state.settings.isUsingCompactMode
    when (val content = pane.content) {
        is DScanPaneContent.ShipTypes -> ShipTypesPane(
            shipGroups = content.items.groupBy { it.typeGroup },
            isCompact = isCompact,
            isShowingShips = state.settings.isShowingShipsInDScanShipClasses,
            isShowingMass = state.settings.isShowingDScanShipMass,
            onShipGroupHover = onShipGroupHover,
        )
        is DScanPaneContent.Ships -> ShipsPane(
            ships = content.items,
            hoveredShipGroup = hoveredShipGroup,
            isShowingMass = state.settings.isShowingDScanShipMass,
            isCompact = isCompact,
        )
        is DScanPaneContent.Structures -> StructuresPane(system, content.items, isCompact)
        is DScanPaneContent.Stations -> StationsPane(system, content.items, isCompact)
        is DScanPaneContent.CelestialBodies -> CelestialBodiesPane(content.items, isCompact)
        is DScanPaneContent.Category -> CategoryPane(content.items, isCompact, content.showGroup)
        is DScanPaneContent.Corpses -> CorpsesPane(content.items, isCompact)
    }
}

private enum class DScanFilter {
    All,
    OnGrid,
    OffGrid,
}

@Composable
private fun ShipTypesPane(
    shipGroups: Map<String?, List<DScanItem>>,
    isCompact: Boolean,
    isShowingShips: Boolean,
    isShowingMass: Boolean,
    onShipGroupHover: (String?) -> Unit,
) {
    shipGroups
        .entries
        .sortedByDescending { it.value.size }
        .forEach { [group, ships] ->
            val pointerInteractionStateHolder = rememberPointerInteractionStateHolder()
            LaunchedEffect(pointerInteractionStateHolder.isHovered) {
                if (pointerInteractionStateHolder.isHovered) {
                    onShipGroupHover(group)
                } else {
                    onShipGroupHover(null)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier
                    .pointerInteraction(pointerInteractionStateHolder)
                    .hoverBackground(pointerInteractionStateHolder = pointerInteractionStateHolder)
                    .padding(horizontal = Spacing.medium)
            ) {
                if (isCompact) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = group.toString(),
                            style = RiftTheme.typography.bodyPrimary
                        )
                        if (isShowingShips) ShipsInClass(ships, isCompact = true)
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = group.toString(),
                            style = RiftTheme.typography.bodyPrimary
                        )
                        if (isShowingShips) ShipsInClass(ships, isCompact = false)
                    }
                }
                Count(ships.getGridCount(), showCount = true, isCompact = isCompact)
            }
        }
    if (isShowingMass) {
        ShipMass(shipGroups.entries.sumOf { it.value.sumOf { it.type.mass ?: 0.0 } })
    }
}

@Composable
private fun ShipsInClass(ships: List<DScanItem>, isCompact: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.unboundedWithFadingRightEdge()
    ) {
        ships
            .groupBy { it.type }
            .entries
            .sortedByDescending { it.value.size }
            .forEach { [type, items] ->
                ClickableShip(type) {
                    RiftTooltipArea(
                        text = stringResource(Res.string.intel_scanner_dscan_item_count, formatNumber(items.size), type.name),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.intel_scanner_dscan_ship_count_compact, formatNumber(items.size)),
                                style = RiftTheme.typography.detailSecondary
                            )
                            AsyncTypeIcon(
                                type = type,
                                modifier = Modifier.size(if (isCompact) 16.dp else 24.dp),
                            )
                        }
                    }
                }
            }
    }
}

@Composable
private fun ShipsPane(ships: List<DScanItem>, hoveredShipGroup: String?, isShowingMass: Boolean, isCompact: Boolean) {
    ships
        .groupBy { it.type }
        .entries
        .sortedByDescending { it.value.size }
        .forEach { [type, instances] ->
            val typeGroup = instances.first().typeGroup
            ItemRow(
                icon = { TypeIcon(type,isCompact) },
                isCompact = isCompact,
                name = type.name,
                description = typeGroup.toString(),
                count = instances.getGridCount(),
                isHighlighted = typeGroup == hoveredShipGroup,
                tooltip = instances.joinToString("\n") { it.name }
            )
        }
    if (isShowingMass) {
        ShipMass(ships.sumOf { it.type.mass ?: 0.0 })
    }
}

@Composable
private fun ShipMass(mass: Double) {
    Text(
        text = stringResource(Res.string.intel_scanner_dscan_total_mass, formatNumber(mass)),
        style = RiftTheme.typography.bodySecondary,
        modifier = Modifier.padding(horizontal = Spacing.medium)
    )
}

@Composable
private fun StructuresPane(
    system: String?,
    structures: List<DScanItem>,
    isCompact: Boolean,
) {
    structures.sortedBy { it.type.id }.forEach { structure ->
        val [namePrefix, name] = if (system != null) {
            if (structure.name.startsWith("$system » ")) "$system » " to structure.name.substringAfter("$system » ")
            else "$system - " to structure.name.substringAfter("$system - ")
        } else null to structure.name
        ItemRow(
            icon = { TypeIcon(structure.type, isCompact) },
            isCompact = isCompact,
            name = name,
            description = "${structure.type.name} – ${structure.typeGroup}",
            namePrefix = if (isCompact) null else namePrefix,
            count = structure.getGridCount(),
            showCount = false,
            tooltip = null,
        )
    }
}

@Composable
private fun StationsPane(
    system: String?,
    stations: List<DScanItem>,
    isCompact: Boolean,
) {
    stations.sortedBy { it.type.id }.forEach { station ->
        val [namePrefix, name] = if (system != null) {
            var prefix = station.name.substringBeforeLast(" - ")
            if (isCompact) prefix = shortenStationPrefix(system, prefix)
            "$prefix - " to station.name.substringAfterLast(" - ")
        } else null to station.name
        ItemRow(
            icon = { TypeIcon(station.type, isCompact) },
            isCompact = isCompact,
            name = name,
            description = null,
            namePrefix = namePrefix,
            count = station.getGridCount(),
            showCount = false,
            tooltip = null,
        )
    }
}

private fun shortenStationPrefix(system: String?, prefix: String): String {
    if (system == null) return prefix
    val regex = """^.* (?<planet>[IVXL]+) - Moon (?<moon>\d+)$""".toRegex()
    val match = regex.matchEntire(prefix) ?: run {
        val shortRegex = """^.* (?<planet>[IVX]+)$""".toRegex()
        val match = shortRegex.matchEntire(prefix) ?: return prefix
        val planet = parseRomanNumerals(match["planet"]) ?: return prefix
        return "$system $planet"
    }
    val planet = parseRomanNumerals(match["planet"]) ?: return prefix
    val moon = match["moon"].toInt()
    return "$system $planet-$moon"
}

@Composable
private fun CelestialBodiesPane(celestials: List<DScanItem>, isCompact: Boolean,) {
    celestials.sortedBy { it.typeGroup }.forEach { celestial ->
        ItemRow(
            icon = { TypeIcon(celestial.type, isCompact) },
            isCompact = isCompact,
            name = celestial.name,
            description = celestial.type.name,
            count = celestial.getGridCount(),
            showCount = false,
            tooltip = null,
        )
    }
}

@Composable
private fun CorpsesPane(corpses: List<DScanItem>, isCompact: Boolean,) {
    corpses.sortedBy { it.typeGroup }.forEach { corpse ->
        ItemRow(
            icon = { TypeIcon(corpse.type, isCompact) },
            isCompact = isCompact,
            name = corpse.name,
            description = corpse.type.name,
            count = corpse.getGridCount(),
            showCount = false,
            tooltip = null,
        )
    }
}

@Composable
private fun CategoryPane(
    items: List<DScanItem>,
    isCompact: Boolean,
    showGroup: Boolean = true,
) {
    items
        .groupBy { it.type }
        .entries
        .sortedByDescending { it.value.size }
        .forEach { [type, instances] ->
            val typeGroup = instances.first().typeGroup
            val hasDuplicateNames = instances.groupBy { it.name }.any { it.value.size > 1 }
            val tooltip = if (hasDuplicateNames) {
                instances
                    .groupBy { it.name }
                    .entries
                    .sortedByDescending { it.value.size }
                    .map { stringResource(Res.string.intel_scanner_dscan_item_count, formatNumber(it.value.size), it.key) }
                    .joinToString("\n")
            } else {
                instances
                    .joinToString("\n") { it.name }
            }
            ItemRow(
                icon = { TypeIcon(type, isCompact) },
                isCompact = isCompact,
                name = type.name,
                description = if (showGroup) typeGroup.toString() else null,
                count = instances.getGridCount(),
                tooltip = tooltip,
            )
        }
}

@Composable
private fun TypeIcon(type: Type, isCompact: Boolean) {
    ClickableType(type) {
        AsyncTypeIcon(
            type = type,
            modifier = Modifier.size(if (isCompact) 24.dp else 32.dp)
        )
    }
}

@Composable
private fun ItemRow(
    icon: @Composable () -> Unit,
    isCompact: Boolean,
    name: String,
    description: String?,
    count: GridCount,
    showCount: Boolean = true,
    isHighlighted: Boolean = false,
    namePrefix: String? = null,
    tooltip: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier
            .hoverBackground(isSelected = isHighlighted)
            .padding(horizontal = Spacing.medium)
    ) {
        RiftTooltipArea(tooltip, modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                icon()
                if (isCompact) {
                    val text = buildAnnotatedString {
                        if (namePrefix != null) {
                            withStyle(RiftTheme.typography.detailSecondary.toSpanStyle()) {
                                append(namePrefix)
                            }
                        }
                        append(name)
                        if (description != null) {
                            append(" ")
                            withStyle(RiftTheme.typography.detailSecondary.toSpanStyle()) {
                                append(description)
                            }
                        }
                    }
                    Text(
                        text = text,
                        style = RiftTheme.typography.bodyPrimary,
                        maxLines = 1,
                        modifier = Modifier.unboundedWithFadingRightEdge()
                    )
                } else {
                    Column {
                        Text(
                            text = buildAnnotatedString {
                                if (namePrefix != null) {
                                    withStyle(RiftTheme.typography.detailSecondary.toSpanStyle()) {
                                        append(namePrefix)
                                    }
                                }
                                append(name)
                            },
                            style = RiftTheme.typography.bodyPrimary
                        )
                        if (description != null) {
                            Text(
                                text = description,
                                style = RiftTheme.typography.detailSecondary
                            )
                        }
                    }
                }
            }
        }
        Count(count, showCount, isCompact)
    }
}

@Composable
fun Count(count: GridCount, showCount: Boolean, isCompact: Boolean) {
    val tooltip = buildString {
        if (showCount) {
            if (count.onGrid > 0) appendLine(pluralStringResource(Res.plurals.intel_scanner_dscan_on_grid_count, count.onGrid, formatNumber(count.onGrid)))
            if (count.offGrid > 0) appendLine(pluralStringResource(Res.plurals.intel_scanner_dscan_off_grid_count, count.offGrid, formatNumber(count.offGrid)))
        } else {
            if (count.onGrid > 0) append(stringResource(Res.string.intel_scanner_dscan_grid_filter_on_grid)) else append(stringResource(Res.string.intel_scanner_dscan_grid_filter_off_grid))
        }
    }.trim()
    val onGridStyle = if (isCompact) RiftTheme.typography.headerHighlighted else RiftTheme.typography.headlineHighlighted
    val offGridStyle = if (isCompact) RiftTheme.typography.headerSecondary else RiftTheme.typography.headlineSecondary

    RiftTooltipArea(tooltip) {
        if (showCount) {
            if (count.onGrid > 0 && count.offGrid > 0) {
                Row {
                    val leftShape = RoundedCornerShape(topStartPercent = 100, bottomStartPercent = 100)
                    Box(
                        modifier = Modifier
                            .background(RiftTheme.colors.backgroundPrimary, leftShape)
                            .border(1.dp, RiftTheme.colors.borderPrimaryDark, leftShape)
                            .padding(start = Spacing.medium, end = Spacing.small)
                    ) {
                        Text(
                            text = "${count.onGrid}",
                            style = onGridStyle
                        )
                    }
                    val rightShape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    Box(
                        modifier = Modifier
                            .background(RiftTheme.colors.backgroundPrimaryDark, rightShape)
                            .border(1.dp, RiftTheme.colors.borderGrey, rightShape)
                            .padding(start = Spacing.small, end = Spacing.medium)
                    ) {
                        Text(
                            text = "${count.offGrid}",
                            style = offGridStyle
                        )
                    }
                }
            } else {
                val background = if (count.onGrid > 0) RiftTheme.colors.backgroundPrimary else RiftTheme.colors.backgroundPrimaryDark
                val border = if (count.onGrid > 0) RiftTheme.colors.borderPrimaryDark else RiftTheme.colors.borderGrey
                Row(
                    modifier = Modifier
                        .background(background, RoundedCornerShape(100))
                        .border(1.dp, border, RoundedCornerShape(100))
                        .padding(horizontal = Spacing.medium)
                ) {
                    if (count.onGrid > 0) {
                        Text(
                            text = "${count.onGrid}",
                            style = onGridStyle
                        )
                    }
                    if (count.offGrid > 0) {
                        Text(
                            text = "${count.offGrid}",
                            style = offGridStyle
                        )
                    }
                }
            }
        } else {
            val background = if (count.onGrid > 0) RiftTheme.colors.backgroundPrimary else RiftTheme.colors.backgroundPrimaryDark
            val border = if (count.onGrid > 0) RiftTheme.colors.borderPrimary else RiftTheme.colors.borderGrey
            Box(
                modifier = Modifier
                    .background(background, RoundedCornerShape(100))
                    .border(1.dp, border, RoundedCornerShape(100))
                    .size(10.dp)
            ) {}
        }
    }
}

private fun LazyStaggeredGridScope.paneItem(
    title: UiText,
    section: IntelScannerDScanSection,
    settings: IntelScannerViewModel.IntelScannerSettings,
    id: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    item(key = id) {
        var isExpanded by rememberSaveable { mutableStateOf(section !in settings.collapsedDScanSections) }
        val player = rememberUiSoundPlayer()
        DScanPane(
            title = title.resolve(),
            isExpanded = isExpanded,
            onClick = {
                player.playAccordion(isExpanded)
                isExpanded = !isExpanded
            },
            modifier = Modifier.animateItem(),
            content = content,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun DScanPane(
    title: String,
    isExpanded: Boolean?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(RiftTheme.colors.backgroundPrimaryDark)
            .border(1.dp, RiftTheme.colors.borderPrimaryDark)
            .padding(bottom = if (isExpanded != false) Spacing.medium else 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier
                            .hoverBackground(hoverSound = UiSound.EntryHover)
                            .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                            .onClick(onClick = onClick)
                    } else {
                        Modifier
                    }
                )
                .padding(vertical = Spacing.small),
        ) {
            if (isExpanded != null) {
                ExpandChevron(isExpanded = isExpanded)
            } else {
                Spacer(Modifier.width(Spacing.medium))
            }
            Text(
                text = title,
                style = RiftTheme.typography.headlinePrimary,
                modifier = Modifier.padding(end = Spacing.medium),
            )
        }
        if (isExpanded != false) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                content = content,
            )
        }
    }
}

data class GridCount(val onGrid: Int, val offGrid: Int)

private val DScanItem.isOnGrid: Boolean get() = distance != null && distance < MAX_GRID_SIZE

private fun DScanItem.getGridCount(): GridCount {
    return GridCount(
        onGrid = if (isOnGrid) 1 else 0,
        offGrid = if (isOnGrid) 0 else 1
    )
}

private fun List<DScanItem>.getGridCount(): GridCount {
    return GridCount(
        onGrid = count { it.isOnGrid },
        offGrid = count { !it.isOnGrid }
    )
}
