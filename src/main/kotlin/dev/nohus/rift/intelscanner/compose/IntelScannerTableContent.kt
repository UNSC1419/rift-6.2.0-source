package dev.nohus.rift.intelscanner.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.CharacterTooltip
import dev.nohus.rift.compose.ClickableAlliance
import dev.nohus.rift.compose.ClickableCharacter
import dev.nohus.rift.compose.ClickableCorporation
import dev.nohus.rift.compose.ClickableShip
import dev.nohus.rift.compose.ContactLabelTag
import dev.nohus.rift.compose.FlagIcon
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.theme.EveColors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitStandings
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.abyssal_deadspace_16px
import dev.nohus.rift.generated.resources.effectcyno_22px
import dev.nohus.rift.generated.resources.high_sec_16px
import dev.nohus.rift.generated.resources.hightolow_12x10
import dev.nohus.rift.generated.resources.hightonull_12x10
import dev.nohus.rift.generated.resources.intel_scanner_activity_likelihood_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_character_load_failed
import dev.nohus.rift.generated.resources.intel_scanner_character_no_alliance
import dev.nohus.rift.generated.resources.intel_scanner_character_not_found
import dev.nohus.rift.generated.resources.intel_scanner_column_awox_name
import dev.nohus.rift.generated.resources.intel_scanner_column_bait_name
import dev.nohus.rift.generated.resources.intel_scanner_command_ship_appearances
import dev.nohus.rift.generated.resources.intel_scanner_cyno_covert
import dev.nohus.rift.generated.resources.intel_scanner_cyno_industrial
import dev.nohus.rift.generated.resources.intel_scanner_cyno_standard
import dev.nohus.rift.generated.resources.intel_scanner_detected_count
import dev.nohus.rift.generated.resources.intel_scanner_fleet_commander_likelihood_description
import dev.nohus.rift.generated.resources.intel_scanner_fleet_commander_likelihood_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_ganker_name
import dev.nohus.rift.generated.resources.intel_scanner_group_size_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_kill_count
import dev.nohus.rift.generated.resources.intel_scanner_kills_heading
import dev.nohus.rift.generated.resources.intel_scanner_large_fleet_appearances
import dev.nohus.rift.generated.resources.intel_scanner_likelihood_high
import dev.nohus.rift.generated.resources.intel_scanner_likelihood_low
import dev.nohus.rift.generated.resources.intel_scanner_likelihood_medium
import dev.nohus.rift.generated.resources.intel_scanner_likelihood_none
import dev.nohus.rift.generated.resources.intel_scanner_losses_heading
import dev.nohus.rift.generated.resources.intel_scanner_monitor_appearances
import dev.nohus.rift.generated.resources.intel_scanner_ship_combat_summary
import dev.nohus.rift.generated.resources.intel_scanner_ship_loss_count
import dev.nohus.rift.generated.resources.intel_scanner_tag_bait_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_black_ops_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_capital_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_cyno_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_default_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_fleet_commander_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_ganker_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_highsec_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_logistics_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_lowsec_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_pochven_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_rookie_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_supercarrier_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_tag_titan_tooltip
import dev.nohus.rift.generated.resources.low_sec_16px
import dev.nohus.rift.generated.resources.lowtohigh_12x10
import dev.nohus.rift.generated.resources.null_sec_16px
import dev.nohus.rift.generated.resources.nulltohigh_12x10
import dev.nohus.rift.generated.resources.table_sort_ascending
import dev.nohus.rift.generated.resources.table_sort_descending
import dev.nohus.rift.generated.resources.triglavian_16
import dev.nohus.rift.generated.resources.wormholes_16px
import dev.nohus.rift.intelscanner.ColumnSkeletonType
import dev.nohus.rift.intelscanner.ColumnType
import dev.nohus.rift.intelscanner.ColumnType.Age
import dev.nohus.rift.intelscanner.ColumnType.Alliance
import dev.nohus.rift.intelscanner.ColumnType.Associates
import dev.nohus.rift.intelscanner.ColumnType.Groups
import dev.nohus.rift.intelscanner.ColumnType.AwoxKills
import dev.nohus.rift.intelscanner.ColumnType.Bait
import dev.nohus.rift.intelscanner.ColumnType.Character
import dev.nohus.rift.intelscanner.ColumnType.Corporation
import dev.nohus.rift.intelscanner.ColumnType.Cyno
import dev.nohus.rift.intelscanner.ColumnType.Danger
import dev.nohus.rift.intelscanner.ColumnType.Faction
import dev.nohus.rift.intelscanner.ColumnType.FleetCommander
import dev.nohus.rift.intelscanner.ColumnType.Ganker
import dev.nohus.rift.intelscanner.ColumnType.Gang
import dev.nohus.rift.intelscanner.ColumnType.GroupSize
import dev.nohus.rift.intelscanner.ColumnType.Isk
import dev.nohus.rift.intelscanner.ColumnType.KillDeathRatio
import dev.nohus.rift.intelscanner.ColumnType.RecentShips
import dev.nohus.rift.intelscanner.ColumnType.Security
import dev.nohus.rift.intelscanner.ColumnType.SoloKills
import dev.nohus.rift.intelscanner.ColumnType.Space
import dev.nohus.rift.intelscanner.ColumnType.TopShips
import dev.nohus.rift.intelscanner.ColumnType.Tags
import dev.nohus.rift.intelscanner.ColumnWidths
import dev.nohus.rift.intelscanner.IntelScannerViewModel.IntelScannerSettings
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterItem
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterState
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterTag
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.UsedShip
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.ZkillboardDetails
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.ZkillboardDetails.IskType
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.ZkillboardDetails.SpaceType
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.map.SecurityColors
import dev.nohus.rift.settings.persistence.IntelScannerSortDirection
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.roundSecurity
import java.text.NumberFormat
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import java.time.Instant
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeaderRow(
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
    onSortingColumnChange: (ColumnType) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth(align = Alignment.Start, unbounded = true)
            .width(getTableWidth(settings, columnWidths))
            .background(RiftTheme.colors.windowBackgroundSecondary)
            .padding(vertical = Spacing.small)
    ) {
        settings.enabledColumns.forEach { column ->
            HeaderCell(
                column = column,
                width = columnWidths[column],
                sortingDirection = settings.sortingDirection.takeIf { column == settings.sortingColumn },
                onClick = { onSortingColumnChange(column) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderCell(
    column: ColumnType,
    width: Dp,
    sortingDirection: IntelScannerSortDirection?,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
        modifier = Modifier
            .width(width)
            .onClick(onClick = onClick),
    ) {
        Text(
            text = column.title.resolve(),
            style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        sortingDirection?.let {
            Image(
                painter = painterResource(
                    when (it) {
                        IntelScannerSortDirection.Ascending -> Res.drawable.table_sort_ascending
                        IntelScannerSortDirection.Descending -> Res.drawable.table_sort_descending
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(SortIndicatorSize),
            )
        }
    }
}

private val SortIndicatorSize = 16.dp

fun getTableWidth(
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
): Dp {
    return settings.enabledColumns.fold(0.dp) { width, column ->
        width + columnWidths[column]
    }
}

@Composable
fun getColumnsWidths(settings: IntelScannerSettings, characters: List<CharacterItem>): ColumnWidths {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val headerTextStyle = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold)
    val textStyle = RiftTheme.typography.bodyPrimary
    val detailTextStyle = RiftTheme.typography.detailSecondary
    val tagTextStyle = RiftTheme.typography.detailPrimary
    val optionalTextSkeletonWidth = with(density) {
        textMeasurer.measure(OptionalTextSkeletonSample, textStyle).size.width.toDp()
    }
    val isEsiLoading = characters.any { it.state == CharacterState.Loading }

    fun isAnyLoading(): Boolean = characters.any { character ->
        when (val state = character.state) {
            CharacterState.Loading -> true
            is CharacterState.Loaded -> state.isZKillboardLoading
            CharacterState.Error, CharacterState.NotFound -> false
        }
    }

    fun measure(texts: List<String>, detailTexts: List<String>, extraSize: Dp): Dp {
        val textSize = texts.maxOfOrNull { text ->
            val textWidthPx = textMeasurer.measure(text, textStyle).size.width
            with(density) { textWidthPx.toDp() }
        } ?: 0.dp
        val detailTextSize = detailTexts.maxOfOrNull { detailText ->
            val textWidthPx = textMeasurer.measure(detailText, detailTextStyle).size.width
            with(density) { textWidthPx.toDp() }
        } ?: 0.dp
        return maxOf(textSize, detailTextSize) + extraSize
    }

    val map = settings.enabledColumns.associateWith { column ->
        val isSourceLoading = column.dataSource != null && isAnyLoading()
        val texts = when (column) {
            Character -> characters.mapNotNull { character ->
                val state = character.state as? CharacterState.Loaded
                state?.let {
                    listOfNotNull(
                        column.textContent(settings, it),
                        column.secondaryTextContent(settings, it),
                    ).joinToString(" ").ifBlank { null }
                } ?: character.name
            }
            else -> characters
                .mapNotNull { it.state as? CharacterState.Loaded }
                .mapNotNull {
                    listOfNotNull(
                        column.textContent(settings, it),
                        column.secondaryTextContent(settings, it),
                    ).joinToString(" ").ifBlank { null }
                }
        }
        val detailTexts = characters
            .mapNotNull { it.state as? CharacterState.Loaded }
            .mapNotNull { column.detailTextContent(settings, it) }
        val tags = when (column) {
            Tags -> characters
                .mapNotNull { it.state as? CharacterState.Loaded }
                .map { state -> state.zKillboardDetails?.tags.orEmpty().map { it.displayName.resolve() } }
            else -> emptyList()
        }
        val dynamicExtraContentWidth = when (column) {
            GroupSize -> {
                val maximumGroupSizeCount = characters
                    .mapNotNull { it.state as? CharacterState.Loaded }
                    .maxOfOrNull { it.zKillboardDetails?.groupSizes.orEmpty().size }
                    ?: 0
                getChartWidth(
                    maxOf(
                        maximumGroupSizeCount,
                        if (isAnyLoading()) GroupSizeSkeletonBarCount else 0,
                    ),
                )
            }
            Space -> getSpaceChartWidth()
            Isk -> getIskChartWidth()
            TopShips -> {
                val maximumShipCount = characters
                    .mapNotNull { it.state as? CharacterState.Loaded }
                    .maxOfOrNull { it.zKillboardDetails?.topShips.orEmpty().take(settings.topShipsCount).size }
                    ?: 0
                val skeletonCount = settings.topShipsCount.takeIf {
                    isAnyLoading()
                } ?: 0
                settings.portraitSize * maxOf(maximumShipCount, skeletonCount)
            }
            RecentShips -> {
                val maximumShipCount = characters
                    .mapNotNull { it.state as? CharacterState.Loaded }
                    .maxOfOrNull { it.zKillboardDetails?.recentShips.orEmpty().take(settings.recentShipsCount).size }
                    ?: 0
                val skeletonCount = settings.recentShipsCount.takeIf {
                    isAnyLoading()
                } ?: 0
                settings.portraitSize * maxOf(maximumShipCount, skeletonCount)
            }
            Groups -> {
                val maximumGroupCount = characters
                    .mapNotNull { it.state as? CharacterState.Loaded }
                    .maxOfOrNull {
                        getZkillAffiliates(
                            affiliates = it.zKillboardDetails?.affiliates,
                            maximumCount = settings.affiliatesCount,
                            minimumKills = settings.affiliatesMinimumKills,
                        ).size
                    }
                    ?: 0
                val skeletonCount = settings.affiliatesCount.takeIf {
                    column.dataSource != null && isAnyLoading()
                } ?: 0
                settings.portraitSize * maxOf(maximumGroupCount, skeletonCount)
            }
            Associates -> {
                val maximumGroupNameWidth = characters
                    .mapNotNull { (it.state as? CharacterState.Loaded)?.associateGroup?.name }
                    .maxOfOrNull { name ->
                        with(density) { textMeasurer.measure(name, tagTextStyle).size.width.toDp() }
                    }
                    ?: 0.dp
                if (maximumGroupNameWidth > 0.dp) {
                    maximumGroupNameWidth + TagHorizontalPadding * 2 + Spacing.small
                } else {
                    0.dp
                }
            }
            Character -> {
                val maximumTextWidth = measure(texts, detailTexts, 0.dp)
                val maximumContentWidth = characters.maxOfOrNull { character ->
                    val state = character.state as? CharacterState.Loaded
                    val text = state?.let { column.textContent(settings, it) } ?: character.name
                    val detailText = state?.let { column.detailTextContent(settings, it) }
                    val textWidth = with(density) {
                        textMeasurer.measure(text, textStyle).size.width.toDp()
                    }
                    val detailTextWidth = detailText?.let {
                        with(density) { textMeasurer.measure(it, detailTextStyle).size.width.toDp() }
                    } ?: 0.dp
                    val hasContactLabel = state?.characterDetails?.let {
                        it.characterLabels.isNotEmpty() ||
                                it.corporationLabels.isNotEmpty() ||
                                it.allianceLabels.isNotEmpty()
                    } == true
                    maxOf(textWidth, detailTextWidth) +
                            if (hasContactLabel) ContactLabelSize + Spacing.small else 0.dp
                } ?: 0.dp
                (maximumContentWidth - maximumTextWidth).coerceAtLeast(0.dp)
            }
            else -> 0.dp
        }
        val columnTitle = column.title.resolve()
        remember(
            columnTitle,
            settings,
            texts,
            detailTexts,
            tags,
            dynamicExtraContentWidth,
            isSourceLoading,
            isEsiLoading,
            textMeasurer,
            density,
            headerTextStyle,
            textStyle,
            detailTextStyle,
            tagTextStyle,
            optionalTextSkeletonWidth,
        ) {
            var extraSize = Spacing.small
            if (column.hasPortrait) extraSize += settings.portraitSize + Spacing.small
            if (column is Character && settings.isShowingCorpWithCharacter) extraSize += settings.portraitSize
            if (column is Character && settings.isShowingAllianceWithCharacter) extraSize += settings.portraitSize
            if (column.extraContentWidth != null) extraSize += column.extraContentWidth + Spacing.small
            extraSize += dynamicExtraContentWidth
            val sortIndicatorWidth = if (column == settings.sortingColumn) SortIndicatorSize + Spacing.verySmall else 0.dp
            val headerWidth = with(density) {
                textMeasurer.measure(columnTitle, headerTextStyle).size.width.toDp() + Spacing.small + sortIndicatorWidth
            }
            if (column == Tags) {
                val contentWidth = tags.maxOfOrNull { characterTags ->
                    getTagsLayoutWidth(characterTags, settings.isUsingCompactMode) { tag ->
                        with(density) { textMeasurer.measure(tag, tagTextStyle).size.width.toDp() }
                    }
                } ?: 0.dp
                val skeletonWidth = if (column.dataSource != null && isAnyLoading()) {
                    TagSkeletonWidth
                } else {
                    0.dp
                }
                val tagsWidth = maxOf(contentWidth, skeletonWidth)
                (tagsWidth + extraSize).coerceAtLeast(headerWidth)
            } else {
                val contentWidth = measure(texts, detailTexts, extraSize)
                val isColumnLoading = column.dataSource?.let { isSourceLoading } ?: isEsiLoading
                val skeletonWidth = if (isColumnLoading) {
                    when (column.skeletonType) {
                        ColumnSkeletonType.Character -> {
                            val isShowingDetail = !settings.isUsingCompactMode &&
                                    settings.isShowingTickersWithCharacter &&
                                    (settings.isShowingCorpWithCharacter || settings.isShowingAllianceWithCharacter)
                            if (isShowingDetail) CharacterSkeletonDetailWidth + extraSize else 0.dp
                        }
                        ColumnSkeletonType.PortraitAndText -> {
                            val isShowingText = column != Faction || settings.isShowingFactionName
                            settings.portraitSize +
                                    (if (isShowingText) Spacing.small + SkeletonTextWidth else 0.dp) +
                                    Spacing.small
                        }
                        ColumnSkeletonType.Text,
                        ColumnSkeletonType.TextWithDetail -> SkeletonTextWidth + Spacing.small
                        ColumnSkeletonType.ShortText -> ShortTextSkeletonWidth + Spacing.small
                        ColumnSkeletonType.Icon -> LikelihoodIconWidth + Spacing.small
                        ColumnSkeletonType.IconWithOptionalText -> {
                            val isShowingText = when (column) {
                                FleetCommander -> settings.isShowingFleetCommanderScore
                                AwoxKills -> settings.isShowingAwoxCount
                                Bait -> settings.isShowingBaitCount
                                Ganker -> settings.isShowingGankerCount
                                else -> false
                            }
                            LikelihoodIconWidth +
                                    (if (isShowingText) Spacing.small + optionalTextSkeletonWidth else 0.dp) +
                                    Spacing.small
                        }
                        ColumnSkeletonType.Chart,
                        ColumnSkeletonType.Tags,
                        ColumnSkeletonType.Portraits -> 0.dp
                    }
                } else {
                    0.dp
                }
                maxOf(contentWidth, skeletonWidth).coerceAtLeast(headerWidth)
            }
        }
    }
    return ColumnWidths(map)
}

fun sortCharacters(
    characters: List<CharacterItem>,
    settings: IntelScannerSettings,
): List<CharacterItem> {
    val column = settings.sortingColumn ?: return characters
    val direction = if (settings.sortingDirection == IntelScannerSortDirection.Ascending) 1 else -1
    return characters.sortedWith { first, second ->
        val isFirstLoading = first.isLoading(column)
        val isSecondLoading = second.isLoading(column)
        if (isFirstLoading != isSecondLoading) {
            return@sortedWith if (isFirstLoading) 1 else -1
        }
        val firstValue = first.getSortingValue(column, settings)
        val secondValue = second.getSortingValue(column, settings)
        when {
            firstValue == null && secondValue == null -> first.name.compareTo(second.name, ignoreCase = true)
            firstValue == null -> 1
            secondValue == null -> -1
            else -> {
                val comparison = when (firstValue) {
                    is Number if secondValue is Number -> firstValue.toDouble().compareTo(secondValue.toDouble()) * direction
                    is String if secondValue is String -> firstValue.compareTo(secondValue, ignoreCase = true) * direction
                    is AssociateSortingValue if secondValue is AssociateSortingValue -> {
                        (firstValue.size.compareTo(secondValue.size) * direction)
                            .takeIf { it != 0 }
                            ?: compareValues(firstValue.name, secondValue.name)
                    }
                    else -> 0
                }
                comparison.takeIf { it != 0 }
                    ?: first.name.compareTo(second.name, ignoreCase = true)
            }
        }
    }
}

private fun CharacterItem.getSortingValue(column: ColumnType, settings: IntelScannerSettings): Any? {
    if (column == Character) return name
    val state = state as? CharacterState.Loaded ?: return null
    val zkillboard = state.zKillboardDetails
    return when (column) {
        Character -> state.characterName
        Age -> -state.characterDetails.birthday.epochSecond
        Corporation -> state.corporationName
        Alliance -> state.allianceName
        Faction -> state.factionName
        Security -> state.securityStatus
        Danger -> zkillboard?.dangerRatio
        Gang -> zkillboard?.gangRatio
        SoloKills -> zkillboard?.soloKills
        KillDeathRatio -> {
            val destroyed = zkillboard?.shipsDestroyed ?: return null
            val lost = zkillboard.shipsLost ?: return null
            when {
                lost == 0 && destroyed > 0 -> Double.POSITIVE_INFINITY
                lost == 0 -> 0.0
                else -> destroyed.toDouble() / lost
            }
        }
        GroupSize -> zkillboard?.groupSizes?.sumOf { (it.shipsDestroyed ?: 0) + (it.shipsLost ?: 0) }
        Cyno -> zkillboard?.cyno?.count
        Space -> zkillboard?.space?.sumOf { (it.shipsDestroyed ?: 0) + (it.shipsLost ?: 0) }
        Isk -> zkillboard?.isk?.sumOf { (it.shipsDestroyed ?: 0) + (it.shipsLost ?: 0) }
        Tags -> zkillboard?.tags?.joinToString(", ") { it.name }?.ifBlank { null }
        FleetCommander -> zkillboard?.fleetCommander?.score ?: return null
        TopShips -> zkillboard?.topShips?.take(settings.topShipsCount)?.sumOf { it.kills + it.losses }
        RecentShips -> zkillboard?.recentShips?.take(settings.recentShipsCount)?.sumOf { it.kills + it.losses }
        Groups -> zkillboard?.let {
            getZkillAffiliates(
                affiliates = it.affiliates,
                maximumCount = settings.affiliatesCount,
                minimumKills = settings.affiliatesMinimumKills,
            ).sumOf { affiliate -> affiliate.sharedKills }
        }
        Associates -> state.associateGroup?.let { AssociateSortingValue(it.name, it.size) }
        AwoxKills -> zkillboard?.awoxCount ?: 0
        Bait -> zkillboard?.baitCount ?: 0
        Ganker -> zkillboard?.gankerCount ?: 0
    }
}

private data class AssociateSortingValue(
    val name: String?,
    val size: Int,
)

@Composable
fun CharacterRow(
    rowIndex: Int,
    character: CharacterItem,
    timestamp: Instant,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .hoverBackground()
            .padding(vertical = Spacing.verySmall)
            .wrapContentWidth(align = Alignment.Start, unbounded = true)
            .width(getTableWidth(settings, columnWidths))
    ) {
        when (val state = character.state) {
            CharacterState.Loading -> LoadingRow(rowIndex, character.name, settings, columnWidths)
            CharacterState.NotFound -> StatusRow(character.name, timestamp, settings, character.state, columnWidths, status = stringResource(Res.string.intel_scanner_character_not_found))
            CharacterState.Error -> StatusRow(character.name, timestamp, settings, character.state, columnWidths, status = stringResource(Res.string.intel_scanner_character_load_failed))
            is CharacterState.Loaded -> LoadedRow(rowIndex, character.name, timestamp, settings, state, columnWidths)
        }
    }
}

@Composable
private fun LoadingRow(
    rowIndex: Int,
    characterName: String,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths
) {
    settings.enabledColumns.forEach { column ->
        ColumnSkeleton(column, rowIndex, characterName, settings, columnWidths)
    }
}

@Composable
private fun StatusRow(
    name: String,
    timestamp: Instant,
    settings: IntelScannerSettings,
    state: CharacterState,
    columnWidths: ColumnWidths,
    status: String
) {
    var isStatusShown = false
    settings.enabledColumns.forEach { column ->
        when {
            column == Character -> CharacterCell(name, timestamp, settings, state, columnWidths)
            !isStatusShown -> {
                TextCell(status, columnWidths[column])
                isStatusShown = true
            }
            else -> Box(Modifier.width(columnWidths[column]))
        }
    }
}

@Composable
private fun LoadedRow(
    rowIndex: Int,
    name: String,
    timestamp: Instant,
    settings: IntelScannerSettings,
    state: CharacterState.Loaded,
    columnWidths: ColumnWidths
) {
    settings.enabledColumns.forEach { column ->
        if (state.isLoading(column)) {
            ColumnSkeleton(column, rowIndex, name, settings, columnWidths)
        } else when (column) {
            Character -> CharacterCell(name, timestamp, settings, state, columnWidths)
            Corporation -> CorporationCell(state.corporationName, state.corporationTicker, state.corporationId, settings, columnWidths)
            Alliance -> AllianceCell(state.allianceName, state.allianceTicker, state.allianceId, settings, columnWidths)
            Faction -> FactionEnlistmentCell(state.factionName, state.factionId, settings, columnWidths)
            Security -> SecurityCell(state.securityStatus, columnWidths)
            Danger -> DangerCell(state.zKillboardDetails?.dangerRatio, columnWidths)
            Age, Gang, SoloKills, KillDeathRatio -> TextColumnCell(column, settings, state, columnWidths)
            AwoxKills -> AwoxCell(state.zKillboardDetails?.awoxCount ?: 0, settings, columnWidths)
            Associates -> AssociatesCell(state, columnWidths)
            Bait -> BaitCell(state.zKillboardDetails?.baitCount ?: 0, settings, columnWidths)
            Ganker -> GankerCell(state.zKillboardDetails?.gankerCount ?: 0, settings, columnWidths)
            Tags -> TagsCell(state.zKillboardDetails?.tags, settings, columnWidths)
            FleetCommander -> FleetCommanderCell(state.zKillboardDetails?.fleetCommander, settings, columnWidths)
            GroupSize -> GroupSizeCell(state.zKillboardDetails?.groupSizes, settings, columnWidths)
            Cyno -> CynoCell(state.zKillboardDetails?.cyno, columnWidths)
            Space -> SpaceCell(state.zKillboardDetails?.space, settings, columnWidths)
            Isk -> IskCell(state.zKillboardDetails?.isk, settings, columnWidths)
            TopShips -> UsedShipsCell(state.zKillboardDetails?.topShips, settings.topShipsCount, settings.portraitSize, columnWidths[TopShips])
            RecentShips -> UsedShipsCell(state.zKillboardDetails?.recentShips, settings.recentShipsCount, settings.portraitSize, columnWidths[RecentShips])
            Groups -> AffiliatesCell(state.zKillboardDetails?.affiliates, settings, columnWidths)
        }
    }
}

private fun CharacterState.Loaded.isLoading(column: ColumnType): Boolean =
    column.dataSource != null && isZKillboardLoading

private fun CharacterItem.isLoading(column: ColumnType): Boolean = when (val state = state) {
    CharacterState.Loading -> true
    is CharacterState.Loaded -> state.isLoading(column)
    CharacterState.Error, CharacterState.NotFound -> false
}

@Composable
private fun ColumnSkeleton(
    column: ColumnType,
    rowIndex: Int,
    characterName: String,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val color = RiftTheme.colors.backgroundPrimaryLight.copy(alpha = alpha)
    val width = columnWidths[column]
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val optionalTextSkeletonWidth = with(density) {
        textMeasurer.measure(OptionalTextSkeletonSample, RiftTheme.typography.bodyPrimary).size.width.toDp()
    }

    @Composable
    fun Bone(modifier: Modifier, cornerRadius: Dp = 3.dp) {
        Box(modifier.background(color, RoundedCornerShape(cornerRadius)))
    }

    @Composable
    fun SquareBone() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(settings.portraitSize),
        ) {
            Bone(
                modifier = Modifier.size(settings.portraitSize - 2.dp),
                cornerRadius = 2.dp,
            )
        }
    }

    @Composable
    fun TextBones(showDetail: Boolean) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.verySmall)) {
            Bone(Modifier.size(width = minOf(SkeletonTextWidth, width - Spacing.small), height = 12.dp))
            if (showDetail) {
                Bone(Modifier.size(width = minOf(38.dp, width - Spacing.small), height = 8.dp))
            }
        }
    }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.width(width),
    ) {
        when (column.skeletonType) {
            ColumnSkeletonType.Character -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                Row {
                    val portraitCount = 1 +
                            (if (settings.isShowingCorpWithCharacter) 1 else 0) +
                            (if (settings.isShowingAllianceWithCharacter) 1 else 0)
                    repeat(portraitCount) {
                        SquareBone()
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.verySmall)) {
                    Text(
                        text = characterName,
                        style = RiftTheme.typography.bodyPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (
                        !settings.isUsingCompactMode &&
                        settings.isShowingTickersWithCharacter &&
                        (settings.isShowingCorpWithCharacter || settings.isShowingAllianceWithCharacter)
                    ) {
                        Bone(Modifier.size(width = CharacterSkeletonDetailWidth, height = 8.dp))
                    }
                }
                Bone(Modifier.size(width = 12.dp, height = 10.dp), cornerRadius = 2.dp)
            }
            ColumnSkeletonType.PortraitAndText -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                SquareBone()
                if (column != Faction || settings.isShowingFactionName) {
                    TextBones(showDetail = !settings.isUsingCompactMode && column != Faction)
                }
            }
            ColumnSkeletonType.Text -> TextBones(showDetail = false)
            ColumnSkeletonType.ShortText -> Bone(Modifier.size(width = ShortTextSkeletonWidth, height = 12.dp))
            ColumnSkeletonType.TextWithDetail -> TextBones(
                showDetail = !settings.isUsingCompactMode && when (column) {
                    Gang -> settings.isShowingAverageGangSize
                    KillDeathRatio -> settings.isShowingKillDeathTotals
                    else -> false
                },
            )
            ColumnSkeletonType.Chart -> ChartSkeleton(column, rowIndex, settings.portraitSize, color)
            ColumnSkeletonType.Tags -> Row(horizontalArrangement = Arrangement.spacedBy(TagHorizontalSpacing)) {
                repeat(TagSkeletonCounts[rowIndex % TagSkeletonCounts.size]) {
                    Bone(Modifier.size(width = TagSkeletonWidth, height = 14.dp), cornerRadius = 6.dp)
                }
            }
            ColumnSkeletonType.Icon -> Bone(Modifier.size(width = 12.dp, height = 10.dp), cornerRadius = 2.dp)
            ColumnSkeletonType.IconWithOptionalText -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                Bone(Modifier.size(width = 12.dp, height = 10.dp), cornerRadius = 2.dp)
                val isShowingText = when (column) {
                    FleetCommander -> settings.isShowingFleetCommanderScore
                    AwoxKills -> settings.isShowingAwoxCount
                    Bait -> settings.isShowingBaitCount
                    Ganker -> settings.isShowingGankerCount
                    else -> false
                }
                if (isShowingText) Bone(Modifier.size(width = optionalTextSkeletonWidth, height = 12.dp))
            }
            ColumnSkeletonType.Portraits -> {
                val count = when (column) {
                    TopShips -> getSkeletonPortraitCount(settings.topShipsCount, rowIndex)
                    RecentShips -> getSkeletonPortraitCount(settings.recentShipsCount, rowIndex)
                    Groups -> getSkeletonPortraitCount(settings.affiliatesCount, rowIndex + 1)
                    else -> 1
                }
                Row {
                    repeat(count) {
                        SquareBone()
                    }
                }
            }
        }
    }
}

