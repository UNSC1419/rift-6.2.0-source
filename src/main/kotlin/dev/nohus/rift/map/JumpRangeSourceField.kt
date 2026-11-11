package dev.nohus.rift.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.compose.RequirementIcon
import dev.nohus.rift.compose.RiftAutocompleteTextField
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.map_ansiblex_capital_system_valid
import dev.nohus.rift.generated.resources.map_jump_range_source_not_found
import dev.nohus.rift.generated.resources.map_jump_range_source_placeholder
import dev.nohus.rift.generated.resources.map_jump_range_source_valid_character
import dev.nohus.rift.map.MapJumpRangeController.MapJumpRangeState
import dev.nohus.rift.map.MapJumpRangeController.MapJumpRangeTarget
import dev.nohus.rift.repositories.SolarSystemsRepository
import org.jetbrains.compose.resources.stringResource

@Composable
fun JumpRangeSourceField(
    state: MapJumpRangeState,
    label: String,
    onTargetUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
    val charactersRepository: LocalCharactersRepository = remember { koin.get() }
    val characters by charactersRepository.characters.collectAsState()
    val systemNames = remember(solarSystemsRepository) { solarSystemsRepository.getSystems().map { it.name } }
    var targetText by remember { mutableStateOf("") }
    val suggestions = remember(targetText, characters, systemNames) {
        (characters.mapNotNull { it.info?.name } + systemNames)
            .asSequence()
            .filter { it.startsWith(targetText, ignoreCase = true) && !it.equals(targetText, ignoreCase = true) }
            .take(5)
            .toList()
    }

    LaunchedEffect(state.target) {
        when (val target = state.target) {
            is MapJumpRangeTarget.Character -> targetText = target.name
            is MapJumpRangeTarget.System -> targetText = target.name
            null -> {}
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(36.dp),
    ) {
        Text(
            text = label,
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.padding(end = Spacing.small),
        )
        RiftAutocompleteTextField(
            text = targetText,
            suggestions = suggestions,
            placeholder = stringResource(Res.string.map_jump_range_source_placeholder),
            onTextChanged = {
                targetText = it
                onTargetUpdate(it)
            },
            modifier = Modifier.width(150.dp).height(32.dp),
        )
        AnimatedVisibility(targetText.isNotBlank()) {
            RequirementIcon(
                isFulfilled = state.target != null,
                fulfilledTooltip = when (state.target) {
                    is MapJumpRangeTarget.Character -> stringResource(Res.string.map_jump_range_source_valid_character)
                    is MapJumpRangeTarget.System -> stringResource(Res.string.map_ansiblex_capital_system_valid)
                    null -> ""
                },
                notFulfilledTooltip = stringResource(Res.string.map_jump_range_source_not_found),
            )
        }
    }
}
