package dev.nohus.rift.overlay.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_star_1
import dev.nohus.rift.generated.resources.intel_star_10
import dev.nohus.rift.generated.resources.intel_star_11
import dev.nohus.rift.generated.resources.intel_star_12
import dev.nohus.rift.generated.resources.intel_star_2
import dev.nohus.rift.generated.resources.intel_star_3
import dev.nohus.rift.generated.resources.intel_star_4
import dev.nohus.rift.generated.resources.intel_star_5
import dev.nohus.rift.generated.resources.intel_star_6
import dev.nohus.rift.generated.resources.intel_star_7
import dev.nohus.rift.generated.resources.intel_star_8
import dev.nohus.rift.generated.resources.intel_star_9
import dev.nohus.rift.generated.resources.intel_star_base
import dev.nohus.rift.map.systemcolor.PercentageSystemColorStrategyPalettes
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun IntelActivityIndicator(count: Int, isCountAlwaysShown: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IntelStar(count)
        var isShown by remember { mutableStateOf(false) }
        var shownCount by remember { mutableIntStateOf(count) }
        var previousCount by remember { mutableIntStateOf(count) }
        val animatedCount by animateIntAsState(shownCount, animationSpec = tween(1000))
        LaunchedEffect(count) {
            if (count == previousCount) return@LaunchedEffect
            previousCount = count
            isShown = true
            delay(1000.milliseconds)
            shownCount = count
            delay(3000.milliseconds)
            isShown = false
        }
        AnimatedVisibility(isShown || (isCountAlwaysShown && count > 0)) {
            val color = if (isShown) RiftTheme.colors.textHighlighted else RiftTheme.colors.textSecondary
            val animatedColor by animateColorAsState(color)
            Text(
                text = animatedCount.toString(),
                style = RiftTheme.typography.detailBoldPrimary,
                color = animatedColor,
                modifier = Modifier.padding(top = Spacing.verySmall),
            )
        }
    }
}

@Composable
private fun IntelStar(count: Int) {
    val targetColor = getIntelActivityColor(count)
    val color by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(1000),
    )
    Box {
        IntelStarImage(Res.drawable.intel_star_base, color)
        val rays = listOf(
            Res.drawable.intel_star_1,
            Res.drawable.intel_star_2,
            Res.drawable.intel_star_3,
            Res.drawable.intel_star_4,
            Res.drawable.intel_star_5,
            Res.drawable.intel_star_6,
            Res.drawable.intel_star_7,
            Res.drawable.intel_star_8,
            Res.drawable.intel_star_9,
            Res.drawable.intel_star_10,
            Res.drawable.intel_star_11,
            Res.drawable.intel_star_12,
        )
        var shownCount by remember { mutableIntStateOf(count) }
        var animationOffset by remember { mutableIntStateOf(0) }
        LaunchedEffect(count, 2) {
            if (count > shownCount) {
                repeat(count - shownCount) {
                    shownCount++
                    delay(200.milliseconds)
                }
                shownCount = count
                repeat(rays.size) {
                    animationOffset = rays.size - 1 - it
                    delay(200.milliseconds)
                }
            } else if (count < shownCount) {
                repeat(rays.size) {
                    animationOffset = it + 1
                    delay(200.milliseconds)
                }
                repeat(shownCount - count) {
                    shownCount--
                    delay(200.milliseconds)
                }
                shownCount = count
            } else {
                shownCount = count
                animationOffset = 0
            }
        }
        rays.forEachIndexed { index, ray ->
            val isHighlighted = ((index + animationOffset) % rays.size) < shownCount
            val rayColor = if (isHighlighted) Color(0xFFFF0B0C) else Color(0xFF7D7E7E)
            IntelStarImage(ray, rayColor)
        }
    }
}

internal fun getIntelActivityColor(count: Int): Color {
    if (count <= 0) return Color(0xFF7D7E7E)
    val palette = PercentageSystemColorStrategyPalettes.negative
    return lerp(palette.first(), palette.last(), (count / 5f).coerceIn(0f, 1f))
}

@Composable
private fun IntelStarImage(image: DrawableResource, color: Color) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color),
        modifier = Modifier.size(16.dp),
    )
}