private val CharacterSkeletonDetailWidth = 38.dp
private val SkeletonTextWidth = 56.dp
private val ShortTextSkeletonWidth = 28.dp
private val ContactLabelSize = 16.dp
private val LikelihoodIconWidth = 12.dp
private val TagSkeletonWidth = 42.dp
private const val OptionalTextSkeletonSample = "00"
private val TagSkeletonCounts = listOf(0, 0, 1, 0, 1)

private val ChartSkeletonProfiles = listOf(
    listOf(0.35f, 0.62f, 0.88f, 1f, 0.76f, 0.48f, 0.28f, 0.18f, 0.12f, 0.08f),
    listOf(0.82f, 1f, 0.7f, 0.46f, 0.58f, 0.32f, 0.2f, 0.3f, 0.14f, 0.1f),
    listOf(0.3f, 0.5f, 0.74f, 0.58f, 1f, 0.8f, 0.42f, 0.24f, 0.16f, 0.1f),
    listOf(1f, 0.78f, 0.56f, 0.68f, 0.4f, 0.26f, 0.34f, 0.18f, 0.12f, 0.08f),
)

private const val GroupSizeSkeletonBarCount = 7

private fun getSkeletonPortraitCount(maximumCount: Int, rowIndex: Int): Int {
    return (maximumCount - rowIndex % 3).coerceAtLeast(1)
}

