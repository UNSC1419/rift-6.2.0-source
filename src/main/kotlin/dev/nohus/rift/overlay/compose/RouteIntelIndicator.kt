package dev.nohus.rift.overlay.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.onClick
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.ClickableSystem
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.overlay_kill_count
import dev.nohus.rift.generated.resources.overlay_kills_at_location
import dev.nohus.rift.generated.resources.overlay_kills_elsewhere
import dev.nohus.rift.generated.resources.overlay_kills_in_system
import dev.nohus.rift.generated.resources.overlay_next_system_summary_kills_last_hour_text
import dev.nohus.rift.generated.resources.overlay_next_system_summary_kills_text
import dev.nohus.rift.generated.resources.overlay_next_system_summary_kills_text_2
import dev.nohus.rift.generated.resources.overlay_next_system_summary_no_known_hostiles_no_kills_text
import dev.nohus.rift.generated.resources.overlay_next_system_summary_system_clear_text
import dev.nohus.rift.generated.resources.overlay_route_hostile_count
import dev.nohus.rift.generated.resources.overlay_route_intel_indicator_no_destination_text
import dev.nohus.rift.generated.resources.overlay_route_intel_indicator_route_intel_text
import dev.nohus.rift.generated.resources.overlay_route_jumps
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.map.SecurityColors
import dev.nohus.rift.map.SystemIntelContents
import dev.nohus.rift.map.groupIntelByTime
import dev.nohus.rift.overlay.OverlayViewModel.RouteIntel
import dev.nohus.rift.overlay.OverlayViewModel.RouteStop
import dev.nohus.rift.overlay.RouteKillmailsRepository.SystemKills
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.StandingsRepository
import dev.nohus.rift.standings.getSystemColor
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.roundSecurity
import dev.nohus.rift.utils.toURIOrNull
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val routeWidth = 256.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RouteIntelIndicator(
    route: RouteIntel?,
    killsBySystemId: Map<Int, SystemKills>,
    characterAllianceId: Int?,
    selectedPosition: Int?,
    onPositionSelected: (Int) -> Unit,
    onSelectionDismissed: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.width(routeWidth),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = stringResource(Res.string.overlay_route_intel_indicator_route_intel_text),
                style = RiftTheme.typography.headlineHighlighted.copy(fontWeight = FontWeight.Bold),
            )
            if (route != null) {
                Text(
                    text = Res.plurals.overlay_route_jumps.toUiText(route.stops.size, route.stops.size.toUiTextArgument()).resolve(),
                    style = RiftTheme.typography.detailHighlighted.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 1.dp),
                )
            }
        }
        if (route == null) {
            Text(
                text = stringResource(Res.string.overlay_route_intel_indicator_no_destination_text),
                style = RiftTheme.typography.bodyHighlighted,
                modifier = Modifier.padding(top = Spacing.small),
            )
        }
        Spacer(Modifier.height(Spacing.small))

        val nextStop = route?.stops?.firstOrNull() ?: return
        val nextSystemKills = killsBySystemId[nextStop.systemIntel.system.id]
        val stripeOffset by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        )
        Box {
            NextSystemSummary(nextStop, nextSystemKills, characterAllianceId)
            Canvas(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(y = 5.dp)
                    .size(width = 10.dp, height = 5.dp),
            ) {
                val pointer = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                }
                drawPath(pointer, Color.Black.copy(alpha = 0.2f))
            }
        }
        Spacer(Modifier.height(Spacing.small))
        Spacer(Modifier.height(Spacing.medium))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
            verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
            modifier = Modifier
                .padding(start = 1.dp)
                .fillMaxWidth(),
        ) {
            route.stops.forEach { stop ->
                val kills = killsBySystemId[stop.systemIntel.system.id]
                val hasActivity = stop.hostileCount > 0 || (kills?.count ?: 0) > 0
                val color by animateColorAsState(
                    targetValue = if (hasActivity) {
                        Color(0xFFFF0B0C)
                    } else {
                        getRouteStopColor(stop, characterAllianceId)
                    },
                    animationSpec = tween(1000),
                )
                RiftTooltipArea(
                    tooltip = { DisplayRouteIntelTooltip(stop, kills, characterAllianceId) },
                    modifier = Modifier
                        .modifyIf(stop.position == selectedPosition) {
                            border(1.dp, RiftTheme.colors.textHighlighted)
                        }
                        .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                        .onPointerEvent(PointerEventType.Press) { onPositionSelected(stop.position) },
                    forcedVisibility = true.takeIf { selectedPosition == stop.position },
                    onDismissRequest = { onSelectionDismissed(stop.position) }
                        .takeIf { selectedPosition == stop.position },
                    hideOnPress = false,
                ) {
                    if (hasActivity) {
                        Canvas(modifier = Modifier.size(8.dp)) {
                            drawRect(color.copy(alpha = 0.01f))
                            val stripeWidth = 2.dp.toPx()
                            val stripePeriod = stripeWidth * 2
                            val slant = size.height
                            var stripeX = -stripePeriod - slant + stripeOffset * stripePeriod
                            clipRect {
                                while (stripeX < size.width + stripePeriod) {
                                    val stripe = Path().apply {
                                        moveTo(stripeX, size.height)
                                        lineTo(stripeX + stripeWidth, size.height)
                                        lineTo(stripeX + stripeWidth + slant, 0f)
                                        lineTo(stripeX + slant, 0f)
                                        close()
                                    }
                                    drawPath(stripe, color)
                                    stripeX += stripePeriod
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.size(8.dp).background(color))
                    }
                }
            }
        }
    }
}

