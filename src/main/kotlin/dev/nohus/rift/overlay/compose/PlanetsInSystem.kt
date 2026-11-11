package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.repositories.MapStatusRepository
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlanetsInSystem(systemStatus: MapStatusRepository.SolarSystemStatus?) {
    systemStatus?.planets?.let {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.verySmall),
            verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
            modifier = Modifier.widthIn(max = 250.dp),
        ) {
            it.forEach { planet ->
                RiftTooltipArea(
                    text = "${planet.name} – ${planet.type.name}",
                ) {
                    Image(
                        painter = painterResource(planet.type.icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
