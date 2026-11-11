package dev.nohus.rift.about

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.about.AboutViewModel.UiState
import dev.nohus.rift.about.UpdateController.UpdateAvailability.NOT_PACKAGED
import dev.nohus.rift.about.UpdateController.UpdateAvailability.NO_UPDATE
import dev.nohus.rift.about.UpdateController.UpdateAvailability.UNKNOWN
import dev.nohus.rift.about.UpdateController.UpdateAvailability.UPDATE_AUTOMATIC
import dev.nohus.rift.about.UpdateController.UpdateAvailability.UPDATE_MANUAL
import dev.nohus.rift.compose.AffiliateCode
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.CreatorCode
import dev.nohus.rift.compose.LinkText
import dev.nohus.rift.compose.MouseButton
import dev.nohus.rift.compose.Patrons
import dev.nohus.rift.compose.RiftAppName
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.onMouseClick
import dev.nohus.rift.compose.text.parseEveFormattedText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.about_credits_action
import dev.nohus.rift.generated.resources.about_credits_description
import dev.nohus.rift.generated.resources.about_debug_action
import dev.nohus.rift.generated.resources.about_developer_caption
import dev.nohus.rift.generated.resources.about_development_version_status
import dev.nohus.rift.generated.resources.about_discord_description
import dev.nohus.rift.generated.resources.about_discord_invite_action
import dev.nohus.rift.generated.resources.about_legal_action
import dev.nohus.rift.generated.resources.about_legal_description
import dev.nohus.rift.generated.resources.about_linux_update_description
import dev.nohus.rift.generated.resources.about_macos_move_application_description
import dev.nohus.rift.generated.resources.about_macos_restart_description
import dev.nohus.rift.generated.resources.about_portable_version_status
import dev.nohus.rift.generated.resources.about_release_notes_action
import dev.nohus.rift.generated.resources.about_up_to_date_status
import dev.nohus.rift.generated.resources.about_update_availability_unknown
import dev.nohus.rift.generated.resources.about_update_available_status
import dev.nohus.rift.generated.resources.about_update_button
import dev.nohus.rift.generated.resources.about_update_check_error
import dev.nohus.rift.generated.resources.about_update_check_status
import dev.nohus.rift.generated.resources.about_update_ready_description
import dev.nohus.rift.generated.resources.about_window_title
import dev.nohus.rift.generated.resources.about_windows_update_description
import dev.nohus.rift.generated.resources.partner_400
import dev.nohus.rift.generated.resources.window_achievements
import dev.nohus.rift.generated.resources.window_concord
import dev.nohus.rift.generated.resources.window_info
import dev.nohus.rift.generated.resources.window_rift_64
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.network.AsyncResource
import dev.nohus.rift.utils.Links
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.OperatingSystem.Linux
import dev.nohus.rift.utils.OperatingSystem.MacOs
import dev.nohus.rift.utils.OperatingSystem.Windows
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.toURIOrNull
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutWindow(
    windowState: WindowManager.RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: AboutViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.about_window_title),
        icon = Res.drawable.window_rift_64,
        state = windowState,
        onCloseClick = onCloseRequest,
        isResizable = false,
    ) {
        AboutWindowContent(
            state = state,
            onUpdateClick = viewModel::onUpdateClick,
            onLogLiteClick = viewModel::onLogLiteClick,
            onDebugClick = viewModel::onDebugClick,
            onLegalClick = viewModel::onLegalClick,
            onCreditsClick = viewModel::onCreditsClick,
            onWhatsNewClick = viewModel::onWhatsNewClick,
        )

        if (state.isUpdateDialogShown) {
            RiftDialog(
                title = stringResource(Res.string.about_update_available_status),
                icon = Res.drawable.window_info,
                parentState = windowState,
                state = rememberWindowState(width = 400.dp, height = Dp.Unspecified),
                onCloseClick = viewModel::onDialogDismissed,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.updateAvailability.success == UPDATE_AUTOMATIC) {
                        Text(
                            text = stringResource(Res.string.about_update_ready_description),
                            style = RiftTheme.typography.headerPrimary,
                        )
                        RiftButton(
                            text = stringResource(Res.string.about_update_button),
                            onClick = viewModel::onTriggerUpdateClick,
                            modifier = Modifier.align(Alignment.End),
                        )
                    } else {
                        Text(
                            text = getUpdateDialogText(state.operatingSystem, state.executablePath),
                            style = RiftTheme.typography.headerPrimary,
                        )
                    }
                }
            }
        }

        if (state.isLegalDialogShown) {
            RiftDialog(
                title = stringResource(Res.string.about_legal_action),
                icon = Res.drawable.window_concord,
                parentState = windowState,
                state = rememberWindowState(width = 400.dp, height = 250.dp),
                onCloseClick = viewModel::onDialogDismissed,
            ) {
                ScrollbarColumn {
                    Text(
                        text = getLegalText(),
                        style = RiftTheme.typography.headerPrimary,
                    )
                }
            }
        }

        if (state.isCreditsDialogShown) {
            RiftDialog(
                title = stringResource(Res.string.about_credits_action),
                icon = Res.drawable.window_achievements,
                parentState = windowState,
                state = rememberWindowState(width = 400.dp, height = Dp.Unspecified),
                onCloseClick = viewModel::onDialogDismissed,
            ) {
                Text(
                    text = getCreditsText(),
                    style = RiftTheme.typography.headerPrimary,
                )
            }
        }
    }
}

