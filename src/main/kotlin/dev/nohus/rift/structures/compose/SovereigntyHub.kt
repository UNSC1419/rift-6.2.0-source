package dev.nohus.rift.structures.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.RiftCircularProgressGauge
import dev.nohus.rift.compose.RiftIndicatorDot
import dev.nohus.rift.compose.RiftProgressBar
import dev.nohus.rift.compose.RiftSolarSystemChip
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.greyScale
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.text.ParseEveFormattedTextUseCase
import dev.nohus.rift.compose.text.LinkedText
import dev.nohus.rift.compose.theme.EveColors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_critical_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_depleted_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_depletion_tooltip
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_empty_tooltip
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_full_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_low_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_rate_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_time_days
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_time_hour_clock
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_time_hours
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_time_minute_clock
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_time_weeks
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_fuel_unused_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_power_label
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_title
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_export_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_export_mode
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_export_pending_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_heading
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_idle_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_idle_mode
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_idle_pending_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_import_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_import_mode
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_import_pending_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_import_source_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_pending_heading
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_transit_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_transit_mode
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_transport_transit_pending_description
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_fuel_consumption_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_fuel_startup_cost_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_fuel_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_low_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_offline_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_online_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_pending_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_power_allocation_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_power_production_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_unknown_status
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_workforce_allocation_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_upgrade_workforce_production_value
import dev.nohus.rift.generated.resources.structures_sovereignty_hub_workforce_label
import dev.nohus.rift.generated.resources.structures_vulnerability_label
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.arrow_down_16px
import dev.nohus.rift.generated.resources.arrow_right_16px
import dev.nohus.rift.generated.resources.arrow_up_16px
import dev.nohus.rift.generated.resources.magmatic_gas_16px
import dev.nohus.rift.generated.resources.power_16px
import dev.nohus.rift.generated.resources.superionic_ice_16px
import dev.nohus.rift.generated.resources.workforce_16px
import dev.nohus.rift.generated.resources.workforce_32px
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.SOLAR_SYSTEM_TYPE_ID
import dev.nohus.rift.network.esi.models.SovereigntyHubPower
import dev.nohus.rift.network.esi.models.SovereigntyHubUpgradePowerState
import dev.nohus.rift.network.esi.models.SovereigntyHubWorkforce
import dev.nohus.rift.structures.EquinoxStructuresRepository.SovereigntyHub
import dev.nohus.rift.structures.EquinoxStructuresRepository.SovereigntyHubReagent
import dev.nohus.rift.structures.EquinoxStructuresRepository.SovereigntyHubWorkforceTransport
import dev.nohus.rift.structures.SovereigntyReagent
import dev.nohus.rift.utils.formatDateTime
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.multiplyBrightness
import dev.nohus.rift.utils.withColor
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jsoup.nodes.Entities
import java.time.Duration
import java.time.Instant
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun SovereigntyHub(
    sovereigntyHub: SovereigntyHub,
    now: Instant,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SovereigntyHubTitleRow(sovereigntyHub)
        Spacer(Modifier.height(Spacing.mediumLarge))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Column {
                Row {
                    sovereigntyHub.reagentBay.forEach { reagent ->
                        ReagentGauge(reagent, sovereigntyHub, now)
                    }
                }
                Spacer(Modifier.height(Spacing.mediumLarge))
                SovereigntyHubUpgrades(sovereigntyHub)
            }
            Column {
                SovereigntyHubPower(sovereigntyHub.resources.power)
                Spacer(Modifier.height(Spacing.mediumLarge))
                SovereigntyHubWorkforce(sovereigntyHub.resources.workforce, sovereigntyHub.workforceTransportConfiguration, sovereigntyHub.workforceTransportState)
                Spacer(Modifier.height(Spacing.mediumLarge))
                Vulnerability(stringResource(Res.string.structures_vulnerability_label), sovereigntyHub.vulnerabilityWindow, now)
            }
        }
    }
}

