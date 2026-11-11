package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.map.SecurityColors
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.utils.roundSecurity

@Composable
fun SystemName(
    system: SolarSystemsRepository.MapSolarSystem,
    showSystem: Boolean,
    showConstellation: Boolean,
    showRegion: Boolean,
    securityTrailingContent: @Composable (() -> Unit)? = null,
) {
    Column {
        if (showSystem) {
            val securityColor = SecurityColors[system.security]
            val systemNameText = buildAnnotatedString {
                append(system.name)
                append(" ")
                withStyle(SpanStyle(color = securityColor)) {
                    append(system.security.roundSecurity().toString())
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = systemNameText,
                    style = RiftTheme.typography.headlineHighlighted.copy(fontWeight = FontWeight.Bold),
                )
                securityTrailingContent?.invoke()
            }
        }

        if (showConstellation || showRegion) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
                if (showConstellation) {
                    Text(
                        text = solarSystemsRepository.getConstellation(system.constellationId)?.name ?: "?",
                        style = RiftTheme.typography.detailHighlighted.copy(fontWeight = FontWeight.Bold),
                    )
                }
                if (showConstellation && showRegion) {
                    Text(
                        text = "<",
                        style = RiftTheme.typography.detailHighlighted.copy(fontWeight = FontWeight.Bold),
                    )
                }
                if (showRegion) {
                    Text(
                        text = solarSystemsRepository.getRegion(system.regionId)?.name ?: "?",
                        style = RiftTheme.typography.detailHighlighted.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}
