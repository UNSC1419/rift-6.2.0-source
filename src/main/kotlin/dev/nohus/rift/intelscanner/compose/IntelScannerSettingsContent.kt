package dev.nohus.rift.intelscanner.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckbox
import dev.nohus.rift.compose.RiftCheckboxWithLabel
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftSliderWithLabel
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.SectionTitle
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.arrow_down_16px
import dev.nohus.rift.generated.resources.arrow_up_16px
import dev.nohus.rift.generated.resources.intel_scanner_alliance_count
import dev.nohus.rift.generated.resources.intel_scanner_corporation_count
import dev.nohus.rift.generated.resources.intel_scanner_data_source
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_celestial_bodies_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_celestial_bodies_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_celestials_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_celestials_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_corpses_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_corpses_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_drones_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_drones_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_other_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_other_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_ships_by_class_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_ships_by_class_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_ships_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_ships_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_stargates_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_stargates_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_stations_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_stations_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_structures_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_structures_name
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_wrecks_description
import dev.nohus.rift.generated.resources.intel_scanner_dscan_section_wrecks_name
import dev.nohus.rift.generated.resources.intel_scanner_ignored_count
import dev.nohus.rift.generated.resources.intel_scanner_kill_count
import dev.nohus.rift.generated.resources.intel_scanner_settings_add_ignored_characters
import dev.nohus.rift.generated.resources.intel_scanner_settings_affiliate_kill_threshold_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_alliance_limit_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_average_gang_size_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_character_alliance_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_character_column_required
import dev.nohus.rift.generated.resources.intel_scanner_settings_character_corporation_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_character_tickers_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_clear_ignored_characters
import dev.nohus.rift.generated.resources.intel_scanner_settings_columns_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_columns_title
import dev.nohus.rift.generated.resources.intel_scanner_settings_compact_mode_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_compact_mode_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_corporation_limit_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_detected_count_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_disable_column
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_limit_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_mass_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_mass_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_section_collapsed_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_sections_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_sections_title
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_ships_in_class_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_dscan_title
import dev.nohus.rift.generated.resources.intel_scanner_settings_enable_column
import dev.nohus.rift.generated.resources.intel_scanner_settings_faction_ignored
import dev.nohus.rift.generated.resources.intel_scanner_settings_faction_name_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_faction_visible
import dev.nohus.rift.generated.resources.intel_scanner_settings_fleet_commander_score_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_general_title
import dev.nohus.rift.generated.resources.intel_scanner_settings_ignore_own_characters_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_ignore_own_characters_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_ignore_positive_standings_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_ignored_characters_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_ignored_characters_title
import dev.nohus.rift.generated.resources.intel_scanner_settings_ignored_factions_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_kill_loss_totals_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_local_scan_limit_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_local_scan_title
import dev.nohus.rift.generated.resources.intel_scanner_settings_move_column_down
import dev.nohus.rift.generated.resources.intel_scanner_settings_move_column_up
import dev.nohus.rift.generated.resources.intel_scanner_settings_open_on_character_copy_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_open_on_dscan_copy_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_organization_name_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_organization_ticker_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_scan_limit_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_ship_limit_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_summary_alliances_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_summary_characters_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_summary_corporations_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_summary_description
import dev.nohus.rift.generated.resources.intel_scanner_settings_summary_factions_label
import dev.nohus.rift.generated.resources.intel_scanner_settings_summary_label
import dev.nohus.rift.generated.resources.intel_scanner_ship_count
import dev.nohus.rift.generated.resources.menu_hide
import dev.nohus.rift.generated.resources.menu_unhide
import dev.nohus.rift.generated.resources.menu_zkillboard
import dev.nohus.rift.intelscanner.ColumnDataSource
import dev.nohus.rift.intelscanner.ColumnType
import dev.nohus.rift.intelscanner.IntelScannerViewModel.IntelScannerSettings
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.settings.persistence.IntelScannerDScanSection
import dev.nohus.rift.utils.formatNumber
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun IntelScannerSettingsContent(
    settings: IntelScannerSettings,
    scannerCharactersCount: Int,
    scrollState: ScrollState,
    onAddIgnoredCharacters: () -> Unit,
    onClearIgnoredCharacters: () -> Unit,
    onIgnoredFactionChange: (factionId: Int, isIgnored: Boolean) -> Unit,
    onIsIgnoringCharactersWithPositiveStandingsChange: (Boolean) -> Unit,
    onIsIgnoringOwnCharactersChange: (Boolean) -> Unit,
    onIsOpenOnClipboardCharacterListEnabledChange: (Boolean) -> Unit,
    onIsUsingCompactModeChange: (Boolean) -> Unit,
    onMaxLocalScansChange: (Int) -> Unit,
    onMaxDScansChange: (Int) -> Unit,
    onIsShowingDScanShipMassChange: (Boolean) -> Unit,
    onIsShowingShipsInDScanShipClassesChange: (Boolean) -> Unit,
    onIsDScanSectionSelectedChange: (IntelScannerDScanSection) -> Unit,
    onIsDScanSectionCollapsedByDefaultChange: (IntelScannerDScanSection) -> Unit,
    onIsOpenOnClipboardDScanEnabledChange: (Boolean) -> Unit,
    onIsShowingSummaryChange: (Boolean) -> Unit,
    onIsShowingCharactersSummaryChange: (Boolean) -> Unit,
    onIsShowingCorporationsSummaryChange: (Boolean) -> Unit,
    onMaxCorporationsInSummaryChange: (Int) -> Unit,
    onIsShowingAlliancesSummaryChange: (Boolean) -> Unit,
    onMaxAlliancesInSummaryChange: (Int) -> Unit,
    onIsShowingFactionWarfareSummaryChange: (Boolean) -> Unit,
    onIsColumnSelectedChange: (ColumnType) -> Unit,
    onColumnsReordered: (Int, Int) -> Unit,
    onIsShowingCorpWithCharacterChange: (Boolean) -> Unit,
    onIsShowingAllianceWithCharacterChange: (Boolean) -> Unit,
    onIsShowingTickersWithCharacterChange: (Boolean) -> Unit,
    onIsShowingCorporationNameChange: (Boolean) -> Unit,
    onIsShowingCorporationTickerChange: (Boolean) -> Unit,
    onIsShowingAllianceNameChange: (Boolean) -> Unit,
    onIsShowingAllianceTickerChange: (Boolean) -> Unit,
    onIsShowingFactionNameChange: (Boolean) -> Unit,
    onIsShowingAverageGangSizeChange: (Boolean) -> Unit,
    onIsShowingKillDeathTotalsChange: (Boolean) -> Unit,
    onIsShowingFleetCommanderScoreChange: (Boolean) -> Unit,
    onIsShowingAwoxCountChange: (Boolean) -> Unit,
    onIsShowingBaitCountChange: (Boolean) -> Unit,
    onIsShowingGankerCountChange: (Boolean) -> Unit,
    onTopShipsCountChange: (Int) -> Unit,
    onRecentShipsCountChange: (Int) -> Unit,
    onAffiliatesCountChange: (Int) -> Unit,
    onAffiliatesMinimumKillsChange: (Int) -> Unit,
) {
    ScrollbarColumn(
        scrollState = scrollState,
        contentPadding = PaddingValues(),
        modifier = Modifier
            .padding(Spacing.large)
            .fillMaxSize(),
    ) {
        SectionTitle(stringResource(Res.string.intel_scanner_settings_general_title))
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.large),
        ) {
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_compact_mode_label),
                tooltip = stringResource(Res.string.intel_scanner_settings_compact_mode_description),
                isChecked = settings.isUsingCompactMode,
                onCheckedChange = onIsUsingCompactModeChange,
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                RiftSliderWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_local_scan_limit_label),
                    width = 100.dp,
                    range = 1..6,
                    currentValue = settings.maxLocalScans,
                    onValueChange = onMaxLocalScansChange,
                    getValueName = { "$it" },
                    isShowingValue = true,
                )
                Text(
                    text = stringResource(Res.string.intel_scanner_settings_scan_limit_description),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                RiftSliderWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_dscan_limit_label),
                    width = 100.dp,
                    range = 1..6,
                    currentValue = settings.maxDScans,
                    onValueChange = onMaxDScansChange,
                    getValueName = { "$it" },
                    isShowingValue = true,
                )
                Text(
                    text = stringResource(Res.string.intel_scanner_settings_scan_limit_description),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
        }

        SectionTitle(stringResource(Res.string.intel_scanner_settings_local_scan_title))
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.large),
        ) {
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_summary_label),
                tooltip = stringResource(Res.string.intel_scanner_settings_summary_description),
                isChecked = settings.isShowingSummary,
                onCheckedChange = onIsShowingSummaryChange,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = Modifier.padding(start = Spacing.large),
            ) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_summary_characters_label),
                    isChecked = settings.isShowingCharactersSummary,
                    onCheckedChange = onIsShowingCharactersSummaryChange,
                    isEnabled = settings.isShowingSummary,
                )
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_summary_factions_label),
                    isChecked = settings.isShowingFactionWarfareSummary,
                    onCheckedChange = onIsShowingFactionWarfareSummaryChange,
                    isEnabled = settings.isShowingSummary,
                )
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_summary_alliances_label),
                    isChecked = settings.isShowingAlliancesSummary,
                    onCheckedChange = onIsShowingAlliancesSummaryChange,
                    isEnabled = settings.isShowingSummary,
                )
                val allianceCountLabels = (3..30).associateWith { pluralStringResource(Res.plurals.intel_scanner_alliance_count, it, formatNumber(it)) }
                RiftSliderWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_alliance_limit_label),
                    width = 100.dp,
                    range = 3..30,
                    currentValue = settings.maxAlliancesInSummary,
                    onValueChange = onMaxAlliancesInSummaryChange,
                    getValueName = { allianceCountLabels.getValue(it) },
                    isShowingValue = true,
                    modifier = Modifier.padding(start = Spacing.large + 12.dp),
                )
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_summary_corporations_label),
                    isChecked = settings.isShowingCorporationsSummary,
                    onCheckedChange = onIsShowingCorporationsSummaryChange,
                    isEnabled = settings.isShowingSummary,
                )
                val corporationCountLabels = (3..30).associateWith { pluralStringResource(Res.plurals.intel_scanner_corporation_count, it, formatNumber(it)) }
                RiftSliderWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_corporation_limit_label),
                    width = 100.dp,
                    range = 3..30,
                    currentValue = settings.maxCorporationsInSummary,
                    onValueChange = onMaxCorporationsInSummaryChange,
                    getValueName = { corporationCountLabels.getValue(it) },
                    isShowingValue = true,
                    modifier = Modifier.padding(start = Spacing.large + 12.dp),
                )
            }
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_open_on_character_copy_label),
                isChecked = settings.isOpenOnClipboardCharacterListEnabled,
                onCheckedChange = onIsOpenOnClipboardCharacterListEnabledChange,
            )
        }

        SectionTitle(stringResource(Res.string.intel_scanner_settings_ignored_characters_title))
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.large),
        ) {
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_ignore_positive_standings_label),
                isChecked = settings.isIgnoringCharactersWithPositiveStandings,
                onCheckedChange = onIsIgnoringCharactersWithPositiveStandingsChange,
            )
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_ignore_own_characters_label),
                tooltip = if (settings.isIgnoringCharactersWithPositiveStandings) {
                    stringResource(Res.string.intel_scanner_settings_ignore_own_characters_description)
                } else null,
                isChecked = settings.isIgnoringOwnCharacters,
                onCheckedChange = onIsIgnoringOwnCharactersChange,
                isEnabled = !settings.isIgnoringCharactersWithPositiveStandings,
            )
            Text(
                text = stringResource(Res.string.intel_scanner_settings_ignored_factions_label),
                style = RiftTheme.typography.bodySecondary,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FactionWarfareFactions.forEach { faction ->
                    val isIgnored = faction.id in settings.ignoredFactionIds
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        modifier = Modifier.width(96.dp),
                    ) {
                        RiftTooltipArea(faction.name) {
                            AsyncCorporationLogo(
                                corporationId = faction.id,
                                size = 64,
                                modifier = Modifier.size(48.dp),
                            )
                        }
                        RiftButton(
                            text = if (isIgnored) stringResource(Res.string.intel_scanner_settings_faction_ignored) else stringResource(Res.string.intel_scanner_settings_faction_visible),
                            icon = if (isIgnored) Res.drawable.menu_unhide else Res.drawable.menu_hide,
                            type = ButtonType.Secondary,
                            cornerCut = ButtonCornerCut.None,
                            isCompact = true,
                            onClick = { onIgnoredFactionChange(faction.id, !isIgnored) },
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = Spacing.large, end = Spacing.large, bottom = Spacing.large)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.intel_scanner_settings_ignored_characters_description),
                    style = RiftTheme.typography.bodySecondary,
                )
                Text(
                    text = pluralStringResource(Res.plurals.intel_scanner_ignored_count, settings.ignoredCharactersCount, formatNumber(settings.ignoredCharactersCount)),
                    style = RiftTheme.typography.bodyPrimary,
                )
            }
            RiftButton(
                text = stringResource(Res.string.intel_scanner_settings_clear_ignored_characters),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.None,
                isEnabled = settings.ignoredCharactersCount > 0,
                onClick = onClearIgnoredCharacters,
            )
            RiftButton(
                text = stringResource(Res.string.intel_scanner_settings_add_ignored_characters),
                isEnabled = scannerCharactersCount > 0,
                onClick = onAddIgnoredCharacters,
            )
        }

        SectionTitle(stringResource(Res.string.intel_scanner_settings_columns_title))
        Text(
            text = stringResource(Res.string.intel_scanner_settings_columns_description),
            style = RiftTheme.typography.bodySecondary,
            modifier = Modifier.padding(horizontal = Spacing.large, vertical = Spacing.medium),
        )
        Divider(color = RiftTheme.colors.divider)
        settings.columnOrder.forEachIndexed { index, column ->
            ColumnSettingsItem(
                column = column,
                index = index,
                isEnabled = column in settings.enabledColumns,
                settings = settings,
                onIsColumnSelectedChange = onIsColumnSelectedChange,
                onColumnsReordered = onColumnsReordered,
                onIsShowingCorpWithCharacterChange = onIsShowingCorpWithCharacterChange,
                onIsShowingAllianceWithCharacterChange = onIsShowingAllianceWithCharacterChange,
                onIsShowingTickersWithCharacterChange = onIsShowingTickersWithCharacterChange,
                onIsShowingCorporationNameChange = onIsShowingCorporationNameChange,
                onIsShowingCorporationTickerChange = onIsShowingCorporationTickerChange,
                onIsShowingAllianceNameChange = onIsShowingAllianceNameChange,
                onIsShowingAllianceTickerChange = onIsShowingAllianceTickerChange,
                onIsShowingFactionNameChange = onIsShowingFactionNameChange,
                onIsShowingAverageGangSizeChange = onIsShowingAverageGangSizeChange,
                onIsShowingKillDeathTotalsChange = onIsShowingKillDeathTotalsChange,
                onIsShowingFleetCommanderScoreChange = onIsShowingFleetCommanderScoreChange,
                onIsShowingAwoxCountChange = onIsShowingAwoxCountChange,
                onIsShowingBaitCountChange = onIsShowingBaitCountChange,
                onIsShowingGankerCountChange = onIsShowingGankerCountChange,
                onTopShipsCountChange = onTopShipsCountChange,
                onRecentShipsCountChange = onRecentShipsCountChange,
                onAffiliatesCountChange = onAffiliatesCountChange,
                onAffiliatesMinimumKillsChange = onAffiliatesMinimumKillsChange,
            )
            if (index < settings.columnOrder.lastIndex) {
                Divider(color = RiftTheme.colors.divider)
            }
        }

        SectionTitle(stringResource(Res.string.intel_scanner_settings_dscan_title))
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.large),
        ) {
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_dscan_mass_label),
                tooltip = stringResource(Res.string.intel_scanner_settings_dscan_mass_description),
                isChecked = settings.isShowingDScanShipMass,
                onCheckedChange = onIsShowingDScanShipMassChange,
            )
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_open_on_dscan_copy_label),
                isChecked = settings.isOpenOnClipboardDScanEnabled,
                onCheckedChange = onIsOpenOnClipboardDScanEnabledChange,
            )
        }

        SectionTitle(stringResource(Res.string.intel_scanner_settings_dscan_sections_title))
        Text(
            text = stringResource(Res.string.intel_scanner_settings_dscan_sections_description),
            style = RiftTheme.typography.bodySecondary,
            modifier = Modifier.padding(horizontal = Spacing.large, vertical = Spacing.medium),
        )
        Divider(color = RiftTheme.colors.divider)
        IntelScannerDScanSection.entries.forEachIndexed { index, section ->
            DScanSectionSettingsItem(
                section = section,
                isEnabled = section in settings.enabledDScanSections,
                isCollapsedByDefault = section in settings.collapsedDScanSections,
                isShowingShips = settings.isShowingShipsInDScanShipClasses,
                onIsSelectedChange = { onIsDScanSectionSelectedChange(section) },
                onIsCollapsedByDefaultChange = { onIsDScanSectionCollapsedByDefaultChange(section) },
                onIsShowingShipsChange = onIsShowingShipsInDScanShipClassesChange,
            )
            if (index < IntelScannerDScanSection.entries.lastIndex) {
                Divider(color = RiftTheme.colors.divider)
            }
        }
    }
}