@Composable
private fun SovereigntyHubUpgrades(sovereigntyHub: SovereigntyHub) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        sovereigntyHub.upgrades.forEach { upgrade ->
            val [name, color, isActive] = when (upgrade.powerState) {
                SovereigntyHubUpgradePowerState.Online -> Triple(stringResource(Res.string.structures_sovereignty_hub_upgrade_online_status), EveColors.successGreen, true)
                SovereigntyHubUpgradePowerState.Low -> Triple(stringResource(Res.string.structures_sovereignty_hub_upgrade_low_status), EveColors.warningOrange, true)
                SovereigntyHubUpgradePowerState.Pending -> Triple(stringResource(Res.string.structures_sovereignty_hub_upgrade_pending_status), EveColors.warningOrange, true)
                SovereigntyHubUpgradePowerState.Offline -> Triple(stringResource(Res.string.structures_sovereignty_hub_upgrade_offline_status), RiftTheme.colors.textSecondary, false)
                SovereigntyHubUpgradePowerState.Unspecified -> Triple(stringResource(Res.string.structures_sovereignty_hub_upgrade_unknown_status), RiftTheme.colors.textSecondary, false)
            }
            val textColor = if (upgrade.powerState == SovereigntyHubUpgradePowerState.Online) {
                RiftTheme.colors.textPrimary
            } else {
                RiftTheme.colors.textSecondary
            }

            RiftTooltipArea(
                tooltip = @Composable {
                    Column(
                        modifier = Modifier.padding(Spacing.large),
                    ) {
                        Text(
                            text = upgrade.type.name,
                            style = RiftTheme.typography.bodyPrimary,
                        )
                        Text(
                            text = name,
                            style = RiftTheme.typography.bodyPrimary.copy(color = color),
                        )
                        Spacer(Modifier.height(Spacing.small))
                        if (upgrade.details.powerConsumption > 0) {
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hub_upgrade_power_allocation_value, formatNumber(upgrade.details.powerConsumption)),
                                style = RiftTheme.typography.detailPrimary,
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hub_upgrade_power_production_value, formatNumber(upgrade.details.powerConsumption.absoluteValue)),
                                style = RiftTheme.typography.detailPrimary,
                            )
                        }
                        if (upgrade.details.workforceConsumption > 0) {
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hub_upgrade_workforce_allocation_value, formatNumber(upgrade.details.workforceConsumption)),
                                style = RiftTheme.typography.detailPrimary,
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hub_upgrade_workforce_production_value, formatNumber(upgrade.details.workforceConsumption.absoluteValue)),
                                style = RiftTheme.typography.detailPrimary,
                            )
                        }
                        when (upgrade.details.fuel?.type) {
                            SovereigntyReagent.SuperionicIce -> {
                                Text(
                                    text = stringResource(Res.string.structures_sovereignty_hub_upgrade_fuel_value, "Superionic Ice"),
                                    style = RiftTheme.typography.detailPrimary,
                                )
                            }
                            SovereigntyReagent.MagmaticGas -> {
                                Text(
                                    text = stringResource(Res.string.structures_sovereignty_hub_upgrade_fuel_value, "Magmatic Gas"),
                                    style = RiftTheme.typography.detailPrimary,
                                )
                            }
                            null -> {}
                        }
                        if (upgrade.details.fuel != null) {
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hub_upgrade_fuel_consumption_value, formatNumber(upgrade.details.fuel.hourlyUpkeep)),
                                style = RiftTheme.typography.detailPrimary,
                            )
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hub_upgrade_fuel_startup_cost_value, formatNumber(upgrade.details.fuel.startupCost)),
                                style = RiftTheme.typography.detailPrimary,
                            )
                        }
                    }
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncTypeIcon(
                        type = upgrade.type,
                        modifier = Modifier
                            .modifyIf(upgrade.powerState != SovereigntyHubUpgradePowerState.Online) {
                                greyScale()
                            }
                            .size(32.dp),
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                        ) {
                            RiftIndicatorDot(
                                color = color,
                                isActive = isActive,
                            )
                            Text(
                                text = upgrade.type.name,
                                style = RiftTheme.typography.bodyPrimary.copy(color = textColor),
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                        ) {
                            SovereigntyHubUpgradesResourceCost(
                                icon = Res.drawable.power_16px,
                                consumption = upgrade.details.powerConsumption,
                                textColor = textColor,
                            )
                            SovereigntyHubUpgradesResourceCost(
                                icon = Res.drawable.workforce_16px,
                                consumption = upgrade.details.workforceConsumption,
                                textColor = textColor,
                            )
                            if (upgrade.details.fuel != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    val icon = when (upgrade.details.fuel.type) {
                                        SovereigntyReagent.SuperionicIce -> Res.drawable.superionic_ice_16px
                                        SovereigntyReagent.MagmaticGas -> Res.drawable.magmatic_gas_16px
                                    }
                                    Image(
                                        painter = painterResource(icon),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(textColor),
                                        modifier = Modifier.size(12.dp),
                                    )
                                    Text(
                                        text = stringResource(Res.string.structures_sovereignty_hub_fuel_rate_value, upgrade.details.fuel.hourlyUpkeep),
                                        style = RiftTheme.typography.detailPrimary.copy(color = textColor),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SovereigntyHubUpgradesResourceCost(
    icon: DrawableResource,
    consumption: Int,
    textColor: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val color = if (consumption < 0) EveColors.leafyGreen else textColor
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = "${if (consumption < 0) "+" else ""}${formatNumber(consumption.absoluteValue)}",
            style = RiftTheme.typography.detailPrimary.copy(color = color),
        )
    }
}

@Composable
private fun SovereigntyHubTitleRow(
    sovereigntyHub: SovereigntyHub,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Brush.linearGradient(listOf(Color.Transparent, RiftTheme.colors.backgroundPrimary)))
            .padding(Spacing.small),
    ) {
        Column {
            val text = buildAnnotatedString {
                append(stringResource(Res.string.structures_sovereignty_hub_title, sovereigntyHub.system.name))
            }
            Text(
                text = text,
                style = RiftTheme.typography.bodyPrimary,
            )
            Spacer(Modifier.height(Spacing.small))
            RiftSolarSystemChip(
                state = sovereigntyHub.systemChip,
            )
        }
        Spacer(Modifier.weight(1f))
        StructureOwner(sovereigntyHub.character)
    }
}