@Composable
private fun AboutWindowContent(
    state: UiState,
    onUpdateClick: () -> Unit,
    onLogLiteClick: () -> Unit,
    onDebugClick: () -> Unit,
    onLegalClick: () -> Unit,
    onCreditsClick: () -> Unit,
    onWhatsNewClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = Modifier.width(200.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.partner_400),
                    contentDescription = null,
                    modifier = Modifier
                        .width(200.dp),
                )
                AffiliateCode()
                CreatorCode()
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f),
            ) {
                Column {
                    RiftAppName(RiftTheme.typography.headlineHighlighted)
                    RiftTooltipArea(
                        text = state.buildTime.resolve(),
                        modifier = Modifier.padding(top = Spacing.medium),
                    ) {
                        Text(
                            text = state.version.resolve(),
                            style = RiftTheme.typography.headerPrimary,
                        )
                    }
                    AnimatedContent(state.updateAvailability) { isUpdateAvailable ->
                        when (isUpdateAvailable) {
                            is AsyncResource.Error -> {
                                Text(
                                    text = stringResource(Res.string.about_update_check_error),
                                    style = RiftTheme.typography.bodySecondary,
                                )
                            }

                            AsyncResource.Loading -> {
                                Text(
                                    text = stringResource(Res.string.about_update_check_status),
                                    style = RiftTheme.typography.bodySecondary,
                                )
                            }

                            is AsyncResource.Ready -> {
                                when (isUpdateAvailable.value) {
                                    NOT_PACKAGED -> {
                                        val text = if (state.isDevelopmentVersion) {
                                            stringResource(Res.string.about_development_version_status)
                                        } else {
                                            stringResource(Res.string.about_portable_version_status)
                                        }
                                        Text(
                                            text = text,
                                            style = RiftTheme.typography.bodySecondary,
                                        )
                                    }

                                    UNKNOWN -> {
                                        Text(
                                            text = stringResource(Res.string.about_update_availability_unknown),
                                            style = RiftTheme.typography.bodySecondary,
                                        )
                                    }

                                    NO_UPDATE -> {
                                        Text(
                                            text = stringResource(Res.string.about_up_to_date_status),
                                            style = RiftTheme.typography.bodySecondary,
                                        )
                                    }

                                    UPDATE_MANUAL, UPDATE_AUTOMATIC -> {
                                        LinkText(
                                            text = stringResource(Res.string.about_update_available_status),
                                            onClick = onUpdateClick,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = stringResource(Res.string.about_developer_caption),
                        style = RiftTheme.typography.headerPrimary,
                        modifier = Modifier.padding(top = Spacing.medium),
                    )
                    LinkText(
                        text = Links.website,
                        onClick = { Links.website.toURIOrNull()?.openBrowser() },
                    )

                    Text(
                        text = stringResource(Res.string.about_discord_description),
                        style = RiftTheme.typography.headerPrimary,
                        modifier = Modifier.padding(top = Spacing.medium),
                    )
                    LinkText(
                        text = stringResource(Res.string.about_discord_invite_action),
                        onClick = { Links.discordInvite.toURIOrNull()?.openBrowser() },
                    )

                    Text(
                        text = "© 2023–2026 Nohus",
                        style = RiftTheme.typography.bodySecondary,
                        modifier = Modifier.padding(top = Spacing.medium),
                    )
                    LinkText(
                        text = stringResource(Res.string.about_legal_action),
                        onClick = onLegalClick,
                    )
                }
            }
        }
        Patrons(state.patrons)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.align(Alignment.End),
        ) {
            var isLogLiteVisible by remember { mutableStateOf(false) }
            if (isLogLiteVisible) {
                RiftButton(
                    text = "LogLite",
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = onLogLiteClick,
                )
            }
            RiftButton(
                text = stringResource(Res.string.about_debug_action),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.None,
                onClick = onDebugClick,
                modifier = Modifier.onMouseClick(MouseButton.Right) {
                    isLogLiteVisible = true
                },
            )
            RiftButton(
                text = stringResource(Res.string.about_credits_action),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.None,
                onClick = onCreditsClick,
            )
            RiftButton(
                text = stringResource(Res.string.about_release_notes_action),
                type = ButtonType.Primary,
                onClick = onWhatsNewClick,
            )
        }
    }
}

@Composable
private fun getUpdateDialogText(
    operatingSystem: OperatingSystem,
    executablePath: String,
): AnnotatedString {
    return when (operatingSystem) {
        Linux -> parseEveFormattedText(stringResource(Res.string.about_linux_update_description))
        Windows -> AnnotatedString(stringResource(Res.string.about_windows_update_description))
        MacOs -> AnnotatedString(
            if ("/AppTranslocation/" in executablePath) {
                stringResource(Res.string.about_macos_move_application_description)
            } else {
                stringResource(Res.string.about_macos_restart_description)
            },
        )
    }
}

@Composable
private fun getLegalText(): AnnotatedString = parseEveFormattedText(stringResource(Res.string.about_legal_description))

@Composable
private fun getCreditsText(): AnnotatedString = parseEveFormattedText(stringResource(Res.string.about_credits_description))
