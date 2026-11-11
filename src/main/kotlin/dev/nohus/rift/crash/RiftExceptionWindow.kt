package dev.nohus.rift.crash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.BuildConfig
import dev.nohus.rift.about.UpdateController
import dev.nohus.rift.about.UpdateController.UpdateAvailability.UPDATE_AUTOMATIC
import dev.nohus.rift.about.UpdateController.UpdateAvailability.UPDATE_MANUAL
import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.SmartAlwaysAboveRepository
import dev.nohus.rift.compose.TransparentWindowController
import dev.nohus.rift.compose.UiScaleController
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koinOrNull
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_action_close
import dev.nohus.rift.generated.resources.crash_copy_code_guidance
import dev.nohus.rift.generated.resources.crash_error_code_caption
import dev.nohus.rift.generated.resources.crash_hide_details_action
import dev.nohus.rift.generated.resources.crash_latest_version_guidance
import dev.nohus.rift.generated.resources.crash_outdated_version_warning
import dev.nohus.rift.generated.resources.crash_problem_description
import dev.nohus.rift.generated.resources.crash_report_action
import dev.nohus.rift.generated.resources.crash_report_guidance
import dev.nohus.rift.generated.resources.crash_show_details_action
import dev.nohus.rift.generated.resources.crash_window_title
import dev.nohus.rift.generated.resources.window_log
import dev.nohus.rift.utils.Links
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.sound.SoundPlayer
import dev.nohus.rift.utils.sound.UiSoundPlayer
import dev.nohus.rift.utils.toURIOrNull
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import dev.nohus.rift.windowing.WindowStatesController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

@Composable
fun RiftExceptionWindow(
    throwable: Throwable,
    errorId: String,
    onCloseRequest: () -> Unit,
) {
    val riftWindowScale = remember { resolveRiftWindowScale() }
    val scale = riftWindowScale ?: 1f
    val windowState = rememberWindowState(width = (450 * scale).dp, height = (240 * scale).dp)
    val title = stringResource(Res.string.crash_window_title)
    val content: @Composable () -> Unit = {
        RiftExceptionContent(throwable, errorId, onCloseRequest, windowState, scale, isSoundEnabled = riftWindowScale != null)
    }
    if (riftWindowScale != null) {
        RiftWindow(
            title = title,
            icon = Res.drawable.window_log,
            state = remember {
                RiftWindowState(windowState = windowState, minimumSize = (450 * scale).toInt() to (240 * scale).toInt())
            },
            onCloseClick = onCloseRequest,
        ) {
            content()
        }
    } else {
        Window(
            title = title,
            icon = painterResource(Res.drawable.window_log),
            state = windowState,
            onCloseRequest = onCloseRequest,
        ) {
            LaunchedEffect(Unit) {
                window.minimumSize = Dimension(450, 240)
            }
            Column(
                modifier = Modifier.fillMaxSize().background(RiftTheme.colors.windowBackground).padding(Spacing.medium),
            ) {
                content()
            }
        }
    }
}

private fun resolveRiftWindowScale(): Float? {
    val koin = koinOrNull ?: return null
    return try {
        // Resolve the window and title bar dependencies before entering composition
        val uiScaleController = koin.get<UiScaleController>()
        koin.get<TransparentWindowController>()
        koin.get<WindowStatesController>()
        koin.get<SmartAlwaysAboveRepository>()
        koin.get<OperatingSystem>()
        koin.get<UiSoundPlayer>()
        koin.get<SoundPlayer>()
        uiScaleController.uiScale
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun RiftExceptionContent(
    throwable: Throwable,
    errorId: String,
    onCloseRequest: () -> Unit,
    windowState: WindowState,
    scale: Float,
    isSoundEnabled: Boolean,
) {
    var isUpdateAvailable: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        try {
            val updateAvailability = UpdateController().isUpdateAvailable()
            isUpdateAvailable = updateAvailability == UPDATE_MANUAL || updateAvailability == UPDATE_AUTOMATIC
        } catch (_: Exception) {}
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        var areDetailsVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            SelectionContainer {
                val highlightStyle = SpanStyle(
                    color = RiftTheme.typography.bodySpecialHighlighted.color,
                    fontWeight = FontWeight.Bold,
                )
                AnimatedContent(isUpdateAvailable) { isUpdateAvailable ->
                    val problemDescription = stringResource(Res.string.crash_problem_description)
                    val outdatedVersionWarning = stringResource(Res.string.crash_outdated_version_warning)
                    val latestVersionGuidance = stringResource(Res.string.crash_latest_version_guidance)
                    val errorCodeCaption = stringResource(Res.string.crash_error_code_caption, errorId)
                    val reportGuidance = stringResource(Res.string.crash_report_guidance)
                    val copyCodeGuidance = stringResource(Res.string.crash_copy_code_guidance)
                    Text(
                        text = buildAnnotatedString {
                            appendLine(problemDescription)
                            if (isUpdateAvailable) {
                                appendLine()
                                withStyle(highlightStyle) {
                                    appendLine(outdatedVersionWarning)
                                }
                                appendLine(latestVersionGuidance)
                            } else {
                                val codeIndex = errorCodeCaption.indexOf(errorId)
                                val captionStart = length
                                appendLine(errorCodeCaption)
                                if (codeIndex >= 0) {
                                    addStyle(highlightStyle, captionStart + codeIndex, captionStart + codeIndex + errorId.length)
                                }
                                appendLine()
                                appendLine(reportGuidance)
                                append(copyCodeGuidance)
                            }
                        },
                    )
                }
            }
            if (areDetailsVisible) {
                ScrollbarColumn(
                    modifier = Modifier
                        .padding(vertical = Spacing.medium)
                        .border(1.dp, RiftTheme.colors.borderGrey),
                    scrollbarModifier = Modifier.padding(vertical = Spacing.small),
                ) {
                    val text = throwable.stackTraceToString()
                    SelectionContainer {
                        Text(
                            text = text,
                            modifier = Modifier.padding(Spacing.medium),
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.align(Alignment.End),
        ) {
            @Suppress("KotlinConstantConditions")
            if (BuildConfig.isDevEnvironment) {
                val detailsButtonText = stringResource(
                    if (areDetailsVisible) Res.string.crash_hide_details_action else Res.string.crash_show_details_action,
                )
                RiftButton(
                    text = detailsButtonText,
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    isSoundEnabled = isSoundEnabled,
                    onClick = {
                        areDetailsVisible = !areDetailsVisible
                        if (areDetailsVisible) {
                            windowState.size = DpSize((650 * scale).dp, (400 * scale).dp)
                        } else {
                            windowState.size = DpSize((450 * scale).dp, (240 * scale).dp)
                        }
                    },
                )
            }
            RiftButton(
                text = stringResource(Res.string.common_action_close),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.None,
                isSoundEnabled = isSoundEnabled,
                onClick = onCloseRequest,
            )
            AnimatedVisibility(!isUpdateAvailable) {
                RiftButton(
                    text = stringResource(Res.string.crash_report_action),
                    type = ButtonType.Primary,
                    isSoundEnabled = isSoundEnabled,
                    onClick = {
                        Clipboard.copy("Error code: $errorId")
                        Links.discordTicket.toURIOrNull()?.openBrowser()
                    },
                )
            }
        }
    }
}
