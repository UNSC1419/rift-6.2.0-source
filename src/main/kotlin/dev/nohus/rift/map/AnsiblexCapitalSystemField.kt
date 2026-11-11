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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RequirementIcon
import dev.nohus.rift.compose.RiftAutocompleteTextField
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.map_ansiblex_capital_system_not_found
import dev.nohus.rift.generated.resources.map_ansiblex_capital_system_placeholder
import dev.nohus.rift.generated.resources.map_ansiblex_capital_system_valid
import dev.nohus.rift.map.MapAnsiblexZonesController.MapAnsiblexZonesState
import dev.nohus.rift.repositories.SolarSystemsRepository
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnsiblexCapitalSystemField(
    state: MapAnsiblexZonesState,
    label: String,
    onCapitalSystemUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
    val systemNames = remember(solarSystemsRepository) { solarSystemsRepository.getSystems().map { it.name } }
    var capitalSystemText by remember { mutableStateOf("") }
    val suggestions = remember(capitalSystemText, systemNames) {
        systemNames.asSequence()
            .filter { it.startsWith(capitalSystemText, ignoreCase = true) && !it.equals(capitalSystemText, ignoreCase = true) }
            .take(5)
            .toList()
    }

    LaunchedEffect(state.capitalSystem) {
        state.capitalSystem?.let { capitalSystemText = it.name }
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
            text = capitalSystemText,
            suggestions = suggestions,
            placeholder = stringResource(Res.string.map_ansiblex_capital_system_placeholder),
            onTextChanged = {
                capitalSystemText = it
                onCapitalSystemUpdate(it)
            },
            modifier = Modifier.width(150.dp).height(32.dp),
        )
        AnimatedVisibility(capitalSystemText.isNotBlank()) {
            RequirementIcon(
                isFulfilled = state.capitalSystem != null,
                fulfilledTooltip = stringResource(Res.string.map_ansiblex_capital_system_valid),
                notFulfilledTooltip = stringResource(Res.string.map_ansiblex_capital_system_not_found),
            )
        }
    }
}
