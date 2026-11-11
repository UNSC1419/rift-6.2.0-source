package dev.nohus.rift.characters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.characters.CharactersViewModel.AuthenticationStatus
import dev.nohus.rift.characters.CharactersViewModel.CharacterItem
import dev.nohus.rift.characters.CharactersViewModel.UiState
import dev.nohus.rift.clones.Clone
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.CharacterTooltip
import dev.nohus.rift.compose.ClickableLocation
import dev.nohus.rift.compose.ClickableShip
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.PointerInteractionStateHolder
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftIconButton
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftIndicatorDot
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarLazyColumn
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.rememberPointerInteractionStateHolder
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitParallax
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.buttoniconminus
import dev.nohus.rift.generated.resources.buttoniconplus
import dev.nohus.rift.generated.resources.characters_authentication_status_icon_missing_esi_scopes_text
import dev.nohus.rift.generated.resources.characters_authentication_status_icon_not_authenticated_esi_click_log_text
import dev.nohus.rift.generated.resources.characters_authentication_status_icon_some_features_won_t_work_text
import dev.nohus.rift.generated.resources.characters_character_id
import dev.nohus.rift.generated.resources.characters_character_row_could_not_load_text
import dev.nohus.rift.generated.resources.characters_character_row_disable_character_text
import dev.nohus.rift.generated.resources.characters_characters_list_disabled_characters_text
import dev.nohus.rift.generated.resources.characters_characters_list_disabled_characters_will_not_used_text
import dev.nohus.rift.generated.resources.characters_characters_list_no_disabled_characters_text
import dev.nohus.rift.generated.resources.characters_characters_window_characters_title
import dev.nohus.rift.generated.resources.characters_characters_window_content_no_characters_found_make_sure_text
import dev.nohus.rift.generated.resources.characters_characters_window_show_clones_text
import dev.nohus.rift.generated.resources.characters_clone_active_clone_text
import dev.nohus.rift.generated.resources.characters_clone_no_implants_text
import dev.nohus.rift.generated.resources.characters_delete_character_dialog_content_delete_text
import dev.nohus.rift.generated.resources.characters_delete_character_dialog_content_settings_files_character_will_deleted_text
import dev.nohus.rift.generated.resources.characters_delete_title
import dev.nohus.rift.generated.resources.characters_docked_station
import dev.nohus.rift.generated.resources.characters_docked_structure
import dev.nohus.rift.generated.resources.characters_hidden_character_row_delete_character_text
import dev.nohus.rift.generated.resources.characters_hidden_character_row_enable_character_text
import dev.nohus.rift.generated.resources.characters_location_text_space_text
import dev.nohus.rift.generated.resources.characters_ship_docked_station
import dev.nohus.rift.generated.resources.characters_ship_docked_structure
import dev.nohus.rift.generated.resources.characters_ship_in_space
import dev.nohus.rift.generated.resources.characters_structure
import dev.nohus.rift.generated.resources.characters_top_row_copy_eve_settings_window_positions_text
import dev.nohus.rift.generated.resources.characters_top_row_enable_or_disable_characters_don_text
import dev.nohus.rift.generated.resources.characters_top_row_manage_eve_settings_text
import dev.nohus.rift.generated.resources.clone
import dev.nohus.rift.generated.resources.common_action_cancel
import dev.nohus.rift.generated.resources.common_action_done
import dev.nohus.rift.generated.resources.delete
import dev.nohus.rift.generated.resources.editplanicon
import dev.nohus.rift.generated.resources.sso
import dev.nohus.rift.generated.resources.sso_dark
import dev.nohus.rift.generated.resources.status_warning_orange
import dev.nohus.rift.generated.resources.status_warning_red
import dev.nohus.rift.generated.resources.window_characters
import dev.nohus.rift.generated.resources.window_delete_character
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.location.CharacterLocationRepository.Location
import dev.nohus.rift.location.LocationRepository.Station
import dev.nohus.rift.location.LocationRepository.Structure
import dev.nohus.rift.network.esi.models.CharacterIdShip
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.sso.SsoAuthority
import dev.nohus.rift.sso.SsoDialog
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.utils.formatIskCompact
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.Duration
import java.time.Instant

