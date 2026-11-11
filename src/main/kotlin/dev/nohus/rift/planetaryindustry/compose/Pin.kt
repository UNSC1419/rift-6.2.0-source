package dev.nohus.rift.planetaryindustry.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.pi_route
import dev.nohus.rift.generated.resources.planetary_industry_average_hourly_output_caption
import dev.nohus.rift.generated.resources.planetary_industry_cpu_caption
import dev.nohus.rift.generated.resources.planetary_industry_cpu_usage
import dev.nohus.rift.generated.resources.planetary_industry_current_cycle_caption
import dev.nohus.rift.generated.resources.planetary_industry_current_cycle_output_caption
import dev.nohus.rift.generated.resources.planetary_industry_factory_input_quantity
import dev.nohus.rift.generated.resources.planetary_industry_idle_duration
import dev.nohus.rift.generated.resources.planetary_industry_idle_status
import dev.nohus.rift.generated.resources.planetary_industry_in_production_caption
import dev.nohus.rift.generated.resources.planetary_industry_input_heading
import dev.nohus.rift.generated.resources.planetary_industry_none_caption
import dev.nohus.rift.generated.resources.planetary_industry_output_units
import dev.nohus.rift.generated.resources.planetary_industry_power_caption
import dev.nohus.rift.generated.resources.planetary_industry_power_usage
import dev.nohus.rift.generated.resources.planetary_industry_program_inactive_caption
import dev.nohus.rift.generated.resources.planetary_industry_route_quantity
import dev.nohus.rift.generated.resources.planetary_industry_storage_caption
import dev.nohus.rift.generated.resources.planetary_industry_storage_usage
import dev.nohus.rift.generated.resources.planetary_industry_stored_items_heading
import dev.nohus.rift.generated.resources.planetary_industry_stored_quantity
import dev.nohus.rift.generated.resources.planetary_industry_time_remaining_caption
import dev.nohus.rift.generated.resources.planetary_industry_toggle_routes_action
import dev.nohus.rift.generated.resources.planetary_industry_total_output_caption
import dev.nohus.rift.generated.resources.planetary_industry_waiting_resources_caption
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.planetaryindustry.models.Colony
import dev.nohus.rift.planetaryindustry.models.Commodities
import dev.nohus.rift.planetaryindustry.models.Pin
import dev.nohus.rift.planetaryindustry.models.Route
import dev.nohus.rift.planetaryindustry.models.Usage
import dev.nohus.rift.planetaryindustry.models.getIcon
import dev.nohus.rift.planetaryindustry.models.getName
import dev.nohus.rift.planetaryindustry.simulation.ExtractionSimulation.Companion.getProgramOutputPrediction
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.utils.formatDurationCompact
import dev.nohus.rift.utils.formatDurationLong
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.formatNumberCompact
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun Pin(
    pin: Pin,
    colony: Colony,
    now: Instant,
    isAdvancingTime: Boolean,
    onRequestSimulation: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        PinSphere(colony, pin, 64.dp, now, isAdvancingTime, onRequestSimulation)

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.small)
                    .background(RiftTheme.colors.windowBackgroundSecondary.copy(alpha = 0.75f))
                    .padding(vertical = Spacing.verySmall, horizontal = Spacing.small),
            ) {
                Image(
                    painter = painterResource(pin.getIcon()),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = pin.getName().resolve(),
                    style = RiftTheme.typography.headerPrimary,
                )
            }

            var isRoutesExpanded by remember { mutableStateOf(false) }

            Row {
                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    when (pin) {
                        is Pin.CommandCenter -> {
                            CommandCenter(pin, colony.usage)
                        }

                        is Pin.Extractor -> {
                            Extractor(now, pin, colony.routes, isAdvancingTime, onRequestSimulation)
                        }

                        is Pin.Factory -> {
                            Factory(now, pin, colony.routes, isAdvancingTime, onRequestSimulation)
                        }

                        is Pin.Launchpad -> {
                            Storage(pin.contents, pin.capacityUsed, 10_000f)
                        }

                        is Pin.Storage -> {
                            Storage(pin.contents, pin.capacityUsed, 12_000f)
                        }
                    }
                }

                val hasRoutes = colony.routes.any { it.sourcePinId == pin.id || it.destinationPinId == pin.id }
                if (hasRoutes) {
                    RiftTooltipArea(
                        text = stringResource(Res.string.planetary_industry_toggle_routes_action),
                    ) {
                        RiftImageButton(
                            resource = Res.drawable.pi_route,
                            size = 32.dp,
                            onClick = { isRoutesExpanded = !isRoutesExpanded },
                            highlightModifier = 0.5f,
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isRoutesExpanded) {
                Routes(colony, pin)
            }
        }
    }
}

