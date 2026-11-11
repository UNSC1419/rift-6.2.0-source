package dev.nohus.rift.wallet.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nohus.rift.compose.text.parseEveFormattedText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.wallet_filter_based_on_description
import dev.nohus.rift.generated.resources.wallet_filter_deposits
import dev.nohus.rift.generated.resources.wallet_filter_showing_description
import dev.nohus.rift.generated.resources.wallet_filter_showing_types_description
import dev.nohus.rift.generated.resources.wallet_filter_transactions
import dev.nohus.rift.generated.resources.wallet_filter_types
import dev.nohus.rift.generated.resources.wallet_filter_wallets
import dev.nohus.rift.generated.resources.wallet_filter_withdrawals
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.wallet.TransferDirection
import dev.nohus.rift.wallet.WalletJournalItem
import dev.nohus.rift.wallet.WalletViewModel.UiState
import dev.nohus.rift.wallet.WalletViewModel.WalletTab

@Composable
fun AppliedFiltersDescription(
    state: UiState,
    journal: List<WalletJournalItem>?,
    tab: WalletTab,
    modifier: Modifier = Modifier,
) {
    val transactionsCount = journal?.size ?: 0
    val transactionsResource = if (tab == WalletTab.Transactions) {
        when (state.filters.direction) {
            TransferDirection.Income -> Res.plurals.wallet_filter_deposits
            TransferDirection.Expense -> Res.plurals.wallet_filter_withdrawals
            null -> Res.plurals.wallet_filter_transactions
        }
    } else {
        Res.plurals.wallet_filter_transactions
    }
    val transactions = transactionsResource.toUiText(transactionsCount, formatNumber(transactionsCount).toUiTextArgument())
    val walletsCount = journal?.map { it.wallet }?.distinct()?.size ?: 0
    val wallets = Res.plurals.wallet_filter_wallets.toUiText(walletsCount, walletsCount.toString().toUiTextArgument())
    val description = if (tab == WalletTab.Transactions && state.filters.referenceTypes.isNotEmpty()) {
        val typesCount = state.filters.referenceTypes.size
        val types = Res.plurals.wallet_filter_types.toUiText(typesCount, typesCount.toString().toUiTextArgument())
        Res.string.wallet_filter_showing_types_description.toUiText(transactions.toUiTextArgument(), wallets.toUiTextArgument(), types.toUiTextArgument())
    } else {
        val resource = if (tab == WalletTab.Transactions) Res.string.wallet_filter_showing_description else Res.string.wallet_filter_based_on_description
        resource.toUiText(transactions.toUiTextArgument(), wallets.toUiTextArgument())
    }
    val text = parseEveFormattedText(description.resolve())

    AnimatedContent(text) { text ->
        Text(
            text = text,
            style = RiftTheme.typography.bodySecondary,
            modifier = modifier,
        )
    }
}