@Composable
private fun DScanSectionSettingsItem(
    section: IntelScannerDScanSection,
    isEnabled: Boolean,
    isCollapsedByDefault: Boolean,
    isShowingShips: Boolean,
    onIsSelectedChange: () -> Unit,
    onIsCollapsedByDefaultChange: () -> Unit,
    onIsShowingShipsChange: (Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.large),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .hoverBackground()
            .padding(top = Spacing.medium, bottom = Spacing.medium, end = Spacing.large, start = Spacing.small),
    ) {
        RiftCheckbox(
            isChecked = isEnabled,
            onCheckedChange = { onIsSelectedChange() },
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.weight(1f),
        ) {
            val [title, description] = getTitleAndDescription(section)
            Text(
                text = title,
                style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = description,
                style = RiftTheme.typography.bodySecondary,
            )
            RiftCheckboxWithLabel(
                label = stringResource(Res.string.intel_scanner_settings_dscan_section_collapsed_label),
                isChecked = isCollapsedByDefault,
                onCheckedChange = { onIsCollapsedByDefaultChange() },
                isEnabled = isEnabled,
                modifier = Modifier.padding(top = Spacing.small),
            )
            if (section == IntelScannerDScanSection.ShipsByClass) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_dscan_ships_in_class_label),
                    isChecked = isShowingShips,
                    onCheckedChange = onIsShowingShipsChange,
                    isEnabled = isEnabled,
                )
            }
        }
    }
}