@Composable
private fun getRouteStopColor(
    stop: RouteStop,
    characterAllianceId: Int?,
): Color {
    val security = stop.systemIntel.system.security.roundSecurity()
    val allianceId = stop.systemIntel.systemStatus?.sovereignty?.alliance?.allianceId
    val standingsRepository: StandingsRepository = remember { koin.get() }
    val standing = standingsRepository.getStandingLevel(allianceId, null, null, includeWars = false)
    return if (security == 1.0) {
        Color(0xFF71E754)
    } else if (security >= 0.5) {
        Color(0xFF71E754)
    } else if (security > 0.0) {
        Color(0xFFF5FF83)
    } else if (allianceId != null && allianceId == characterAllianceId) {
        Color(0xFF2C75E1)
    } else {
        standing.getSystemColor()
    }
}

@Composable
private fun NextSystemSummary(stop: RouteStop, kills: SystemKills?, characterAllianceId: Int?) {
    val systemIntel = stop.systemIntel
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(Spacing.medium),
    ) {
        ClickableSystem(systemIntel.system.id) {
            Text(
                text = systemIntel.system.name,
                style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = SecurityColors[systemIntel.system.security])) {
                    append(systemIntel.system.security.roundSecurity().toString())
                }
            },
            style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
        )
        SpaceType(
            system = systemIntel.system,
            systemStatus = systemIntel.systemStatus,
            characterAllianceId = characterAllianceId,
            iconOnly = true,
        )
        val killsText = when {
            kills?.count != null -> Res.string.overlay_kill_count.toUiText(kills.count.toUiTextArgument()).resolve()
            kills?.isUnavailable == true -> stringResource(Res.string.overlay_next_system_summary_kills_text)
            else -> stringResource(Res.string.overlay_next_system_summary_kills_text_2)
        }
        Row(modifier = Modifier.padding(top = 1.dp, start = Spacing.verySmall)) {
            val style = RiftTheme.typography.detailHighlighted.copy(fontWeight = FontWeight.Bold)
            if (stop.hostileCount == 0 && kills?.count == 0) {
                RiftTooltipArea(text = stringResource(Res.string.overlay_next_system_summary_no_known_hostiles_no_kills_text)) {
                    Text(stringResource(Res.string.overlay_next_system_summary_system_clear_text), style = style)
                }
            } else {
                Text(Res.string.overlay_route_hostile_count.toUiText(stop.hostileCount.toUiTextArgument()).resolve(), style = style)
                RiftTooltipArea(text = stringResource(Res.string.overlay_next_system_summary_kills_last_hour_text)) {
                    Text(killsText, style = style)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DisplayRouteIntelTooltip(stop: RouteStop, kills: SystemKills?, characterAllianceId: Int?) {
    val systemIntel = stop.systemIntel
    ScrollbarColumn(
        isScrollbarConditional = true,
        isFillWidth = false,
        modifier = Modifier.widthIn(max = 320.dp).heightIn(max = 400.dp).padding(Spacing.small),
    ) {
        NextSystemSummary(stop, kills, characterAllianceId)
        kills?.groups?.takeIf { it.isNotEmpty() }?.let { groups ->
            groups.forEach { group ->
                val text = if (group.locationText != null) {
                    Res.plurals.overlay_kills_at_location.toUiText(group.count, group.count.toUiTextArgument(), group.locationText.toUiTextArgument()).resolve()
                } else {
                    val resource = if (groups.size == 1) Res.plurals.overlay_kills_in_system else Res.plurals.overlay_kills_elsewhere
                    resource.toUiText(group.count, group.count.toUiTextArgument()).resolve()
                }
                Text(
                    text = text,
                    style = RiftTheme.typography.detailSecondary,
                    modifier = Modifier
                        .padding(start = Spacing.medium, bottom = Spacing.small)
                        .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                        .onClick {
                            "https://zkillboard.com/system/${systemIntel.system.id}/".toURIOrNull()?.openBrowser()
                        },
                )
            }
        }
        if (systemIntel.intel.isNotEmpty()) {
            Divider(color = RiftTheme.colors.divider, modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.verySmall))
            SystemIntelContents(groupIntelByTime(systemIntel.intel), systemIntel.system)
        }
    }
}
