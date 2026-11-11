package dev.nohus.rift.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AnimatedImage
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftDropdownWithLabel
import dev.nohus.rift.compose.RiftMessageDialog
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.TypingText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.configurationpack.displayName
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.partner_400
import dev.nohus.rift.generated.resources.settings_other_settings_default_text
import dev.nohus.rift.generated.resources.tray_tray_128
import dev.nohus.rift.generated.resources.window_agent
import dev.nohus.rift.generated.resources.wizard_characters_detected_body
import dev.nohus.rift.generated.resources.wizard_characters_partial_body
import dev.nohus.rift.generated.resources.wizard_characters_step_all_characters_are_authenticated_ready_text
import dev.nohus.rift.generated.resources.wizard_characters_step_authenticate_characters_text
import dev.nohus.rift.generated.resources.wizard_characters_step_characters_detected_text
import dev.nohus.rift.generated.resources.wizard_characters_step_characters_partially_set_up_text
import dev.nohus.rift.generated.resources.wizard_characters_step_characters_set_up_text
import dev.nohus.rift.generated.resources.wizard_characters_step_check_characters_text
import dev.nohus.rift.generated.resources.wizard_characters_step_i_did_not_detect_any_text
import dev.nohus.rift.generated.resources.wizard_characters_step_no_characters_detected_text
import dev.nohus.rift.generated.resources.wizard_configuration_packs_step_alliance_features_text
import dev.nohus.rift.generated.resources.wizard_configuration_packs_step_configuration_pack_label
import dev.nohus.rift.generated.resources.wizard_configuration_packs_step_selected_default_settings_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_change_installation_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_check_installation_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_eve_online_detected_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_eve_online_located_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_eve_online_not_detected_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_i_have_located_installation_eve_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_installation_eve_now_set_up_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_select_installation_text
import dev.nohus.rift.generated.resources.wizard_eve_installation_step_set_up_rift_properly_i_text
import dev.nohus.rift.generated.resources.wizard_finish_step_all_done_text
import dev.nohus.rift.generated.resources.wizard_finish_step_rift_now_ready_enhance_capabilities_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_add_intel_channels_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_change_intel_channels_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_have_configured_intel_channels_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_if_are_alliance_intel_channels_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_intel_channels_set_up_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_intel_channels_text
import dev.nohus.rift.generated.resources.wizard_intel_channels_step_skip_text
import dev.nohus.rift.generated.resources.wizard_pack_features_body
import dev.nohus.rift.generated.resources.wizard_step_content_continue_anyway_text
import dev.nohus.rift.generated.resources.wizard_step_content_continue_text
import dev.nohus.rift.generated.resources.wizard_welcome_step_are_now_possession_rift_prototype_text
import dev.nohus.rift.generated.resources.wizard_welcome_step_welcome_capsuleer_text
import dev.nohus.rift.get
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.settings.persistence.ConfigurationPack
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import dev.nohus.rift.wizard.WizardViewModel.EveInstallationState
import dev.nohus.rift.wizard.WizardViewModel.UiState
import dev.nohus.rift.wizard.WizardViewModel.WizardStep
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WizardWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: WizardViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = "RIFT Intel Fusion Tool",
        icon = Res.drawable.window_agent,
        state = windowState,
        onCloseClick = onCloseRequest,
        isResizable = false,
    ) {
        WizardWindowContent(
            state = state,
            onSetEveInstallationClick = viewModel::onSetEveInstallationClick,
            onCharactersClick = viewModel::onCharactersClick,
            onConfigurationPackChange = viewModel::onConfigurationPackChange,
            onSetIntelChannelsClick = viewModel::onSetIntelChannelsClick,
            onContinueClick = viewModel::onContinueClick,
            onKeyEvent = viewModel::onKeyEvent,
        )

        state.dialogMessage?.let {
            RiftMessageDialog(
                dialog = it,
                parentWindowState = windowState,
                onDismiss = viewModel::onCloseDialogMessage,
            )
        }

        if (state.onFinishedEvent.get()) onCloseRequest()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WizardWindowContent(
    state: UiState,
    onSetEveInstallationClick: () -> Unit,
    onCharactersClick: () -> Unit,
    onConfigurationPackChange: (ConfigurationPack?) -> Unit,
    onSetIntelChannelsClick: () -> Unit,
    onContinueClick: () -> Unit,
    onKeyEvent: (KeyEvent) -> Unit,
) {
    val focusRequester = FocusRequester()
    Row(
        modifier = Modifier
            .onKeyEvent {
                onKeyEvent(it)
                false
            }
            .focusRequester(focusRequester)
            .focusable()
            .onClick { focusRequester.requestFocus() },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(end = Spacing.large),
        ) {
            AnimatedImage(
                resource = "aura.gif",
                modifier = Modifier
                    .size(200.dp)
                    .border(2.dp, RiftTheme.colors.borderPrimary),
            )
            Image(
                painter = painterResource(Res.drawable.partner_400),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp),
            )
        }
        when (val step = state.step) {
            WizardStep.Welcome -> WelcomeStep(
                onContinueClick = onContinueClick,
            )
            is WizardStep.EveInstallation -> EveInstallationStep(
                step = step,
                onSetEveInstallationClick = onSetEveInstallationClick,
                onContinueClick = onContinueClick,
            )
            is WizardStep.Characters -> CharactersStep(
                step = step,
                onCharactersClick = onCharactersClick,
                onContinueClick = onContinueClick,
            )
            is WizardStep.ConfigurationPacks -> ConfigurationPacksStep(
                step = step,
                onConfigurationPackChange = onConfigurationPackChange,
                onContinueClick = onContinueClick,
            )
            is WizardStep.IntelChannels -> IntelChannelsStep(
                step = step,
                onSetIntelChannelsClick = onSetIntelChannelsClick,
                onContinueClick = onContinueClick,
            )
            WizardStep.Finish -> FinishStep(
                onContinueClick = onContinueClick,
            )
        }
    }
}

