package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.overlay_distance_intel_tooltip_current_system_text
import dev.nohus.rift.generated.resources.overlay_distance_intel_tooltip_no_recent_intel_text
import dev.nohus.rift.generated.resources.overlay_distance_summary
import dev.nohus.rift.generated.resources.overlay_hostile_count
import dev.nohus.rift.generated.resources.overlay_jumps_away
import dev.nohus.rift.generated.resources.overlay_system_count
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.map.SecurityColors
import dev.nohus.rift.map.StandingsIndicator
import dev.nohus.rift.map.SystemIntelContents
import dev.nohus.rift.map.groupIntelByTime
import dev.nohus.rift.overlay.OverlayViewModel.DistanceBand
import dev.nohus.rift.overlay.OverlayViewModel.SystemIntel
import dev.nohus.rift.utils.roundSecurity
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DistanceIntelIndicator(
    bands: List<DistanceBand>,
    isCountAlwaysShown: Boolean,
    selectedDistance: Int?,
    onDistanceSelected: (Int) -> Unit,
    onSelectionDismissed: (Int) -> Unit,
) {
    if (bands.isEmpty()) return
    Row(
        modifier = Modifier
            .padding(top = 1.dp)
            .wrapContentWidth(align = Alignment.Start, unbounded = true),
    ) {
        bands.forEachIndexed { index, band ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .modifyIf(index == selectedDistance) {
                        background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, RiftTheme.colors.borderGreyLight)
                    }
                    .background(Color.Black.copy(0.01f))
                    .padding(top = Spacing.verySmall),
            ) {
                if (index > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .width(6.dp)
                            .height(1.dp)
                            .background(RiftTheme.colors.borderGreyLight),
                    )
                }
                RiftTooltipArea(
                    tooltip = { DistanceIntelTooltip(band) },
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                        .onPointerEvent(PointerEventType.Press) { onDistanceSelected(band.distance) },
                    forcedVisibility = true.takeIf { selectedDistance == band.distance },
                    onDismissRequest = { onSelectionDismissed(band.distance) }
                        .takeIf { selectedDistance == band.distance },
                    hideOnPress = false,
                ) {
                    IntelActivityIndicator(band.hostileCount, isCountAlwaysShown)
                }
                if (index < bands.lastIndex) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .width(6.dp)
                            .height(1.dp)
                            .background(RiftTheme.colors.borderGreyLight),
                    )
                }
            }
        }
    }
}

@Composable
private fun DistanceIntelTooltip(band: DistanceBand) {
    val systemsWithIntel = band.systems.filter { it.intel.isNotEmpty() }.sortedBy { it.system.name }
    ScrollbarColumn(
        isScrollbarConditional = true,
        isFillWidth = false,
        modifier = Modifier.widthIn(max = 320.dp).heightIn(max = 400.dp).padding(Spacing.small),
    ) {
        Text(
            text = if (band.distance == 0) {
                stringResource(Res.string.overlay_distance_intel_tooltip_current_system_text)
            } else {
                Res.plurals.overlay_jumps_away.toUiText(band.distance, band.distance.toUiTextArgument()).resolve()
            },
            style = RiftTheme.typography.detailBoldPrimary,
        )
        Text(
            text = Res.string.overlay_distance_summary.toUiText(Res.plurals.overlay_system_count.toUiText(band.systems.size, band.systems.size.toUiTextArgument()).toUiTextArgument(), Res.plurals.overlay_hostile_count.toUiText(band.hostileCount, band.hostileCount.toUiTextArgument()).toUiTextArgument()).resolve(),
            style = RiftTheme.typography.detailSecondary,
        )
        if (systemsWithIntel.isEmpty()) {
            Text(stringResource(Res.string.overlay_distance_intel_tooltip_no_recent_intel_text), style = RiftTheme.typography.detailSecondary)
        } else {
            systemsWithIntel.forEach { systemIntel ->
                SystemIntelDetails(systemIntel)
            }
        }
    }
}

@Composable
internal fun SystemIntelDetails(systemIntel: SystemIntel) {
    Divider(color = RiftTheme.colors.divider, modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.verySmall))
    Row(verticalAlignment = Alignment.CenterVertically) {
        val systemName = buildAnnotatedString {
            append(systemIntel.system.name)
            append(" ")
            withStyle(SpanStyle(color = SecurityColors[systemIntel.system.security])) {
                append(systemIntel.system.security.roundSecurity().toString())
            }
        }
        Text(systemName, style = RiftTheme.typography.detailBoldPrimary)
        StandingsIndicator(systemIntel.system.security, systemIntel.systemStatus)
    }
    SystemIntelContents(groupIntelByTime(systemIntel.intel), systemIntel.system)
}
