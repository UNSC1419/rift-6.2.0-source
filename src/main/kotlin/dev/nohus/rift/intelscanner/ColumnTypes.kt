package dev.nohus.rift.intelscanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_scanner_age_months
import dev.nohus.rift.generated.resources.intel_scanner_age_new
import dev.nohus.rift.generated.resources.intel_scanner_age_years
import dev.nohus.rift.generated.resources.intel_scanner_average_gang_size
import dev.nohus.rift.generated.resources.intel_scanner_average_gang_size_tooltip
import dev.nohus.rift.generated.resources.intel_scanner_column_affiliates_description
import dev.nohus.rift.generated.resources.intel_scanner_column_affiliates_name
import dev.nohus.rift.generated.resources.intel_scanner_column_age_description
import dev.nohus.rift.generated.resources.intel_scanner_column_age_name
import dev.nohus.rift.generated.resources.intel_scanner_column_alliance_description
import dev.nohus.rift.generated.resources.intel_scanner_column_alliance_name
import dev.nohus.rift.generated.resources.intel_scanner_column_associates_description
import dev.nohus.rift.generated.resources.intel_scanner_column_associates_name
import dev.nohus.rift.generated.resources.intel_scanner_column_awox_description
import dev.nohus.rift.generated.resources.intel_scanner_column_awox_name
import dev.nohus.rift.generated.resources.intel_scanner_column_bait_description
import dev.nohus.rift.generated.resources.intel_scanner_column_bait_name
import dev.nohus.rift.generated.resources.intel_scanner_column_character_description
import dev.nohus.rift.generated.resources.intel_scanner_column_character_name
import dev.nohus.rift.generated.resources.intel_scanner_column_corporation_description
import dev.nohus.rift.generated.resources.intel_scanner_column_corporation_name
import dev.nohus.rift.generated.resources.intel_scanner_column_cyno_description
import dev.nohus.rift.generated.resources.intel_scanner_column_cyno_name
import dev.nohus.rift.generated.resources.intel_scanner_column_danger_description
import dev.nohus.rift.generated.resources.intel_scanner_column_danger_name
import dev.nohus.rift.generated.resources.intel_scanner_column_faction_description
import dev.nohus.rift.generated.resources.intel_scanner_column_faction_name
import dev.nohus.rift.generated.resources.intel_scanner_column_fleet_commander_description
import dev.nohus.rift.generated.resources.intel_scanner_column_fleet_commander_name
import dev.nohus.rift.generated.resources.intel_scanner_column_gang_description
import dev.nohus.rift.generated.resources.intel_scanner_column_gang_name
import dev.nohus.rift.generated.resources.intel_scanner_column_ganker_description
import dev.nohus.rift.generated.resources.intel_scanner_column_ganker_name
import dev.nohus.rift.generated.resources.intel_scanner_column_group_size_description
import dev.nohus.rift.generated.resources.intel_scanner_column_group_size_name
import dev.nohus.rift.generated.resources.intel_scanner_column_isk_description
import dev.nohus.rift.generated.resources.intel_scanner_column_isk_name
import dev.nohus.rift.generated.resources.intel_scanner_column_kill_death_ratio_description
import dev.nohus.rift.generated.resources.intel_scanner_column_kill_death_ratio_name
import dev.nohus.rift.generated.resources.intel_scanner_column_recent_ships_description
import dev.nohus.rift.generated.resources.intel_scanner_column_recent_ships_name
import dev.nohus.rift.generated.resources.intel_scanner_column_security_description
import dev.nohus.rift.generated.resources.intel_scanner_column_security_name
import dev.nohus.rift.generated.resources.intel_scanner_column_solo_kills_description
import dev.nohus.rift.generated.resources.intel_scanner_column_solo_kills_name
import dev.nohus.rift.generated.resources.intel_scanner_column_space_description
import dev.nohus.rift.generated.resources.intel_scanner_column_space_name
import dev.nohus.rift.generated.resources.intel_scanner_column_tags_description
import dev.nohus.rift.generated.resources.intel_scanner_column_tags_name
import dev.nohus.rift.generated.resources.intel_scanner_column_top_ships_description
import dev.nohus.rift.generated.resources.intel_scanner_column_top_ships_name
import dev.nohus.rift.generated.resources.intel_scanner_kill_loss_totals_tooltip
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterState
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.settings.persistence.IntelScannerColumn
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.roundSecurity
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.text.NumberFormat
import java.time.Instant
import java.time.Period
import java.time.ZoneOffset