@Composable
private fun WelcomeStep(
    onContinueClick: () -> Unit,
) {
    var hasFinishedTyping by remember { mutableStateOf(false) }
    StepContent(
        onContinueClick = onContinueClick,
        isContinueVisible = hasFinishedTyping,
    ) {
        val text = buildAnnotatedString {
            withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                append(stringResource(Res.string.wizard_welcome_step_welcome_capsuleer_text))
            }
            append(
                stringResource(Res.string.wizard_welcome_step_are_now_possession_rift_prototype_text),
            )
        }
        TypingText(
            text = text,
            style = RiftTheme.typography.headerPrimary,
            onFinishedTyping = { hasFinishedTyping = true },
        )
    }
}

@Composable
private fun EveInstallationStep(
    step: WizardStep.EveInstallation,
    onSetEveInstallationClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    val isContinueWarning = when (step.state) {
        EveInstallationState.None -> true
        EveInstallationState.Detected -> false
        EveInstallationState.Set -> false
    }
    var hasFinishedTyping by remember { mutableStateOf(false) }

    StepContent(
        onContinueClick = onContinueClick,
        isContinueVisible = hasFinishedTyping,
        isWarning = isContinueWarning,
    ) {
        val text = buildAnnotatedString {
            when (step.state) {
                EveInstallationState.None -> {
                    withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                        append(stringResource(Res.string.wizard_eve_installation_step_eve_online_not_detected_text))
                    }
                    append(
                        stringResource(Res.string.wizard_eve_installation_step_set_up_rift_properly_i_text),
                    )
                }
                EveInstallationState.Detected -> {
                    withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                        append(stringResource(Res.string.wizard_eve_installation_step_eve_online_detected_text))
                    }
                    append(
                        stringResource(Res.string.wizard_eve_installation_step_i_have_located_installation_eve_text),
                    )
                }
                EveInstallationState.Set -> {
                    withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                        append(stringResource(Res.string.wizard_eve_installation_step_eve_online_located_text))
                    }
                    append(
                        stringResource(Res.string.wizard_eve_installation_step_installation_eve_now_set_up_text),
                    )
                }
            }
        }
        TypingText(
            text = text,
            style = RiftTheme.typography.headerPrimary,
            onFinishedTyping = { hasFinishedTyping = true },
        )
        AnimatedVisibility(
            visible = hasFinishedTyping,
            enter = fadeIn(),
            modifier = Modifier
                .padding(top = Spacing.large)
                .align(Alignment.CenterHorizontally),
        ) {
            val [type, buttonText] = when (step.state) {
                EveInstallationState.None -> ButtonType.Primary to stringResource(Res.string.wizard_eve_installation_step_select_installation_text)
                EveInstallationState.Detected -> ButtonType.Secondary to stringResource(Res.string.wizard_eve_installation_step_check_installation_text)
                EveInstallationState.Set -> ButtonType.Secondary to stringResource(Res.string.wizard_eve_installation_step_change_installation_text)
            }
            RiftButton(
                text = buttonText,
                type = type,
                cornerCut = ButtonCornerCut.Both,
                onClick = onSetEveInstallationClick,
            )
        }
    }
}

