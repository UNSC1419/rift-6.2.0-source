package dev.nohus.rift.jabber

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.ContextMenuItem.CheckboxItem
import dev.nohus.rift.compose.ContextMenuItem.TextItem
import dev.nohus.rift.compose.LinkText
import dev.nohus.rift.compose.LoadingSpinner
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftContextMenuArea
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWarningBanner
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.ScrollbarLazyColumn
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.annotateLinks
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.bee
import dev.nohus.rift.generated.resources.jabber_account_recovery_action
import dev.nohus.rift.generated.resources.jabber_add_contact_button
import dev.nohus.rift.generated.resources.jabber_add_room_button
import dev.nohus.rift.generated.resources.jabber_back_button
import dev.nohus.rift.generated.resources.jabber_bigger_font_action
import dev.nohus.rift.generated.resources.jabber_connect_button
import dev.nohus.rift.generated.resources.jabber_connecting_status
import dev.nohus.rift.generated.resources.jabber_contacts_tab
import dev.nohus.rift.generated.resources.jabber_copy_message_action
import dev.nohus.rift.generated.resources.jabber_copy_row_action
import dev.nohus.rift.generated.resources.jabber_get_started_button
import dev.nohus.rift.generated.resources.jabber_group_label
import dev.nohus.rift.generated.resources.jabber_group_placeholder
import dev.nohus.rift.generated.resources.jabber_import_account_button
import dev.nohus.rift.generated.resources.jabber_intro_description
import dev.nohus.rift.generated.resources.jabber_intro_pidgin_description
import dev.nohus.rift.generated.resources.jabber_login_heading
import dev.nohus.rift.generated.resources.jabber_logout_action
import dev.nohus.rift.generated.resources.jabber_manual_login_button
import dev.nohus.rift.generated.resources.jabber_nickname_label
import dev.nohus.rift.generated.resources.jabber_nickname_placeholder
import dev.nohus.rift.generated.resources.jabber_password_label
import dev.nohus.rift.generated.resources.jabber_password_placeholder
import dev.nohus.rift.generated.resources.jabber_password_unchanged_placeholder
import dev.nohus.rift.generated.resources.jabber_reconnecting_status
import dev.nohus.rift.generated.resources.jabber_room_name_placeholder
import dev.nohus.rift.generated.resources.jabber_self_sender_name
import dev.nohus.rift.generated.resources.jabber_send_button
import dev.nohus.rift.generated.resources.jabber_send_tooltip
import dev.nohus.rift.generated.resources.jabber_username_label
import dev.nohus.rift.generated.resources.jabber_username_placeholder
import dev.nohus.rift.generated.resources.jabber_window_title
import dev.nohus.rift.generated.resources.logout
import dev.nohus.rift.generated.resources.window_chatchannels
import dev.nohus.rift.jabber.JabberAccountRepository.JabberAccountResult.JabberAccount
import dev.nohus.rift.jabber.JabberViewModel.ContactListState
import dev.nohus.rift.jabber.JabberViewModel.TabModel
import dev.nohus.rift.jabber.JabberViewModel.UiState
import dev.nohus.rift.jabber.client.MultiUserChatController.MultiUserMessage
import dev.nohus.rift.jabber.client.RosterUsersController.RosterUser
import dev.nohus.rift.jabber.client.UserChatController.UserChat
import dev.nohus.rift.jabber.client.UserChatController.UserMessage
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.toURIOrNull
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smackx.muc.MultiUserChat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun JabberWindow(
    inputModel: JabberInputModel,
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: JabberViewModel = viewModel(inputModel)
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.jabber_window_title),
        icon = Res.drawable.window_chatchannels,
        state = windowState,
        tuneContextMenuItems = getTuneContextMenuItems(state, viewModel),
        onCloseClick = onCloseRequest,
        withContentPadding = false,
    ) {
        JabberWindowContent(
            state = state,
            onTabSelect = viewModel::onTabSelect,
            onImportClick = viewModel::onImportClick,
            onLoginClick = viewModel::onLoginClick,
            onConnectClick = viewModel::onConnectClick,
            onRosterUserClick = viewModel::onRosterUserClick,
            onRosterUserRemove = viewModel::onRosterUserRemove,
            onChatRoomClick = viewModel::onChatRoomClick,
            onChatRoomRemove = viewModel::onChatRoomRemove,
            onChatClosed = viewModel::onChatClosed,
            onChatRoomClosed = viewModel::onChatRoomClosed,
            onAddContactClick = viewModel::onAddContactClick,
            onAddChatRoomClick = viewModel::onAddChatRoomClick,
            onAddContactSubmitClick = viewModel::onAddContactSubmitClick,
            onAddChatRoomSubmitClick = viewModel::onAddChatRoomSubmitClick,
            onBackClick = viewModel::onBackClick,
            onMessageSend = viewModel::onMessageSend,
            onChatRoomMessageSend = viewModel::onMessageSend,
            onCollapsedGroupToggle = viewModel::onCollapsedGroupToggle,
        )
    }
}

