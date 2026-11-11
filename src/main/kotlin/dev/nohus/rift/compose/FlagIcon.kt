package dev.nohus.rift.compose

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_flag_alliance_tooltip
import dev.nohus.rift.generated.resources.common_flag_bad_standing_tooltip
import dev.nohus.rift.generated.resources.common_flag_corporation_tooltip
import dev.nohus.rift.generated.resources.common_flag_excellent_standing_tooltip
import dev.nohus.rift.generated.resources.common_flag_good_standing_tooltip
import dev.nohus.rift.generated.resources.common_flag_no_standing_tooltip
import dev.nohus.rift.generated.resources.common_flag_self_tooltip
import dev.nohus.rift.generated.resources.common_flag_terrible_standing_tooltip
import dev.nohus.rift.generated.resources.common_flag_war_target_tooltip
import dev.nohus.rift.generated.resources.flag_background
import dev.nohus.rift.generated.resources.flag_negative
import dev.nohus.rift.generated.resources.flag_neutral
import dev.nohus.rift.generated.resources.flag_positive
import dev.nohus.rift.generated.resources.flag_self
import dev.nohus.rift.generated.resources.flag_star
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.StandingUtils
import dev.nohus.rift.standings.getFlagColor
import dev.nohus.rift.standings.isFlagFlashing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FlagIcon(
    standing: Float,
    disableFlashing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val level = StandingUtils.getStandingLevel(standing)
    FlagIcon(level, disableFlashing, modifier)
}

@Composable
fun FlagIcon(
    standing: Standing,
    disableFlashing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val tooltip = when (standing) {
        Standing.WarTarget -> Res.string.common_flag_war_target_tooltip
        Standing.Terrible -> Res.string.common_flag_terrible_standing_tooltip
        Standing.Bad -> Res.string.common_flag_bad_standing_tooltip
        Standing.Neutral -> Res.string.common_flag_no_standing_tooltip
        Standing.Good -> Res.string.common_flag_good_standing_tooltip
        Standing.Excellent -> Res.string.common_flag_excellent_standing_tooltip
        Standing.Self -> Res.string.common_flag_self_tooltip
        Standing.Corporation -> Res.string.common_flag_corporation_tooltip
        Standing.Alliance -> Res.string.common_flag_alliance_tooltip
    }
    RiftTooltipArea(stringResource(tooltip), modifier) {
        val tint = standing.getFlagColor().copy(alpha = 0.75f)

        val isFlashing = !disableFlashing && standing.isFlagFlashing()
        val alpha by if (isFlashing) {
            val transition = rememberInfiniteTransition()
            transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = Easing { fraction -> fraction * fraction * (3f - 2f * fraction) }),
                    repeatMode = RepeatMode.Reverse,
                ),
            )
        } else {
            mutableStateOf(1f)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.alpha(alpha),
        ) {
            Image(
                painter = painterResource(Res.drawable.flag_background),
                contentDescription = null,
                colorFilter = ColorFilter.tint(tint),
                modifier = Modifier.size(12.dp),
            )
            val icon = when (standing) {
                Standing.WarTarget -> Res.drawable.flag_star
                Standing.Terrible, Standing.Bad -> Res.drawable.flag_negative
                Standing.Neutral -> Res.drawable.flag_neutral
                Standing.Good, Standing.Excellent -> Res.drawable.flag_positive
                Standing.Self -> Res.drawable.flag_self
                Standing.Corporation, Standing.Alliance -> Res.drawable.flag_star
            }
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(8.dp),
            )
        }
    }
}