@Composable
private fun getTitleAndDescription(section: IntelScannerDScanSection): Pair<String, String> {
    return when (section) {
        IntelScannerDScanSection.ShipsByClass -> stringResource(Res.string.intel_scanner_dscan_section_ships_by_class_name) to stringResource(Res.string.intel_scanner_dscan_section_ships_by_class_description)
        IntelScannerDScanSection.Ships -> stringResource(Res.string.intel_scanner_dscan_section_ships_name) to stringResource(Res.string.intel_scanner_dscan_section_ships_description)
        IntelScannerDScanSection.Structures -> stringResource(Res.string.intel_scanner_dscan_section_structures_name) to stringResource(Res.string.intel_scanner_dscan_section_structures_description)
        IntelScannerDScanSection.Stations -> stringResource(Res.string.intel_scanner_dscan_section_stations_name) to stringResource(Res.string.intel_scanner_dscan_section_stations_description)
        IntelScannerDScanSection.CelestialBodies -> stringResource(Res.string.intel_scanner_dscan_section_celestial_bodies_name) to stringResource(Res.string.intel_scanner_dscan_section_celestial_bodies_description)
        IntelScannerDScanSection.Stargates -> stringResource(Res.string.intel_scanner_dscan_section_stargates_name) to stringResource(Res.string.intel_scanner_dscan_section_stargates_description)
        IntelScannerDScanSection.Celestials -> stringResource(Res.string.intel_scanner_dscan_section_celestials_name) to stringResource(Res.string.intel_scanner_dscan_section_celestials_description)
        IntelScannerDScanSection.Wrecks -> stringResource(Res.string.intel_scanner_dscan_section_wrecks_name) to stringResource(Res.string.intel_scanner_dscan_section_wrecks_description)
        IntelScannerDScanSection.Drones -> stringResource(Res.string.intel_scanner_dscan_section_drones_name) to stringResource(Res.string.intel_scanner_dscan_section_drones_description)
        IntelScannerDScanSection.Corpses -> stringResource(Res.string.intel_scanner_dscan_section_corpses_name) to stringResource(Res.string.intel_scanner_dscan_section_corpses_description)
        IntelScannerDScanSection.Other -> stringResource(Res.string.intel_scanner_dscan_section_other_name) to stringResource(Res.string.intel_scanner_dscan_section_other_description)
    }
}