@Composable
private fun getTuneContextMenuItems(
    state: UiState,
    viewModel: JabberViewModel,
): List<ContextMenuItem>? {
    val canLogout = when (state) {
        is UiState.NoAccount -> false
        is UiState.Login -> false
        UiState.Connecting -> true
        is UiState.LoggedIn -> true
    }
    val isUsingBiggerFontSize = (state as? UiState.LoggedIn)?.isUsingBiggerFontSize
    return buildList {
        if (canLogout) add(TextItem(stringResource(Res.string.jabber_logout_action), Res.drawable.logout, onClick = viewModel::onLogoutClick))
        if (isUsingBiggerFontSize != null) add(CheckboxItem(stringResource(Res.string.jabber_bigger_font_action), isSelected = isUsingBiggerFontSize, onClick = viewModel::onBiggerFontSizeClick))
    }.takeIf { it.isNotEmpty() }
}

@Composable
private fun JabberWindowContent(
    state: UiState,
    onTabSelect: (TabModel) -> Unit,
    onImportClick: () -> Unit,
    onLoginClick: () -> Unit,
    onConnectClick: (jidLocalPart: String, password: String) -> Unit,
    onRosterUserClick: (RosterUser) -> Unit,
    onRosterUserRemove: (RosterUser) -> Unit,
    onChatRoomClick: (MultiUserChat) -> Unit,
    onChatRoomRemove: (MultiUserChat) -> Unit,
    onChatClosed: (Chat) -> Unit,
    onChatRoomClosed: (MultiUserChat) -> Unit,
    onAddContactClick: () -> Unit,
    onAddChatRoomClick: () -> Unit,
    onAddContactSubmitClick: (jidLocalPart: String, name: String, groups: List<String>) -> Unit,
    onAddChatRoomSubmitClick: (jidLocalPart: String) -> Unit,
    onBackClick: () -> Unit,
    onMessageSend: (UserChat, String) -> Unit,
    onChatRoomMessageSend: (MultiUserChat, String) -> Unit,
    onCollapsedGroupToggle: (String) -> Unit,
) {
    AnimatedContent(
        targetState = state,
        contentKey = { it.contentKey },
    ) { state ->
        when (state) {
            UiState.Connecting -> ConnectingContent()
            is UiState.NoAccount -> NoAccountContent(
                state = state,
                onImportClick = onImportClick,
                onLoginClick = onLoginClick,
            )
            is UiState.Login -> LoginContent(
                state = state,
                onConnectClick = onConnectClick,
            )
            is UiState.LoggedIn -> LoggedInContent(
                state = state,
                onTabSelect = onTabSelect,
                onRosterUserClick = onRosterUserClick,
                onRosterUserRemove = onRosterUserRemove,
                onChatRoomClick = onChatRoomClick,
                onChatRoomRemove = onChatRoomRemove,
                onChatClosed = onChatClosed,
                onChatRoomClosed = onChatRoomClosed,
                onAddContactClick = onAddContactClick,
                onAddChatRoomClick = onAddChatRoomClick,
                onAddContactSubmitClick = onAddContactSubmitClick,
                onAddChatRoomSubmitClick = onAddChatRoomSubmitClick,
                onBackClick = onBackClick,
                onMessageSend = onMessageSend,
                onChatRoomMessageSend = onChatRoomMessageSend,
                onCollapsedGroupToggle = onCollapsedGroupToggle,
            )
        }
    }
}

