package dev.nohus.rift.intelscanner

import androidx.compose.ui.unit.dp
import dev.nohus.rift.ViewModel
import dev.nohus.rift.intelscanner.DScanParser.DScanEntity
import dev.nohus.rift.intelscanner.IntelScannerClipboardWatcher.ClipboardResult
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterItem
import dev.nohus.rift.intelscanner.LoadDScanIntelUseCase.DScanItem
import dev.nohus.rift.settings.persistence.IntelScannerColumn
import dev.nohus.rift.settings.persistence.IntelScannerDScanSection
import dev.nohus.rift.settings.persistence.IntelScannerSortDirection
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.settings.persistence.SettingsModel
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.isFriendly
import dev.nohus.rift.utils.toggle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.time.Instant
import java.util.UUID

@Factory
class IntelScannerViewModel(
    private val intelScannerClipboardWatcher: IntelScannerClipboardWatcher,
    private val settings: Settings,
    private val loadCharactersIntelUseCase: LoadCharactersIntelUseCase,
    private val loadDScanIntelUseCase: LoadDScanIntelUseCase,
) : ViewModel() {

    data class UiState(
        val scans: List<Scan> = emptyList(),
        val settings: IntelScannerSettings,
        val isShareHintDialogShown: Boolean = false,
    )

    sealed class Scan(
        open val id: UUID,
        open val timestamp: Instant,
    ) {
        data class LocalScan(
            val items: List<CharacterItem>,
            override val timestamp: Instant,
            override val id: UUID = UUID.randomUUID(),
        ) : Scan(id, timestamp)

        data class DScan(
            val dscan: LoadDScanIntelUseCase.DScan,
            override val timestamp: Instant,
            override val id: UUID = UUID.randomUUID(),
        ) : Scan(id, timestamp)
    }

    data class IntelScannerSettings(
        val isOpenOnClipboardCharacterListEnabled: Boolean,
        val isUsingCompactMode: Boolean,
        val maxLocalScans: Int,
        val maxDScans: Int,
        val isShowingDScanShipMass: Boolean,
        val isShowingShipsInDScanShipClasses: Boolean,
        val enabledDScanSections: Set<IntelScannerDScanSection>,
        val collapsedDScanSections: Set<IntelScannerDScanSection>,
        val isOpenOnClipboardDScanEnabled: Boolean,
        val isShowingSummary: Boolean,
        val isShowingCharactersSummary: Boolean,
        val isShowingCorporationsSummary: Boolean,
        val maxCorporationsInSummary: Int,
        val isShowingAlliancesSummary: Boolean,
        val maxAlliancesInSummary: Int,
        val isShowingFactionWarfareSummary: Boolean,
        val isShowingCorpWithCharacter: Boolean,
        val isShowingAllianceWithCharacter: Boolean,
        val isShowingTickersWithCharacter: Boolean,
        val isShowingCorporationName: Boolean,
        val isShowingCorporationTicker: Boolean,
        val isShowingAllianceName: Boolean,
        val isShowingAllianceTicker: Boolean,
        val isShowingFactionName: Boolean,
        val isShowingAverageGangSize: Boolean,
        val isShowingKillDeathTotals: Boolean,
        val isShowingFleetCommanderScore: Boolean,
        val isShowingAwoxCount: Boolean,
        val isShowingBaitCount: Boolean,
        val isShowingGankerCount: Boolean,
        val topShipsCount: Int,
        val recentShipsCount: Int,
        val affiliatesCount: Int,
        val affiliatesMinimumKills: Int,
        val ignoredCharactersCount: Int,
        val ignoredFactionIds: Set<Int>,
        val isIgnoringCharactersWithPositiveStandings: Boolean,
        val isIgnoringOwnCharacters: Boolean,
        val enabledColumns: List<ColumnType>,
        val columnOrder: List<ColumnType>,
        val sortingColumn: ColumnType?,
        val sortingDirection: IntelScannerSortDirection,
    ) {
        val portraitSize get() = if (isUsingCompactMode) 24.dp else 32.dp
    }

    private data class CharacterFilters(
        val ignoredCharacters: Set<String>,
        val ignoredFactionIds: Set<Int>,
        val isIgnoringCharactersWithPositiveStandings: Boolean,
        val isIgnoringOwnCharacters: Boolean,
    )

    private val _state = MutableStateFlow(
        UiState(settings = getSettings()),
    )
    val state = _state.asStateFlow()

    // TODO: Rethink if this is needed because there are already `settings` in the UiState
    private var characterFilters: CharacterFilters = getCharacterFilters(settings.updateFlow.value)

    init {
        viewModelScope.launch {
            settings.updateFlow
                .map(::getCharacterFilters)
                .distinctUntilChanged()
                .collect {
                    characterFilters = it
                }
        }
        viewModelScope.launch {
            settings.updateFlow.map { it.intelScanner }.collect {
                _state.update {
                    it.copy(
                        settings = getSettings(),
                    )
                }
            }
        }
        viewModelScope.launch {
            intelScannerClipboardWatcher.result.collect {
                when (it) {
                    is ClipboardResult.Characters -> onNewCharacters(it.names)
                    is ClipboardResult.DScan -> onNewDScan(it.entities)
                    ClipboardResult.None -> {}
                }
            }
        }
    }

    private suspend fun onNewCharacters(names: List<String>) {
        val filters = characterFilters
        val filteredNames = names.filterNot { it.lowercase() in filters.ignoredCharacters }

        val existingScans = _state.value.scans.filterIsInstance<Scan.LocalScan>()
        val existingCharacterItems = existingScans
            .sortedByDescending { it.timestamp }
            .flatMap { it.items }
            .groupBy { it.name }
            .map { it.value.first() }

        val newScan = Scan.LocalScan(
            items = existingCharacterItems.filter { item -> filteredNames.any { it.equals(item.name, ignoreCase = true) } },
            timestamp = Instant.now(),
        )
        _state.update { state ->
            state.copy(
                scans = state.scans.withNewScan(
                    newScan = newScan,
                    maxOpenScans = settings.intelScanner.maxLocalScans,
                ),
            )
        }

        loadCharactersIntelUseCase(
            characters = filteredNames,
            shouldIgnoreCharacter = { character ->
                (filters.isIgnoringCharactersWithPositiveStandings && character.standingLevel.isFriendly) ||
                    (filters.isIgnoringOwnCharacters && character.standingLevel == Standing.Self) ||
                    (character.factionId?.let { it in filters.ignoredFactionIds } == true)
            },
        ).collect { items ->
            _state.update { state ->
                state.copy(
                    scans = state.scans.map { scan ->
                        if (scan is Scan.LocalScan && scan.id == newScan.id) {
                            scan.copy(items = items)
                        } else {
                            scan
                        }
                    },
                )
            }
        }
    }

    private suspend fun onNewDScan(items: List<DScanEntity>) {
        val dscan = loadDScanIntelUseCase(items)
        val newScan = Scan.DScan(dscan, Instant.now())
        _state.update { state ->
            state.copy(
                scans = state.scans.withNewScan(
                    newScan = newScan,
                    maxOpenScans = settings.intelScanner.maxDScans,
                ),
            )
        }
    }

    private fun List<Scan>.withNewScan(newScan: Scan, maxOpenScans: Int): List<Scan> {
        val scansOfSameType = filter { it::class == newScan::class }
        val oldestScanIdsToClose = scansOfSameType
            .sortedBy { it.timestamp }
            .take((scansOfSameType.size - maxOpenScans + 1).coerceAtLeast(0))
            .map { it.id }
            .toSet()
        return filterNot { it.id in oldestScanIdsToClose } + newScan
    }

    private fun getCharacterFilters(model: SettingsModel): CharacterFilters {
        return CharacterFilters(
            ignoredCharacters = model.intelScanner.ignoredCharacters.map { it.lowercase() }.toSet(),
            ignoredFactionIds = model.intelScanner.ignoredFactionIds.toSet(),
            isIgnoringCharactersWithPositiveStandings = model.intelScanner.isIgnoringCharactersWithPositiveStandings,
            isIgnoringOwnCharacters = model.intelScanner.isIgnoringOwnCharacters,
        )
    }

    fun onTabClosed(scanId: UUID) {
        _state.update { state ->
            state.copy(scans = state.scans.filter { it.id != scanId })
        }
    }

    fun onIsUsingCompactModeChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isUsingCompactMode = enabled)
    }

    fun onMaxLocalScansChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(maxLocalScans = count)
    }

    fun onMaxDScansChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(maxDScans = count)
    }

    fun onIsShowingDScanShipMassChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingDScanShipMass = enabled)
    }

    fun onIsShowingShipsInDScanShipClassesChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingShipsInDScanShipClasses = enabled)
    }

    fun onIsDScanSectionSelectedChange(section: IntelScannerDScanSection) {
        settings.intelScanner = settings.intelScanner.copy(
            enabledDScanSections = settings.intelScanner.enabledDScanSections.toggle(section),
        )
    }

    fun onIsDScanSectionCollapsedByDefaultChange(section: IntelScannerDScanSection) {
        settings.intelScanner = settings.intelScanner.copy(
            collapsedDScanSections = settings.intelScanner.collapsedDScanSections.toggle(section),
        )
    }

    fun onIsOpenOnClipboardDScanEnabledChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isOpenOnClipboardDScanEnabled = enabled)
    }

    fun onIsOpenOnClipboardCharacterListEnabledChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isOpenOnClipboardCharacterListEnabled = enabled)
    }

    fun onIsShowingSummaryChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingSummary = enabled)
    }

    fun onIsShowingCharactersSummaryChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingCharactersSummary = enabled)
    }

    fun onIsShowingCorporationsSummaryChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingCorporationsSummary = enabled)
    }

    fun onMaxCorporationsInSummaryChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(maxCorporationsInSummary = count)
    }

    fun onIsShowingAlliancesSummaryChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingAlliancesSummary = enabled)
    }

    fun onMaxAlliancesInSummaryChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(maxAlliancesInSummary = count)
    }

    fun onIsShowingFactionWarfareSummaryChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingFactionWarfareSummary = enabled)
    }

    fun onIsColumnSelectedChange(column: ColumnType) {
        val columnOrder = getColumnOrder()
        settings.intelScanner = settings.intelScanner.copy(
            enabledColumns = getEnabledColumns()
                .toggle(column.settingsType)
                .sortedBy { columnOrder.indexOf(it) },
        )
    }

    fun onColumnsReordered(fromIndex: Int, toIndex: Int) {
        val columnOrder = getColumnOrder().move(fromIndex, toIndex)
        settings.intelScanner = settings.intelScanner.copy(
            enabledColumns = getEnabledColumns().sortedBy { columnOrder.indexOf(it) },
            columnOrder = columnOrder,
        )
    }

    fun onSortingColumnChange(column: ColumnType) {
        val isAlreadySorting = settings.intelScanner.sortingColumn == column.settingsType
        settings.intelScanner = settings.intelScanner.copy(
            sortingColumn = column.settingsType,
            sortingDirection = if (isAlreadySorting) {
                when (settings.intelScanner.sortingDirection) {
                    IntelScannerSortDirection.Ascending -> IntelScannerSortDirection.Descending
                    IntelScannerSortDirection.Descending -> IntelScannerSortDirection.Ascending
                }
            } else {
                IntelScannerSortDirection.Descending
            },
        )
    }

    fun onIsShowingCorpWithCharacterChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingCorpWithCharacter = enabled)
    }

    fun onIsShowingAllianceWithCharacterChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingAllianceWithCharacter = enabled)
    }

    fun onIsShowingTickersWithCharacterChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingTickersWithCharacter = enabled)
    }

    fun onIsShowingCorporationNameChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingCorporationName = enabled)
    }

    fun onIsShowingCorporationTickerChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingCorporationTicker = enabled)
    }

    fun onIsShowingAllianceNameChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingAllianceName = enabled)
    }

    fun onIsShowingAllianceTickerChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingAllianceTicker = enabled)
    }

    fun onIsShowingFactionNameChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingFactionName = enabled)
    }

    fun onIsShowingAverageGangSizeChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingAverageGangSize = enabled)
    }

    fun onIsShowingKillDeathTotalsChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingKillDeathTotals = enabled)
    }

    fun onIsShowingFleetCommanderScoreChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingFleetCommanderScore = enabled)
    }

    fun onIsShowingAwoxCountChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingAwoxCount = enabled)
    }

    fun onIsShowingBaitCountChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingBaitCount = enabled)
    }

    fun onIsShowingGankerCountChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isShowingGankerCount = enabled)
    }

    fun onTopShipsCountChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(topShipsCount = count)
    }

    fun onRecentShipsCountChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(recentShipsCount = count)
    }

    fun onAffiliatesCountChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(affiliatesCount = count)
    }

    fun onAffiliatesMinimumKillsChange(count: Int) {
        settings.intelScanner = settings.intelScanner.copy(affiliatesMinimumKills = count)
    }

    fun onAddIgnoredCharacters() {
        val namesInScans = _state.value.scans.filterIsInstance<Scan.LocalScan>().flatMap { it.items.map { it.name } }
        val ignoredCharacters = (settings.intelScanner.ignoredCharacters + namesInScans).distinctBy { it.lowercase() }
        settings.intelScanner = settings.intelScanner.copy(ignoredCharacters = ignoredCharacters)
    }

    fun onClearIgnoredCharacters() {
        settings.intelScanner = settings.intelScanner.copy(ignoredCharacters = emptyList())
    }

    fun onIgnoredFactionChange(factionId: Int, isIgnored: Boolean) {
        val ignoredFactionIds = settings.intelScanner.ignoredFactionIds.toMutableSet()
        if (isIgnored) {
            ignoredFactionIds += factionId
        } else {
            ignoredFactionIds -= factionId
        }
        settings.intelScanner = settings.intelScanner.copy(ignoredFactionIds = ignoredFactionIds.toList())
    }

    fun onIsIgnoringCharactersWithPositiveStandingsChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isIgnoringCharactersWithPositiveStandings = enabled)
    }

    fun onIsIgnoringOwnCharactersChange(enabled: Boolean) {
        settings.intelScanner = settings.intelScanner.copy(isIgnoringOwnCharacters = enabled)
    }

    fun onScanImageCopied() {
        if (!settings.intelScanner.isShareHintShown) {
            settings.intelScanner = settings.intelScanner.copy(isShareHintShown = true)
            _state.update { it.copy(isShareHintDialogShown = true) }
        }
    }

    fun onShareHintDialogDismissed() {
        _state.update { it.copy(isShareHintDialogShown = false) }
    }

    private fun getSettings(): IntelScannerSettings {
        val columnOrder = getColumnOrder()
        val enabledColumns = getEnabledColumns()
        return IntelScannerSettings(
            isOpenOnClipboardCharacterListEnabled = settings.intelScanner.isOpenOnClipboardCharacterListEnabled,
            isUsingCompactMode = settings.intelScanner.isUsingCompactMode,
            maxLocalScans = settings.intelScanner.maxLocalScans.coerceIn(1..6),
            maxDScans = settings.intelScanner.maxDScans.coerceIn(1..6),
            isShowingDScanShipMass = settings.intelScanner.isShowingDScanShipMass,
            isShowingShipsInDScanShipClasses = settings.intelScanner.isShowingShipsInDScanShipClasses,
            enabledDScanSections = settings.intelScanner.enabledDScanSections.toSet(),
            collapsedDScanSections = settings.intelScanner.collapsedDScanSections.toSet(),
            isOpenOnClipboardDScanEnabled = settings.intelScanner.isOpenOnClipboardDScanEnabled,
            isShowingSummary = settings.intelScanner.isShowingSummary,
            isShowingCharactersSummary = settings.intelScanner.isShowingCharactersSummary,
            isShowingCorporationsSummary = settings.intelScanner.isShowingCorporationsSummary,
            maxCorporationsInSummary = settings.intelScanner.maxCorporationsInSummary.coerceIn(3..30),
            isShowingAlliancesSummary = settings.intelScanner.isShowingAlliancesSummary,
            maxAlliancesInSummary = settings.intelScanner.maxAlliancesInSummary.coerceIn(3..30),
            isShowingFactionWarfareSummary = settings.intelScanner.isShowingFactionWarfareSummary,
            isShowingCorpWithCharacter = settings.intelScanner.isShowingCorpWithCharacter,
            isShowingAllianceWithCharacter = settings.intelScanner.isShowingAllianceWithCharacter,
            isShowingTickersWithCharacter = settings.intelScanner.isShowingTickersWithCharacter,
            isShowingCorporationName = settings.intelScanner.isShowingCorporationName,
            isShowingCorporationTicker = settings.intelScanner.isShowingCorporationTicker,
            isShowingAllianceName = settings.intelScanner.isShowingAllianceName,
            isShowingAllianceTicker = settings.intelScanner.isShowingAllianceTicker,
            isShowingFactionName = settings.intelScanner.isShowingFactionName,
            isShowingAverageGangSize = settings.intelScanner.isShowingAverageGangSize,
            isShowingKillDeathTotals = settings.intelScanner.isShowingKillDeathTotals,
            isShowingFleetCommanderScore = settings.intelScanner.isShowingFleetCommanderScore,
            isShowingAwoxCount = settings.intelScanner.isShowingAwoxCount,
            isShowingBaitCount = settings.intelScanner.isShowingBaitCount,
            isShowingGankerCount = settings.intelScanner.isShowingGankerCount,
            topShipsCount = settings.intelScanner.topShipsCount.coerceIn(2..10),
            recentShipsCount = settings.intelScanner.recentShipsCount.coerceIn(2..10),
            affiliatesCount = settings.intelScanner.affiliatesCount.coerceIn(3..10),
            affiliatesMinimumKills = settings.intelScanner.affiliatesMinimumKills.coerceIn(0..20),
            ignoredCharactersCount = settings.intelScanner.ignoredCharacters.distinctBy { it.lowercase() }.size,
            ignoredFactionIds = settings.intelScanner.ignoredFactionIds.toSet(),
            isIgnoringCharactersWithPositiveStandings = settings.intelScanner.isIgnoringCharactersWithPositiveStandings,
            isIgnoringOwnCharacters = settings.intelScanner.isIgnoringOwnCharacters,
            enabledColumns = enabledColumns
                .sortedBy { columnOrder.indexOf(it) }
                .map(::getColumnType),
            columnOrder = columnOrder.map(::getColumnType),
            sortingColumn = settings.intelScanner.sortingColumn?.let(::getColumnType),
            sortingDirection = settings.intelScanner.sortingDirection,
        )
    }

    private fun getColumnOrder(): List<IntelScannerColumn> {
        return (settings.intelScanner.columnOrder + IntelScannerColumnTypes.map { it.settingsType }).distinct()
    }

    private fun getEnabledColumns(): List<IntelScannerColumn> {
        return (settings.intelScanner.enabledColumns + IntelScannerColumn.Character).distinct()
    }

    private fun getColumnType(column: IntelScannerColumn): ColumnType = when (column) {
        IntelScannerColumn.Character -> ColumnType.Character
        IntelScannerColumn.Age -> ColumnType.Age
        IntelScannerColumn.Corporation -> ColumnType.Corporation
        IntelScannerColumn.Alliance -> ColumnType.Alliance
        IntelScannerColumn.Faction -> ColumnType.Faction
        IntelScannerColumn.Security -> ColumnType.Security
        IntelScannerColumn.Danger -> ColumnType.Danger
        IntelScannerColumn.Gang -> ColumnType.Gang
        IntelScannerColumn.SoloKills -> ColumnType.SoloKills
        IntelScannerColumn.KillDeathRatio -> ColumnType.KillDeathRatio
        IntelScannerColumn.GroupSize -> ColumnType.GroupSize
        IntelScannerColumn.Cyno -> ColumnType.Cyno
        IntelScannerColumn.Space -> ColumnType.Space
        IntelScannerColumn.Isk -> ColumnType.Isk
        IntelScannerColumn.Tags -> ColumnType.Tags
        IntelScannerColumn.FleetCommander -> ColumnType.FleetCommander
        IntelScannerColumn.RecentShips -> ColumnType.RecentShips
        IntelScannerColumn.TopShips -> ColumnType.TopShips
        IntelScannerColumn.Groups -> ColumnType.Groups
        IntelScannerColumn.Partners -> ColumnType.Associates
        IntelScannerColumn.AwoxKills -> ColumnType.AwoxKills
        IntelScannerColumn.Bait -> ColumnType.Bait
        IntelScannerColumn.Ganker -> ColumnType.Ganker
    }

    private fun <T> List<T>.move(fromIndex: Int, toIndex: Int): List<T> {
        if (fromIndex !in indices || toIndex !in indices) return this
        return toMutableList().also { items ->
            items.add(toIndex, items.removeAt(fromIndex))
        }
    }
}