@Composable
fun CommandCenter(
    pin: Pin.CommandCenter,
    usage: Usage,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val cpuPercent = usage.cpuUsage / usage.cpuSupply.toFloat()
            val powerPercent = usage.powerUsage / usage.powerSupply.toFloat()
            AnnotatedProgressBar(
                title = stringResource(Res.string.planetary_industry_cpu_caption, String.format("%.1f%%", cpuPercent * 100)),
                percentage = cpuPercent,
                description = stringResource(Res.string.planetary_industry_cpu_usage, usage.cpuUsage, usage.cpuSupply),
                color = Color(0xFF00FFE3),
            )
            AnnotatedProgressBar(
                title = stringResource(Res.string.planetary_industry_power_caption, String.format("%.1f%%", powerPercent * 100)),
                percentage = powerPercent,
                description = stringResource(Res.string.planetary_industry_power_usage, usage.powerUsage, usage.powerSupply),
                color = Color(0xFFF3252F),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Storage(pin.contents, pin.capacityUsed, 500f)
        }
    }
}

@Composable
fun Extractor(
    currentTime: Instant,
    pin: Pin.Extractor,
    routes: List<Route>,
    isAdvancingTime: Boolean,
    onRequestSimulation: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        if (pin.installTime != null && pin.expiryTime != null && pin.cycleTime != null && pin.baseValue != null && pin.productType != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (pin.isActive) {
                    val elapsedTime = Duration.between(pin.installTime, currentTime)
                    val cycleProgressDuration = Duration.ofSeconds(elapsedTime.toSeconds() % pin.cycleTime.toSeconds())
                    val totalCycleDuration = pin.cycleTime
                    AnnotatedProgressBar(
                        title = stringResource(Res.string.planetary_industry_current_cycle_caption),
                        duration = cycleProgressDuration,
                        totalDuration = totalCycleDuration,
                        color = RiftTheme.colors.textHighlighted,
                        isAdvancingTime = isAdvancingTime,
                        onFinished = onRequestSimulation,
                    )
                } else {
                    val description = pin.lastRunTime?.let {
                        val totalWaitTime = Duration.between(it + pin.cycleTime, currentTime)
                        if (totalWaitTime > Duration.ZERO) {
                            stringResource(Res.string.planetary_industry_idle_duration, formatDurationCompact(totalWaitTime).resolve())
                        } else {
                            null
                        }
                    } ?: stringResource(Res.string.planetary_industry_idle_status)
                    AnnotatedProgressBar(
                        title = stringResource(Res.string.planetary_industry_program_inactive_caption),
                        percentage = 0f,
                        description = description,
                        color = RiftTheme.colors.textHighlighted,
                        titleStyle = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.textRed, fontWeight = FontWeight.Bold),
                    )
                }

                ProducedCommodity(pin, routes, pin.productType)

                if (pin.isActive) {
                    val timeToExpiry = Duration.between(currentTime, pin.expiryTime)
                    TitledText(stringResource(Res.string.planetary_industry_time_remaining_caption), formatDurationLong(timeToExpiry).resolve())
                }
            }

            if (pin.isActive) {
                val totalProgramDuration = Duration.between(pin.installTime, pin.expiryTime)
                val elapsedTime = Duration.between(pin.installTime, currentTime)
                val totalCycles = (totalProgramDuration.toSeconds() / pin.cycleTime.toSeconds()).toInt()
                val currentCycle = (elapsedTime.toSeconds() / pin.cycleTime.toSeconds()).toInt().takeIf { it < totalCycles }

                val prediction = remember(pin.baseValue, pin.cycleTime, totalCycles) {
                    getProgramOutputPrediction(pin.baseValue, pin.cycleTime, totalCycles)
                }
                val currentCycleMined = currentCycle?.let { prediction[it] }
                val totalMined = prediction.sum()
                val averagePerHour = (totalMined / totalProgramDuration.toHours().toFloat()).roundToInt()

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                    TitledText(stringResource(Res.string.planetary_industry_average_hourly_output_caption), stringResource(Res.string.planetary_industry_output_units, formatNumber(averagePerHour)))
                    TitledText(stringResource(Res.string.planetary_industry_current_cycle_output_caption), currentCycleMined?.let { stringResource(Res.string.planetary_industry_output_units, formatNumber(currentCycleMined)) } ?: stringResource(Res.string.planetary_industry_none_caption))
                    TitledText(stringResource(Res.string.planetary_industry_total_output_caption), stringResource(Res.string.planetary_industry_output_units, formatNumber(totalMined)))
                }

                ExtractionBarGraph(
                    prediction = prediction,
                    totalProgramDuration = totalProgramDuration,
                    elapsedTime = elapsedTime,
                )
            }
        } else {
            // Not set up
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnnotatedProgressBar(
                    title = stringResource(Res.string.planetary_industry_program_inactive_caption),
                    percentage = 0f,
                    description = stringResource(Res.string.planetary_industry_idle_status),
                    color = RiftTheme.colors.textHighlighted,
                    titleStyle = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.textRed, fontWeight = FontWeight.Bold),
                )

                ProducedCommodity(pin, routes, pin.productType)
            }
        }
    }
}

