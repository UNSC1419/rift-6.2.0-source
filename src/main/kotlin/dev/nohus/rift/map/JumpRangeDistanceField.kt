package dev.nohus.rift.map

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftDropdownWithLabel
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.map_jump_range_eight_ly
import dev.nohus.rift.generated.resources.map_jump_range_seven_ly
import dev.nohus.rift.generated.resources.map_jump_range_six_ly
import dev.nohus.rift.generated.resources.map_jump_range_ten_ly
import dev.nohus.rift.map.MapJumpRangeController.MapJumpRangeState
import org.jetbrains.compose.resources.stringResource

@Composable
fun JumpRangeDistanceField(
    state: MapJumpRangeState,
    label: String,
    onRangeUpdate: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val jumpRanges = mapOf(
        6.0 to stringResource(Res.string.map_jump_range_six_ly),
        7.0 to stringResource(Res.string.map_jump_range_seven_ly),
        8.0 to stringResource(Res.string.map_jump_range_eight_ly),
        10.0 to stringResource(Res.string.map_jump_range_ten_ly),
    )
    RiftDropdownWithLabel(
        label = label,
        items = jumpRanges.keys.toList(),
        selectedItem = state.distanceLy.takeIf { it in jumpRanges } ?: jumpRanges.keys.first(),
        onItemSelected = onRangeUpdate,
        getItemName = { jumpRanges.getValue(it) },
        modifier = modifier.height(36.dp),
    )
}
