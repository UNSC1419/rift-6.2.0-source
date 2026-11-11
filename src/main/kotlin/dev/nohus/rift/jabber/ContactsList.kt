package dev.nohus.rift.jabber

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.RiftContextMenuArea
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.expand_more_16px
import dev.nohus.rift.generated.resources.jabber_nickname_tooltip
import dev.nohus.rift.generated.resources.jabber_other_directors_heading
import dev.nohus.rift.generated.resources.jabber_presence_available
import dev.nohus.rift.generated.resources.jabber_presence_away
import dev.nohus.rift.generated.resources.jabber_presence_do_not_disturb
import dev.nohus.rift.generated.resources.jabber_presence_extended_away
import dev.nohus.rift.generated.resources.jabber_presence_free_to_chat
import dev.nohus.rift.generated.resources.jabber_remove_action
import dev.nohus.rift.generated.resources.jabber_rooms_heading
import dev.nohus.rift.generated.resources.jabber_ungrouped_heading
import dev.nohus.rift.generated.resources.jabber_unknown_presence
import dev.nohus.rift.generated.resources.people_16px
import dev.nohus.rift.jabber.client.RosterUsersController.PresenceMode
import dev.nohus.rift.jabber.client.RosterUsersController.RosterUser
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jivesoftware.smackx.muc.MultiUserChat
import javax.imageio.ImageIO

@Composable
fun ContactsList(
    multiUserChats: List<MultiUserChat>,
    users: List<RosterUser>,
    collapsedGroups: List<String>,
    onRosterUserClick: (RosterUser) -> Unit,
    onRosterUserRemove: (RosterUser) -> Unit,
    onChatRoomClick: (MultiUserChat) -> Unit,
    onChatRoomRemove: (MultiUserChat) -> Unit,
    onCollapsedGroupToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollbarColumn(
        scrollbarModifier = Modifier.padding(Spacing.small),
        modifier = modifier,
    ) {
        ContactsListChats(
            chats = multiUserChats,
            onChatRoomClick = onChatRoomClick,
            onChatRoomRemove = onChatRoomRemove,
        )
        ContactsListContacts(
            collapsedGroups = collapsedGroups,
            users = users,
            onRosterUserClick = onRosterUserClick,
            onRosterUserRemove = onRosterUserRemove,
            onCollapsedGroupToggle = onCollapsedGroupToggle,
        )
    }
}

