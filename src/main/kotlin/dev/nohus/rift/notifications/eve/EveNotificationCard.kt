package dev.nohus.rift.notifications.eve

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.CharacterStack
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.RiftContextMenuArea
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.getRelativeTime
import dev.nohus.rift.compose.text.LinkedText
import dev.nohus.rift.compose.text.toFormattedText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.notification_history_hide_type_action
import dev.nohus.rift.generated.resources.window_titlebar_tune
import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.types.TypeDetails
import org.jetbrains.compose.resources.stringResource
import java.time.ZoneId

@Composable
fun EveNotificationCard(
    notification: EveNotification,
    typeDetails: TypeDetails,
    notificationTypeTitles: Map<String, String>,
    displayTimezone: ZoneId,
    onHideType: ((String) -> Unit)?,
    modifier: Modifier,
) {
    val eveNotificationTextParser: EveNotificationTextParser = remember { koin.get() }
    val localCharactersRepository: LocalCharactersRepository = remember { koin.get() }
    val recipientCharacters = localCharactersRepository.characters.value
        .filter { it.characterId in notification.characterIds }
    val parsedEveNotification = remember(
        notification.type,
        notification.text,
        notification.senderId,
        notification.characterIds,
        notification.timestamp,
        typeDetails,
    ) {
        notification.text?.let {
            eveNotificationTextParser.parse(
                type = notification.type,
                text = it,
                typeDetails = typeDetails,
                senderId = notification.senderId,
                receiverId = notification.characterIds.firstOrNull()?.toLong() ?: 0L,
                createdTimestamp = notification.timestamp,
            )
        } ?: ParsedEveNotification(
            subject = null,
            body = "No notification details".toFormattedText(),
            data = emptyMap(),
        )
    }
    Column(
        modifier = modifier
            .border(1.dp, RiftTheme.colors.borderGrey),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(RiftTheme.colors.backgroundPrimaryDark)
                .padding(Spacing.medium)
                .fillMaxWidth(),
        ) {
            NotificationIcon(typeDetails, notification, parsedEveNotification.data)
            Spacer(Modifier.width(Spacing.medium))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                LinkedText(
                    text = parsedEveNotification.subject ?: notificationTypeTitles[notification.type]?.toFormattedText()
                        ?: notification.type.toFormattedText(),
                    style = RiftTheme.typography.headerPrimary,

                )
                Text(
                    text = getRelativeTime(notification.timestamp, displayTimezone),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
            Spacer(Modifier.width(Spacing.small))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                if (onHideType != null) {
                    RiftContextMenuArea(
                        items = listOf(
                            ContextMenuItem.HeaderItem(notificationTypeTitles[notification.type] ?: notification.type),
                            ContextMenuItem.TextItem(
                                text = stringResource(Res.string.notification_history_hide_type_action),
                                onClick = { onHideType(notification.type) },
                            ),
                        ),
                        acceptsLeftClick = true,
                        acceptsRightClick = false,
                    ) {
                        RiftImageButton(Res.drawable.window_titlebar_tune, 16.dp, {})
                    }
                }

                CharacterStack(
                    characters = recipientCharacters,
                    portraitSize = 24.dp,
                    showCharacterAgeInTooltip = false,
                    modifier = Modifier.widthIn(max = 100.dp),
                )
            }
        }
        Divider(color = RiftTheme.colors.divider)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RiftTheme.colors.windowBackground)
                .padding(Spacing.medium),
        ) {
            if (parsedEveNotification.body != null) {
                LinkedText(
                    text = parsedEveNotification.body,
                    style = RiftTheme.typography.bodyPrimary,
                )
            }
        }
    }
}

@Composable
private fun NotificationIcon(typeDetails: TypeDetails, notification: EveNotification, data: Map<String, Any?>) {
    if (notification.type in listOf("KillReportVictim", "KillReportFinalBlow")) {
        (data["victimShipTypeID"] as? Int)?.let {
            val type = (typeDetails[it.toLong()] as? TypeDetail.Type)?.type
            if (type != null) {
                RiftTooltipArea(
                    text = type.name,
                ) {
                    AsyncTypeIcon(type, modifier = Modifier.size(32.dp))
                }
            }
        }
    } else {
        val senderTypeDetail = typeDetails[notification.senderId]
        if (senderTypeDetail != null) {
            NotificationSender(senderTypeDetail)
        }
    }
}