@Composable
private fun ReagentGauge(
    reagent: SovereigntyHubReagent,
    sovereigntyHub: SovereigntyHub,
    now: Instant,
) {
    val timePassed = Duration.between(sovereigntyHub.reagentBayLastUpdated, now)
    val burningPerSecond = reagent.burningPerHour / 3600f
    val amountNow = (reagent.amount - burningPerSecond * timePassed.toSeconds()).toInt().coerceAtLeast(0)

    val hoursRemaining = if (reagent.amount > 0 && reagent.burningPerHour > 0) reagent.amount / reagent.burningPerHour else 0
    val depletesAt = sovereigntyHub.reagentBayLastUpdated + Duration.ofHours(hoursRemaining.toLong())

    val timeLeft = Duration.between(now, depletesAt)
    RiftTooltipArea(
        text = if (timeLeft.isPositive) {
            stringResource(Res.string.structures_sovereignty_hub_fuel_depletion_tooltip, reagent.name, formatDateTime(depletesAt))
        } else {
            stringResource(Res.string.structures_sovereignty_hub_fuel_empty_tooltip, reagent.name)
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RiftCircularProgressGauge(
                progress = timeLeft.toSeconds().toFloat() / Duration.ofDays(28).toSeconds(),
                trackColor = EveColors.cryoBlue.multiplyBrightness(0.4f),
                color = EveColors.cryoBlue,
                diameter = 125.dp,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(reagent.icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = formatReagentTimeLeft(timeLeft).resolve(),
                        style = RiftTheme.typography.headlineHighlighted,
                    )
                    Text(
                        text = formatNumber(amountNow),
                        style = RiftTheme.typography.bodySecondary,
                    )
                    Text(
                        text = stringResource(Res.string.structures_sovereignty_hub_fuel_rate_value, reagent.burningPerHour),
                        style = RiftTheme.typography.bodySecondary,
                    )
                }
            }
            val isUsed = sovereigntyHub.upgrades
                .filter { it.powerState != SovereigntyHubUpgradePowerState.Offline }
                .any {
                    it.details.fuel?.type == reagent.type && it.details.fuel.hourlyUpkeep > 0
                }
            val [statusText, color] = when {
                timeLeft.toDays() >= 28 -> stringResource(Res.string.structures_sovereignty_hub_fuel_full_status) to RiftTheme.colors.textSecondary
                timeLeft.toDays() >= 7 -> stringResource(Res.string.structures_sovereignty_hub_fuel_low_status) to EveColors.warningOrange
                timeLeft.isPositive -> stringResource(Res.string.structures_sovereignty_hub_fuel_critical_status) to EveColors.dangerRed
                else -> if (isUsed) {
                    stringResource(Res.string.structures_sovereignty_hub_fuel_depleted_status) to EveColors.dangerRed
                } else {
                    stringResource(Res.string.structures_sovereignty_hub_fuel_unused_status) to RiftTheme.colors.textSecondary
                }
            }
            Text(
                text = statusText,
                style = RiftTheme.typography.detailSecondary.copy(color = color, fontWeight = FontWeight.Bold),
            )
        }
    }
}