private data class FactionWarfareFaction(
    val id: Int,
    val name: String,
)

private val FactionWarfareFactions = listOf(
    FactionWarfareFaction(500003, "Amarr Empire"),
    FactionWarfareFaction(500001, "Caldari State"),
    FactionWarfareFaction(500004, "Gallente Federation"),
    FactionWarfareFaction(500002, "Minmatar Republic"),
    FactionWarfareFaction(500011, "Angel Cartel"),
    FactionWarfareFaction(500010, "Guristas Pirates"),
)

@Composable
private fun ColumnSettingsItem(
    column: ColumnType,
    index: Int,
    isEnabled: Boolean,
    settings: IntelScannerSettings,
    onIsColumnSelectedChange: (ColumnType) -> Unit,
    onColumnsReordered: (Int, Int) -> Unit,
    onIsShowingCorpWithCharacterChange: (Boolean) -> Unit,
    onIsShowingAllianceWithCharacterChange: (Boolean) -> Unit,
    onIsShowingTickersWithCharacterChange: (Boolean) -> Unit,
    onIsShowingCorporationNameChange: (Boolean) -> Unit,
    onIsShowingCorporationTickerChange: (Boolean) -> Unit,
    onIsShowingAllianceNameChange: (Boolean) -> Unit,
    onIsShowingAllianceTickerChange: (Boolean) -> Unit,
    onIsShowingFactionNameChange: (Boolean) -> Unit,
    onIsShowingAverageGangSizeChange: (Boolean) -> Unit,
    onIsShowingKillDeathTotalsChange: (Boolean) -> Unit,
    onIsShowingFleetCommanderScoreChange: (Boolean) -> Unit,
    onIsShowingAwoxCountChange: (Boolean) -> Unit,
    onIsShowingBaitCountChange: (Boolean) -> Unit,
    onIsShowingGankerCountChange: (Boolean) -> Unit,
    onTopShipsCountChange: (Int) -> Unit,
    onRecentShipsCountChange: (Int) -> Unit,
    onAffiliatesCountChange: (Int) -> Unit,
    onAffiliatesMinimumKillsChange: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.large),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .hoverBackground()
            .padding(top = Spacing.medium, bottom = Spacing.medium, end = Spacing.large, start = Spacing.small)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            val isCharacterColumn = column == ColumnType.Character
            val checkboxTooltip = when {
                isCharacterColumn -> stringResource(Res.string.intel_scanner_settings_character_column_required)
                isEnabled -> stringResource(Res.string.intel_scanner_settings_disable_column)
                else -> stringResource(Res.string.intel_scanner_settings_enable_column)
            }
            RiftTooltipArea(checkboxTooltip) {
                RiftCheckbox(
                    isChecked = isEnabled,
                    onCheckedChange = { onIsColumnSelectedChange(column) },
                    isEnabled = !isCharacterColumn,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                RiftTooltipArea(stringResource(Res.string.intel_scanner_settings_move_column_up)) {
                    RiftImageButton(
                        resource = Res.drawable.arrow_up_16px,
                        size = 16.dp,
                        isEnabled = index > 0,
                        onClick = { onColumnsReordered(index, index - 1) },
                    )
                }
                RiftTooltipArea(stringResource(Res.string.intel_scanner_settings_move_column_down)) {
                    RiftImageButton(
                        resource = Res.drawable.arrow_down_16px,
                        size = 16.dp,
                        isEnabled = index < settings.columnOrder.lastIndex,
                        onClick = { onColumnsReordered(index, index + 1) },
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.weight(1f),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = column.title.resolve(),
                    style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
                )
                column.dataSource?.let { DataSourceIcon(it) }
            }
            Text(
                text = column.description.resolve(),
                style = RiftTheme.typography.bodySecondary,
            )
            if (column == ColumnType.Character) {
                CharacterColumnSettings(
                    settings = settings,
                    onIsShowingCorpWithCharacterChange = onIsShowingCorpWithCharacterChange,
                    onIsShowingAllianceWithCharacterChange = onIsShowingAllianceWithCharacterChange,
                    onIsShowingTickersWithCharacterChange = onIsShowingTickersWithCharacterChange,
                )
            }
            if (column == ColumnType.Corporation) {
                OrganizationColumnSettings(
                    isShowingName = settings.isShowingCorporationName,
                    isShowingTicker = settings.isShowingCorporationTicker,
                    onIsShowingNameChange = onIsShowingCorporationNameChange,
                    onIsShowingTickerChange = onIsShowingCorporationTickerChange,
                )
            }
            if (column == ColumnType.Alliance) {
                OrganizationColumnSettings(
                    isShowingName = settings.isShowingAllianceName,
                    isShowingTicker = settings.isShowingAllianceTicker,
                    onIsShowingNameChange = onIsShowingAllianceNameChange,
                    onIsShowingTickerChange = onIsShowingAllianceTickerChange,
                )
            }
            if (column == ColumnType.Faction) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_faction_name_label),
                    isChecked = settings.isShowingFactionName,
                    onCheckedChange = onIsShowingFactionNameChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.Gang) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_average_gang_size_label),
                    isChecked = settings.isShowingAverageGangSize,
                    onCheckedChange = onIsShowingAverageGangSizeChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.KillDeathRatio) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_kill_loss_totals_label),
                    isChecked = settings.isShowingKillDeathTotals,
                    onCheckedChange = onIsShowingKillDeathTotalsChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.FleetCommander) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_fleet_commander_score_label),
                    isChecked = settings.isShowingFleetCommanderScore,
                    onCheckedChange = onIsShowingFleetCommanderScoreChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.AwoxKills) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_detected_count_label),
                    isChecked = settings.isShowingAwoxCount,
                    onCheckedChange = onIsShowingAwoxCountChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.Bait) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_detected_count_label),
                    isChecked = settings.isShowingBaitCount,
                    onCheckedChange = onIsShowingBaitCountChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.Ganker) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_detected_count_label),
                    isChecked = settings.isShowingGankerCount,
                    onCheckedChange = onIsShowingGankerCountChange,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.TopShips) {
                val shipCountLabels = (2..10).associateWith { pluralStringResource(Res.plurals.intel_scanner_ship_count, it, formatNumber(it)) }
                RiftSliderWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_ship_limit_label),
                    width = 100.dp,
                    range = 2..10,
                    currentValue = settings.topShipsCount,
                    onValueChange = onTopShipsCountChange,
                    getValueName = { shipCountLabels.getValue(it) },
                    isShowingValue = true,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.RecentShips) {
                val shipCountLabels = (2..10).associateWith { pluralStringResource(Res.plurals.intel_scanner_ship_count, it, formatNumber(it)) }
                RiftSliderWithLabel(
                    label = stringResource(Res.string.intel_scanner_settings_ship_limit_label),
                    width = 100.dp,
                    range = 2..10,
                    currentValue = settings.recentShipsCount,
                    onValueChange = onRecentShipsCountChange,
                    getValueName = { shipCountLabels.getValue(it) },
                    isShowingValue = true,
                    modifier = Modifier.padding(top = Spacing.small),
                )
            }
            if (column == ColumnType.Groups) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier.padding(top = Spacing.small),
                ) {
                    val allianceCountLabels = (3..10).associateWith { pluralStringResource(Res.plurals.intel_scanner_alliance_count, it, formatNumber(it)) }
                    RiftSliderWithLabel(
                        label = stringResource(Res.string.intel_scanner_settings_alliance_limit_label),
                        width = 100.dp,
                        range = 3..10,
                        currentValue = settings.affiliatesCount,
                        onValueChange = onAffiliatesCountChange,
                        getValueName = { allianceCountLabels.getValue(it) },
                        isShowingValue = true,
                    )
                    val killCountLabels = (0..20).associateWith { pluralStringResource(Res.plurals.intel_scanner_kill_count, it, formatNumber(it)) }
                    RiftSliderWithLabel(
                        label = stringResource(Res.string.intel_scanner_settings_affiliate_kill_threshold_label),
                        width = 100.dp,
                        range = 0..20,
                        currentValue = settings.affiliatesMinimumKills,
                        onValueChange = onAffiliatesMinimumKillsChange,
                        getValueName = { killCountLabels.getValue(it) },
                        isShowingValue = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun OrganizationColumnSettings(
    isShowingName: Boolean,
    isShowingTicker: Boolean,
    onIsShowingNameChange: (Boolean) -> Unit,
    onIsShowingTickerChange: (Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(top = Spacing.small),
    ) {
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.intel_scanner_settings_organization_name_label),
            isChecked = isShowingName,
            onCheckedChange = onIsShowingNameChange,
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.intel_scanner_settings_organization_ticker_label),
            isChecked = isShowingTicker,
            onCheckedChange = onIsShowingTickerChange,
        )
    }
}

@Composable
private fun CharacterColumnSettings(
    settings: IntelScannerSettings,
    onIsShowingCorpWithCharacterChange: (Boolean) -> Unit,
    onIsShowingAllianceWithCharacterChange: (Boolean) -> Unit,
    onIsShowingTickersWithCharacterChange: (Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(top = Spacing.small),
    ) {
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.intel_scanner_settings_character_corporation_label),
            isChecked = settings.isShowingCorpWithCharacter,
            onCheckedChange = onIsShowingCorpWithCharacterChange,
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.intel_scanner_settings_character_alliance_label),
            isChecked = settings.isShowingAllianceWithCharacter,
            onCheckedChange = onIsShowingAllianceWithCharacterChange,
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.intel_scanner_settings_character_tickers_label),
            isChecked = settings.isShowingTickersWithCharacter,
            onCheckedChange = onIsShowingTickersWithCharacterChange,
        )
    }
}

@Composable
private fun DataSourceIcon(source: ColumnDataSource) {
    val text = stringResource(Res.string.intel_scanner_data_source, source.displayName)
    RiftTooltipArea(text) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.menu_zkillboard),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = source.displayName,
                style = RiftTheme.typography.detailSecondary
            )
        }
    }
}
