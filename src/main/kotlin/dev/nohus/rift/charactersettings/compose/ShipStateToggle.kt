package dev.nohus.rift.charactersettings.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import dev.nohus.rift.compose.RiftToggleButton
import dev.nohus.rift.compose.ToggleButtonType
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.character_settings_ship_state_docked_station
import dev.nohus.rift.generated.resources.character_settings_ship_state_docked_structure
import dev.nohus.rift.generated.resources.character_settings_ship_state_in_space
import org.jetbrains.compose.resources.stringResource

enum class ShipState {
    InSpace,
    DockedStation,
    DockedStructure,
}

@Composable
fun ShipStateToggle(
    state: ShipState,
    onChange: (ShipState) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        RiftToggleButton(
            text = stringResource(Res.string.character_settings_ship_state_in_space),
            isSelected = state == ShipState.InSpace,
            type = ToggleButtonType.Left,
            onClick = { onChange(ShipState.InSpace) },
        )
        RiftToggleButton(
            text = stringResource(Res.string.character_settings_ship_state_docked_station),
            isSelected = state == ShipState.DockedStation,
            type = ToggleButtonType.Middle,
            onClick = { onChange(ShipState.DockedStation) },
        )
        RiftToggleButton(
            text = stringResource(Res.string.character_settings_ship_state_docked_structure),
            isSelected = state == ShipState.DockedStructure,
            type = ToggleButtonType.Right,
            onClick = { onChange(ShipState.DockedStructure) },
        )
    }
}