private fun formatReagentTimeLeft(timeLeft: Duration): UiText {
    return when {
        timeLeft.isNegative -> "–".toUiText()
        timeLeft.toDays() >= 28 -> Res.plurals.structures_sovereignty_hub_fuel_time_weeks.toUiText(
            (timeLeft.toDays() / 7).coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
            (timeLeft.toDays() / 7).toUiTextArgument(),
        )
        timeLeft.toDays() >= 2 -> Res.plurals.structures_sovereignty_hub_fuel_time_days.toUiText(
            timeLeft.toDays().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
            timeLeft.toDays().toUiTextArgument(),
        )
        timeLeft.toHours() >= 24 -> Res.plurals.structures_sovereignty_hub_fuel_time_hours.toUiText(
            timeLeft.toHours().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
            timeLeft.toHours().toUiTextArgument(),
        )
        timeLeft.toHours() >= 1 -> Res.plurals.structures_sovereignty_hub_fuel_time_hour_clock.toUiText(
            timeLeft.toHours().toInt(),
            String.format(Locale.ENGLISH, "%d:%02d", timeLeft.toHours(), timeLeft.toMinutesPart()).toUiTextArgument(),
        )
        else -> Res.plurals.structures_sovereignty_hub_fuel_time_minute_clock.toUiText(
            timeLeft.toMinutes().toInt(),
            String.format(Locale.ENGLISH, "%d:%02d", timeLeft.toMinutes(), timeLeft.toSecondsPart()).toUiTextArgument(),
        )
    }
}

@Composable
private fun SovereigntyHubPower(power: SovereigntyHubPower) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Max),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.structures_sovereignty_hub_power_label),
                    style = RiftTheme.typography.detailSecondary,
                )
                Text(
                    text = buildAnnotatedString {
                        withColor(RiftTheme.colors.textPrimary) {
                            append(formatNumber(power.allocated))
                        }
                        append(" / ")
                        append(formatNumber(power.available))
                    },
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            Text(
                text = formatNumber(power.available),
                style = RiftTheme.typography.bodySecondary,
            )
        }
        Spacer(Modifier.height(Spacing.small))
        val unallocatedPower = power.available - power.allocated
        RiftProgressBar(
            percentage = power.allocated / power.available.toFloat(),
            height = 4.dp,
            secondaryPercentage = unallocatedPower / power.available.toFloat(),
            color = EveColors.smokeBlue,
            secondaryColor = EveColors.cryoBlue,
            modifier = Modifier.width(300.dp),
        )
    }
}

@Composable
private fun SovereigntyHubWorkforce(
    workforce: SovereigntyHubWorkforce,
    configuration: SovereigntyHubWorkforceTransport,
    state: SovereigntyHubWorkforceTransport,
) {
    Column(
        modifier = Modifier.width(300.dp),
    ) {
        val exported = (state as? SovereigntyHubWorkforceTransport.Export)?.amount ?: 0
        val total = workforce.available + exported

        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.structures_sovereignty_hub_workforce_label),
                    style = RiftTheme.typography.detailSecondary,
                )
                Text(
                    text = buildAnnotatedString {
                        withColor(RiftTheme.colors.textPrimary) {
                            append(formatNumber(workforce.allocated))
                        }
                        append(" / ")
                        append(formatNumber(workforce.available))
                    },
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            Text(
                text = formatNumber(total),
                style = RiftTheme.typography.bodySecondary,
            )
        }
        Spacer(Modifier.height(Spacing.small))

        RiftProgressBar(
            percentage = workforce.allocated / total.toFloat(),
            height = 4.dp,
            secondaryPercentage = (workforce.available - workforce.allocated) / total.toFloat(),
            tertiaryPercentage = exported / total.toFloat(),
            color = EveColors.smokeBlue,
            secondaryColor = EveColors.cryoBlue,
            tertiaryColor = EveColors.copperOxideGreen,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.mediumLarge))

        SovereigntyHubWorkforceTransportStatus(state)
        if (!state.isMatching(configuration)) {
            Spacer(Modifier.height(Spacing.mediumLarge))
            SovereigntyHubWorkforceTransportStatus(configuration, isPending = true)
        }
    }
}