@Composable
private fun ChartSkeleton(
    column: ColumnType,
    rowIndex: Int,
    height: Dp,
    color: Color,
) {
    val profile = ChartSkeletonProfiles[rowIndex % ChartSkeletonProfiles.size]
    val barCount = when (column) {
        GroupSize -> GroupSizeSkeletonBarCount
        Space -> SpaceType.entries.size
        Isk -> IskType.entries.size
        else -> 0
    }
    val barWidth = if (column == Space) SpaceBarWidth else GroupSizeBarWidth
    val barSpacing = if (column == Space) SpaceBarSpacing else GroupSizeBarSpacing
    val chartHeight = if (column == Space) height - SpaceIconSize - 1.dp else height

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        modifier = Modifier.height(height),
    ) {
        repeat(barCount) { index ->
            if (column == Space) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.size(width = barWidth, height = height),
                ) {
                    Box(
                        Modifier
                            .size(width = barWidth, height = chartHeight * profile[index])
                            .background(color, RoundedCornerShape(1.dp)),
                    )
                    Box(
                        Modifier
                            .padding(top = 1.dp)
                            .size(SpaceIconSize)
                            .background(color, RoundedCornerShape(2.dp)),
                    )
                }
            } else {
                Box(
                    Modifier
                        .size(width = barWidth, height = height * profile[index])
                        .background(color, RoundedCornerShape(1.dp)),
                )
            }
        }
    }
}

