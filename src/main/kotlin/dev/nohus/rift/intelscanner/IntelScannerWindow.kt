package dev.nohus.rift.intelscanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.KeyName
import dev.nohus.rift.compose.MessageDialogType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftMessageDialog
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftVerticalScrollbarWidth
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarLazyColumn
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.TitleBarStyle
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_scanner_action_share
import dev.nohus.rift.generated.resources.intel_scanner_dscan_tab
import dev.nohus.rift.generated.resources.intel_scanner_empty_instruction
import dev.nohus.rift.generated.resources.intel_scanner_empty_shortcuts_prefix
import dev.nohus.rift.generated.resources.intel_scanner_empty_shortcuts_separator
import dev.nohus.rift.generated.resources.intel_scanner_empty_shortcuts_suffix
import dev.nohus.rift.generated.resources.intel_scanner_local_scan_tab
import dev.nohus.rift.generated.resources.intel_scanner_settings_tab
import dev.nohus.rift.generated.resources.intel_scanner_share_copied
import dev.nohus.rift.generated.resources.intel_scanner_share_hint_message
import dev.nohus.rift.generated.resources.intel_scanner_share_hint_title
import dev.nohus.rift.generated.resources.intel_scanner_system_dscan_tab
import dev.nohus.rift.generated.resources.intel_scanner_window_title
import dev.nohus.rift.generated.resources.window_directional_scan
import dev.nohus.rift.intelscanner.IntelScannerViewModel.Scan
import dev.nohus.rift.intelscanner.IntelScannerViewModel.UiState
import dev.nohus.rift.intelscanner.compose.CharacterRow
import dev.nohus.rift.intelscanner.compose.DScanCapture
import dev.nohus.rift.intelscanner.compose.DScanContent
import dev.nohus.rift.intelscanner.compose.HeaderRow
import dev.nohus.rift.intelscanner.compose.IntelScannerSettingsContent
import dev.nohus.rift.intelscanner.compose.IntelScannerSummary
import dev.nohus.rift.intelscanner.compose.IntelScannerTableCapture
import dev.nohus.rift.intelscanner.compose.getColumnsWidths
import dev.nohus.rift.intelscanner.compose.sortCharacters
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun IntelScannerWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: IntelScannerViewModel = viewModel()
    val player = rememberUiSoundPlayer()
    val state by viewModel.state.collectAsState()

    val tabs = remember(state.scans) { getTabs(state.scans) }
    val latestScanId = state.scans.maxByOrNull { it.timestamp }?.id
    var selectedTab by remember(tabs, latestScanId) {
        val index = tabs.indexOfFirst { it.scanId == latestScanId }.takeIf { it > -1 } ?: 0
        mutableIntStateOf(index)
    }
    val selectedScanId = tabs[selectedTab].scanId
    val selectedScan = state.scans.firstOrNull { it.id == selectedScanId }

    var isCapturingTable by remember { mutableStateOf(false) }
    var isCopiedConfirmationVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isCopiedConfirmationVisible) {
        if (isCopiedConfirmationVisible) {
            delay(2_000.milliseconds)
            isCopiedConfirmationVisible = false
        }
    }

    val stateHolder = rememberSaveableStateHolder()
    val savedScanIds = remember { mutableSetOf<UUID>() }
    val currentScanIds = state.scans.mapTo(mutableSetOf()) { it.id }
    SideEffect {
        (savedScanIds - currentScanIds).forEach(stateHolder::removeState)
        savedScanIds.retainAll(currentScanIds)
        savedScanIds.addAll(currentScanIds)
    }
    val settingsScrollState = rememberScrollState()

    RiftWindow(
        title = stringResource(Res.string.intel_scanner_window_title),
        icon = Res.drawable.window_directional_scan,
        state = windowState,
        onCloseClick = onCloseRequest,
        titleBarStyle = if (state.settings.isUsingCompactMode) TitleBarStyle.Small else TitleBarStyle.Full,
        titleBarContent = { height ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RiftTabBar(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onTabClosed = { tabs[it].scanId?.let { id -> viewModel.onTabClosed(id) } },
                    withUnderline = false,
                    withWideTabs = true,
                    fixedHeight = height,
                )
                Spacer(Modifier.weight(1f))
                AnimatedVisibility(
                    visible = isCopiedConfirmationVisible,
                    enter = fadeIn(tween(150)) + slideInHorizontally(tween(150)) { it / 2 },
                    exit = fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 2 },
                ) {
                    Text(
                        text = stringResource(Res.string.intel_scanner_share_copied),
                        style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.successGreen),
                        modifier = Modifier.padding(end = Spacing.medium),
                    )
                }
                RiftButton(
                    text = stringResource(Res.string.intel_scanner_action_share),
                    type = ButtonType.Secondary,
                    isCompact = height <= 32.dp,
                    isEnabled = selectedScan != null && !isCapturingTable,
                    modifier = Modifier.padding(end = Spacing.medium),
                    onClick = { isCapturingTable = true },
                )
            }
        },
        withContentPadding = false,
    ) {
        Box {
            Column {
                val offset = LocalDensity.current.run { 1.dp.toPx() }
                Box(
                    modifier = Modifier
                        .graphicsLayer(translationY = -offset)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(RiftTheme.colors.borderGreyLight),
                )
                if (selectedTab == tabs.lastIndex) {
                    IntelScannerSettingsContent(
                        settings = state.settings,
                        scannerCharactersCount = state.scans.filterIsInstance<Scan.LocalScan>().flatMap { it.items.map { it.name } }.distinct().size,
                        scrollState = settingsScrollState,
                        onAddIgnoredCharacters = viewModel::onAddIgnoredCharacters,
                        onClearIgnoredCharacters = viewModel::onClearIgnoredCharacters,
                        onIgnoredFactionChange = viewModel::onIgnoredFactionChange,
                        onIsIgnoringCharactersWithPositiveStandingsChange = viewModel::onIsIgnoringCharactersWithPositiveStandingsChange,
                        onIsIgnoringOwnCharactersChange = viewModel::onIsIgnoringOwnCharactersChange,
                        onIsOpenOnClipboardCharacterListEnabledChange = viewModel::onIsOpenOnClipboardCharacterListEnabledChange,
                        onIsUsingCompactModeChange = viewModel::onIsUsingCompactModeChange,
                        onMaxLocalScansChange = viewModel::onMaxLocalScansChange,
                        onMaxDScansChange = viewModel::onMaxDScansChange,
                        onIsShowingDScanShipMassChange = viewModel::onIsShowingDScanShipMassChange,
                        onIsShowingShipsInDScanShipClassesChange = viewModel::onIsShowingShipsInDScanShipClassesChange,
                        onIsDScanSectionSelectedChange = viewModel::onIsDScanSectionSelectedChange,
                        onIsDScanSectionCollapsedByDefaultChange = viewModel::onIsDScanSectionCollapsedByDefaultChange,
                        onIsOpenOnClipboardDScanEnabledChange = viewModel::onIsOpenOnClipboardDScanEnabledChange,
                        onIsShowingSummaryChange = viewModel::onIsShowingSummaryChange,
                        onIsShowingCharactersSummaryChange = viewModel::onIsShowingCharactersSummaryChange,
                        onIsShowingCorporationsSummaryChange = viewModel::onIsShowingCorporationsSummaryChange,
                        onMaxCorporationsInSummaryChange = viewModel::onMaxCorporationsInSummaryChange,
                        onIsShowingAlliancesSummaryChange = viewModel::onIsShowingAlliancesSummaryChange,
                        onMaxAlliancesInSummaryChange = viewModel::onMaxAlliancesInSummaryChange,
                        onIsShowingFactionWarfareSummaryChange = viewModel::onIsShowingFactionWarfareSummaryChange,
                        onIsColumnSelectedChange = viewModel::onIsColumnSelectedChange,
                        onColumnsReordered = viewModel::onColumnsReordered,
                        onIsShowingCorpWithCharacterChange = viewModel::onIsShowingCorpWithCharacterChange,
                        onIsShowingAllianceWithCharacterChange = viewModel::onIsShowingAllianceWithCharacterChange,
                        onIsShowingTickersWithCharacterChange = viewModel::onIsShowingTickersWithCharacterChange,
                        onIsShowingCorporationNameChange = viewModel::onIsShowingCorporationNameChange,
                        onIsShowingCorporationTickerChange = viewModel::onIsShowingCorporationTickerChange,
                        onIsShowingAllianceNameChange = viewModel::onIsShowingAllianceNameChange,
                        onIsShowingAllianceTickerChange = viewModel::onIsShowingAllianceTickerChange,
                        onIsShowingFactionNameChange = viewModel::onIsShowingFactionNameChange,
                        onIsShowingAverageGangSizeChange = viewModel::onIsShowingAverageGangSizeChange,
                        onIsShowingKillDeathTotalsChange = viewModel::onIsShowingKillDeathTotalsChange,
                        onIsShowingFleetCommanderScoreChange = viewModel::onIsShowingFleetCommanderScoreChange,
                        onIsShowingAwoxCountChange = viewModel::onIsShowingAwoxCountChange,
                        onIsShowingBaitCountChange = viewModel::onIsShowingBaitCountChange,
                        onIsShowingGankerCountChange = viewModel::onIsShowingGankerCountChange,
                        onTopShipsCountChange = viewModel::onTopShipsCountChange,
                        onRecentShipsCountChange = viewModel::onRecentShipsCountChange,
                        onAffiliatesCountChange = viewModel::onAffiliatesCountChange,
                        onAffiliatesMinimumKillsChange = viewModel::onAffiliatesMinimumKillsChange,
                    )
                } else {
                    IntelScannerWindowContent(
                        state = state,
                        scan = selectedScan,
                        stateHolder = stateHolder,
                        onSortingColumnChange = viewModel::onSortingColumnChange,
                    )
                }
            }
            if (isCapturingTable && selectedScan != null) {
                val onCaptured: suspend (ImageBitmap?) -> Unit = { image ->
                    image?.let {
                        withContext(Dispatchers.Default) {
                            Clipboard.copy(it)
                        }
                        player.play(UiSound.CameraShutter)
                        isCopiedConfirmationVisible = true
                        viewModel.onScanImageCopied()
                    }
                    isCapturingTable = false
                }
                when (selectedScan) {
                    is Scan.LocalScan -> IntelScannerTableCapture(state, selectedScan, onCaptured)
                    is Scan.DScan -> DScanCapture(state, selectedScan, onCaptured)
                }
            }
        }
        if (state.isShareHintDialogShown) {
            RiftMessageDialog(
                title = stringResource(Res.string.intel_scanner_share_hint_title),
                message = stringResource(Res.string.intel_scanner_share_hint_message),
                type = MessageDialogType.Info,
                parentWindowState = windowState,
                onDismiss = viewModel::onShareHintDialogDismissed,
            )
        }
    }
}