@Composable
fun CharactersWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: CharactersViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.characters_characters_window_characters_title),
        icon = Res.drawable.window_characters,
        state = windowState,
        tuneContextMenuItems = listOf(
            ContextMenuItem.CheckboxItem(
                text = stringResource(Res.string.characters_characters_window_show_clones_text),
                isSelected = state.isShowingClones,
                onClick = { viewModel.onIsShowingCharactersClonesChange(!state.isShowingClones) },
            ),
        ),
        onCloseClick = onCloseRequest,
    ) {
        CharactersWindowContent(
            state = state,
            onSsoClick = viewModel::onSsoClick,
            onCopySettingsClick = viewModel::onCopySettingsClick,
            onChooseDisabledClick = viewModel::onChooseDisabledClick,
            onDisableCharacterClick = viewModel::onDisableCharacterClick,
            onEnableCharacterClick = viewModel::onEnableCharacterClick,
            onDeleteCharacterClick = viewModel::onDeleteCharacterClick,
        )

        if (state.isSsoDialogOpen) {
            SsoDialog(
                inputModel = SsoAuthority.Eve,
                parentWindowState = windowState,
                onDismiss = viewModel::onCloseSso,
            )
        } else if (state.deletingCharacter != null) {
            val name = state.deletingCharacter?.info?.name ?: stringResource(Res.string.characters_character_id, state.deletingCharacter?.characterId.toString())
            RiftDialog(
                title = stringResource(Res.string.characters_delete_title, name),
                icon = Res.drawable.window_delete_character,
                parentState = windowState,
                state = rememberWindowState(width = 380.dp, height = Dp.Unspecified),
                onCloseClick = viewModel::onDeleteCharacterCancel,
            ) {
                DeleteCharacterDialogContent(viewModel)
            }
        }
    }
}

@Composable
private fun DeleteCharacterDialogContent(viewModel: CharactersViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.characters_delete_character_dialog_content_settings_files_character_will_deleted_text),
            style = RiftTheme.typography.bodyPrimary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            RiftButton(
                text = stringResource(Res.string.common_action_cancel),
                cornerCut = ButtonCornerCut.BottomLeft,
                type = ButtonType.Secondary,
                onClick = viewModel::onDeleteCharacterCancel,
                modifier = Modifier.weight(1f),
            )
            RiftButton(
                text = stringResource(Res.string.characters_delete_character_dialog_content_delete_text),
                type = ButtonType.Negative,
                onClick = viewModel::onDeleteCharacterConfirm,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CharactersWindowContent(
    state: UiState,
    onSsoClick: () -> Unit,
    onCopySettingsClick: () -> Unit,
    onChooseDisabledClick: () -> Unit,
    onDisableCharacterClick: (characterId: Int) -> Unit,
    onEnableCharacterClick: (characterId: Int) -> Unit,
    onDeleteCharacterClick: (characterId: Int) -> Unit,
) {
    Column {
        TopRow(
            state = state,
            onSsoClick = onSsoClick,
            onCopySettingsClick = onCopySettingsClick,
            onChooseDisabledClick = onChooseDisabledClick,
        )
        if (state.characters.isNotEmpty()) {
            CharactersList(
                state = state,
                onDisableCharacterClick = onDisableCharacterClick,
                onChooseDisabledClick = onChooseDisabledClick,
                onEnableCharacterClick = onEnableCharacterClick,
                onDeleteCharacterClick = onDeleteCharacterClick,
            )
        } else {
            Text(
                text = stringResource(Res.string.characters_characters_window_content_no_characters_found_make_sure_text),
                style = RiftTheme.typography.headerPrimary,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.medium),
            )
        }
    }
}

