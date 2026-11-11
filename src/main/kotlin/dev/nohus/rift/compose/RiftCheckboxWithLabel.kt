package dev.nohus.rift.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing

@Composable
fun RiftCheckboxWithLabel(
    label: String,
    labelStyle: TextStyle = RiftTheme.typography.bodyPrimary,
    tooltip: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    val row = remember(label, isChecked, onCheckedChange, isEnabled) {
        movableContentOf {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = modifier,
            ) {
                RiftCheckbox(
                    isChecked = isChecked,
                    onCheckedChange = onCheckedChange,
                    isEnabled = isEnabled,
                )
                Text(
                    text = label,
                    style = if (isEnabled) labelStyle else labelStyle.copy(color = RiftTheme.colors.textSecondary),
                )
            }
        }
    }
    if (tooltip != null) {
        RiftTooltipArea(
            text = tooltip,
            contentAnchor = Anchor.Left,
            horizontalOffset = 10.dp,
            content = row,
        )
    } else {
        row()
    }
}