@Composable
private fun ConnectingContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            LoadingSpinner()
            Text(
                text = stringResource(Res.string.jabber_connecting_status),
                style = RiftTheme.typography.headerPrimary,
                modifier = Modifier
                    .padding(top = Spacing.large),
            )
        }
    }
}

@Composable
private fun NoAccountContent(
    state: UiState.NoAccount,
    onImportClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(Spacing.medium),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.bee),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Text(
                    text = stringResource(Res.string.jabber_login_heading),
                    style = RiftTheme.typography.headlineHighlighted,
                    textAlign = TextAlign.Center,
                )
                val isUsingPidgin = state.canImport
                val text = if (isUsingPidgin) {
                    stringResource(Res.string.jabber_intro_pidgin_description)
                } else {
                    stringResource(Res.string.jabber_intro_description)
                }
                Text(
                    text = text,
                    style = RiftTheme.typography.headerPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (state.canImport) {
            RiftButton(
                text = stringResource(Res.string.jabber_import_account_button),
                cornerCut = ButtonCornerCut.Both,
                onClick = onImportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.medium),
            )
            RiftButton(
                text = stringResource(Res.string.jabber_manual_login_button),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.Both,
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.medium),
            )
        } else {
            RiftButton(
                text = stringResource(Res.string.jabber_get_started_button),
                cornerCut = ButtonCornerCut.Both,
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.medium),
            )
        }
    }
}

@Composable
private fun LoginContent(
    state: UiState.Login,
    onConnectClick: (jidLocalPart: String, password: String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(Spacing.medium),
    ) {
        val account = remember { koin.get<JabberAccountRepository>().getAccount() as? JabberAccount }
        var jidLocalPart by remember { mutableStateOf(account?.jid?.substringBeforeLast("@") ?: "") }
        val savedPassword = remember { account?.password }
        var password by remember { mutableStateOf("") }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.bee),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Text(
                    text = stringResource(Res.string.jabber_login_heading),
                    style = RiftTheme.typography.headlineHighlighted,
                    textAlign = TextAlign.Center,
                )
                if (state.errorMessage != null) {
                    RiftWarningBanner(
                        text = state.errorMessage.resolve(),
                        modifier = Modifier.padding(top = Spacing.medium),
                    )
                }
                Column(
                    modifier = Modifier.padding(top = Spacing.medium),
                ) {
                    Text(
                        text = stringResource(Res.string.jabber_username_label),
                        style = RiftTheme.typography.headerPrimary,
                    )
                    RiftTextField(
                        text = jidLocalPart,
                        placeholder = stringResource(Res.string.jabber_username_placeholder),
                        onTextChanged = { jidLocalPart = it },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = Spacing.small),
                    )
                    Text(
                        text = stringResource(Res.string.jabber_password_label),
                        style = RiftTheme.typography.headerPrimary,
                        modifier = Modifier
                            .padding(top = Spacing.small),
                    )
                    RiftTextField(
                        text = password,
                        placeholder = if (savedPassword != null) stringResource(Res.string.jabber_password_unchanged_placeholder) else stringResource(Res.string.jabber_password_placeholder),
                        isPassword = true,
                        onTextChanged = { password = it },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = Spacing.small),
                    )
                    LinkText(
                        text = stringResource(Res.string.jabber_account_recovery_action),
                        onClick = { "https://gice.goonfleet.com/Manage/ServicePassword".toURIOrNull()?.openBrowser() },
                        modifier = Modifier.padding(top = Spacing.small),
                    )
                }
            }
        }
        RiftButton(
            text = stringResource(Res.string.jabber_connect_button),
            cornerCut = ButtonCornerCut.Both,
            onClick = {
                val effectivePassword = password.takeIf { it.isNotEmpty() } ?: savedPassword ?: password
                onConnectClick(jidLocalPart.substringBeforeLast("@"), effectivePassword)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.medium),
        )
    }
}