@Composable
fun Factory(
    currentTime: Instant,
    pin: Pin.Factory,
    routes: List<Route>,
    isAdvancingTime: Boolean,
    onRequestSimulation: () -> Unit,
) {
    if (pin.schematic != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pin.isActive && pin.lastRunTime != null) {
                val cycleProgressDuration = Duration.between(pin.lastRunTime, currentTime)
                val totalCycleDuration = pin.schematic.cycleTime
                AnnotatedProgressBar(
                    title = stringResource(Res.string.planetary_industry_in_production_caption),
                    duration = cycleProgressDuration,
                    totalDuration = totalCycleDuration,
                    color = RiftTheme.colors.textHighlighted,
                    titleStyle = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.textGreen, fontWeight = FontWeight.Bold),
                    isAdvancingTime = isAdvancingTime,
                    onFinished = onRequestSimulation,
                )
            } else {
                val description = pin.lastCycleStartTime?.let {
                    val totalWaitTime = Duration.between(it + pin.schematic.cycleTime, currentTime)
                    if (totalWaitTime > Duration.ZERO) {
                        stringResource(Res.string.planetary_industry_idle_duration, formatDurationCompact(totalWaitTime).resolve())
                    } else {
                        null
                    }
                } ?: stringResource(Res.string.planetary_industry_idle_status)
                AnnotatedProgressBar(
                    title = stringResource(Res.string.planetary_industry_waiting_resources_caption),
                    percentage = 0f,
                    description = description,
                    color = RiftTheme.colors.textHighlighted,
                )
            }

            ProducedCommodity(pin, routes, pin.schematic.outputType)

            Column {
                Text(
                    text = stringResource(Res.string.planetary_industry_input_heading),
                    style = RiftTheme.typography.bodyHighlighted,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    pin.schematic.inputs.forEach { [type, quantity] ->
                        val stored = pin.contents[type] ?: 0
                        val percentage = (stored / quantity.toFloat()).coerceIn(0f, 1f)
                        RiftTooltipArea(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = RiftTheme.colors.textPrimary)) {
                                    append(type.name)
                                }
                                append(" ")
                                withStyle(SpanStyle(color = RiftTheme.colors.textSecondary)) {
                                    append(Commodities.getTierName(type.name).resolve())
                                }
                                appendLine()
                                withStyle(SpanStyle(color = RiftTheme.colors.textPrimary)) {
                                    append(pluralStringResource(Res.plurals.planetary_industry_factory_input_quantity, quantity.coerceIn(0, Int.MAX_VALUE.toLong()).toInt(), stored, quantity))
                                }
                            },
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(RiftTheme.colors.progressBarBackground),
                            ) {
                                var animatedPercentage by remember { mutableStateOf(0f) }
                                val progressWidth by animateDpAsState(
                                    animatedPercentage * 32.dp,
                                    spring(stiffness = StiffnessLow),
                                )
                                LaunchedEffect(percentage) { animatedPercentage = percentage }
                                Box(
                                    modifier = Modifier
                                        .size(progressWidth, 32.dp)
                                        .background(RiftTheme.colors.progressBarProgress),
                                ) {}
                                AsyncTypeIcon(
                                    type = type,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Not set up
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnnotatedProgressBar(
                title = stringResource(Res.string.planetary_industry_program_inactive_caption),
                percentage = 0f,
                description = stringResource(Res.string.planetary_industry_idle_status),
                color = RiftTheme.colors.textHighlighted,
                titleStyle = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.textRed, fontWeight = FontWeight.Bold),
            )

            ProducedCommodity(pin, routes, null)
        }
    }
}

@Composable
fun Storage(
    contents: Map<Type, Long>,
    capacityUsed: Float,
    maxVolume: Float?,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (maxVolume != null) {
            AnnotatedProgressBar(
                title = stringResource(Res.string.planetary_industry_storage_caption),
                percentage = capacityUsed / maxVolume,
                description = stringResource(Res.string.planetary_industry_storage_usage, String.format("%.0f", capacityUsed), String.format("%.0f", maxVolume)),
                color = RiftTheme.colors.progressBarProgress,
            )
        }

        Column {
            Text(
                text = stringResource(Res.string.planetary_industry_stored_items_heading),
                style = RiftTheme.typography.bodyHighlighted,
            )
            if (contents.isEmpty()) {
                Text(
                    text = stringResource(Res.string.planetary_industry_none_caption),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                contents.forEach { [type, quantity] ->
                    RiftTooltipArea(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = RiftTheme.colors.textPrimary)) {
                                append(type.name)
                            }
                            append(" ")
                            withStyle(SpanStyle(color = RiftTheme.colors.textSecondary)) {
                                append(Commodities.getTierName(type.name).resolve())
                            }
                            appendLine()
                            append(pluralStringResource(Res.plurals.planetary_industry_stored_quantity, quantity.coerceIn(0, Int.MAX_VALUE.toLong()).toInt(), formatNumber(quantity)))
                        },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val formatted = if (quantity >= 10_000) formatNumberCompact(quantity) else formatNumber(quantity)
                            Text(
                                text = stringResource(Res.string.planetary_industry_route_quantity, formatted),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            AsyncTypeIcon(
                                type = type,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
