package dev.nohus.rift.notifications.eve

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.ClickableAlliance
import dev.nohus.rift.compose.ClickableCharacter
import dev.nohus.rift.compose.ClickableCorporation
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitStandings
import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.wallet.compose.TypeDetailItem

@Composable
fun NotificationSender(
    type: TypeDetail,
) {
    val rowHeight = 32.dp
    RiftTooltipArea(
        tooltip = {
            Box(Modifier.padding(Spacing.medium)) {
                TypeDetailItem(type, borderColor = Color.Transparent)
            }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(rowHeight),
        ) {
            when (type) {
                is TypeDetail.Character -> {
                    val character = type.character
                    ClickableCharacter(character.characterId) {
                        DynamicCharacterPortraitStandings(
                            characterId = character.characterId,
                            size = rowHeight,
                            standingLevel = character.standingLevel,
                            isAnimated = true,
                        )
                    }
                }

                is TypeDetail.Corporation -> {
                    val corporation = type.corporation
                    ClickableCorporation(corporation.corporationId) {
                        AsyncCorporationLogo(
                            corporationId = corporation.corporationId,
                            size = 32,
                            modifier = Modifier.size(rowHeight),
                        )
                    }
                }

                is TypeDetail.Alliance -> {
                    val alliance = type.alliance
                    ClickableAlliance(alliance.allianceId) {
                        AsyncAllianceLogo(
                            allianceId = alliance.allianceId,
                            size = 32,
                            modifier = Modifier.size(rowHeight),
                        )
                    }
                }

                is TypeDetail.Faction -> {
                    AsyncCorporationLogo(
                        corporationId = type.id.toInt(),
                        size = 32,
                        modifier = Modifier.size(rowHeight),
                    )
                }

                /**
                 * Other TypeDetail types cannot be notification senders, hopefully
                 */
                is TypeDetail.SolarSystem,
                is TypeDetail.Station,
                is TypeDetail.Structure,
                is TypeDetail.Type,
                is TypeDetail.CorporationProject,
                is TypeDetail.FreelanceProject,
                is TypeDetail.DailyGoal,
                is TypeDetail.Celestial,
                is TypeDetail.Unknown,
                -> {}
            }
        }
    }
}
