package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.angel_cartel_logo_32px
import dev.nohus.rift.generated.resources.blood_raiders_logo_32px
import dev.nohus.rift.generated.resources.drones_32px
import dev.nohus.rift.generated.resources.guristas_logo_32px
import dev.nohus.rift.generated.resources.overlay_rats_tooltip
import dev.nohus.rift.generated.resources.sansha_logo_32px
import dev.nohus.rift.generated.resources.serpentis_logo_32px
import dev.nohus.rift.generated.resources.triglavian_logo_32px
import dev.nohus.rift.repositories.MapStatusRepository
import dev.nohus.rift.repositories.RatsRepository
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocalRats(systemStatus: MapStatusRepository.SolarSystemStatus?) {
    val [text, icon] = when (systemStatus?.ratType) {
        RatsRepository.RatType.BloodRaiders -> "Blood Raiders" to Res.drawable.blood_raiders_logo_32px
        RatsRepository.RatType.Guristas -> "Guristas" to Res.drawable.guristas_logo_32px
        RatsRepository.RatType.SanshasNation -> "Sansha's Nation" to Res.drawable.sansha_logo_32px
        RatsRepository.RatType.Serpentis -> "Serpentis" to Res.drawable.serpentis_logo_32px
        RatsRepository.RatType.AngelCartel -> "Angel Cartel" to Res.drawable.angel_cartel_logo_32px
        RatsRepository.RatType.RogueDrones -> "Rogue Drones" to Res.drawable.drones_32px
        RatsRepository.RatType.TriglavianCollective -> "Triglavians" to Res.drawable.triglavian_logo_32px
        null -> null to null
    }
    if (icon != null) {
        RiftTooltipArea(text?.let { stringResource(Res.string.overlay_rats_tooltip, it) }) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(RiftTheme.colors.textHighlighted),
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