private fun getTabs(scans: List<Scan>): List<Tab> {
    var id = 0
    return buildList {
        if (scans.isEmpty()) {
            add(Tab(id = id++, title = Res.string.intel_scanner_window_title.toUiText(), isCloseable = false))
        }
        scans.forEach { scan ->
            val scanName = when (scan) {
                is Scan.LocalScan -> {
                    Res.string.intel_scanner_local_scan_tab.toUiText(scan.items.size.toUiTextArgument(useGrouping = true))
                }
                is Scan.DScan -> {
                    val system = scan.dscan.systemChip?.name
                    val ships = scan.dscan.items.filter { it.typeCategory == "Ship" }.size
                    if (system != null) Res.string.intel_scanner_system_dscan_tab.toUiText(system.toUiTextArgument(), ships.toUiTextArgument()) else Res.string.intel_scanner_dscan_tab.toUiText(ships.toUiTextArgument())
                }
            }
            add(Tab(id = id++, title = scanName, isCloseable = true, payload = scan.id))
        }
        add(Tab(id = id, title = Res.string.intel_scanner_settings_tab.toUiText(), isCloseable = false))
    }
}

@Composable
private fun IntelScannerWindowContent(
    state: UiState,
    scan: Scan?,
    stateHolder: SaveableStateHolder,
    onSortingColumnChange: (ColumnType) -> Unit,
) {
    val outerPadding = if (state.settings.isUsingCompactMode) Spacing.medium else Spacing.large
    Column(
        modifier = Modifier
            .padding(start = outerPadding, end = outerPadding, bottom = Spacing.medium)
            .fillMaxSize(),
    ) {
        if (scan != null) {
            stateHolder.SaveableStateProvider(scan.id) {
                when (scan) {
                    is Scan.LocalScan -> LocalScanContent(state, scan, onSortingColumnChange)
                    is Scan.DScan -> DScanContent(state, scan)
                }
            }
        } else {
            EmptyState()
        }
    }
}

