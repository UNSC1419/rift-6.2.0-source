package dev.nohus.rift.wallet.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.nohus.rift.compose.MulticolorIconType
import dev.nohus.rift.compose.RiftDropdown
import dev.nohus.rift.compose.RiftMulticolorIcon
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.wallet_history_description
import dev.nohus.rift.generated.resources.wallet_timespan_days
import dev.nohus.rift.generated.resources.wallet_timespan_hours
import dev.nohus.rift.wallet.WalletFilters
import dev.nohus.rift.wallet.WalletViewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.time.Duration

@Composable
fun TimespanDropdown(
    state: WalletViewModel.UiState,
    onFiltersUpdate: (WalletFilters) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = modifier,
    ) {
        val timespanNames = (state.availableTimestamps + state.filters.timeSpan).associateWith {
            if (it < Duration.ofDays(2)) {
                pluralStringResource(Res.plurals.wallet_timespan_hours, it.toHours().toInt(), it.toHours().toString())
            } else {
                pluralStringResource(Res.plurals.wallet_timespan_days, it.toDays().toInt(), it.toDays().toString())
            }
        }
        RiftDropdown(
            items = state.availableTimestamps,
            selectedItem = state.filters.timeSpan,
            onItemSelected = { onFiltersUpdate(state.filters.copy(timeSpan = it)) },
            getItemName = { timespanNames.getValue(it) },
        )

        RiftTooltipArea(
            text = stringResource(Res.string.wallet_history_description),
        ) {
            RiftMulticolorIcon(
                type = MulticolorIconType.Info,
            )
        }
    }
}