sealed class ColumnType(
    val settingsType: IntelScannerColumn,
    val title: UiText,
    val description: UiText,
    val dataSource: ColumnDataSource? = null,
    val skeletonType: ColumnSkeletonType = ColumnSkeletonType.Text,
    val hasPortrait: Boolean = false,
    val extraContentWidth: Dp? = null,
    val textContent: @Composable (IntelScannerViewModel.IntelScannerSettings, CharacterState.Loaded) -> String? = { _, _ -> null },
    val secondaryTextContent: (IntelScannerViewModel.IntelScannerSettings, CharacterState.Loaded) -> String? = { _, _ -> null },
    val detailTextContent: @Composable (IntelScannerViewModel.IntelScannerSettings, CharacterState.Loaded) -> String? = { _, _ -> null },
    val tooltipTextContent: @Composable (CharacterState.Loaded) -> String? = { null },
) {
    object Character : ColumnType(
        settingsType = IntelScannerColumn.Character,
        title = Res.string.intel_scanner_column_character_name.toUiText(),
        description = Res.string.intel_scanner_column_character_description.toUiText(),
        hasPortrait = true,
        skeletonType = ColumnSkeletonType.Character,
        extraContentWidth = 12.dp,
        textContent = { settings, it ->
            if (settings.isUsingCompactMode) {
                buildList {
                    add(it.characterName)
                    if (settings.isShowingTickersWithCharacter && settings.isShowingCorpWithCharacter) {
                        add(it.corporationTicker)
                    }
                    if (settings.isShowingTickersWithCharacter && settings.isShowingAllianceWithCharacter) {
                        add(it.allianceTicker)
                    }
                }.joinToString(" ")
            } else {
                it.characterName
            }
        },
        detailTextContent = { settings, it ->
            if (settings.isUsingCompactMode) {
                null
            } else {
                buildList {
                    if (settings.isShowingTickersWithCharacter && settings.isShowingCorpWithCharacter) {
                        add(it.corporationTicker)
                    }
                    if (settings.isShowingTickersWithCharacter && settings.isShowingAllianceWithCharacter) {
                        add(it.allianceTicker)
                    }
                }.joinToString(" ")
            }
        },
    )
    object Age : ColumnType(
        settingsType = IntelScannerColumn.Age,
        title = Res.string.intel_scanner_column_age_name.toUiText(),
        description = Res.string.intel_scanner_column_age_description.toUiText(),
        skeletonType = ColumnSkeletonType.ShortText,
        textContent = { _, it -> formatAge(it.characterDetails.birthday) },
    )
    object Corporation : ColumnType(
        settingsType = IntelScannerColumn.Corporation,
        title = Res.string.intel_scanner_column_corporation_name.toUiText(),
        description = Res.string.intel_scanner_column_corporation_description.toUiText(),
        hasPortrait = true,
        skeletonType = ColumnSkeletonType.PortraitAndText,
        textContent = { settings, it -> it.corporationName.takeIf { settings.isShowingCorporationName } },
        secondaryTextContent = { settings, it ->
            it.corporationTicker.takeIf { settings.isUsingCompactMode && settings.isShowingCorporationTicker }
        },
        detailTextContent = { settings, it ->
            it.corporationTicker.takeIf { !settings.isUsingCompactMode && settings.isShowingCorporationTicker }
        },
    )
    object Alliance : ColumnType(
        settingsType = IntelScannerColumn.Alliance,
        title = Res.string.intel_scanner_column_alliance_name.toUiText(),
        description = Res.string.intel_scanner_column_alliance_description.toUiText(),
        hasPortrait = true,
        skeletonType = ColumnSkeletonType.PortraitAndText,
        textContent = { settings, it -> it.allianceName.takeIf { settings.isShowingAllianceName } },
        secondaryTextContent = { settings, it ->
            it.allianceTicker.takeIf { settings.isUsingCompactMode && settings.isShowingAllianceTicker }
        },
        detailTextContent = { settings, it ->
            it.allianceTicker.takeIf { !settings.isUsingCompactMode && settings.isShowingAllianceTicker }
        },
    )
    object Faction : ColumnType(
        settingsType = IntelScannerColumn.Faction,
        title = Res.string.intel_scanner_column_faction_name.toUiText(),
        description = Res.string.intel_scanner_column_faction_description.toUiText(),
        hasPortrait = true,
        skeletonType = ColumnSkeletonType.PortraitAndText,
        textContent = { settings, it -> it.factionName.takeIf { settings.isShowingFactionName } },
    )
    object Security : ColumnType(
        settingsType = IntelScannerColumn.Security,
        title = Res.string.intel_scanner_column_security_name.toUiText(),
        description = Res.string.intel_scanner_column_security_description.toUiText(),
        skeletonType = ColumnSkeletonType.ShortText,
        textContent = { _, it -> it.securityStatus?.roundSecurity()?.toString() },
    )
    object Danger : ColumnType(
        settingsType = IntelScannerColumn.Danger,
        title = Res.string.intel_scanner_column_danger_name.toUiText(),
        description = Res.string.intel_scanner_column_danger_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        textContent = { _, it -> it.zKillboardDetails?.dangerRatio?.let { NumberFormat.getPercentInstance().format(it / 100.0) } },
    )
    object Gang : ColumnType(
        settingsType = IntelScannerColumn.Gang,
        title = Res.string.intel_scanner_column_gang_name.toUiText(),
        description = Res.string.intel_scanner_column_gang_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.TextWithDetail,
        textContent = { _, it -> it.zKillboardDetails?.gangRatio?.let { NumberFormat.getPercentInstance().format(it / 100.0) } },
        secondaryTextContent = { settings, it ->
            if (settings.isUsingCompactMode && settings.isShowingAverageGangSize) {
                it.zKillboardDetails?.avgGangSize?.let { "~${formatNumber(it)}" }
            } else {
                null
            }
        },
        detailTextContent = { settings, it ->
            if (!settings.isUsingCompactMode && settings.isShowingAverageGangSize) {
                it.zKillboardDetails?.avgGangSize?.let { stringResource(Res.string.intel_scanner_average_gang_size, formatNumber(it)) }
            } else {
                null
            }
        },
        tooltipTextContent = {
            stringResource(Res.string.intel_scanner_average_gang_size_tooltip, formatNumber(it.zKillboardDetails?.avgGangSize ?: 0))
        },
    )
    object SoloKills : ColumnType(
        settingsType = IntelScannerColumn.SoloKills,
        title = Res.string.intel_scanner_column_solo_kills_name.toUiText(),
        description = Res.string.intel_scanner_column_solo_kills_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        textContent = { _, it -> it.zKillboardDetails?.soloKills?.let { formatNumber(it) } },
    )
    object KillDeathRatio : ColumnType(
        settingsType = IntelScannerColumn.KillDeathRatio,
        title = Res.string.intel_scanner_column_kill_death_ratio_name.toUiText(),
        description = Res.string.intel_scanner_column_kill_death_ratio_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.TextWithDetail,
        textContent = { _, it ->
            val shipsKilled = it.zKillboardDetails?.shipsDestroyed
            val shipsDestroyed = it.zKillboardDetails?.shipsLost
            when {
                shipsKilled == null || shipsDestroyed == null -> null
                shipsDestroyed == 0 && shipsKilled > 0 -> "∞"
                shipsDestroyed == 0 -> formatNumber(0.0, withDecimals = true)
                else -> formatNumber(shipsKilled.toDouble() / shipsDestroyed, withDecimals = true)
            }
        },
        secondaryTextContent = { settings, it ->
            if (settings.isUsingCompactMode && settings.isShowingKillDeathTotals) {
                val shipsKilled = it.zKillboardDetails?.shipsDestroyed
                val shipsDestroyed = it.zKillboardDetails?.shipsLost
                if (shipsKilled != null && shipsDestroyed != null) "${formatNumber(shipsKilled)} / ${formatNumber(shipsDestroyed)}" else null
            } else {
                null
            }
        },
        detailTextContent = { settings, it ->
            if (!settings.isUsingCompactMode && settings.isShowingKillDeathTotals) {
                val shipsKilled = it.zKillboardDetails?.shipsDestroyed
                val shipsDestroyed = it.zKillboardDetails?.shipsLost
                if (shipsKilled != null && shipsDestroyed != null) "${formatNumber(shipsKilled)} / ${formatNumber(shipsDestroyed)}" else null
            } else {
                null
            }
        },
        tooltipTextContent = {
            val shipsKilled = it.zKillboardDetails?.shipsDestroyed ?: 0
            val shipsLost = it.zKillboardDetails?.shipsLost ?: 0
            stringResource(Res.string.intel_scanner_kill_loss_totals_tooltip, formatNumber(shipsKilled), formatNumber(shipsLost))
        },
    )
    object GroupSize : ColumnType(
        settingsType = IntelScannerColumn.GroupSize,
        title = Res.string.intel_scanner_column_group_size_name.toUiText(),
        description = Res.string.intel_scanner_column_group_size_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Chart,
    )
    object Cyno : ColumnType(
        settingsType = IntelScannerColumn.Cyno,
        title = Res.string.intel_scanner_column_cyno_name.toUiText(),
        description = Res.string.intel_scanner_column_cyno_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.ShortText,
        extraContentWidth = 22.dp,
        textContent = { _, it -> it.zKillboardDetails?.cyno?.count?.let(::formatNumber) },
    )
    object Space : ColumnType(
        settingsType = IntelScannerColumn.Space,
        title = Res.string.intel_scanner_column_space_name.toUiText(),
        description = Res.string.intel_scanner_column_space_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Chart,
    )
    object Isk : ColumnType(
        settingsType = IntelScannerColumn.Isk,
        title = Res.string.intel_scanner_column_isk_name.toUiText(),
        description = Res.string.intel_scanner_column_isk_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Chart,
    )
    object Tags : ColumnType(
        settingsType = IntelScannerColumn.Tags,
        title = Res.string.intel_scanner_column_tags_name.toUiText(),
        description = Res.string.intel_scanner_column_tags_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Tags,
        textContent = { _, it -> it.zKillboardDetails?.tags?.map { tag -> tag.displayName.resolve() }?.joinToString(", ")?.ifBlank { null } },
    )
    object FleetCommander : ColumnType(
        settingsType = IntelScannerColumn.FleetCommander,
        title = Res.string.intel_scanner_column_fleet_commander_name.toUiText(),
        description = Res.string.intel_scanner_column_fleet_commander_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.IconWithOptionalText,
        extraContentWidth = 12.dp,
        textContent = { settings, it ->
            it.zKillboardDetails?.fleetCommander?.score?.toString().takeIf { settings.isShowingFleetCommanderScore }
        },
    )
    object TopShips : ColumnType(
        settingsType = IntelScannerColumn.TopShips,
        title = Res.string.intel_scanner_column_top_ships_name.toUiText(),
        description = Res.string.intel_scanner_column_top_ships_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Portraits,
    )
    object RecentShips : ColumnType(
        settingsType = IntelScannerColumn.RecentShips,
        title = Res.string.intel_scanner_column_recent_ships_name.toUiText(),
        description = Res.string.intel_scanner_column_recent_ships_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Portraits,
    )
    object Groups : ColumnType(
        settingsType = IntelScannerColumn.Groups,
        title = Res.string.intel_scanner_column_affiliates_name.toUiText(),
        description = Res.string.intel_scanner_column_affiliates_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.Portraits,
    )
    object Associates : ColumnType(
        settingsType = IntelScannerColumn.Partners,
        title = Res.string.intel_scanner_column_associates_name.toUiText(),
        description = Res.string.intel_scanner_column_associates_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.ShortText,
        textContent = { _, it -> it.associateGroup?.size?.toString() },
    )
    object AwoxKills : ColumnType(
        settingsType = IntelScannerColumn.AwoxKills,
        title = Res.string.intel_scanner_column_awox_name.toUiText(),
        description = Res.string.intel_scanner_column_awox_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.IconWithOptionalText,
        extraContentWidth = 12.dp,
        textContent = { settings, it ->
            it.zKillboardDetails?.awoxCount?.let { "$it" }.takeIf { settings.isShowingAwoxCount }
        },
    )
    object Bait : ColumnType(
        settingsType = IntelScannerColumn.Bait,
        title = Res.string.intel_scanner_column_bait_name.toUiText(),
        description = Res.string.intel_scanner_column_bait_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.IconWithOptionalText,
        extraContentWidth = 12.dp,
        textContent = { settings, it ->
            it.zKillboardDetails?.baitCount?.let { "$it" }.takeIf { settings.isShowingBaitCount }
        },
    )
    object Ganker : ColumnType(
        settingsType = IntelScannerColumn.Ganker,
        title = Res.string.intel_scanner_column_ganker_name.toUiText(),
        description = Res.string.intel_scanner_column_ganker_description.toUiText(),
        dataSource = ColumnDataSource.Zkillboard,
        skeletonType = ColumnSkeletonType.IconWithOptionalText,
        extraContentWidth = 12.dp,
        textContent = { settings, it ->
            it.zKillboardDetails?.gankerCount?.let { "$it" }.takeIf { settings.isShowingGankerCount }
        },
    )
}