@Composable
private fun ColumnScope.CharactersList(
    state: UiState,
    onDisableCharacterClick: (characterId: Int) -> Unit,
    onChooseDisabledClick: () -> Unit,
    onEnableCharacterClick: (characterId: Int) -> Unit,
    onDeleteCharacterClick: (characterId: Int) -> Unit,
) {
    val now = remember(state.characters) { Instant.now() }
    ScrollbarLazyColumn(
        modifier = Modifier.weight(1f),
    ) {
        itemsIndexed(
            items = state.characters.filterNot { it.isHidden },
            key = { _, it -> it.characterId },
        ) { index, character ->
            Box(modifier = Modifier.animateItem()) {
                CharacterRow(
                    character = character,
                    enterTimestamp = now + (Duration.ofMillis(500L + index * 150)),
                    isOnline = character.characterId in state.onlineCharacters,
                    location = state.locations[character.characterId],
                    isChoosingDisabledCharacters = state.isChoosingDisabledCharacters,
                    isShowingClones = state.isShowingClones,
                    onDisableCharacterClick = onDisableCharacterClick,
                )
            }
        }
        item(key = "disabled characters") {
            Box(modifier = Modifier.animateItem()) {
                RiftTooltipArea(
                    text = stringResource(Res.string.characters_characters_list_disabled_characters_will_not_used_text),
                    modifier = Modifier.padding(vertical = Spacing.medium),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    ) {
                        if (state.characters.any { it.isHidden }) {
                            Text(
                                text = stringResource(Res.string.characters_characters_list_disabled_characters_text),
                                style = RiftTheme.typography.headerPrimary,
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.characters_characters_list_no_disabled_characters_text),
                                style = RiftTheme.typography.headerPrimary,
                            )
                        }
                        RiftImageButton(
                            resource = Res.drawable.editplanicon,
                            size = 20.dp,
                            onClick = onChooseDisabledClick,
                        )
                    }
                }
            }
        }
        items(state.characters.filter { it.isHidden }, key = { it.characterId }) { character ->
            Box(modifier = Modifier.animateItem()) {
                HiddenCharacterRow(
                    character = character,
                    isChoosingDisabledCharacters = state.isChoosingDisabledCharacters,
                    onEnableCharacterClick = onEnableCharacterClick,
                    onDeleteCharacterClick = onDeleteCharacterClick,
                )
            }
        }
    }
}