@Composable
private fun CharactersStep(
    step: WizardStep.Characters,
    onCharactersClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    var hasFinishedTyping by remember { mutableStateOf(false) }
    StepContent(
        onContinueClick = onContinueClick,
        isContinueVisible = hasFinishedTyping,
        isWarning = step.authenticatedCharacterCount < step.characterCount,
    ) {
        val text = buildAnnotatedString {
            if (step.characterCount == 0) {
                withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                    append(stringResource(Res.string.wizard_characters_step_no_characters_detected_text))
                }
                append(
                    stringResource(Res.string.wizard_characters_step_i_did_not_detect_any_text),
                )
            } else {
                if (step.authenticatedCharacterCount == 0) {
                    withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                        append(stringResource(Res.string.wizard_characters_step_characters_detected_text))
                    }
                    append(
                        Res.string.wizard_characters_detected_body.toUiText(step.characterCount.toUiTextArgument()).resolve(),
                    )
                } else if (step.authenticatedCharacterCount < step.characterCount) {
                    withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                        append(stringResource(Res.string.wizard_characters_step_characters_partially_set_up_text))
                    }
                    append(
                        Res.string.wizard_characters_partial_body.toUiText(
                            step.characterCount.toUiTextArgument(),
                            step.authenticatedCharacterCount.toUiTextArgument()
                        ).resolve(),
                    )
                } else {
                    withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                        append(stringResource(Res.string.wizard_characters_step_characters_set_up_text))
                    }
                    append(
                        stringResource(Res.string.wizard_characters_step_all_characters_are_authenticated_ready_text),
                    )
                }
            }
        }
        TypingText(
            text = text,
            style = RiftTheme.typography.headerPrimary,
            onFinishedTyping = { hasFinishedTyping = true },
        )
        AnimatedVisibility(
            visible = hasFinishedTyping,
            enter = fadeIn(),
            modifier = Modifier
                .padding(top = Spacing.large)
                .align(Alignment.CenterHorizontally),
        ) {
            val [type, buttonText] = if (step.authenticatedCharacterCount < step.characterCount) {
                ButtonType.Primary to stringResource(Res.string.wizard_characters_step_authenticate_characters_text)
            } else {
                ButtonType.Secondary to stringResource(Res.string.wizard_characters_step_check_characters_text)
            }
            RiftButton(
                text = buttonText,
                type = type,
                cornerCut = ButtonCornerCut.Both,
                onClick = onCharactersClick,
            )
        }
    }
}

@Composable
private fun ConfigurationPacksStep(
    step: WizardStep.ConfigurationPacks,
    onConfigurationPackChange: (ConfigurationPack?) -> Unit,
    onContinueClick: () -> Unit,
) {
    var hasFinishedTyping by remember { mutableStateOf(false) }
    val defaultPackName = stringResource(Res.string.settings_other_settings_default_text)
    val getPackName: (ConfigurationPack?) -> String = { it?.displayName ?: defaultPackName }
    StepContent(
        onContinueClick = onContinueClick,
    ) {
        val text = buildAnnotatedString {
            withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                append(stringResource(Res.string.wizard_configuration_packs_step_alliance_features_text))
            }
            if (step.pack != null) {
                append(
                    Res.string.wizard_pack_features_body.toUiText(getPackName(step.pack).toUiTextArgument()).resolve(),
                )
            } else {
                append(
                    stringResource(Res.string.wizard_configuration_packs_step_selected_default_settings_text),
                )
            }
        }
        TypingText(
            text = text,
            style = RiftTheme.typography.headerPrimary,
            onFinishedTyping = { hasFinishedTyping = true },
        )
        AnimatedVisibility(
            visible = hasFinishedTyping,
            enter = fadeIn(),
            modifier = Modifier
                .padding(top = Spacing.large)
                .align(Alignment.CenterHorizontally),
        ) {
            RiftDropdownWithLabel(
                label = stringResource(Res.string.wizard_configuration_packs_step_configuration_pack_label),
                items = listOf(null) + ConfigurationPack.entries,
                selectedItem = step.pack,
                onItemSelected = onConfigurationPackChange,
                getItemName = getPackName,
            )
        }
    }
}

