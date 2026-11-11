package dev.nohus.rift.alerts.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.alerts.create.CreateAlertViewModel.UiState
import dev.nohus.rift.alerts.create.FormAnswer.CharacterAnswer
import dev.nohus.rift.alerts.create.FormAnswer.ContactsLabelAnswer
import dev.nohus.rift.alerts.create.FormAnswer.FreeformTextAnswer
import dev.nohus.rift.alerts.create.FormAnswer.IntelChannelAnswer
import dev.nohus.rift.alerts.create.FormAnswer.JumpsRangeAnswer
import dev.nohus.rift.alerts.create.FormAnswer.MultipleChoiceAnswer
import dev.nohus.rift.alerts.create.FormAnswer.PlanetaryIndustryColoniesAnswer
import dev.nohus.rift.alerts.create.FormAnswer.SingleChoiceAnswer
import dev.nohus.rift.alerts.create.FormAnswer.SoundAnswer
import dev.nohus.rift.alerts.create.FormAnswer.SpecificCharactersAnswer
import dev.nohus.rift.alerts.create.FormAnswer.SystemAnswer
import dev.nohus.rift.characters.repositories.LocalCharactersRepository.LocalCharacter
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.PointerInteractionStateHolder
import dev.nohus.rift.compose.RequirementIcon
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckbox
import dev.nohus.rift.compose.RiftCheckboxWithLabel
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftDropdown
import dev.nohus.rift.compose.RiftFileChooserButton
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftRadioButton
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.contacts.ContactsRepository.Label
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.alerts_create_action_back
import dev.nohus.rift.generated.resources.alerts_create_action_continue
import dev.nohus.rift.generated.resources.alerts_create_action_finish
import dev.nohus.rift.generated.resources.alerts_create_answer_any_colony
import dev.nohus.rift.generated.resources.alerts_create_answer_character_count
import dev.nohus.rift.generated.resources.alerts_create_answer_colony_count
import dev.nohus.rift.generated.resources.alerts_create_answer_jumps_between
import dev.nohus.rift.generated.resources.alerts_create_answer_jumps_exactly
import dev.nohus.rift.generated.resources.alerts_create_answer_jumps_up_to
import dev.nohus.rift.generated.resources.alerts_create_answer_label_count
import dev.nohus.rift.generated.resources.alerts_create_answer_same_system
import dev.nohus.rift.generated.resources.alerts_create_answer_specific_colony
import dev.nohus.rift.generated.resources.alerts_create_character_names_invalid
import dev.nohus.rift.generated.resources.alerts_create_character_names_optional_placeholder
import dev.nohus.rift.generated.resources.alerts_create_character_names_placeholder
import dev.nohus.rift.generated.resources.alerts_create_character_names_valid
import dev.nohus.rift.generated.resources.alerts_create_characters_empty
import dev.nohus.rift.generated.resources.alerts_create_colonies_empty
import dev.nohus.rift.generated.resources.alerts_create_combat_target_optional_hint
import dev.nohus.rift.generated.resources.alerts_create_contact_labels_empty
import dev.nohus.rift.generated.resources.alerts_create_intel_channels_empty
import dev.nohus.rift.generated.resources.alerts_create_jump_count
import dev.nohus.rift.generated.resources.alerts_create_jump_same_system
import dev.nohus.rift.generated.resources.alerts_create_range_maximum_label
import dev.nohus.rift.generated.resources.alerts_create_range_minimum_label
import dev.nohus.rift.generated.resources.alerts_create_recent_targets_empty_hint
import dev.nohus.rift.generated.resources.alerts_create_recent_targets_hint
import dev.nohus.rift.generated.resources.alerts_create_recent_targets_label
import dev.nohus.rift.generated.resources.alerts_create_regex_description
import dev.nohus.rift.generated.resources.alerts_create_regex_label
import dev.nohus.rift.generated.resources.alerts_create_regex_match_label
import dev.nohus.rift.generated.resources.alerts_create_regex_match_prefix
import dev.nohus.rift.generated.resources.alerts_create_regex_test_does_not_match
import dev.nohus.rift.generated.resources.alerts_create_regex_test_matches
import dev.nohus.rift.generated.resources.alerts_create_regex_test_placeholder
import dev.nohus.rift.generated.resources.alerts_create_sound_file_label
import dev.nohus.rift.generated.resources.alerts_create_sound_file_type
import dev.nohus.rift.generated.resources.alerts_create_sound_path_invalid
import dev.nohus.rift.generated.resources.alerts_create_sound_path_valid
import dev.nohus.rift.generated.resources.alerts_create_sound_preview_hint
import dev.nohus.rift.generated.resources.alerts_create_sound_tab_builtin
import dev.nohus.rift.generated.resources.alerts_create_sound_tab_custom
import dev.nohus.rift.generated.resources.alerts_create_system_invalid
import dev.nohus.rift.generated.resources.alerts_create_system_optional_placeholder
import dev.nohus.rift.generated.resources.alerts_create_system_placeholder
import dev.nohus.rift.generated.resources.alerts_create_system_valid
import dev.nohus.rift.generated.resources.alerts_create_title_edit_action
import dev.nohus.rift.generated.resources.alerts_create_title_new
import dev.nohus.rift.generated.resources.play
import dev.nohus.rift.generated.resources.window_loudspeaker_icon
import dev.nohus.rift.get
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.planetaryindustry.PlanetaryIndustryRepository.ColonyItem
import dev.nohus.rift.utils.sound.Sound
import dev.nohus.rift.utils.sound.SoundPlayer
import dev.nohus.rift.utils.sound.SoundPlayer.SoundSource
import dev.nohus.rift.utils.toRegexOrNull
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.nameWithoutExtension
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WindowScope.CreateAlertDialog(
    inputModel: CreateAlertInputModel,
    parentWindowState: WindowManager.RiftWindowState,
    onDismiss: () -> Unit,
) {
    val viewModel: CreateAlertViewModel = viewModel(inputModel)
    val state by viewModel.state.collectAsState()

    if (state.dismissEvent.get()) onDismiss()

    val title = when (inputModel) {
        CreateAlertInputModel.New -> stringResource(Res.string.alerts_create_title_new)
        is CreateAlertInputModel.EditAction -> stringResource(Res.string.alerts_create_title_edit_action)
    }
    RiftDialog(
        title = title,
        icon = Res.drawable.window_loudspeaker_icon,
        parentState = parentWindowState,
        state = rememberWindowState(width = 400.dp, height = Dp.Unspecified),
        onCloseClick = onDismiss,
    ) {
        CreateAlertDialogContent(
            state = state,
            onFormPendingAnswer = viewModel::onFormPendingAnswer,
            onBackClick = viewModel::onBackClick,
            onContinueClick = viewModel::onContinueClick,
        )
    }
}