@Composable
private fun TopRow(
    state: UiState,
    onSsoClick: () -> Unit,
    onCopySettingsClick: () -> Unit,
    onChooseDisabledClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(bottom = Spacing.medium).fillMaxWidth(),
    ) {
        AnimatedContent(state.isChoosingDisabledCharacters) { isChoosingDisabledCharacters ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                if (isChoosingDisabledCharacters) {
                    Text(
                        text = stringResource(Res.string.characters_top_row_enable_or_disable_characters_don_text),
                        style = RiftTheme.typography.bodyPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    RiftButton(
                        text = stringResource(Res.string.common_action_done),
                        type = ButtonType.Primary,
                        onClick = onChooseDisabledClick,
                    )
                } else {
                    SsoButton(onClick = onSsoClick)
                    if (state.characters.isNotEmpty()) {
                        RiftTooltipArea(
                            text = stringResource(Res.string.characters_top_row_copy_eve_settings_window_positions_text),
                        ) {
                            RiftButton(
                                text = stringResource(Res.string.characters_top_row_manage_eve_settings_text),
                                onClick = onCopySettingsClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SsoButton(
    onClick: () -> Unit,
) {
    val pointerState = remember { PointerInteractionStateHolder() }
    Box(
        modifier = Modifier
            .pointerInteraction(pointerState)
            .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
            .padding(end = Spacing.small)
            .clickable { onClick() },
    ) {
        AnimatedContent(
            targetState = pointerState.isHovered,
            transitionSpec = {
                fadeIn(animationSpec = tween(300))
                    .togetherWith(fadeOut(animationSpec = tween(300)))
            },
        ) { isHovered ->
            val image = if (isHovered) Res.drawable.sso else Res.drawable.sso_dark
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(30.dp),
            )
        }
    }
}

@Composable
private fun CharacterRow(
    character: CharacterItem,
    enterTimestamp: Instant,
    isOnline: Boolean,
    location: Location?,
    isChoosingDisabledCharacters: Boolean,
    isShowingClones: Boolean,
    onDisableCharacterClick: (characterId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    CharacterTooltip(character.info) {
        val pointerInteractionStateHolder = rememberPointerInteractionStateHolder()
        Column(
            modifier = modifier
                .hoverBackground(pointerInteractionStateHolder = pointerInteractionStateHolder)
                .padding(Spacing.verySmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OnlineIndicatorBar(isOnline)
                DynamicCharacterPortraitParallax(character.characterId, 64.dp, enterTimestamp, pointerInteractionStateHolder)
                when (character.info) {
                    null -> {
                        Text(
                            text = stringResource(Res.string.characters_character_row_could_not_load_text),
                            style = RiftTheme.typography.bodySecondary.copy(color = RiftTheme.colors.borderError),
                            modifier = Modifier
                                .padding(horizontal = Spacing.medium)
                                .weight(1f),
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier.padding(start = Spacing.medium),
                        ) {
                            RiftTooltipArea(character.info.corporationName) {
                                AsyncCorporationLogo(
                                    corporationId = character.info.corporationId,
                                    size = 32,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                            if (character.info.allianceId != null) {
                                RiftTooltipArea(character.info.allianceName) {
                                    AsyncAllianceLogo(
                                        allianceId = character.info.allianceId,
                                        size = 32,
                                        modifier = Modifier.size(32.dp),
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(horizontal = Spacing.medium)
                                .weight(1f),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.width(IntrinsicSize.Max),
                            ) {
                                Text(
                                    text = character.info.name,
                                    style = RiftTheme.typography.headerHighlighted,
                                    modifier = Modifier.weight(1f),
                                )
                                OnlineIndicatorDot(
                                    isOnline = isOnline,
                                    modifier = Modifier.padding(horizontal = Spacing.medium),
                                )
                                AuthenticationStatusIcon(character.authenticationStatus)
                            }
                            LocationText(location)
                            if (character.walletBalance != null) {
                                Text(
                                    text = formatIskCompact(character.walletBalance),
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                            }
                        }

                        AnimatedVisibility(!isChoosingDisabledCharacters) {
                            Location(location)
                        }
                    }
                }

                AnimatedVisibility(isChoosingDisabledCharacters) {
                    RiftTooltipArea(stringResource(Res.string.characters_character_row_disable_character_text)) {
                        RiftIconButton(
                            icon = Res.drawable.buttoniconminus,
                            onClick = { onDisableCharacterClick(character.characterId) },
                            modifier = Modifier.padding(start = Spacing.small),
                        )
                    }
                }
            }

            AnimatedVisibility(!isChoosingDisabledCharacters && isShowingClones) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                ) {
                    for (clone in character.clones.sortedWith(
                        compareBy(
                            { !it.isActive },
                            { it.station?.solarSystemId ?: it.structure?.solarSystemId },
                            { -it.id },
                        ),
                    )) {
                        if (clone.isActive && clone.implants.isEmpty()) continue
                        Clone(clone)
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationText(location: Location?) {
    if (location != null) {
        Column {
            val systemsRepository: SolarSystemsRepository = remember { koin.get() }
            val typesRepository: TypesRepository = remember { koin.get() }
            val systemName = systemsRepository.getSystemName(location.solarSystemId)
            val shipName = if (location.ship != null) {
                typesRepository.getType(location.ship.shipTypeId)?.name
            } else {
                null
            }

            if (systemName != null) {
                val locationId = location.station?.stationId?.toLong() ?: location.structure?.structureId
                val text = if (locationId != null) {
                    if (location.station != null) {
                        if (shipName != null) {
                            stringResource(Res.string.characters_ship_docked_station, shipName)
                        } else {
                            stringResource(Res.string.characters_docked_station)
                        }
                    } else {
                        val structureTypeName = location.structure?.typeId?.let { typesRepository.getTypeName(it) }
                            ?: stringResource(Res.string.characters_structure)
                        if (shipName != null) {
                            stringResource(Res.string.characters_ship_docked_structure, shipName, structureTypeName)
                        } else {
                            stringResource(Res.string.characters_docked_structure, structureTypeName)
                        }
                    }
                } else if (shipName != null) {
                    stringResource(Res.string.characters_ship_in_space, shipName)
                } else {
                    stringResource(Res.string.characters_location_text_space_text)
                }
                Text(
                    text = text,
                    style = RiftTheme.typography.bodyPrimary,
                )
            }
        }
    }
}

@Composable
fun AuthenticationStatusIcon(
    status: AuthenticationStatus,
    modifier: Modifier = Modifier,
) {
    RiftTooltipArea(
        tooltip = {
            when (status) {
                AuthenticationStatus.Authenticated -> {}
                is AuthenticationStatus.PartiallyAuthenticated -> {
                    val scopes = status.missingScopes.map { it.name.resolve() }.joinToString("\n")
                    Column(
                        modifier = Modifier.padding(Spacing.large),
                    ) {
                        Text(
                            text = stringResource(Res.string.characters_authentication_status_icon_missing_esi_scopes_text),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                        Text(
                            text = scopes,
                            style = RiftTheme.typography.bodySecondary,
                            modifier = Modifier.padding(vertical = Spacing.small),
                        )
                        Text(
                            text = stringResource(Res.string.characters_authentication_status_icon_some_features_won_t_work_text),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                    }
                }
                AuthenticationStatus.Unauthenticated -> {
                    Text(
                        text = stringResource(Res.string.characters_authentication_status_icon_not_authenticated_esi_click_log_text),
                        style = RiftTheme.typography.bodyPrimary,
                        modifier = Modifier.padding(Spacing.large),
                    )
                }
            }
        },
        modifier = modifier,
    ) {
        val icon = when (status) {
            AuthenticationStatus.Authenticated -> null
            is AuthenticationStatus.PartiallyAuthenticated -> Res.drawable.status_warning_orange
            AuthenticationStatus.Unauthenticated -> Res.drawable.status_warning_red
        }
        AnimatedContent(icon) {
            if (it != null) {
                Box(
                    modifier = Modifier
                        .clipToBounds()
                        .size(24.dp),
                ) {
                    Image(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.requiredSize(36.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Clone(clone: Clone) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(Res.drawable.clone),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )

        if (clone.implants.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.weight(1f),
            ) {
                for (implant in clone.implants) {
                    RiftTooltipArea(
                        text = implant.name,
                        modifier = Modifier.size(32.dp),
                    ) {
                        AsyncTypeIcon(
                            type = implant,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
        } else {
            Text(
                text = stringResource(Res.string.characters_clone_no_implants_text),
                style = RiftTheme.typography.bodySecondary,
                modifier = Modifier
                    .padding(start = Spacing.medium)
                    .weight(1f),
            )
        }

        if (clone.isActive) {
            Text(
                text = stringResource(Res.string.characters_clone_active_clone_text),
                style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
            )
        } else {
            CloneLocation(clone.station, clone.structure)
        }
    }
}

@Composable
private fun HiddenCharacterRow(
    character: CharacterItem,
    isChoosingDisabledCharacters: Boolean,
    onEnableCharacterClick: (characterId: Int) -> Unit,
    onDeleteCharacterClick: (characterId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pointerInteractionStateHolder = rememberPointerInteractionStateHolder()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .hoverBackground(pointerInteractionStateHolder = pointerInteractionStateHolder)
            .padding(Spacing.verySmall),
    ) {
        DynamicCharacterPortraitParallax(
            characterId = character.characterId,
            size = 32.dp,
            enterTimestamp = null,
            pointerInteractionStateHolder = pointerInteractionStateHolder,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            when (character.info) {
                null -> {
                    Text(
                        text = stringResource(Res.string.characters_character_row_could_not_load_text),
                        style = RiftTheme.typography.bodySecondary.copy(color = RiftTheme.colors.borderError),
                        modifier = Modifier.padding(horizontal = Spacing.medium),
                    )
                }

                else -> {
                    AsyncCorporationLogo(
                        corporationId = character.info.corporationId,
                        size = 32,
                        modifier = Modifier.size(32.dp),
                    )
                    if (character.info.allianceId != null) {
                        AsyncAllianceLogo(
                            allianceId = character.info.allianceId,
                            size = 32,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    Text(
                        text = character.info.name,
                        style = RiftTheme.typography.headerSecondary,
                        modifier = Modifier
                            .padding(horizontal = Spacing.medium),
                    )
                }
            }
        }
        AnimatedVisibility(isChoosingDisabledCharacters) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                RiftTooltipArea(stringResource(Res.string.characters_hidden_character_row_delete_character_text)) {
                    RiftIconButton(
                        icon = Res.drawable.delete,
                        type = ButtonType.Negative,
                        cornerCut = ButtonCornerCut.None,
                        onClick = { onDeleteCharacterClick(character.characterId) },
                    )
                }
                RiftTooltipArea(stringResource(Res.string.characters_hidden_character_row_enable_character_text)) {
                    RiftIconButton(
                        icon = Res.drawable.buttoniconplus,
                        onClick = { onEnableCharacterClick(character.characterId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun OnlineIndicatorBar(isOnline: Boolean) {
    val height by animateDpAsState(
        targetValue = if (isOnline) 64.dp else 0.dp,
        animationSpec = tween(1000),
    )
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(height)
            .background(RiftTheme.colors.successGreen),
    ) {}
}

@Composable
fun OnlineIndicatorDot(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    RiftIndicatorDot(
        color = if (isOnline) RiftTheme.colors.successGreen else RiftTheme.colors.hotRed,
        isActive = isOnline,
        modifier = modifier,
    )
}

@Composable
private fun Location(location: Location?) {
    if (location == null) return
    val systemsRepository: SolarSystemsRepository = remember { koin.get() }
    val typesRepository: TypesRepository = remember { koin.get() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            AnimatedContent(location, contentKey = { it.solarSystemId }) {
                val system = systemsRepository.getSystem(location.solarSystemId) ?: return@AnimatedContent
                LocationIcon(location, system, typesRepository)
            }
            AnimatedContent(location.ship) {
                ShipIcon(location.ship, typesRepository)
            }
        }
        AnimatedContent(location, contentKey = { it.solarSystemId }) {
            val systemName = systemsRepository.getSystemName(location.solarSystemId) ?: return@AnimatedContent
            Text(
                text = systemName,
                style = RiftTheme.typography.bodyLink,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 64.dp),
            )
        }
    }
}

@Composable
private fun LocationIcon(
    location: Location,
    system: MapSolarSystem,
    typesRepository: TypesRepository,
) {
    val typeId = location.station?.typeId
        ?: location.structure?.typeId
        ?: system.sunTypeId
    val type = typesRepository.getTypeOrPlaceholder(typeId)
    val locationId = location.station?.stationId?.toLong() ?: location.structure?.structureId

    ClickableLocation(
        systemId = location.solarSystemId,
        locationId = locationId,
        locationTypeId = typeId,
        locationName = location.station?.name ?: location.structure?.name ?: system.name,
    ) {
        RiftTooltipArea(
            tooltip = {
                Text(
                    text = buildAnnotatedString {
                        val stationName = location.station?.name?.replace(" - ", "\n")
                        val structureName = location.structure?.name?.replace(" - ", "\n")
                        if (stationName != null) {
                            withStyle(RiftTheme.typography.bodyHighlighted.toSpanStyle()) {
                                appendLine(type.name)
                            }
                            append(stationName.trim())
                        } else if (structureName != null) {
                            withStyle(RiftTheme.typography.bodyHighlighted.toSpanStyle()) {
                                appendLine(type.name)
                            }
                            append(structureName.removePrefix(system.name).trim())
                        } else {
                            append(stringResource(Res.string.characters_location_text_space_text))
                        }
                    },
                    style = RiftTheme.typography.bodyPrimary,
                    modifier = Modifier.padding(Spacing.large),
                )
            },
        ) {
            AsyncTypeIcon(
                typeId = typeId,
                modifier = Modifier.size(32.dp).border(1.dp, RiftTheme.colors.borderGreyLight),
            )
        }
    }
}

@Composable
private fun ShipIcon(ship: CharacterIdShip?, typesRepository: TypesRepository) {
    if (ship == null) return
    val type = typesRepository.getType(ship.shipTypeId) ?: return

    ClickableShip(type) {
        RiftTooltipArea(
            tooltip = {
                Text(
                    text = buildAnnotatedString {
                        withStyle(RiftTheme.typography.bodyHighlighted.toSpanStyle()) {
                            appendLine(type.name)
                        }
                        append(ship.shipName)
                    },
                    style = RiftTheme.typography.bodyPrimary,
                    modifier = Modifier.padding(Spacing.large),
                )
            },
        ) {
            AsyncTypeIcon(
                type = type,
                modifier = Modifier.size(32.dp).border(1.dp, RiftTheme.colors.borderGreyLight),
            )
        }
    }
}

@Composable
private fun CloneLocation(station: Station?, structure: Structure?) {
    val solarSystemId = station?.solarSystemId ?: structure?.solarSystemId ?: return
    val repository: SolarSystemsRepository = remember { koin.get() }
    val systemName = repository.getSystemName(solarSystemId) ?: return
    val locationId = station?.stationId?.toLong() ?: structure?.structureId

    ClickableLocation(
        systemId = solarSystemId,
        locationId = locationId,
        locationTypeId = station?.typeId ?: structure?.typeId,
        locationName = station?.name ?: structure?.name,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            val typeId = station?.typeId ?: structure?.typeId ?: return@ClickableLocation
            val tooltip = station?.name?.replace(" - ", "\n")
                ?: structure?.name?.replace(" - ", "\n")
                ?: return@ClickableLocation
            Text(
                text = systemName,
                style = RiftTheme.typography.bodyLink,
                modifier = Modifier.widthIn(max = 50.dp),
            )
            RiftTooltipArea(
                text = tooltip,
            ) {
                AsyncTypeIcon(
                    typeId = typeId,
                    modifier = Modifier.size(32.dp).border(1.dp, RiftTheme.colors.borderGreyLight),
                )
            }
        }
    }
}