@Composable
private fun SovereigntyHubWorkforceTransportStatus(
    transport: SovereigntyHubWorkforceTransport,
    isPending: Boolean = false,
) {
    Column {
        Text(
            text = if (isPending) stringResource(Res.string.structures_sovereignty_hub_transport_pending_heading) else stringResource(Res.string.structures_sovereignty_hub_transport_heading),
            style = RiftTheme.typography.detailSecondary,
        )
        val [mode, icon] = when (transport) {
            is SovereigntyHubWorkforceTransport.Export -> stringResource(Res.string.structures_sovereignty_hub_transport_export_mode) to Res.drawable.arrow_up_16px
            is SovereigntyHubWorkforceTransport.Import -> stringResource(Res.string.structures_sovereignty_hub_transport_import_mode) to Res.drawable.arrow_down_16px
            SovereigntyHubWorkforceTransport.Transit -> stringResource(Res.string.structures_sovereignty_hub_transport_transit_mode) to Res.drawable.arrow_right_16px
            SovereigntyHubWorkforceTransport.Idle -> stringResource(Res.string.structures_sovereignty_hub_transport_idle_mode) to Res.drawable.workforce_32px
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = mode,
                style = RiftTheme.typography.headlinePrimary,
            )
        }
        val parseEveFormattedText: ParseEveFormattedTextUseCase = remember { koin.get() }
        when (transport) {
            is SovereigntyHubWorkforceTransport.Export -> {
                val resource = if (isPending) Res.plurals.structures_sovereignty_hub_transport_export_pending_description else Res.plurals.structures_sovereignty_hub_transport_export_description
                val message = pluralStringResource(resource, transport.amount, formatNumber(transport.amount), getWorkforceSystemText(transport.solarSystem))
                val text = parseEveFormattedText(message, decodeEntities = true)
                LinkedText(
                    text = text,
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            is SovereigntyHubWorkforceTransport.Import -> {
                val sources = transport.sources.map { source ->
                    if (isPending) {
                        getWorkforceSystemText(source.solarSystem)
                    } else {
                        pluralStringResource(Res.plurals.structures_sovereignty_hub_transport_import_source_description, source.amount, formatNumber(source.amount), getWorkforceSystemText(source.solarSystem))
                    }
                }.joinToString(", ")
                val resource = if (isPending) Res.string.structures_sovereignty_hub_transport_import_pending_description else Res.string.structures_sovereignty_hub_transport_import_description
                val message = stringResource(resource, sources)
                val text = parseEveFormattedText(message, decodeEntities = true)
                LinkedText(
                    text = text,
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            SovereigntyHubWorkforceTransport.Transit -> {
                Text(
                    text = if (isPending) stringResource(Res.string.structures_sovereignty_hub_transport_transit_pending_description) else stringResource(Res.string.structures_sovereignty_hub_transport_transit_description),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            SovereigntyHubWorkforceTransport.Idle -> {
                Text(
                    text = if (isPending) stringResource(Res.string.structures_sovereignty_hub_transport_idle_pending_description) else stringResource(Res.string.structures_sovereignty_hub_transport_idle_description),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
        }
    }
}

private fun getWorkforceSystemText(system: MapSolarSystem?): String {
    return system?.let {
        "<a href=\"showinfo:$SOLAR_SYSTEM_TYPE_ID//${it.id}\"><b><font color=\"textPrimary\">${Entities.escape(it.name)}</font></b></a>"
    } ?: ""
}