@Composable
private fun CreateAlertDialogContent(
    state: UiState,
    onFormPendingAnswer: (FormAnswer) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    Column {
        if (state.formAnswers.isNotEmpty()) {
            Column {
                for ((question, answer) in state.formAnswers) {
                    val answerText = (question to answer).toAnswerString(state.characters)
                    if (answerText != null) {
                        val text = buildAnnotatedString {
                            append(question.title.resolve())
                            append(" ")
                            withStyle(SpanStyle(color = RiftTheme.colors.textPrimary)) {
                                append(answerText)
                            }
                        }
                        Text(
                            text = text,
                            style = RiftTheme.typography.bodySecondary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = Spacing.small),
                        )
                    }
                }
            }
        }
        Column {
            AnimatedContent(state.formQuestion) { formQuestion ->
                if (formQuestion != null) {
                    val highlightAnimationState = remember { AnimationState(0f) }
                    LaunchedEffect(state.highlightQuestionEvent) {
                        if (state.highlightQuestionEvent.get()) {
                            highlightAnimationState.animateTo(1f, animationSpec = tween(300))
                            highlightAnimationState.animateTo(0f, animationSpec = tween(300))
                        }
                    }
                    Box(
                        modifier = Modifier.alpha(1f - (0.5f * highlightAnimationState.value)),
                    ) {
                        FormQuestion(
                            formQuestion = formQuestion,
                            isPendingAnswerValid = state.isPendingAnswerValid,
                            pendingAnswerInvalidReason = state.pendingAnswerInvalidReason,
                            characters = state.characters,
                            intelChannels = state.intelChannels,
                            sounds = state.sounds,
                            recentTargets = state.recentTargets,
                            colonies = state.colonies,
                            labels = state.labels,
                            onFormAnswer = onFormPendingAnswer,
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = Modifier.padding(top = Spacing.medium),
            ) {
                RiftButton(
                    text = stringResource(Res.string.alerts_create_action_back),
                    cornerCut = ButtonCornerCut.BottomLeft,
                    type = ButtonType.Secondary,
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                )
                val label = if (state.formQuestion != null) stringResource(Res.string.alerts_create_action_continue) else stringResource(Res.string.alerts_create_action_finish)
                RiftButton(
                    text = label,
                    onClick = onContinueClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FormQuestion(
    formQuestion: FormQuestion,
    isPendingAnswerValid: Boolean?,
    pendingAnswerInvalidReason: UiText?,
    characters: List<LocalCharacter>,
    intelChannels: List<String>,
    sounds: List<Sound>,
    recentTargets: Set<String>,
    colonies: List<ColonyItem>,
    labels: List<Label>,
    onFormAnswer: (FormAnswer) -> Unit,
) {
    Column {
        Text(
            text = formQuestion.title.resolve(),
            style = RiftTheme.typography.headerSecondary,
            modifier = Modifier
                .padding(vertical = Spacing.medium),
        )
        when (formQuestion) {
            is FormQuestion.SingleChoiceQuestion -> {
                ScrollbarColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                ) {
                    var selected: FormChoiceItem? by remember { mutableStateOf(null) }
                    for (item in formQuestion.items) {
                        ListSelectorRow(
                            text = item.text.resolve(),
                            description = item.description?.resolve(),
                            isMultipleChoice = false,
                            isSelected = item == selected,
                            onSelect = {
                                selected = item
                                onFormAnswer(SingleChoiceAnswer(item.id))
                            },
                        )
                    }
                }
            }

            is FormQuestion.MultipleChoiceQuestion -> {
                ScrollbarColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                ) {
                    var selected: List<FormChoiceItem> by remember { mutableStateOf(emptyList()) }
                    for (item in formQuestion.items) {
                        ListSelectorRow(
                            text = item.text.resolve(),
                            description = item.description?.resolve(),
                            isMultipleChoice = true,
                            isSelected = item in selected,
                            onSelect = {
                                if (item in selected) selected -= item else selected += item
                                onFormAnswer(MultipleChoiceAnswer(selected))
                            },
                        )
                    }
                }
            }

            is FormQuestion.SystemQuestion -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .heightIn(min = 36.dp) // For requirement icon
                        .fillMaxWidth(),
                ) {
                    var system: String by remember { mutableStateOf("") }
                    val placeholder = if (formQuestion.allowEmpty) {
                        stringResource(Res.string.alerts_create_system_optional_placeholder)
                    } else {
                        stringResource(Res.string.alerts_create_system_placeholder)
                    }
                    if (formQuestion.allowEmpty) {
                        LaunchedEffect(Unit) {
                            onFormAnswer(SystemAnswer(""))
                        }
                    }
                    RiftTextField(
                        text = system,
                        placeholder = placeholder,
                        onTextChanged = {
                            system = it
                            onFormAnswer(SystemAnswer(it))
                        },
                        modifier = Modifier.weight(1f),
                    )
                    if (isPendingAnswerValid != null) {
                        RequirementIcon(
                            isFulfilled = isPendingAnswerValid,
                            fulfilledTooltip = stringResource(Res.string.alerts_create_system_valid),
                            notFulfilledTooltip = stringResource(Res.string.alerts_create_system_invalid),
                            modifier = Modifier.padding(start = Spacing.medium),
                        )
                    }
                }
            }

            is FormQuestion.JumpsRangeQuestion -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    var min: Int by remember { mutableStateOf(0) }
                    var max: Int by remember { mutableStateOf(0) }

                    val jumpNames = List(16) { jumps ->
                        if (jumps == 0) stringResource(Res.string.alerts_create_jump_same_system) else pluralStringResource(Res.plurals.alerts_create_jump_count, jumps, jumps)
                    }
                    fun getItemName(jumps: Int) = jumpNames[jumps]

                    LaunchedEffect(formQuestion) {
                        onFormAnswer(JumpsRangeAnswer(minJumps = min, maxJumps = max))
                    }
                    Text(
                        text = stringResource(Res.string.alerts_create_range_minimum_label),
                        style = RiftTheme.typography.headerPrimary,
                    )
                    val maxJumps = 16 // 0 - 15
                    RiftDropdown(
                        items = List(maxJumps) { it },
                        selectedItem = min,
                        onItemSelected = {
                            min = it
                            if (max < min) max = min
                            onFormAnswer(JumpsRangeAnswer(minJumps = min, maxJumps = max))
                        },
                        getItemName = { getItemName(it) },
                        maxItems = 5,
                    )
                    Text(
                        text = stringResource(Res.string.alerts_create_range_maximum_label),
                        style = RiftTheme.typography.headerPrimary,
                    )
                    RiftDropdown(
                        items = List(maxJumps - min) { min + it },
                        selectedItem = max,
                        onItemSelected = {
                            max = it
                            onFormAnswer(JumpsRangeAnswer(minJumps = min, maxJumps = max))
                        },
                        getItemName = { getItemName(it) },
                        maxItems = 5,
                    )
                }
            }

            is FormQuestion.OwnedCharacterQuestion -> {
                if (characters.isNotEmpty()) {
                    var selected: Int by remember { mutableStateOf(characters.first().characterId) }
                    LaunchedEffect(formQuestion) {
                        onFormAnswer(CharacterAnswer(selected))
                    }
                    RiftDropdown(
                        items = characters.map { it.characterId },
                        selectedItem = selected,
                        onItemSelected = { characterId ->
                            selected = characterId
                            onFormAnswer(CharacterAnswer(characterId))
                        },
                        getItemName = { characterId ->
                            characters.firstOrNull { it.characterId == characterId }?.info?.name ?: "$characterId"
                        },
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.alerts_create_characters_empty),
                        style = RiftTheme.typography.headerPrimary,
                    )
                }
            }

            is FormQuestion.IntelChannelQuestion -> {
                if (intelChannels.isNotEmpty()) {
                    var selected: String by remember { mutableStateOf(intelChannels.first()) }
                    LaunchedEffect(formQuestion) {
                        onFormAnswer(IntelChannelAnswer(selected))
                    }
                    RiftDropdown(
                        items = intelChannels,
                        selectedItem = selected,
                        onItemSelected = {
                            selected = it
                            onFormAnswer(IntelChannelAnswer(it))
                        },
                        getItemName = { it },
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.alerts_create_intel_channels_empty),
                        style = RiftTheme.typography.headerPrimary,
                    )
                }
            }

            is FormQuestion.SoundQuestion -> {
                val soundPlayer: SoundPlayer = remember { koin.get() }
                var selected: Sound? by remember { mutableStateOf(null) }
                var selectedTabIndex by remember { mutableStateOf(0) }
                RiftTabBar(
                    tabs = listOf(
                        Tab(0, Res.string.alerts_create_sound_tab_builtin.toUiText(), isCloseable = false),
                        Tab(1, Res.string.alerts_create_sound_tab_custom.toUiText(), isCloseable = false),
                    ),
                    selectedTab = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    onTabClosed = {},
                )
                ScrollbarColumn(
                    modifier = Modifier.height(180.dp),
                ) {
                    if (selectedTabIndex == 0) {
                        for (sound in sounds) {
                            ListSelectorRow(
                                text = sound.name.resolve(),
                                description = null,
                                isMultipleChoice = false,
                                isSelected = sound == selected,
                                onSelect = {
                                    selected = sound
                                    onFormAnswer(SoundAnswer.BuiltInSound(sound))
                                },
                                rightContent = {
                                    RiftImageButton(
                                        Res.drawable.play,
                                        size = 16.dp,
                                        onClick = { soundPlayer.play(sound.resource, SoundSource.Alert) },
                                        modifier = Modifier.padding(horizontal = Spacing.small),
                                    )
                                },
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(Res.string.alerts_create_sound_file_label),
                            style = RiftTheme.typography.bodyPrimary,
                            modifier = Modifier.padding(top = Spacing.medium),
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier
                                .padding(top = Spacing.medium)
                                .heightIn(min = 36.dp),
                        ) {
                            var text by remember { mutableStateOf("") }
                            RiftTextField(
                                text = text,
                                onTextChanged = {
                                    text = it
                                    onFormAnswer(SoundAnswer.CustomSound(it))
                                },
                                modifier = Modifier.weight(1f),
                            )
                            AnimatedVisibility(isPendingAnswerValid != null) {
                                RequirementIcon(
                                    isFulfilled = isPendingAnswerValid ?: false,
                                    fulfilledTooltip = stringResource(Res.string.alerts_create_sound_path_valid),
                                    notFulfilledTooltip = pendingAnswerInvalidReason?.resolve() ?: stringResource(Res.string.alerts_create_sound_path_invalid),
                                )
                            }
                            RiftFileChooserButton(
                                typesDescription = stringResource(Res.string.alerts_create_sound_file_type),
                                extensions = listOf("wav"),
                                onFileChosen = {
                                    text = it.absolutePathString()
                                    onFormAnswer(SoundAnswer.CustomSound(it.absolutePathString()))
                                },
                            )
                            RiftImageButton(
                                Res.drawable.play,
                                size = 16.dp,
                                onClick = { soundPlayer.playFile(text, SoundSource.Alert) },
                                modifier = Modifier.padding(horizontal = Spacing.small),
                            )
                        }
                        Text(
                            text = stringResource(Res.string.alerts_create_sound_preview_hint),
                            style = RiftTheme.typography.bodyPrimary,
                            modifier = Modifier.padding(vertical = Spacing.medium),
                        )
                    }
                }
            }

            is FormQuestion.SpecificCharactersQuestion -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .heightIn(min = 36.dp) // For requirement icon
                        .fillMaxWidth(),
                ) {
                    var text: String by remember { mutableStateOf("") }
                    val placeholder = if (formQuestion.allowEmpty) {
                        stringResource(Res.string.alerts_create_character_names_optional_placeholder)
                    } else {
                        stringResource(Res.string.alerts_create_character_names_placeholder)
                    }
                    if (formQuestion.allowEmpty) {
                        LaunchedEffect(Unit) {
                            onFormAnswer(SpecificCharactersAnswer(emptyList()))
                        }
                    }
                    RiftTextField(
                        text = text,
                        placeholder = placeholder,
                        onTextChanged = {
                            text = it
                            val splitCharacters = text
                                .split(",")
                                .map { it.trim() }
                                .filterNot { it.isBlank() }
                            onFormAnswer(SpecificCharactersAnswer(splitCharacters))
                        },
                        modifier = Modifier.weight(1f),
                    )
                    if (isPendingAnswerValid != null) {
                        RequirementIcon(
                            isFulfilled = isPendingAnswerValid,
                            fulfilledTooltip = stringResource(Res.string.alerts_create_character_names_valid),
                            notFulfilledTooltip = pendingAnswerInvalidReason?.resolve() ?: stringResource(Res.string.alerts_create_character_names_invalid),
                            modifier = Modifier.padding(start = Spacing.medium),
                        )
                    }
                }
            }

            is FormQuestion.CombatTargetQuestion -> {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        var text: String by remember { mutableStateOf("") }
                        LaunchedEffect(Unit) {
                            onFormAnswer(FreeformTextAnswer(""))
                        }
                        RiftTextField(
                            text = text,
                            placeholder = formQuestion.placeholder.resolve(),
                            onTextChanged = {
                                text = it
                                onFormAnswer(FreeformTextAnswer(it.trim()))
                            },
                            modifier = Modifier.weight(1f),
                        )
                        if (recentTargets.isNotEmpty()) {
                            Spacer(Modifier.width(Spacing.medium))
                            RiftDropdown(
                                items = recentTargets.toList(),
                                selectedItem = stringResource(Res.string.alerts_create_recent_targets_label),
                                onItemSelected = {
                                    text = it
                                    onFormAnswer(FreeformTextAnswer(it.trim()))
                                },
                                getItemName = {
                                    if (it.length > 20) it.take(20) + "…" else it
                                },
                            )
                        }
                    }
                    val helpText = if (recentTargets.isNotEmpty()) {
                        stringResource(Res.string.alerts_create_recent_targets_hint)
                    } else {
                        stringResource(Res.string.alerts_create_recent_targets_empty_hint)
                    }
                    Text(
                        text = helpText,
                        style = RiftTheme.typography.bodyPrimary,
                        modifier = Modifier.padding(top = Spacing.medium),
                    )
                }
            }

            is FormQuestion.PlanetaryIndustryColoniesQuestion -> {
                AnimatedContent(colonies.isNotEmpty()) { isNotEmpty ->
                    Column {
                        if (isNotEmpty) {
                            Text(
                                text = stringResource(Res.string.alerts_create_combat_target_optional_hint),
                                style = RiftTheme.typography.bodySecondary,
                                modifier = Modifier.padding(bottom = Spacing.small),
                            )
                            LaunchedEffect(Unit) {
                                onFormAnswer(PlanetaryIndustryColoniesAnswer(emptyList()))
                            }
                            ScrollbarColumn(
                                modifier = Modifier.heightIn(max = 200.dp),
                            ) {
                                var selected: List<String> by remember { mutableStateOf(emptyList()) }
                                for (item in colonies) {
                                    val id = item.colony.id
                                    ListSelectorRow(
                                        text = item.colony.planet.name,
                                        description = item.characterName,
                                        isMultipleChoice = true,
                                        isSelected = id in selected,
                                        onSelect = {
                                            if (item.colony.id in selected) selected -= id else selected += id
                                            onFormAnswer(PlanetaryIndustryColoniesAnswer(selected))
                                        },
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(Res.string.alerts_create_colonies_empty),
                                style = RiftTheme.typography.headerPrimary,
                            )
                        }
                    }
                }
            }

            is FormQuestion.FreeformTextQuestion -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    var isRegex by remember { mutableStateOf(false) }
                    var text: String by remember { mutableStateOf("") }
                    var testText: String by remember { mutableStateOf("") }

                    val regex = if (isRegex) text.toRegexOrNull(RegexOption.IGNORE_CASE) else null
                    val match = regex?.find(testText)

                    LaunchedEffect(text, regex) {
                        val answer = if (regex != null) text else text.trim()
                        onFormAnswer(FreeformTextAnswer(answer, regex != null))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RiftTextField(
                            text = text,
                            placeholder = formQuestion.placeholder.resolve(),
                            onTextChanged = { text = it },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (formQuestion.isRegexAllowed) {
                        RiftCheckboxWithLabel(
                            label = stringResource(Res.string.alerts_create_regex_label),
                            tooltip = stringResource(Res.string.alerts_create_regex_description),
                            isChecked = isRegex,
                            onCheckedChange = { isRegex = it },
                        )
                        AnimatedVisibility(isRegex) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                RiftTextField(
                                    text = testText,
                                    placeholder = stringResource(Res.string.alerts_create_regex_test_placeholder),
                                    onTextChanged = {
                                        testText = it
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                                AnimatedContent(match, contentKey = { it != null }) { match ->
                                    if (match != null) {
                                        Text(
                                            text = buildAnnotatedString {
                                                val trimmed = match.value.trim()
                                                if (trimmed.isNotBlank()) {
                                                    append(stringResource(Res.string.alerts_create_regex_match_prefix))
                                                    withColor(RiftTheme.colors.textSpecialHighlighted) {
                                                        append(trimmed)
                                                    }
                                                } else {
                                                    append(stringResource(Res.string.alerts_create_regex_match_label))
                                                }
                                            },
                                            maxLines = 1,
                                            overflow = TextOverflow.MiddleEllipsis,
                                            style = RiftTheme.typography.bodyPrimary,
                                            modifier = Modifier
                                                .padding(start = Spacing.medium)
                                                .widthIn(max = 150.dp),
                                        )
                                    }
                                }
                                RequirementIcon(
                                    isFulfilled = match != null,
                                    fulfilledTooltip = stringResource(Res.string.alerts_create_regex_test_matches),
                                    notFulfilledTooltip = stringResource(Res.string.alerts_create_regex_test_does_not_match),
                                    modifier = Modifier.padding(start = Spacing.small),
                                )
                            }
                        }
                    }
                }
            }

            is FormQuestion.ContactsLabelQuestion -> {
                if (labels.isNotEmpty()) {
                    ScrollbarColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                    ) {
                        var selected: List<Label> by remember { mutableStateOf(emptyList()) }
                        for (label in labels) {
                            ListSelectorRow(
                                text = label.name,
                                description = label.owner.name,
                                isMultipleChoice = true,
                                isSelected = label in selected,
                                onSelect = {
                                    if (label in selected) selected -= label else selected += label
                                    onFormAnswer(ContactsLabelAnswer(selected))
                                },
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.alerts_create_contact_labels_empty),
                        style = RiftTheme.typography.headerPrimary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListSelectorRow(
    text: String,
    description: String?,
    isMultipleChoice: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    rightContent: @Composable () -> Unit = {},
) {
    val pointerInteractionStateHolder = remember { PointerInteractionStateHolder() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier
            .fillMaxWidth()
            .hoverBackground(pointerInteractionStateHolder = pointerInteractionStateHolder)
            .onClick { onSelect() }
            .padding(Spacing.medium),
    ) {
        if (isMultipleChoice) {
            RiftCheckbox(
                isChecked = isSelected,
                onCheckedChange = { onSelect() },
                pointerInteractionStateHolder = pointerInteractionStateHolder,
            )
        } else {
            RiftRadioButton(
                isChecked = isSelected,
                onChecked = onSelect,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
        ) {
            val style = if (isSelected) {
                RiftTheme.typography.headerPrimary.copy(color = RiftTheme.colors.primary)
            } else {
                RiftTheme.typography.headerPrimary
            }
            Text(
                text = text,
                style = style,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = RiftTheme.typography.bodySecondary,
                )
            }
        }
        rightContent()
    }
}

@Composable
private fun Pair<FormQuestion, FormAnswer>.toAnswerString(
    characters: List<LocalCharacter>,
): String? {
    val question = first
    val answer = second
    return when (question) {
        is FormQuestion.SingleChoiceQuestion -> {
            val id = (answer as SingleChoiceAnswer).id
            question.items.first { it.id == id }.text.resolve()
        }

        is FormQuestion.MultipleChoiceQuestion -> {
            val ids = (answer as MultipleChoiceAnswer).ids
            question.items.filter { it.id in ids }.map { it.text.resolve() }.joinToString()
        }

        is FormQuestion.SystemQuestion -> {
            (answer as SystemAnswer).system.takeIf { it.isNotEmpty() }
        }

        is FormQuestion.JumpsRangeQuestion -> {
            val [min, max] = (answer as JumpsRangeAnswer).let { it.minJumps to it.maxJumps }
            if (min == 0 && max == 0) {
                stringResource(Res.string.alerts_create_answer_same_system)
            } else if (min == 0) {
                pluralStringResource(Res.plurals.alerts_create_answer_jumps_up_to, max, max)
            } else if (min == max) {
                pluralStringResource(Res.plurals.alerts_create_answer_jumps_exactly, max, max)
            } else {
                pluralStringResource(Res.plurals.alerts_create_answer_jumps_between, max, min, max)
            }
        }

        is FormQuestion.OwnedCharacterQuestion -> {
            val characterId = (answer as CharacterAnswer).characterId
            characters.firstOrNull { it.characterId == characterId }?.info?.name ?: "$characterId"
        }

        is FormQuestion.IntelChannelQuestion -> {
            (answer as IntelChannelAnswer).channel
        }

        is FormQuestion.SoundQuestion -> {
            when (val soundAnswer = answer as SoundAnswer) {
                is SoundAnswer.BuiltInSound -> soundAnswer.item.name.resolve()
                is SoundAnswer.CustomSound -> Path.of(soundAnswer.path).nameWithoutExtension
            }
        }

        is FormQuestion.SpecificCharactersQuestion -> {
            val specificCharacters = (answer as SpecificCharactersAnswer).characters
            when {
                specificCharacters.isEmpty() -> null
                specificCharacters.size == 1 -> specificCharacters.single()
                else -> pluralStringResource(Res.plurals.alerts_create_answer_character_count, specificCharacters.size, specificCharacters.size)
            }
        }

        is FormQuestion.CombatTargetQuestion -> {
            (answer as FreeformTextAnswer).text.takeIf { it.isNotBlank() }
        }

        is FormQuestion.PlanetaryIndustryColoniesQuestion -> {
            val colonies = (answer as PlanetaryIndustryColoniesAnswer).colonies
            when {
                colonies.isEmpty() -> stringResource(Res.string.alerts_create_answer_any_colony)
                colonies.size == 1 -> stringResource(Res.string.alerts_create_answer_specific_colony)
                else -> pluralStringResource(Res.plurals.alerts_create_answer_colony_count, colonies.size, colonies.size)
            }
        }

        is FormQuestion.FreeformTextQuestion -> {
            (answer as FreeformTextAnswer).text.takeIf { it.isNotBlank() }
        }

        is FormQuestion.ContactsLabelQuestion -> {
            val labels = (answer as ContactsLabelAnswer).labels
            if (labels.size == 1) {
                labels.single().name
            } else {
                pluralStringResource(Res.plurals.alerts_create_answer_label_count, labels.size, labels.size)
            }
        }
    }
}
