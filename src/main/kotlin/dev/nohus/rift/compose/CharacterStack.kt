package dev.nohus.rift.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.characters_tooltip_age_days
import dev.nohus.rift.utils.formatNumber
import org.jetbrains.compose.resources.pluralStringResource
import java.time.Duration
import java.time.Instant

@Composable
fun CharacterStack(
    characters: List<LocalCharactersRepository.LocalCharacter>,
    modifier: Modifier = Modifier,
    portraitSize: Dp = 32.dp,
    showCharacterAgeInTooltip: Boolean = true,
) {
    if (characters.isEmpty()) return
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        val now = Instant.now()
        RiftTooltipArea(
            tooltip = {
                Column(Modifier.padding(Spacing.large)) {
                    characters.sortedByDescending { it.info!!.birthday }.forEach {
                        val days = Duration.between(it.info!!.birthday, now).toDays()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                        ) {
                            AsyncCharacterPortrait(
                                characterId = it.characterId,
                                size = 32,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                            )
                            Text(
                                text = it.info.name,
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            if (showCharacterAgeInTooltip) {
                                Text(
                                    text = pluralStringResource(Res.plurals.characters_tooltip_age_days, days.toInt(), formatNumber(days)),
                                    style = RiftTheme.typography.bodyHighlighted,
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive)),
        ) {
            Layout(
                content = {
                    characters.forEach { character ->
                        AsyncCharacterPortrait(
                            characterId = character.characterId,
                            size = 64,
                            modifier = Modifier
                                .size(portraitSize)
                                .clip(CircleShape),
                        )
                    }
                },
            ) { measurables, constraints ->
                val placeables = measurables.map {
                    it.measure(
                        Constraints(
                            maxWidth = constraints.maxWidth,
                            maxHeight = constraints.maxHeight,
                        ),
                    )
                }
                val height = placeables.maxOf { it.height }
                val totalWidth = placeables.sumOf { it.width }
                val width = minOf(totalWidth, constraints.maxWidth)
                layout(width = width, height = height) {
                    val singleWidth = placeables.first().measuredWidth
                    val offset = if (placeables.size > 1) {
                        (width - singleWidth) / (placeables.size - 1)
                    } else {
                        0
                    }
                    placeables.forEachIndexed { index, placeable ->
                        val x = index * offset
                        placeable.placeRelative(x = x, y = 0)
                    }
                }
            }
        }
    }
}