@Composable
private fun ContactsListChats(
    chats: List<MultiUserChat>,
    onChatRoomClick: (MultiUserChat) -> Unit,
    onChatRoomRemove: (MultiUserChat) -> Unit,
) {
    if (chats.isNotEmpty()) {
        Text(
            text = stringResource(Res.string.jabber_rooms_heading),
            style = RiftTheme.typography.headerHighlighted,
            modifier = Modifier.padding(Spacing.medium),
        )
        chats.forEach {
            Chat(
                chat = it,
                onClick = { onChatRoomClick(it) },
                onRemoveClick = { onChatRoomRemove(it) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Chat(
    chat: MultiUserChat,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    RiftContextMenuArea(
        items = listOf(
            ContextMenuItem.TextItem(stringResource(Res.string.jabber_remove_action), onClick = onRemoveClick),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .hoverBackground()
                .onClick { onClick() }
                .pointerHoverIcon(PointerIcon(Cursors.pointerDropdown))
                .padding(vertical = Spacing.small)
                .padding(start = Spacing.medium)
                .fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(Res.drawable.people_16px),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "${chat.room}",
                style = RiftTheme.typography.bodyPrimary,
                modifier = Modifier.padding(start = Spacing.small),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContactsListContacts(
    users: List<RosterUser>,
    collapsedGroups: List<String>,
    onRosterUserClick: (RosterUser) -> Unit,
    onRosterUserRemove: (RosterUser) -> Unit,
    onCollapsedGroupToggle: (String) -> Unit,
) {
    val player = rememberUiSoundPlayer()
    val groups = groupUsers(users)
    groups.forEach { (group, usersInGroup) ->
        val isCollapsed = group in collapsedGroups
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .hoverBackground()
                .onClick {
                    player.playAccordion(group !in collapsedGroups)
                    onCollapsedGroupToggle(group)
                }
                .padding(vertical = Spacing.medium),
        ) {
            val rotation by animateFloatAsState(if (isCollapsed) 180f else 0f)
            Image(
                painter = painterResource(Res.drawable.expand_more_16px),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = Spacing.small)
                    .rotate(rotation)
                    .size(16.dp),
            )
            Text(
                text = when (group) {
                    "No group" -> stringResource(Res.string.jabber_ungrouped_heading)
                    "Director Team - Other" -> stringResource(Res.string.jabber_other_directors_heading)
                    else -> group
                },
                style = RiftTheme.typography.headerHighlighted,
                modifier = Modifier
                    .padding(end = Spacing.small),
            )
        }
        AnimatedVisibility(group !in collapsedGroups) {
            Column {
                usersInGroup.forEach {
                    Contact(
                        user = it,
                        onClick = { onRosterUserClick(it) },
                        onRemoveClick = { onRosterUserRemove(it) },
                    )
                }
            }
        }
    }
}

private fun groupUsers(users: List<RosterUser>): Map<String, List<RosterUser>> {
    val noGroup = "No group"
    val otherDirectors = "Director Team - Other"
    val groups = users.flatMap { it.groups }.distinct() + otherDirectors + noGroup
    return groups
        .sortedBy { it.startsWith("Director Team") }
        .associateWith { group ->
            when (group) {
                noGroup -> {
                    users.filter { it.groups.isEmpty() }
                }
                otherDirectors -> {
                    users.filter { "Director Team - All" in it.groups && it.groups.count { it.startsWith("Director Team") } == 1 }
                }
                else -> {
                    users.filter { group in it.groups }
                }
            }
        }
        .filter { it.key != "Director Team - All" }
        .filter { it.value.isNotEmpty() }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Contact(
    user: RosterUser,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    RiftContextMenuArea(
        items = listOf(
            ContextMenuItem.TextItem(stringResource(Res.string.jabber_remove_action), onClick = onRemoveClick),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .hoverBackground()
                .onClick { onClick() }
                .pointerHoverIcon(PointerIcon(Cursors.pointerDropdown))
                .padding(vertical = Spacing.small)
                .padding(start = Spacing.medium)
                .fillMaxWidth(),
        ) {
            val nicknameText = user.vCard?.nickname?.let { stringResource(Res.string.jabber_nickname_tooltip, it) }
            val presenceNames = user.presences.associate { it.mode to getPresenceModeText(it.mode) }
            val tooltip = buildString {
                appendLine(user.jid.toString())
                nicknameText?.let { appendLine(it) }
                if (!user.isSubscriptionPending) {
                    user.presences.forEach {
                        append(presenceNames.getValue(it.mode))
                        if (!it.status.isNullOrBlank()) append(" – ${it.status}")
                        if (it.jid.resourceOrNull != null) {
                            append(" (${it.jid.resourceOrNull})")
                        }
                        appendLine()
                    }
                }
            }.trim()
            if (user.presences.isNotEmpty()) {
                RiftTooltipArea(tooltip) {
                    PresenceIndicatorDot(
                        presence = user.presences.last(),
                        isSubscriptionPending = user.isSubscriptionPending,
                        modifier = Modifier.padding(end = Spacing.medium),
                    )
                }
            }
            val name = user.name.takeIf { it.isNotBlank() } ?: user.jid.localpartOrNull?.toString() ?: ""
            val image = user.vCard?.avatarBytes?.let { bytes ->
                ImageIO.read(bytes.inputStream())
            }
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = name,
                    style = RiftTheme.typography.bodyPrimary,
                )
                user.bestPresence.status?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = RiftTheme.typography.bodySecondary,
                    )
                }
                if (user.isSubscriptionPending) {
                    Text(
                        text = stringResource(Res.string.jabber_unknown_presence),
                        style = RiftTheme.typography.bodySecondary,
                    )
                }
            }
            if (image != null) {
                Image(
                    bitmap = image.toComposeImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(48.dp)
                        .heightIn(max = 48.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
            }
        }
    }
}

@Composable
private fun getPresenceModeText(mode: PresenceMode): String = stringResource(
    when (mode) {
        PresenceMode.FreeToChat -> Res.string.jabber_presence_free_to_chat
        PresenceMode.Available -> Res.string.jabber_presence_available
        PresenceMode.Away -> Res.string.jabber_presence_away
        PresenceMode.ExtendedAway -> Res.string.jabber_presence_extended_away
        PresenceMode.DoNotDisturb -> Res.string.jabber_presence_do_not_disturb
    },
)
