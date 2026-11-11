package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.concord_logo_16px
import dev.nohus.rift.generated.resources.high_sec_16px
import dev.nohus.rift.generated.resources.low_sec_16px
import dev.nohus.rift.generated.resources.null_sec_16px
import dev.nohus.rift.generated.resources.overlay_space_type_alliance_null_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_friendly_null_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_high_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_hostile_null_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_low_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_neutral_null_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_npc_null_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_safe_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_unclaimed_null_sec_text
import dev.nohus.rift.generated.resources.overlay_space_type_wormhole_space_text
import dev.nohus.rift.generated.resources.triglavian_logo_16px
import dev.nohus.rift.generated.resources.wormholes_16px
import dev.nohus.rift.repositories.MapStatusRepository
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.standings.StandingsRepository
import dev.nohus.rift.standings.getSystemColor
import dev.nohus.rift.utils.roundSecurity
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SpaceType(
    system: SolarSystemsRepository.MapSolarSystem,
    systemStatus: MapStatusRepository.SolarSystemStatus?,
    characterAllianceId: Int?,
    iconOnly: Boolean = false,
) {
    val security = system.security.roundSecurity()
    val standingsRepository: StandingsRepository = remember { koin.get() }
    val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
    val isWormholeSpace = remember(system) { solarSystemsRepository.isWormholeSpace(system.id) }
    val isPochven = remember(system) { system.regionId == 10000070 }
    val allianceId = systemStatus?.sovereignty?.alliance?.allianceId
    val standing = standingsRepository.getStandingLevel(allianceId, null, null, includeWars = false)

    val icon = if (security == 1.0) {
        Res.drawable.concord_logo_16px
    } else if (security >= 0.5) {
        Res.drawable.high_sec_16px
    } else if (security > 0.0) {
        Res.drawable.low_sec_16px
    } else if (isWormholeSpace) {
        Res.drawable.wormholes_16px
    } else if (isPochven) {
        Res.drawable.triglavian_logo_16px
    }  else {
        Res.drawable.null_sec_16px
    }

    val [tooltip, color] = if (security == 1.0) {
        stringResource(Res.string.overlay_space_type_safe_sec_text) to Color(0xFF71E754)
    } else if (security >= 0.5) {
        stringResource(Res.string.overlay_space_type_high_sec_text) to Color(0xFF71E754)
    } else if (security > 0.0) {
        stringResource(Res.string.overlay_space_type_low_sec_text) to Color(0xFFF5FF83)
    } else if (isWormholeSpace) {
        stringResource(Res.string.overlay_space_type_wormhole_space_text) to RiftTheme.colors.textHighlighted
    } else if (isPochven) {
        "Pochven" to RiftTheme.colors.textHighlighted
    } else if (systemStatus?.sovereignty?.faction != null) {
        stringResource(Res.string.overlay_space_type_npc_null_sec_text) to RiftTheme.colors.textHighlighted
    } else if (systemStatus?.sovereignty?.unclaimed == true) {
        stringResource(Res.string.overlay_space_type_unclaimed_null_sec_text) to RiftTheme.colors.textHighlighted
    } else if (allianceId != null && allianceId == characterAllianceId) {
        stringResource(Res.string.overlay_space_type_alliance_null_sec_text) to Color(0xFF2C75E1)
    } else {
        when (standing) {
            Standing.WarTarget -> error("Not used")
            Standing.Terrible -> stringResource(Res.string.overlay_space_type_hostile_null_sec_text) to standing.getSystemColor()
            Standing.Bad -> stringResource(Res.string.overlay_space_type_hostile_null_sec_text) to standing.getSystemColor()
            Standing.Neutral -> stringResource(Res.string.overlay_space_type_neutral_null_sec_text) to standing.getSystemColor()
            Standing.Good -> stringResource(Res.string.overlay_space_type_friendly_null_sec_text) to standing.getSystemColor()
            Standing.Excellent -> stringResource(Res.string.overlay_space_type_friendly_null_sec_text) to standing.getSystemColor()
            Standing.Self, Standing.Corporation, Standing.Alliance -> stringResource(Res.string.overlay_space_type_friendly_null_sec_text) to standing.getSystemColor()
        }
    }

    val alphaMultiplier = 1.5f
    val iconColorFilter = remember(color) {
        ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0f, 0f, 0f, 0f, color.red * 255f,
                    0f, 0f, 0f, 0f, color.green * 255f,
                    0f, 0f, 0f, 0f, color.blue * 255f,
                    0f, 0f, 0f, alphaMultiplier, 0f,
                )
            )
        )
    }
    val iconContent: @Composable () -> Unit = {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = iconColorFilter,
            modifier = Modifier.size(16.dp)
        )
    }
    if (iconOnly) {
        RiftTooltipArea(text = tooltip) { iconContent() }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            iconContent()
            Text(
                text = tooltip,
                style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
