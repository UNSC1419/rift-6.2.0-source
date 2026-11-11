package dev.nohus.rift.compose

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import dev.nohus.rift.windowing.LocalRiftWindowState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RiftImageButton(
    resource: DrawableResource,
    size: Dp,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    tint: Color? = null,
    isFullAlpha: Boolean = false,
    iconPadding: Dp = 0.dp,
    highlightModifier: Float = 1f,
    clickSound: UiSound = UiSound.ButtonClick,
) {
    val windowOpenTimestamp = LocalRiftWindowState.current?.openTimestamp
    // This is to clear hover / press states and animations when window is reopened
    key(windowOpenTimestamp) {
        val player = rememberUiSoundPlayer()
        val pointerInteractionStateHolder = remember { PointerInteractionStateHolder() }
        val transition = updateTransition(pointerInteractionStateHolder.current)
        val highlightAlpha by transition.animateFloat {
            when (it) {
                PointerInteractionState.Normal -> 0f * highlightModifier
                PointerInteractionState.Hover -> 0.5f * highlightModifier
                PointerInteractionState.Press -> 1f * highlightModifier
            }
        }
        val iconAlpha by transition.animateFloat {
            when (it) {
                PointerInteractionState.Normal -> if (isFullAlpha) 1f else 0.75f
                PointerInteractionState.Hover -> 1f
                PointerInteractionState.Press -> 1f
            }
        }
        val clickModifier = if (onClick != null && isEnabled) {
            Modifier.onClick {
                player.play(clickSound)
                onClick()
            }
        } else {
            Modifier
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .pointerInteraction(
                    state = pointerInteractionStateHolder,
                    hoverSound = UiSound.ButtonHover.takeIf { onClick != null && isEnabled },
                )
                .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                .then(clickModifier),
        ) {
            Box(
                modifier = Modifier.size(size + iconPadding * 2),
            ) {
                if (onClick != null && isEnabled) {
                    val blur = LocalDensity.current.run { size.toPx() } * 0.35f
                    repeat(2) {
                        Image(
                            painter = painterResource(resource),
                            contentDescription = null,
                            colorFilter = tint?.let { ColorFilter.tint(tint) },
                            modifier = Modifier
                                .graphicsLayer(renderEffect = BlurEffect(blur, blur, edgeTreatment = TileMode.Decal))
                                .size(size + iconPadding * 2)
                                .alpha(highlightAlpha),
                        )
                    }
                }
            }
            Image(
                painter = painterResource(resource),
                contentDescription = null,
                colorFilter = tint?.let { ColorFilter.tint(tint) },
                modifier = Modifier
                    .modifyIf(!isEnabled) { greyScale().alpha(0.4f) }
                    .size(size)
                    .alpha(iconAlpha),
            )
        }
    }
}
