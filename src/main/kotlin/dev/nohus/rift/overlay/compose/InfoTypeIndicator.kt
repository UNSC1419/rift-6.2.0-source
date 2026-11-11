package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoTypeIndicator(
    text: String?,
    icon: DrawableResource,
    tooltip: String? = null,
    tint: Color? = Color.White,
    onClick: (() -> Unit)? = null,
) {
    if (text == null) return
    val content = movableContentOf {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
            modifier = Modifier.modifyIf(onClick != null) {
                pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                    .onClick { onClick?.invoke() }
            },
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = tint?.let { ColorFilter.tint(it) },
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = text,
                style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
    if (tooltip != null) {
        RiftTooltipArea(
            text = tooltip,
        ) {
            content()
        }
    } else {
        content()
    }
}
