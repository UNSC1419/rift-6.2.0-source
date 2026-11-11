package dev.nohus.rift.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import dev.nohus.rift.compose.theme.EveColors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing

@Composable
fun KeyName(
    key: String,
    name: String? = null,
    suffix: String? = null,
    style: TextStyle = RiftTheme.typography.bodyPrimary,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        if (name != null) {
            Text(
                text = name,
                style = style,
                modifier = Modifier.padding(end = Spacing.medium),
            )
        }
        Box(
            modifier = Modifier
                .background(EveColors.coalBlack)
                .padding(Spacing.small),
        ) {
            Text(
                text = key,
                style = style.copy(
                    color = RiftTheme.typography.bodySecondary.color,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        if (suffix != null) {
            Text(
                text = suffix,
                style = style,
                modifier = Modifier.padding(start = Spacing.medium),
            )
        }
    }
}