@Composable
private fun IntelChannelsStep(
    step: WizardStep.IntelChannels,
    onSetIntelChannelsClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    var hasFinishedTyping by remember { mutableStateOf(false) }
    StepContent(
        onContinueClick = onContinueClick,
        isContinueVisible = hasFinishedTyping,
        isWarning = !step.hasChannels,
        warningButtonText = stringResource(Res.string.wizard_intel_channels_step_skip_text),
    ) {
        val text = buildAnnotatedString {
            if (step.hasChannels) {
                withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                    append(stringResource(Res.string.wizard_intel_channels_step_intel_channels_set_up_text))
                }
                append(
                    stringResource(Res.string.wizard_intel_channels_step_have_configured_intel_channels_text),
                )
            } else {
                withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                    append(stringResource(Res.string.wizard_intel_channels_step_intel_channels_text))
                }
                append(
                    stringResource(Res.string.wizard_intel_channels_step_if_are_alliance_intel_channels_text),
                )
            }
        }
        TypingText(
            text = text,
            style = RiftTheme.typography.headerPrimary,
            onFinishedTyping = { hasFinishedTyping = true },
        )
        AnimatedVisibility(
            visible = hasFinishedTyping,
            enter = fadeIn(),
            modifier = Modifier
                .padding(top = Spacing.large)
                .align(Alignment.CenterHorizontally),
        ) {
            val [type, buttonText] = if (!step.hasChannels) {
                ButtonType.Primary to stringResource(Res.string.wizard_intel_channels_step_add_intel_channels_text)
            } else {
                ButtonType.Secondary to stringResource(Res.string.wizard_intel_channels_step_change_intel_channels_text)
            }
            RiftButton(
                text = buttonText,
                type = type,
                cornerCut = ButtonCornerCut.Both,
                onClick = onSetIntelChannelsClick,
            )
        }
    }
}

@Composable
private fun FinishStep(
    onContinueClick: () -> Unit,
) {
    var hasFinishedTyping by remember { mutableStateOf(false) }
    StepContent(
        onContinueClick = onContinueClick,
        isContinueVisible = hasFinishedTyping,
    ) {
        val text = buildAnnotatedString {
            withStyle(SpanStyle(color = RiftTheme.colors.textHighlighted, fontSize = RiftTheme.typography.headlineHighlighted.fontSize)) {
                append(stringResource(Res.string.wizard_finish_step_all_done_text))
            }
            append(
                stringResource(Res.string.wizard_finish_step_rift_now_ready_enhance_capabilities_text),
            )
        }
        TypingText(
            text = text,
            style = RiftTheme.typography.headerPrimary,
            onFinishedTyping = { hasFinishedTyping = true },
        )
        AnimatedVisibility(
            visible = hasFinishedTyping,
            enter = fadeIn(),
            modifier = Modifier
                .padding(top = Spacing.large),
        ) {
            Image(
                painter = painterResource(Res.drawable.tray_tray_128),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )
        }
    }
}

@Composable
private fun StepContent(
    onContinueClick: () -> Unit,
    isContinueVisible: Boolean = true,
    isWarning: Boolean = false,
    warningButtonText: String = stringResource(Res.string.wizard_step_content_continue_anyway_text),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        content()
        Spacer(Modifier.weight(1f))
        AnimatedVisibility(
            visible = isContinueVisible,
            enter = fadeIn(),
            modifier = Modifier.align(Alignment.End),
        ) {
            val text = if (isWarning) warningButtonText else stringResource(Res.string.wizard_step_content_continue_text)
            val type = if (isWarning) ButtonType.Negative else ButtonType.Primary
            RiftButton(
                text = text,
                type = type,
                onClick = onContinueClick,
            )
        }
    }
}