private val TagHorizontalPadding = Spacing.small
private val TagHorizontalSpacing = Spacing.verySmall

@Composable
private fun SmallPill(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = RiftTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = TagHorizontalPadding),
    ) {
        Text(
            text = text,
            style = RiftTheme.typography.detailPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun getTagTooltip(tag: CharacterTag): String {
    val count = tag.count ?: 0
    return when (tag.name) {
        "FC" -> stringResource(Res.string.intel_scanner_tag_fleet_commander_tooltip, formatNumber(count))
        "LOGI" -> pluralStringResource(Res.plurals.intel_scanner_tag_logistics_tooltip, count, formatNumber(count))
        "BLOPS" -> pluralStringResource(Res.plurals.intel_scanner_tag_black_ops_tooltip, count, formatNumber(count))
        "SUPER" -> pluralStringResource(Res.plurals.intel_scanner_tag_supercarrier_tooltip, count, formatNumber(count))
        "TITAN" -> pluralStringResource(Res.plurals.intel_scanner_tag_titan_tooltip, count, formatNumber(count))
        "CAPITAL" -> pluralStringResource(Res.plurals.intel_scanner_tag_capital_tooltip, count, formatNumber(count))
        "HIGHSEC" -> pluralStringResource(Res.plurals.intel_scanner_tag_highsec_tooltip, count, formatNumber(count))
        "LOWSEC" -> pluralStringResource(Res.plurals.intel_scanner_tag_lowsec_tooltip, count, formatNumber(count))
        "POCHVEN" -> pluralStringResource(Res.plurals.intel_scanner_tag_pochven_tooltip, count, formatNumber(count))
        "GANKER" -> pluralStringResource(Res.plurals.intel_scanner_tag_ganker_tooltip, count, formatNumber(count))
        "CYNO" -> pluralStringResource(Res.plurals.intel_scanner_tag_cyno_tooltip, count, formatNumber(count))
        "BAIT" -> pluralStringResource(Res.plurals.intel_scanner_tag_bait_tooltip, count, formatNumber(count))
        "ROOKIE" -> stringResource(Res.string.intel_scanner_tag_rookie_tooltip)
        else -> stringResource(Res.string.intel_scanner_tag_default_tooltip)
    }
}

private fun getTagsLayoutWidth(
    tags: List<String>,
    isUsingCompactMode: Boolean,
    measureTextWidth: (String) -> Dp,
): Dp {
    val tagWidths = tags.map { tag -> measureTextWidth(tag) + TagHorizontalPadding * 2 }

    fun measureLine(widths: List<Dp>): Dp {
        val pillsWidth = widths.fold(0.dp) { width, tagWidth -> width + tagWidth }
        return pillsWidth + TagHorizontalSpacing * (widths.size - 1).coerceAtLeast(0)
    }

    if (isUsingCompactMode || tagWidths.size < 2) return measureLine(tagWidths)

    return (1 until tagWidths.size).minOf { splitIndex ->
        maxOf(
            measureLine(tagWidths.take(splitIndex)),
            measureLine(tagWidths.drop(splitIndex)),
        )
    }
}

@Composable
private fun TagsCell(
    tags: List<CharacterTag>?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    if (tags.isNullOrEmpty()) {
        Text(
            text = "-",
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.width(columnWidths[Tags]),
        )
    } else {
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        val textStyle = RiftTheme.typography.detailPrimary
        val tagsWidth = remember(tags, settings.isUsingCompactMode, density, textStyle) {
            getTagsLayoutWidth(tags.map { it.name }, settings.isUsingCompactMode) { tag ->
                with(density) { textMeasurer.measure(tag, textStyle).size.width.toDp() }
            }
        }
        Box(modifier = Modifier.width(columnWidths[Tags])) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(TagHorizontalSpacing),
                verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                modifier = Modifier.width(tagsWidth),
            ) {
                tags.forEach { tag ->
                    RiftTooltipArea(text = getTagTooltip(tag)) {
                        SmallPill(tag.displayName.resolve())
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterCell(
    name: String,
    timestamp: Instant,
    settings: IntelScannerSettings,
    state: CharacterState,
    columnWidths: ColumnWidths
) {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.width(columnWidths[Character])
        ) {
            when (state) {
                is CharacterState.Loaded -> {
                    Row {
                        ClickableCharacter(state.characterId) {
                            CharacterTooltip(state.characterDetails) {
                                DynamicCharacterPortraitStandings(
                                    characterId = state.characterId,
                                    size = settings.portraitSize,
                                    standingLevel = state.standingLevel,
                                    isAnimated = true,
                                    enterTimestamp = timestamp,
                                )
                            }
                        }
                        if (settings.isShowingCorpWithCharacter) {
                            ClickableCorporation(state.corporationId) {
                                RiftTooltipArea(text = state.corporationName) {
                                    AsyncCorporationLogo(
                                        corporationId = state.corporationId,
                                        size = 32,
                                        modifier = Modifier.size(settings.portraitSize),
                                    )
                                }
                            }
                        }
                        if (settings.isShowingAllianceWithCharacter) {
                            ClickableAlliance(state.allianceId) {
                                RiftTooltipArea(text = state.allianceName ?: stringResource(Res.string.intel_scanner_character_no_alliance)) {
                                    AsyncAllianceLogo(
                                        allianceId = state.allianceId,
                                        size = 32,
                                        modifier = Modifier.size(settings.portraitSize),
                                    )
                                }
                            }
                        }
                    }
                }
                CharacterState.Loading -> {
                    ColumnSkeleton(Character, 0, name, settings, columnWidths)
                }
                CharacterState.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.width(settings.portraitSize),
                    ) {}
                }
                CharacterState.NotFound -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.width(settings.portraitSize),
                    ) {}
                }
            }

            val isCorpVisible = settings.isShowingTickersWithCharacter && settings.isShowingCorpWithCharacter
            val isAllianceVisible = settings.isShowingTickersWithCharacter && settings.isShowingAllianceWithCharacter
            val corporationTicker = (state as? CharacterState.Loaded)?.corporationTicker?.takeIf { isCorpVisible }
            val allianceTicker = (state as? CharacterState.Loaded)?.allianceTicker?.takeIf { isAllianceVisible }
            val tickersText = listOfNotNull(corporationTicker, allianceTicker).joinToString(" ")
            if (settings.isUsingCompactMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = (state as? CharacterState.Loaded)?.characterName ?: name,
                        style = RiftTheme.typography.bodyPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (tickersText.isNotBlank()) {
                        Text(
                            text = " $tickersText",
                            style = RiftTheme.typography.bodySecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            } else {
                Column {
                    Text(
                        text = (state as? CharacterState.Loaded)?.characterName ?: name,
                        style = RiftTheme.typography.bodyPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (tickersText.isNotBlank()) {
                        Text(
                            text = tickersText,
                            style = RiftTheme.typography.detailSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            if (state is CharacterState.Loaded) {
                FlagIcon(state.standingLevel)
                ContactLabelTag(state.characterDetails)
            }
        }
    }
}

@Composable
private fun CorporationCell(
    corporationName: String,
    corporationTicker: String,
    corporationId: Int,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.width(columnWidths[Corporation])
    ) {
        ClickableCorporation(corporationId) {
            RiftTooltipArea(text = corporationName) {
                AsyncCorporationLogo(
                    corporationId = corporationId,
                    size = 64,
                    modifier = Modifier.size(settings.portraitSize),
                )
            }
        }

        OrganizationText(
            name = corporationName.takeIf { settings.isShowingCorporationName },
            ticker = corporationTicker.takeIf { settings.isShowingCorporationTicker },
            isUsingCompactMode = settings.isUsingCompactMode,
        )
    }
}

@Composable
private fun AllianceCell(
    allianceName: String?,
    allianceTicker: String?,
    allianceId: Int?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.width(columnWidths[Alliance])
    ) {
        if (allianceId != null) {
            ClickableAlliance(allianceId) {
                RiftTooltipArea(text = allianceName.orEmpty()) {
                    AsyncAllianceLogo(
                        allianceId = allianceId,
                        size = 64,
                        modifier = Modifier.size(settings.portraitSize),
                    )
                }
            }
        }

        OrganizationText(
            name = (allianceName ?: "-").takeIf { settings.isShowingAllianceName },
            ticker = allianceTicker.takeIf { settings.isShowingAllianceTicker },
            isUsingCompactMode = settings.isUsingCompactMode,
        )
    }
}

@Composable
private fun OrganizationText(
    name: String?,
    ticker: String?,
    isUsingCompactMode: Boolean,
) {
    if (isUsingCompactMode) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            name?.let {
                Text(
                    text = it,
                    style = RiftTheme.typography.bodyPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ticker?.let {
                Text(
                    text = if (name != null) " $it" else it,
                    style = RiftTheme.typography.bodySecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    } else {
        Column {
            name?.let {
                Text(
                    text = it,
                    style = RiftTheme.typography.bodyPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ticker?.let {
                Text(
                    text = it,
                    style = RiftTheme.typography.detailSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun FactionEnlistmentCell(
    factionName: String?,
    factionId: Int?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.width(columnWidths[Faction])
    ) {
        if (factionId != null && !settings.isShowingFactionName && factionName != null) {
            RiftTooltipArea(factionName) {
                AsyncCorporationLogo(
                    corporationId = factionId,
                    size = 64,
                    modifier = Modifier.size(settings.portraitSize),
                )
            }
        } else if (factionId != null) {
            AsyncCorporationLogo(
                corporationId = factionId,
                size = 64,
                modifier = Modifier.size(settings.portraitSize),
            )
        }

        if (settings.isShowingFactionName || factionId == null) {
            Text(
                text = factionName ?: "-",
                style = RiftTheme.typography.bodyPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TextCell(text: String, width: Dp) {
    Text(
        text = text,
        style = RiftTheme.typography.bodyPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(width),
    )
}

@Composable
private fun SecurityCell(securityStatus: Double?, columnWidths: ColumnWidths) {
    val security = securityStatus?.roundSecurity()
    Text(
        text = security?.toString() ?: "-",
        style = RiftTheme.typography.bodyPrimary.copy(
            color = security?.let { SecurityColors[it] } ?: RiftTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1,
        modifier = Modifier.width(columnWidths[Security]),
    )
}

@Composable
private fun DangerCell(dangerRatio: Int?, columnWidths: ColumnWidths) {
    val security = dangerRatio?.let { 1.0 - (it.coerceIn(0, 100) / 100.0) }
    Text(
        text = dangerRatio?.let { NumberFormat.getPercentInstance().format(it / 100.0) } ?: "-",
        style = RiftTheme.typography.bodyPrimary.copy(
            color = security?.let { SecurityColors[it] } ?: RiftTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1,
        modifier = Modifier.width(columnWidths[Danger]),
    )
}

@Composable
private fun TextColumnCell(
    column: ColumnType,
    settings: IntelScannerSettings,
    state: CharacterState.Loaded,
    columnWidths: ColumnWidths,
) {
    val text = column.textContent(settings, state)
    val secondaryText = column.secondaryTextContent(settings, state)
    val detailText = column.detailTextContent(settings, state)
    val tooltipText = column.tooltipTextContent(state)
    RiftTooltipArea(
        text = tooltipText,
        modifier = Modifier.width(columnWidths[column]),
    ) {
        Column {
            Text(
                text = buildAnnotatedString {
                    text?.let(::append)
                    secondaryText?.let {
                        if (length > 0) append(" ")
                        withStyle(SpanStyle(color = RiftTheme.colors.textSecondary)) {
                            append(it)
                        }
                    }
                    if (length == 0) append("-")
                },
                style = RiftTheme.typography.bodyPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (detailText != null) {
                Text(
                    text = detailText,
                    style = RiftTheme.typography.detailSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AssociatesCell(
    state: CharacterState.Loaded,
    columnWidths: ColumnWidths,
) {
    val group = state.associateGroup
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.width(columnWidths[Associates]),
    ) {
        group?.name?.let { SmallPill(it) }
        Text(
            text = group?.size?.toString() ?: "-",
            style = RiftTheme.typography.bodyPrimary,
            maxLines = 1,
        )
    }
}

private enum class ScanLikelihood(val label: StringResource, val icon: DrawableResource) {
    None(Res.string.intel_scanner_likelihood_none, Res.drawable.nulltohigh_12x10),
    Low(Res.string.intel_scanner_likelihood_low, Res.drawable.lowtohigh_12x10),
    Medium(Res.string.intel_scanner_likelihood_medium, Res.drawable.hightolow_12x10),
    High(Res.string.intel_scanner_likelihood_high, Res.drawable.hightonull_12x10),
}

@Composable
private fun FleetCommanderCell(
    fleetCommander: ZkillboardDetails.FleetCommander?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    val likelihood = when (fleetCommander?.score) {
        in 0..34 -> ScanLikelihood.None
        in 35..59 -> ScanLikelihood.Low
        in 60..99 -> ScanLikelihood.Medium
        null -> null
        else -> ScanLikelihood.High
    }
    val icon = likelihood?.icon
    RiftTooltipArea(
        text = listOfNotNull(
            likelihood?.let { stringResource(Res.string.intel_scanner_fleet_commander_likelihood_tooltip, stringResource(it.label), formatNumber(fleetCommander?.score ?: 0)) },
            fleetCommander?.monitorAppearances?.let { pluralStringResource(Res.plurals.intel_scanner_monitor_appearances, it, formatNumber(it)) },
            fleetCommander?.commandShipAppearances?.let { pluralStringResource(Res.plurals.intel_scanner_command_ship_appearances, it, formatNumber(it)) },
            fleetCommander?.largeFleetAppearances?.let { pluralStringResource(Res.plurals.intel_scanner_large_fleet_appearances, it, formatNumber(it)) },
        ).joinToString("\n"),
        modifier = Modifier.width(columnWidths[FleetCommander]),
    ) {
        if (icon != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = stringResource(Res.string.intel_scanner_fleet_commander_likelihood_description, stringResource(likelihood.label)),
                    modifier = Modifier
                        .rotate(180f)
                        .size(width = 12.dp, height = 10.dp),
                )
                if (settings.isShowingFleetCommanderScore) {
                    Text(
                        text = "${fleetCommander?.score}",
                        style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.textSecondary),
                        maxLines = 1,
                    )
                }
            }
        } else {
            Text(
                text = "-",
                style = RiftTheme.typography.bodyPrimary,
            )
        }
    }
}

@Composable
private fun AwoxCell(
    awoxCount: Int,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    LikelihoodCountCell(
        title = stringResource(Res.string.intel_scanner_column_awox_name),
        count = awoxCount,
        isShowingCount = settings.isShowingAwoxCount,
        column = AwoxKills,
        columnWidths = columnWidths,
    )
}

@Composable
private fun BaitCell(
    baitCount: Int,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    LikelihoodCountCell(
        title = stringResource(Res.string.intel_scanner_column_bait_name),
        count = baitCount,
        isShowingCount = settings.isShowingBaitCount,
        column = Bait,
        columnWidths = columnWidths,
    )
}

@Composable
private fun GankerCell(
    gankerCount: Int,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    LikelihoodCountCell(
        title = stringResource(Res.string.intel_scanner_ganker_name),
        count = gankerCount,
        isShowingCount = settings.isShowingGankerCount,
        column = Ganker,
        columnWidths = columnWidths,
    )
}

@Composable
private fun LikelihoodCountCell(
    title: String,
    count: Int,
    isShowingCount: Boolean,
    column: ColumnType,
    columnWidths: ColumnWidths,
) {
    val likelihood = when {
        count >= 25 -> ScanLikelihood.High
        count >= 10 -> ScanLikelihood.Medium
        count >= 5 -> ScanLikelihood.Low
        else -> ScanLikelihood.None
    }
    val icon = likelihood.icon
    RiftTooltipArea(
        text = listOf(
            stringResource(Res.string.intel_scanner_activity_likelihood_tooltip, title, stringResource(likelihood.label)),
            pluralStringResource(Res.plurals.intel_scanner_detected_count, count, formatNumber(count)),
        ).joinToString("\n"),
        modifier = Modifier.width(columnWidths[column]),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .rotate(180f)
                    .size(width = 12.dp, height = 10.dp),
            )
            if (isShowingCount) {
                Text(
                    text = "$count",
                    style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.textSecondary),
                    maxLines = 1,
                )
            }
        }
    }
}

private val GroupSizeBarWidth = 6.dp
private val GroupSizeBarSpacing = 2.dp

private data class KillLossTooltipRow(
    val label: String,
    val shipsDestroyed: Int?,
    val shipsLost: Int?,
    val icon: DrawableResource? = null,
)

@Composable
private fun KillLossTooltip(rows: List<KillLossTooltipRow>) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val labelStyle = RiftTheme.typography.bodyPrimary
    val headingStyle = RiftTheme.typography.bodySecondary
    val lossesHeading = stringResource(Res.string.intel_scanner_losses_heading)
    val killsHeading = stringResource(Res.string.intel_scanner_kills_heading)
    val shipsDestroyed = rows.map { formatNumber(it.shipsDestroyed ?: 0) }
    val shipsLost = rows.map { formatNumber(it.shipsLost ?: 0) }
    val destroyedColor = EveColors.focusBlue
    val lostColor = EveColors.cherryRed
    val headingMarkerSize = 6.dp
    val headingMarkerWidth = headingMarkerSize + Spacing.small

    fun getTextWidth(texts: List<String>, includeHeading: String? = null): Dp {
        return (texts + listOfNotNull(includeHeading)).maxOf { text ->
            val style = if (text == includeHeading) headingStyle else labelStyle
            with(density) { textMeasurer.measure(text, style).size.width.toDp() }
        }
    }

    val iconWidth = if (rows.any { it.icon != null }) 16.dp + Spacing.small else 0.dp
    val labelWidth = getTextWidth(rows.map { it.label }) + iconWidth
    val destroyedWidth = maxOf(
        getTextWidth(shipsDestroyed),
        getTextWidth(emptyList(), killsHeading) + headingMarkerWidth,
    )
    val lostWidth = maxOf(
        getTextWidth(shipsLost),
        getTextWidth(emptyList(), lossesHeading) + headingMarkerWidth,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(Spacing.large),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            Box(modifier = Modifier.width(labelWidth))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(destroyedWidth),
            ) {
                Box(
                    modifier = Modifier
                        .size(headingMarkerSize)
                        .background(destroyedColor, RoundedCornerShape(1.dp)),
                )
                Text(text = killsHeading, style = headingStyle)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(lostWidth),
            ) {
                Box(
                    modifier = Modifier
                        .size(headingMarkerSize)
                        .background(lostColor, RoundedCornerShape(1.dp)),
                )
                Text(text = lossesHeading, style = headingStyle)
            }
        }
        rows.forEachIndexed { index, row ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    modifier = Modifier.width(labelWidth),
                ) {
                    row.icon?.let {
                        Image(
                            painter = painterResource(it),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Text(text = row.label, style = labelStyle)
                }
                Text(
                    text = shipsDestroyed[index],
                    style = labelStyle,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(destroyedWidth),
                )
                Text(
                    text = shipsLost[index],
                    style = labelStyle,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(lostWidth),
                )
            }
        }
    }
}

private fun getChartWidth(
    barCount: Int,
    barWidth: Dp = GroupSizeBarWidth,
    barSpacing: Dp = GroupSizeBarSpacing,
): Dp {
    if (barCount == 0) return 0.dp
    return barWidth * barCount + barSpacing * (barCount - 1)
}

private data class KillLossChartBar(
    val shipsDestroyed: Int?,
    val shipsLost: Int?,
)

@Composable
private fun KillLossChart(
    bars: List<KillLossChartBar>,
    height: Dp,
    modifier: Modifier = Modifier,
    barWidth: Dp = GroupSizeBarWidth,
    barSpacing: Dp = GroupSizeBarSpacing,
    bottomContent: (@Composable (Int) -> Unit)? = null,
) {
    val maximumTotal = bars.maxOfOrNull { (it.shipsDestroyed ?: 0) + (it.shipsLost ?: 0) } ?: 0
    val destroyedColor = EveColors.focusBlue
    val lostColor = EveColors.cherryRed
    val separatorColor = RiftTheme.colors.windowBackground

    Row(
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        modifier = modifier,
    ) {
        bars.forEachIndexed { index, bar ->
            val shipsDestroyed = bar.shipsDestroyed ?: 0
            val shipsLost = bar.shipsLost ?: 0
            val total = shipsDestroyed + shipsLost
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier
                    .width(barWidth)
                    .height(height),
            ) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    if (total > 0 && maximumTotal > 0) {
                        val totalHeight = size.height * total / maximumTotal.toFloat()
                        val destroyedHeight = totalHeight * shipsDestroyed / total.toFloat()
                        val lostHeight = totalHeight - destroyedHeight
                        val cornerRadius = 2.dp.toPx()
                        val barShape = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = 0f,
                                    top = size.height - totalHeight,
                                    right = size.width,
                                    bottom = size.height,
                                    cornerRadius = CornerRadius(cornerRadius),
                                ),
                            )
                        }
                        clipPath(barShape) {
                            drawRect(
                                color = lostColor,
                                topLeft = Offset(0f, size.height - totalHeight),
                                size = Size(size.width, lostHeight),
                            )
                            drawRect(
                                color = destroyedColor,
                                topLeft = Offset(0f, size.height - destroyedHeight),
                                size = Size(size.width, destroyedHeight),
                            )
                            if (shipsDestroyed > 0 && shipsLost > 0) {
                                val separatorHeight = minOf(1.dp.toPx(), destroyedHeight, lostHeight)
                                drawRect(
                                    color = separatorColor,
                                    topLeft = Offset(
                                        x = 0f,
                                        y = size.height - destroyedHeight - separatorHeight / 2,
                                    ),
                                    size = Size(size.width, separatorHeight),
                                )
                            }
                        }
                    }
                }
                bottomContent?.invoke(index)
            }
        }
    }
}

@Composable
private fun CynoCell(
    cyno: ZkillboardDetails.Cyno?,
    columnWidths: ColumnWidths,
) {
    val visibleCyno = cyno?.takeIf { it.count > 0 }
    RiftTooltipArea(
        tooltip = visibleCyno?.let {
            {
                CynoTooltip(
                    rows = listOf(
                        CynoTooltipRow(stringResource(Res.string.intel_scanner_cyno_standard), it.standard, tint = null),
                        CynoTooltipRow(stringResource(Res.string.intel_scanner_cyno_covert), it.covert, tint = EveColors.cryoBlue),
                        CynoTooltipRow(stringResource(Res.string.intel_scanner_cyno_industrial), it.industrial, tint = EveColors.omegaYellow),
                    ),
                )
            }
        },
        modifier = Modifier.width(columnWidths[Cyno]),
    ) {
        if (visibleCyno != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                CynoIcon(
                    size = 22.dp,
                    tint = visibleCyno.getDominantCynoTint(),
                )
                Text(
                    text = formatNumber(visibleCyno.count),
                    style = RiftTheme.typography.bodyPrimary,
                    maxLines = 1,
                )
            }
        } else {
            Text(
                text = "-",
                style = RiftTheme.typography.bodyPrimary,
            )
        }
    }
}

private data class CynoTooltipRow(
    val label: String,
    val count: Int,
    val tint: Color?,
)

@Composable
private fun CynoTooltip(rows: List<CynoTooltipRow>) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val labelStyle = RiftTheme.typography.bodyPrimary
    val countStyle = RiftTheme.typography.bodyPrimary
    val labelWidth = with(density) {
        rows.maxOf { textMeasurer.measure(it.label, labelStyle).size.width }.toDp()
    }
    val countWidth = with(density) {
        rows.maxOf { textMeasurer.measure(formatNumber(it.count), countStyle).size.width }.toDp()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(Spacing.large),
    ) {
        rows.forEach { row ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                CynoIcon(
                    size = 16.dp,
                    tint = row.tint,
                )
                Text(
                    text = row.label,
                    style = labelStyle,
                    modifier = Modifier.width(labelWidth),
                )
                Text(
                    text = formatNumber(row.count),
                    style = countStyle,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(countWidth),
                )
            }
        }
    }
}

@Composable
private fun CynoIcon(size: Dp, tint: Color?) {
    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60_000, easing = LinearEasing),
        ),
    )
    Image(
        painter = painterResource(Res.drawable.effectcyno_22px),
        contentDescription = null,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        modifier = Modifier
            .size(size)
            .rotate(rotation),
    )
}

private fun ZkillboardDetails.Cyno.getDominantCynoTint(): Color? {
    return listOf(
        standard to null,
        covert to EveColors.cryoBlue,
        industrial to EveColors.omegaYellow,
    ).maxByOrNull { (count) -> count }?.second
}

@Composable
private fun GroupSizeCell(
    groupSizes: List<ZkillboardDetails.GroupSize>?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GroupSizeBarSpacing),
        modifier = Modifier.width(columnWidths[GroupSize]),
    ) {
        if (groupSizes.isNullOrEmpty()) {
            Text(
                text = "-",
                style = RiftTheme.typography.bodyPrimary,
            )
        } else {
            RiftTooltipArea(
                tooltip = {
                    KillLossTooltip(
                        rows = groupSizes.map { groupSize ->
                            val name = groupSize.name.removePrefix("#:").removePrefix("#")
                            KillLossTooltipRow(
                                label = stringResource(Res.string.intel_scanner_group_size_tooltip, name),
                                shipsDestroyed = groupSize.shipsDestroyed,
                                shipsLost = groupSize.shipsLost,
                            )
                        },
                    )
                },
                modifier = Modifier
                    .width(getChartWidth(groupSizes.size))
                    .height(settings.portraitSize),
            ) {
                KillLossChart(
                    bars = groupSizes.map {
                        KillLossChartBar(
                            shipsDestroyed = it.shipsDestroyed,
                            shipsLost = it.shipsLost,
                        )
                    },
                    height = settings.portraitSize,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private val IskTypes = IskType.entries.toList()

private fun getIskChartWidth(): Dp {
    return getChartWidth(IskTypes.size)
}

@Composable
private fun IskCell(
    isk: List<ZkillboardDetails.Isk>?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    val iskByType = isk.orEmpty().associateBy { it.type }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(columnWidths[Isk]),
    ) {
        RiftTooltipArea(
            tooltip = {
                KillLossTooltip(
                    rows = IskTypes.map { type ->
                        val details = iskByType[type]
                        KillLossTooltipRow(
                            label = type.displayName.resolve(),
                            shipsDestroyed = details?.shipsDestroyed,
                            shipsLost = details?.shipsLost,
                        )
                    },
                )
            },
            modifier = Modifier
                .width(getIskChartWidth())
                .height(settings.portraitSize),
        ) {
            KillLossChart(
                bars = IskTypes.map { type ->
                    val details = iskByType[type]
                    KillLossChartBar(
                        shipsDestroyed = details?.shipsDestroyed,
                        shipsLost = details?.shipsLost,
                    )
                },
                height = settings.portraitSize,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private val SpaceTypes = SpaceType.entries.toList()
private val SpaceBarWidth = 12.dp
private val SpaceBarSpacing = 2.dp
private val SpaceIconSize = 12.dp

private fun getSpaceChartWidth(): Dp {
    return getChartWidth(SpaceTypes.size, SpaceBarWidth, SpaceBarSpacing)
}

@Composable
private fun SpaceCell(
    space: List<ZkillboardDetails.Space>?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    val spaceByType = space.orEmpty().associateBy { it.type }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(columnWidths[Space]),
    ) {
        RiftTooltipArea(
            tooltip = {
                KillLossTooltip(
                    rows = SpaceTypes.map { type ->
                        val details = spaceByType[type]
                        KillLossTooltipRow(
                            label = type.displayName.resolve(),
                            shipsDestroyed = details?.shipsDestroyed,
                            shipsLost = details?.shipsLost,
                            icon = type.getIcon(),
                        )
                    },
                )
            },
            modifier = Modifier
                .width(getSpaceChartWidth())
                .height(settings.portraitSize),
        ) {
            KillLossChart(
                bars = SpaceTypes.map { type ->
                    val details = spaceByType[type]
                    KillLossChartBar(
                        shipsDestroyed = details?.shipsDestroyed,
                        shipsLost = details?.shipsLost,
                    )
                },
                height = settings.portraitSize,
                barWidth = SpaceBarWidth,
                barSpacing = SpaceBarSpacing,
                modifier = Modifier.fillMaxSize(),
            ) { index ->
                val type = SpaceTypes[index]
                Image(
                    painter = painterResource(type.getIcon()),
                    contentDescription = type.displayName.resolve(),
                    modifier = Modifier.size(SpaceIconSize),
                )
            }
        }
    }
}

private fun SpaceType.getIcon() = when (this) {
    SpaceType.Highsec -> Res.drawable.high_sec_16px
    SpaceType.Nullsec -> Res.drawable.null_sec_16px
    SpaceType.Lowsec -> Res.drawable.low_sec_16px
    SpaceType.WormholeSpace -> Res.drawable.wormholes_16px
    SpaceType.Pochven -> Res.drawable.triglavian_16
    SpaceType.Abyssal -> Res.drawable.abyssal_deadspace_16px
}

@Composable
private fun UsedShipsCell(
    ships: List<UsedShip>?,
    maxShips: Int,
    portraitSize: Dp,
    columnWidth: Dp,
) {
    val topShips = ships.orEmpty().take(maxShips)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(columnWidth),
    ) {
        if (topShips.isEmpty()) {
            Text(
                text = "-",
                style = RiftTheme.typography.bodyPrimary,
            )
        } else {
            topShips.forEach { ship ->
                ClickableShip(ship.type) {
                    RiftTooltipArea(
                        text = stringResource(Res.string.intel_scanner_ship_combat_summary, ship.type.name, pluralStringResource(Res.plurals.intel_scanner_kill_count, ship.kills, formatNumber(ship.kills)), pluralStringResource(Res.plurals.intel_scanner_ship_loss_count, ship.losses, formatNumber(ship.losses))),
                        modifier = Modifier.size(portraitSize),
                    ) {
                        AsyncTypeIcon(
                            type = ship.type,
                            modifier = Modifier.size(portraitSize),
                        )
                    }
                }
            }
        }
    }
}

private fun getZkillAffiliates(
    affiliates: List<ZkillboardDetails.Affiliate>?,
    maximumCount: Int,
    minimumKills: Int,
): List<ZkillboardDetails.Affiliate> {
    return affiliates.orEmpty()
        .filter { it.sharedKills > minimumKills }
        .take(maximumCount)
}

@Composable
private fun AffiliatesCell(
    affiliates: List<ZkillboardDetails.Affiliate>?,
    settings: IntelScannerSettings,
    columnWidths: ColumnWidths,
) {
    val filteredAffiliates = getZkillAffiliates(
        affiliates = affiliates,
        maximumCount = settings.affiliatesCount,
        minimumKills = settings.affiliatesMinimumKills,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(columnWidths[Groups]),
    ) {
        if (filteredAffiliates.isEmpty()) {
            Text(
                text = "-",
                style = RiftTheme.typography.bodyPrimary,
            )
        } else {
            filteredAffiliates.forEach { affiliate ->
                ClickableAlliance(affiliate.allianceId) {
                    RiftTooltipArea(
                        text = pluralStringResource(Res.plurals.intel_scanner_kill_count, affiliate.sharedKills, formatNumber(affiliate.sharedKills)),
                        modifier = Modifier.size(settings.portraitSize),
                    ) {
                        AsyncAllianceLogo(
                            allianceId = affiliate.allianceId,
                            size = 64,
                            modifier = Modifier.size(settings.portraitSize),
                        )
                    }
                }
            }
        }
    }
}