enum class ColumnDataSource(val displayName: String) {
    Zkillboard("zKillboard.com"),
}

enum class ColumnSkeletonType {
    Character,
    PortraitAndText,
    Text,
    ShortText,
    TextWithDetail,
    Chart,
    Tags,
    Icon,
    IconWithOptionalText,
    Portraits,
}

val IntelScannerColumnTypes = listOf(
    ColumnType.Character,
    ColumnType.Age,
    ColumnType.Corporation,
    ColumnType.Alliance,
    ColumnType.Faction,
    ColumnType.Security,
    ColumnType.Danger,
    ColumnType.Gang,
    ColumnType.SoloKills,
    ColumnType.KillDeathRatio,
    ColumnType.GroupSize,
    ColumnType.Cyno,
    ColumnType.Space,
    ColumnType.Isk,
    ColumnType.Tags,
    ColumnType.FleetCommander,
    ColumnType.RecentShips,
    ColumnType.TopShips,
    ColumnType.Groups,
    ColumnType.Associates,
    ColumnType.AwoxKills,
    ColumnType.Bait,
    ColumnType.Ganker,
)

class ColumnWidths(private val map: Map<ColumnType, Dp>) {
    operator fun get(column: ColumnType) = map[column]!!
}

@Composable
private fun formatAge(birthday: Instant, now: Instant = Instant.now()): String {
    val period = Period.between(
        birthday.atZone(ZoneOffset.UTC).toLocalDate(),
        now.atZone(ZoneOffset.UTC).toLocalDate(),
    )
    return when {
        period.isNegative || (period.years == 0 && period.months == 0) -> stringResource(Res.string.intel_scanner_age_new)
        period.years > 0 -> pluralStringResource(Res.plurals.intel_scanner_age_years, period.years, formatNumber(period.years))
        else -> pluralStringResource(Res.plurals.intel_scanner_age_months, period.months, formatNumber(period.months))
    }
}