@Composable
private fun LocalScanContent(
    state: UiState,
    scan: Scan.LocalScan,
    onSortingColumnChange: (ColumnType) -> Unit,
) {
    val listState = rememberLazyListState()
    val sortKey = "${state.settings.sortingColumn?.settingsType}:${state.settings.sortingDirection.ordinal}"
    var appliedSortKey by rememberSaveable(scan.id) { mutableStateOf(sortKey) }
    LaunchedEffect(sortKey) {
        if (appliedSortKey != sortKey && scan.items.isNotEmpty()) {
            listState.scrollToItem(0)
        }
        appliedSortKey = sortKey
    }

    val sortedItems = remember(scan.items, state.settings) {
        sortCharacters(scan.items, state.settings)
    }
    val columnWidths = getColumnsWidths(state.settings, sortedItems)
    if (state.settings.isShowingSummary) {
        IntelScannerSummary(
            characters = scan.items,
            settings = state.settings,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = RiftVerticalScrollbarWidth)
            .clipToBounds(),
    ) {
        HeaderRow(
            settings = state.settings,
            columnWidths = columnWidths,
            onSortingColumnChange = onSortingColumnChange,
        )
    }
    ScrollbarLazyColumn(
        listState = listState,
        contentPadding = PaddingValues(vertical = Spacing.verySmall),
        modifier = Modifier
            .fillMaxSize(),
    ) {
        itemsIndexed(sortedItems, key = { _, character -> character.name }) { rowIndex, character ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .animateItem(),
            ) {
                CharacterRow(rowIndex, character, scan.timestamp, state.settings, columnWidths)
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.large),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Text(
                text = stringResource(Res.string.intel_scanner_empty_instruction),
                style = RiftTheme.typography.headerPrimary,
                color = RiftTheme.colors.textSecondary,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.intel_scanner_empty_shortcuts_prefix),
                    style = RiftTheme.typography.headerPrimary,
                    color = RiftTheme.colors.textSecondary,
                )
                KeyName("Ctrl+A")
                Text(
                    text = stringResource(Res.string.intel_scanner_empty_shortcuts_separator),
                    style = RiftTheme.typography.headerPrimary,
                    color = RiftTheme.colors.textSecondary,
                )
                KeyName("Ctrl+C")
                Text(
                    text = stringResource(Res.string.intel_scanner_empty_shortcuts_suffix),
                    style = RiftTheme.typography.headerPrimary,
                    color = RiftTheme.colors.textSecondary,
                )
            }
        }
    }
}

private val Tab.scanId get() = payload as? UUID