@Composable
private fun LoggedInContent(
    state: UiState.LoggedIn,
    onTabSelect: (TabModel) -> Unit,
    onRosterUserClick: (RosterUser) -> Unit,
    onRosterUserRemove: (RosterUser) -> Unit,
    onChatRoomClick: (MultiUserChat) -> Unit,
    onChatRoomRemove: (MultiUserChat) -> Unit,
    onChatClosed: (Chat) -> Unit,
    onChatRoomClosed: (MultiUserChat) -> Unit,
    onAddContactClick: () -> Unit,
    onAddChatRoomClick: () -> Unit,
    onAddContactSubmitClick: (jidLocalPart: String, name: String, groups: List<String>) -> Unit,
    onAddChatRoomSubmitClick: (jidLocalPart: String) -> Unit,
    onBackClick: () -> Unit,
    onMessageSend: (UserChat, String) -> Unit,
    onCollapsedGroupToggle: (String) -> Unit,
    onChatRoomMessageSend: (MultiUserChat, String) -> Unit,
) {
    Column {
        if (!state.jabberState.isConnected) {
            RiftWarningBanner(
                text = stringResource(Res.string.jabber_reconnecting_status),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.small),
            )
        }
        val contactsTab = Tab(id = 0, stringResource(Res.string.jabber_contacts_tab), false, payload = TabModel.Contacts)
        val chatRoomTabs = state.jabberState.openMultiUserChats.withIndex().associateWith { (index, chat) ->
            Tab(
                id = index + 1,
                title = chat.localpartOrNull?.toString() ?: "",
                isCloseable = true,
                isNotified = chat in state.unreadChats,
                payload = TabModel.MultiUserChat(chat),
            )
        }
        val chatTabs = state.jabberState.userChats.withIndex().associateWith { (index, chat) ->
            Tab(
                id = index + 1 + chatRoomTabs.size,
                title = chat.name,
                isCloseable = true,
                isNotified = chat.chat.xmppAddressOfChatPartner in state.unreadChats,
                payload = TabModel.UserChat(chat),
            )
        }
        val selectedTab = when (val selectedTab = state.selectedTab) {
            TabModel.Contacts -> 0
            is TabModel.MultiUserChat -> chatRoomTabs.entries.firstOrNull { it.key.value == selectedTab.chat }?.value?.id ?: 0
            is TabModel.UserChat -> chatTabs.entries.firstOrNull { it.key.value.chat == selectedTab.chat.chat }?.value?.id ?: 0
        }
        val tabs = listOf(contactsTab) + chatRoomTabs.values + chatTabs.values

        RiftTabBar(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { id ->
                val model = tabs.firstOrNull { it.id == id }?.payload as? TabModel ?: return@RiftTabBar
                onTabSelect(model)
            },
            onTabClosed = { id ->
                val model = tabs.firstOrNull { it.id == id }?.payload as? TabModel ?: return@RiftTabBar
                when (model) {
                    TabModel.Contacts -> {}
                    is TabModel.MultiUserChat -> state.jabberState.multiUserChats.firstOrNull { it.room == model.chat }?.let { onChatRoomClosed(it) }
                    is TabModel.UserChat -> onChatClosed(model.chat.chat)
                }
                onTabSelect(TabModel.Contacts)
            },
            modifier = Modifier
                .fillMaxWidth(),
        )
        if (selectedTab == 0) {
            AnimatedContent(
                state.contactListState,
                modifier = Modifier.weight(1f),
            ) { contactListState ->
                when (contactListState) {
                    ContactListState.Contacts -> {
                        Column {
                            ContactsList(
                                multiUserChats = state.jabberState.multiUserChats,
                                users = state.jabberState.users.values.toList(),
                                collapsedGroups = state.collapsedGroups,
                                onRosterUserClick = onRosterUserClick,
                                onRosterUserRemove = onRosterUserRemove,
                                onChatRoomClick = onChatRoomClick,
                                onChatRoomRemove = onChatRoomRemove,
                                onCollapsedGroupToggle = onCollapsedGroupToggle,
                                modifier = Modifier.weight(1f),
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.medium)
                                    .padding(bottom = Spacing.medium),
                            ) {
                                RiftButton(
                                    text = stringResource(Res.string.jabber_add_contact_button),
                                    cornerCut = ButtonCornerCut.BottomLeft,
                                    onClick = onAddContactClick,
                                    modifier = Modifier.weight(1f),
                                )
                                RiftButton(
                                    text = stringResource(Res.string.jabber_add_room_button),
                                    cornerCut = ButtonCornerCut.BottomRight,
                                    onClick = onAddChatRoomClick,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    is ContactListState.AddContact -> {
                        AddContact(
                            error = contactListState.error,
                            onAddContactSubmitClick = onAddContactSubmitClick,
                            onBackClick = onBackClick,
                        )
                    }
                    is ContactListState.AddChatRoom -> {
                        AddChatRoom(
                            error = contactListState.error,
                            onAddChatRoomSubmitClick = onAddChatRoomSubmitClick,
                            onBackClick = onBackClick,
                        )
                    }
                }
            }
        } else {
            val chatRoom = chatRoomTabs.entries.firstOrNull { it.value.id == selectedTab }?.key?.value
                ?.let { jid -> state.jabberState.multiUserChats.firstOrNull { it.room == jid } }
            if (chatRoom != null) {
                val messages = state.jabberState.multiUserChatMessages[chatRoom] ?: emptyList()
                MultiUserChat(
                    multiUserChat = chatRoom,
                    subject = state.jabberState.multiUserChatSubjects[chatRoom.room],
                    messages = messages,
                    isUsingBiggerFontSize = state.isUsingBiggerFontSize,
                    onMessageSend = { onChatRoomMessageSend(chatRoom, it) },
                )
            } else {
                val userChat = chatTabs.entries.first { it.value.id == selectedTab }.key.value
                val rosterUser = state.jabberState.users[userChat.chat.xmppAddressOfChatPartner]
                val messages = state.jabberState.userChatMessages[userChat.chat] ?: emptyList()
                key(userChat) {
                    UserChat(
                        userChat = userChat,
                        rosterUser = rosterUser,
                        messages = messages,
                        isUsingBiggerFontSize = state.isUsingBiggerFontSize,
                        onMessageSend = { onMessageSend(userChat, it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AddContact(
    error: UiText?,
    onAddContactSubmitClick: (jidLocalPart: String, name: String, groups: List<String>) -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.jabber_add_contact_button),
            style = RiftTheme.typography.headerPrimary,
        )
        var jidLocalPart by remember { mutableStateOf("") }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RiftTextField(
                text = jidLocalPart,
                placeholder = stringResource(Res.string.jabber_username_label),
                onTextChanged = {
                    jidLocalPart = it
                },
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "@goonfleet.com",
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        Text(
            text = stringResource(Res.string.jabber_nickname_label),
            style = RiftTheme.typography.headerPrimary,
        )
        var name by remember { mutableStateOf("") }
        RiftTextField(
            text = name,
            placeholder = stringResource(Res.string.jabber_nickname_placeholder),
            onTextChanged = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(Res.string.jabber_group_label),
            style = RiftTheme.typography.headerPrimary,
        )
        var group by remember { mutableStateOf("") }
        RiftTextField(
            text = group,
            placeholder = stringResource(Res.string.jabber_group_placeholder),
            onTextChanged = {
                group = it
            },
            modifier = Modifier.fillMaxWidth(),
        )
        if (error != null) {
            Text(
                text = error.resolve(),
                style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.borderError),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            RiftButton(
                text = stringResource(Res.string.jabber_back_button),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.BottomLeft,
                onClick = { onBackClick() },
                modifier = Modifier.weight(1f),
            )
            RiftButton(
                text = stringResource(Res.string.jabber_add_contact_button),
                cornerCut = ButtonCornerCut.BottomRight,
                onClick = {
                    val name = name.takeIf { it.isNotBlank() } ?: jidLocalPart
                    onAddContactSubmitClick(jidLocalPart, name, listOf(group))
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AddChatRoom(
    error: UiText?,
    onAddChatRoomSubmitClick: (jidLocalPart: String) -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(Spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.jabber_add_room_button),
            style = RiftTheme.typography.headerPrimary,
        )
        var jidLocalPart by remember { mutableStateOf("") }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RiftTextField(
                text = jidLocalPart,
                placeholder = stringResource(Res.string.jabber_room_name_placeholder),
                onTextChanged = {
                    jidLocalPart = it
                },
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "@conference.goonfleet.com",
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        if (error != null) {
            Text(
                text = error.resolve(),
                style = RiftTheme.typography.bodyPrimary.copy(color = RiftTheme.colors.borderError),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            RiftButton(
                text = stringResource(Res.string.jabber_back_button),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.BottomLeft,
                onClick = { onBackClick() },
                modifier = Modifier.weight(1f),
            )
            RiftButton(
                text = stringResource(Res.string.jabber_add_room_button),
                cornerCut = ButtonCornerCut.BottomRight,
                onClick = { onAddChatRoomSubmitClick(jidLocalPart) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun UserChat(
    userChat: UserChat,
    rosterUser: RosterUser?,
    messages: List<UserMessage>,
    isUsingBiggerFontSize: Boolean,
    onMessageSend: (String) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.medium),
        ) {
            rosterUser?.bestPresence?.let {
                PresenceIndicatorDot(
                    presence = it,
                    isSubscriptionPending = rosterUser.isSubscriptionPending,
                )
            }
            Text(
                text = userChat.name,
                style = RiftTheme.typography.headerPrimary,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(RiftTheme.colors.borderGreyLight),
        ) {}
        val listState = rememberLazyListState()
        var lastMessage by remember { mutableStateOf(messages.lastOrNull()) }
        LaunchedEffect(messages) {
            val newLastMessage = messages.lastOrNull()
            if (newLastMessage != lastMessage) {
                if (messages.lastIndex >= 0) listState.animateScrollToItem(messages.lastIndex)
                lastMessage = newLastMessage
            }
        }
        LaunchedEffect(Unit) {
            if (messages.lastIndex >= 0) listState.animateScrollToItem(messages.lastIndex)
        }
        ScrollbarLazyColumn(
            listState = listState,
            scrollbarModifier = Modifier.padding(Spacing.small),
            modifier = Modifier.weight(1f),
        ) {
            items(messages) {
                ChatMessage(isUsingBiggerFontSize, userChat, it)
            }
        }
        NewMessageField(onMessageSend)
    }
}

@Composable
private fun MultiUserChat(
    multiUserChat: MultiUserChat,
    subject: String?,
    messages: List<MultiUserMessage>,
    isUsingBiggerFontSize: Boolean,
    onMessageSend: (String) -> Unit,
) {
    Column {
        ScrollbarColumn(
            modifier = Modifier
                .heightIn(max = 85.dp)
                .padding(Spacing.medium),
        ) {
            if (subject != null) {
                val linkStyle = SpanStyle(color = RiftTheme.colors.textLink, fontWeight = FontWeight.Bold)
                val linkifiedSubject = remember(subject) { annotateLinks(subject.trim(), linkStyle) }
                Text(
                    text = linkifiedSubject,
                    style = RiftTheme.typography.bodyPrimary,
                )
            } else {
                Text(
                    text = multiUserChat.room.localpartOrNull?.toString() ?: "",
                    style = RiftTheme.typography.headerPrimary,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(RiftTheme.colors.borderGreyLight),
        ) {}
        val listState = rememberLazyListState()
        var lastMessage by remember { mutableStateOf(messages.lastOrNull()) }
        LaunchedEffect(messages) {
            val newLastMessage = messages.lastOrNull()
            if (newLastMessage != lastMessage) {
                if (messages.lastIndex >= 0) listState.animateScrollToItem(messages.lastIndex)
                lastMessage = newLastMessage
            }
        }
        LaunchedEffect(Unit) {
            if (messages.lastIndex >= 0) listState.animateScrollToItem(messages.lastIndex)
        }
        ScrollbarLazyColumn(
            listState = listState,
            contentPadding = PaddingValues(vertical = Spacing.medium),
            scrollbarModifier = Modifier.padding(Spacing.small),
            modifier = Modifier.weight(1f),
        ) {
            items(messages) {
                ChatMessage(isUsingBiggerFontSize, multiUserChat, it)
            }
        }
        NewMessageField(onMessageSend)
    }
}

@Composable
private fun NewMessageField(
    onMessageSend: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier
            .padding(horizontal = Spacing.medium)
            .padding(bottom = Spacing.medium),
    ) {
        var messageInput by remember { mutableStateOf("") }
        RiftTextField(
            text = messageInput,
            onTextChanged = { messageInput = it },
            isSingleLine = false,
            modifier = Modifier
                .weight(1f)
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter && !it.isShiftPressed) {
                        if (it.type == KeyEventType.KeyDown) {
                            onMessageSend(messageInput)
                            messageInput = ""
                        }
                        true
                    } else {
                        false
                    }
                },
        )
        RiftTooltipArea(stringResource(Res.string.jabber_send_tooltip)) {
            RiftButton(
                text = stringResource(Res.string.jabber_send_button),
                onClick = {
                    onMessageSend(messageInput)
                    messageInput = ""
                },
            )
        }
    }
}

@Composable
private fun ChatMessage(
    isUsingBiggerFontSize: Boolean,
    userChat: UserChat,
    message: UserMessage,
) {
    val from = if (message.isOutgoing) stringResource(Res.string.jabber_self_sender_name) else userChat.name
    ChatMessage(isUsingBiggerFontSize, message.timestamp, from, message.text)
}

@Composable
private fun ChatMessage(
    isUsingBiggerFontSize: Boolean,
    userChat: MultiUserChat,
    message: MultiUserMessage,
) {
    val from = message.sender ?: stringResource(Res.string.jabber_self_sender_name)
    ChatMessage(isUsingBiggerFontSize, message.timestamp, from, message.text)
}

@Composable
private fun ChatMessage(
    isUsingBiggerFontSize: Boolean,
    timestamp: Instant,
    sender: String,
    message: String,
) {
    val time: ZonedDateTime = ZonedDateTime.ofInstant(timestamp, ZoneId.systemDefault())
    val formatter = if (time.isBefore(ZonedDateTime.of(LocalDate.now().atTime(0, 0), ZoneId.systemDefault()))) {
        DateTimeFormatter.ofPattern("MMM dd, HH:mm:ss")
    } else {
        DateTimeFormatter.ofPattern("HH:mm:ss")
    }
    val formattedTime = formatter.format(time)
    val linkStyle = SpanStyle(color = RiftTheme.colors.textLink, fontWeight = FontWeight.Bold)
    val linkifiedMessage = remember(message) { annotateLinks(message, linkStyle) }
    val text = buildAnnotatedString {
        append("[$formattedTime] ")
        withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted)) {
            append(sender)
        }
        append(" > ")
        append(linkifiedMessage)
    }

    RiftContextMenuArea(
        items = listOf(
            TextItem(stringResource(Res.string.jabber_copy_row_action), onClick = { Clipboard.copy(text.toString()) }),
            TextItem(stringResource(Res.string.jabber_copy_message_action), onClick = { Clipboard.copy(message) }),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hoverBackground()
                .padding(horizontal = Spacing.medium, vertical = Spacing.verySmall),
        ) {
            val style = if (isUsingBiggerFontSize) RiftTheme.typography.headerPrimary else RiftTheme.typography.bodyPrimary
            Text(
                text = text,
                style = style,
            )
        }
    }
}
