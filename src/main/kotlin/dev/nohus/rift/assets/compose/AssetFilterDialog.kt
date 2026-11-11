package dev.nohus.rift.assets.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.assets.AssetFilterAttribute
import dev.nohus.rift.assets.AssetFilterComparison
import dev.nohus.rift.assets.AssetFilterComparison.BooleanValue
import dev.nohus.rift.assets.AssetFilterComparison.GroupValue
import dev.nohus.rift.assets.AssetFilterComparison.MetaGroupValue
import dev.nohus.rift.assets.AssetFilterComparison.NumberValue
import dev.nohus.rift.assets.AssetFilterComparison.TechLevelValue
import dev.nohus.rift.assets.AssetFilterComparison.TextValue
import dev.nohus.rift.assets.AssetFilterDefinition
import dev.nohus.rift.assets.AssetFilterMatch
import dev.nohus.rift.assets.AssetSubfilter
import dev.nohus.rift.assets.GroupFilterOperator
import dev.nohus.rift.assets.NumberFilterOperator
import dev.nohus.rift.assets.TextFilterOperator
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftDropdown
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftRadioButton
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.rememberPointerInteractionStateHolder
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_add_attribute_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_all_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_any_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_delete_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_filter_name_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_match_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_content_save_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_edit_filter_text
import dev.nohus.rift.generated.resources.assets_asset_filter_dialog_new_filter_text
import dev.nohus.rift.generated.resources.assets_filter_assembled
import dev.nohus.rift.generated.resources.assets_filter_blueprint_copy
import dev.nohus.rift.generated.resources.assets_filter_contains
import dev.nohus.rift.generated.resources.assets_filter_does_not_contain
import dev.nohus.rift.generated.resources.assets_filter_does_not_start_with
import dev.nohus.rift.generated.resources.assets_filter_equal_to
import dev.nohus.rift.generated.resources.assets_filter_greater_than
import dev.nohus.rift.generated.resources.assets_filter_group
import dev.nohus.rift.generated.resources.assets_filter_is
import dev.nohus.rift.generated.resources.assets_filter_is_not
import dev.nohus.rift.generated.resources.assets_filter_less_than
import dev.nohus.rift.generated.resources.assets_filter_meta_group
import dev.nohus.rift.generated.resources.assets_filter_meta_level
import dev.nohus.rift.generated.resources.assets_filter_name
import dev.nohus.rift.generated.resources.assets_filter_owner
import dev.nohus.rift.generated.resources.assets_filter_stack_size
import dev.nohus.rift.generated.resources.assets_filter_starts_with
import dev.nohus.rift.generated.resources.assets_filter_tech_level
import dev.nohus.rift.generated.resources.assets_filter_unit_price
import dev.nohus.rift.generated.resources.assets_filter_volume
import dev.nohus.rift.generated.resources.assets_subfilter_row_false_text
import dev.nohus.rift.generated.resources.assets_subfilter_row_true_text
import dev.nohus.rift.generated.resources.assets_subfilter_row_value_placeholder
import dev.nohus.rift.generated.resources.assets_tech_level
import dev.nohus.rift.generated.resources.common_action_cancel
import dev.nohus.rift.generated.resources.minus_12px
import dev.nohus.rift.generated.resources.window_assets
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.types.TypesRepository.MetaGroup
import dev.nohus.rift.types.TypesRepository.TypeCategory
import dev.nohus.rift.types.TypesRepository.TypeGroup
import dev.nohus.rift.utils.getRomanNumerals
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import dev.nohus.rift.windowing.WindowManager
import org.jetbrains.compose.resources.stringResource

@Composable
fun WindowScope.AssetFilterDialog(
    filter: AssetFilterDefinition?,
    parentWindowState: WindowManager.RiftWindowState,
    onDismiss: () -> Unit,
    onSaveClick: (AssetFilterDefinition) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    RiftDialog(
        title = if (filter == null) stringResource(Res.string.assets_asset_filter_dialog_new_filter_text) else stringResource(Res.string.assets_asset_filter_dialog_edit_filter_text),
        icon = Res.drawable.window_assets,
        parentState = parentWindowState,
        state = rememberWindowState(width = 550.dp, height = Dp.Unspecified),
        onCloseClick = onDismiss,
    ) {
        AssetFilterDialogContent(
            filter = filter,
            onCancelClick = onDismiss,
            onSaveClick = onSaveClick,
            onDeleteClick = onDeleteClick,
        )
    }
}

@Composable
private fun AssetFilterDialogContent(
    filter: AssetFilterDefinition?,
    onCancelClick: () -> Unit,
    onSaveClick: (AssetFilterDefinition) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    val typesRepository: TypesRepository = remember { koin.get() }
    val categories = remember {
        typesRepository.getCategories().filter { it.isPublished }
    }
    val metaGroups = remember {
        typesRepository.getMetaGroups()
    }
    var name by remember(filter) { mutableStateOf(filter?.name.orEmpty()) }
    var match by remember(filter) { mutableStateOf(filter?.match ?: AssetFilterMatch.All) }
    var subfilters by remember(filter, categories, metaGroups) {
        val initialSubfilters = filter?.subfilters?.takeIf { it.isNotEmpty() } ?: listOf(defaultSubfilter())
        mutableStateOf(
            initialSubfilters.map {
                when {
                    it.attribute == AssetFilterAttribute.Group && it.comparison !is GroupValue -> {
                        it.copy(comparison = defaultComparison(AssetFilterAttribute.Group, categories, metaGroups))
                    }
                    it.attribute == AssetFilterAttribute.MetaGroup && it.comparison !is MetaGroupValue -> {
                        it.copy(comparison = defaultMetaGroupComparison(it.comparison, metaGroups))
                    }
                    else -> it
                }
            },
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.heightIn(max = 400.dp),
    ) {
        ScrollbarColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            isScrollbarConditional = true,
            scrollbarModifier = Modifier.padding(start = Spacing.medium),
            modifier = Modifier.weight(1f, fill = false),
        ) {
            subfilters.forEachIndexed { index, subfilter ->
                SubfilterRow(
                    subfilter = subfilter,
                    categories = categories,
                    metaGroups = metaGroups,
                    getGroupsInCategory = {
                        typesRepository.getGroupsInCategory(it).filter { it.isPublished }
                    },
                    isRemovable = subfilters.size > 1,
                    onChange = { updated ->
                        subfilters = subfilters.toMutableList().also { it[index] = updated }
                    },
                    onRemove = {
                        subfilters = subfilters.toMutableList().also { it.removeAt(index) }
                    },
                )
            }
            RiftButton(
                text = stringResource(Res.string.assets_asset_filter_dialog_content_add_attribute_text),
                type = ButtonType.Secondary,
                isCompact = true,
                onClick = { subfilters = subfilters + defaultSubfilter() },
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.large),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    Text(stringResource(Res.string.assets_asset_filter_dialog_content_match_text), style = RiftTheme.typography.bodyPrimary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    ) {
                        MatchRadioButton(
                            text = stringResource(Res.string.assets_asset_filter_dialog_content_all_text),
                            isChecked = match == AssetFilterMatch.All,
                            onChecked = { match = AssetFilterMatch.All },
                        )
                        MatchRadioButton(
                            text = stringResource(Res.string.assets_asset_filter_dialog_content_any_text),
                            isChecked = match == AssetFilterMatch.Any,
                            onChecked = { match = AssetFilterMatch.Any },
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(Res.string.assets_asset_filter_dialog_content_filter_name_text), style = RiftTheme.typography.bodyPrimary)
                    RiftTextField(
                        text = name,
                        placeholder = stringResource(Res.string.assets_asset_filter_dialog_content_filter_name_text),
                        onTextChanged = { name = it.take(64) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            if (filter != null) {
                RiftButton(
                    text = stringResource(Res.string.assets_asset_filter_dialog_content_delete_text),
                    type = ButtonType.Negative,
                    cornerCut = ButtonCornerCut.BottomLeft,
                    onClick = { onDeleteClick(filter.id) },
                    modifier = Modifier.weight(1f),
                )
            }
            RiftButton(
                text = stringResource(Res.string.common_action_cancel),
                type = ButtonType.Secondary,
                cornerCut = if (filter == null) ButtonCornerCut.BottomLeft else ButtonCornerCut.None,
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
            )
            RiftButton(
                text = stringResource(Res.string.assets_asset_filter_dialog_content_save_text),
                isEnabled = name.isNotBlank() && subfilters.all { it.isValid() },
                onClick = {
                    onSaveClick(
                        AssetFilterDefinition(
                            id = filter?.id.orEmpty(),
                            name = name.trim(),
                            match = match,
                            subfilters = subfilters,
                        ),
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MatchRadioButton(
    text: String,
    isChecked: Boolean,
    onChecked: () -> Unit,
) {
    val pointerInteractionStateHolder = rememberPointerInteractionStateHolder()
    val player = rememberUiSoundPlayer()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier
            .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
            .onClick {
                player.playSelect(isChecked)
                onChecked()
            }
            .pointerInteraction(pointerInteractionStateHolder),
    ) {
        RiftRadioButton(
            isChecked = isChecked,
            onChecked = onChecked,
            pointerInteractionStateHolder = pointerInteractionStateHolder,
        )
        Text(text, style = RiftTheme.typography.bodyPrimary)
    }
}

@Composable
private fun SubfilterRow(
    subfilter: AssetSubfilter,
    categories: List<TypeCategory>,
    metaGroups: List<MetaGroup>,
    getGroupsInCategory: (Int) -> List<TypeGroup>,
    isRemovable: Boolean,
    onChange: (AssetSubfilter) -> Unit,
    onRemove: () -> Unit,
) {
    val attributeNames = AssetFilterAttribute.entries.associateWith { it.label.resolve() }
    val numberOperatorNames = NumberFilterOperator.entries.associateWith { it.label.resolve() }
    val groupOperatorNames = GroupFilterOperator.entries.associateWith { it.label.resolve() }
    val textOperatorNames = TextFilterOperator.entries.associateWith { it.label.resolve() }
    val trueName = stringResource(Res.string.assets_subfilter_row_true_text)
    val falseName = stringResource(Res.string.assets_subfilter_row_false_text)
    val techNames = listOf(1, 2, 3).associateWith { stringResource(Res.string.assets_tech_level, getRomanNumerals(it)) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        RiftImageButton(
            resource = Res.drawable.minus_12px,
            size = 12.dp,
            isEnabled = isRemovable,
            onClick = onRemove,
        )
        RiftDropdown(
            items = AssetFilterAttribute.entries,
            selectedItem = subfilter.attribute,
            onItemSelected = { onChange(AssetSubfilter(it, defaultComparison(it, categories, metaGroups))) },
            getItemName = { attributeNames.getValue(it) },
        )
        val valueDropdownWidth = 160.dp
        when (val comparison = subfilter.comparison) {
            is BooleanValue -> {
                RiftDropdown(
                    items = listOf(true, false),
                    selectedItem = comparison.value,
                    onItemSelected = { onChange(subfilter.copy(comparison = BooleanValue(it))) },
                    getItemName = { if (it) trueName else falseName },
                    modifier = Modifier.width(valueDropdownWidth),
                )
            }
            is NumberValue -> {
                RiftDropdown(
                    items = NumberFilterOperator.entries,
                    selectedItem = comparison.operator,
                    onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(operator = it))) },
                    getItemName = { numberOperatorNames.getValue(it) },
                    modifier = Modifier.width(valueDropdownWidth),
                )
                RiftTextField(
                    text = comparison.value.takeIf { it != 0L }?.toString().orEmpty(),
                    placeholder = "0",
                    onTextChanged = { value ->
                        val number = value.toLongOrNull() ?: 0L
                        onChange(subfilter.copy(comparison = comparison.copy(value = number)))
                    },
                    modifier = Modifier.weight(1f),
                )
            }
            is GroupValue -> {
                val selectedCategory = categories.firstOrNull { it.id == comparison.categoryId } ?: categories.firstOrNull()
                if (selectedCategory != null) {
                    val groupOptions = listOf(GroupOption(null, stringResource(Res.string.assets_asset_filter_dialog_content_all_text))) + getGroupsInCategory(selectedCategory.id).map { GroupOption(it.id, it.name) }
                    val selectedGroup = groupOptions.firstOrNull { it.id == comparison.groupId } ?: groupOptions.first()
                    RiftDropdown(
                        items = GroupFilterOperator.entries,
                        selectedItem = comparison.operator,
                        onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(operator = it))) },
                        getItemName = { groupOperatorNames.getValue(it) },
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        modifier = Modifier.weight(1f),
                    ) {
                        RiftDropdown(
                            items = categories,
                            selectedItem = selectedCategory,
                            onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(categoryId = it.id, groupId = null))) },
                            getItemName = { it.name },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        RiftDropdown(
                            items = groupOptions,
                            selectedItem = selectedGroup,
                            onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(groupId = it.id))) },
                            getItemName = { it.name },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            is MetaGroupValue -> {
                val selectedMetaGroup = metaGroups.firstOrNull { it.id == comparison.metaGroupId } ?: metaGroups.firstOrNull()
                if (selectedMetaGroup != null) {
                    RiftDropdown(
                        items = GroupFilterOperator.entries,
                        selectedItem = comparison.operator,
                        onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(operator = it))) },
                        getItemName = { groupOperatorNames.getValue(it) },
                    )
                    RiftDropdown(
                        items = metaGroups,
                        selectedItem = selectedMetaGroup,
                        onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(metaGroupId = it.id))) },
                        getItemName = { it.name },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            is TechLevelValue -> {
                RiftDropdown(
                    items = GroupFilterOperator.entries,
                    selectedItem = comparison.operator,
                    onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(operator = it))) },
                    getItemName = { groupOperatorNames.getValue(it) },
                )
                RiftDropdown(
                    items = listOf(1, 2, 3),
                    selectedItem = comparison.techLevel,
                    onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(techLevel = it))) },
                    getItemName = { techNames.getValue(it) },
                    modifier = Modifier.weight(1f),
                )
            }
            is TextValue -> {
                RiftDropdown(
                    items = TextFilterOperator.entries,
                    selectedItem = comparison.operator,
                    onItemSelected = { onChange(subfilter.copy(comparison = comparison.copy(operator = it))) },
                    getItemName = { textOperatorNames.getValue(it) },
                    modifier = Modifier.width(valueDropdownWidth),
                )
                RiftTextField(
                    text = comparison.value,
                    placeholder = stringResource(Res.string.assets_subfilter_row_value_placeholder),
                    onTextChanged = { onChange(subfilter.copy(comparison = comparison.copy(value = it.take(32)))) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun defaultSubfilter(): AssetSubfilter {
    return AssetSubfilter(AssetFilterAttribute.Assembled, BooleanValue(true))
}

private fun defaultComparison(attribute: AssetFilterAttribute, categories: List<TypeCategory>, metaGroups: List<MetaGroup>): AssetFilterComparison {
    return when (attribute.type) {
        AttributeType.Boolean -> BooleanValue(true)
        AttributeType.Number -> NumberValue(NumberFilterOperator.LessThan, 0)
        AttributeType.Group -> GroupValue(GroupFilterOperator.Is, categoryId = categories.firstOrNull()?.id ?: 0, groupId = null)
        AttributeType.MetaGroup -> MetaGroupValue(GroupFilterOperator.Is, metaGroupId = metaGroups.firstOrNull()?.id ?: 0)
        AttributeType.TechLevel -> TechLevelValue(GroupFilterOperator.Is, techLevel = 1)
        AttributeType.Text -> TextValue(TextFilterOperator.Contains, "")
    }
}

private fun defaultMetaGroupComparison(comparison: AssetFilterComparison, metaGroups: List<MetaGroup>): MetaGroupValue {
    val metaGroupId = if (comparison is TextValue) {
        metaGroups.firstOrNull { it.name.equals(comparison.value, ignoreCase = true) }?.id
    } else {
        null
    } ?: metaGroups.firstOrNull()?.id ?: 0
    val operator = if (comparison is TextValue && comparison.operator == TextFilterOperator.IsNot) {
        GroupFilterOperator.IsNot
    } else {
        GroupFilterOperator.Is
    }
    return MetaGroupValue(operator, metaGroupId)
}

private fun AssetSubfilter.isValid(): Boolean {
    return when (comparison) {
        is BooleanValue -> attribute.type == AttributeType.Boolean
        is NumberValue -> attribute.type == AttributeType.Number
        is GroupValue -> attribute.type == AttributeType.Group
        is MetaGroupValue -> attribute.type == AttributeType.MetaGroup && comparison.metaGroupId != 0
        is TechLevelValue -> attribute.type == AttributeType.TechLevel
        is TextValue -> attribute.type == AttributeType.Text && comparison.value.isNotBlank()
    }
}

private enum class AttributeType {
    Boolean,
    Number,
    Group,
    MetaGroup,
    TechLevel,
    Text,
}

private data class GroupOption(
    val id: Int?,
    val name: String,
)

private val AssetFilterAttribute.type: AttributeType
    get() = when (this) {
        AssetFilterAttribute.Assembled,
        AssetFilterAttribute.BlueprintCopy,
        -> AttributeType.Boolean
        AssetFilterAttribute.UnitPrice,
        AssetFilterAttribute.MetaLevel,
        AssetFilterAttribute.StackSize,
        AssetFilterAttribute.Volume,
        -> AttributeType.Number
        AssetFilterAttribute.Group,
        -> AttributeType.Group
        AssetFilterAttribute.MetaGroup,
        -> AttributeType.MetaGroup
        AssetFilterAttribute.TechLevel,
        -> AttributeType.TechLevel
        AssetFilterAttribute.Name,
        AssetFilterAttribute.Owner,
        -> AttributeType.Text
    }

private val AssetFilterAttribute.label: UiText
    get() = when (this) {
        AssetFilterAttribute.Assembled -> Res.string.assets_filter_assembled.toUiText()
        AssetFilterAttribute.BlueprintCopy -> Res.string.assets_filter_blueprint_copy.toUiText()
        AssetFilterAttribute.UnitPrice -> Res.string.assets_filter_unit_price.toUiText()
        AssetFilterAttribute.Group -> Res.string.assets_filter_group.toUiText()
        AssetFilterAttribute.MetaGroup -> Res.string.assets_filter_meta_group.toUiText()
        AssetFilterAttribute.MetaLevel -> Res.string.assets_filter_meta_level.toUiText()
        AssetFilterAttribute.TechLevel -> Res.string.assets_filter_tech_level.toUiText()
        AssetFilterAttribute.Name -> Res.string.assets_filter_name.toUiText()
        AssetFilterAttribute.StackSize -> Res.string.assets_filter_stack_size.toUiText()
        AssetFilterAttribute.Volume -> Res.string.assets_filter_volume.toUiText()
        AssetFilterAttribute.Owner -> Res.string.assets_filter_owner.toUiText()
    }

private val NumberFilterOperator.label: UiText
    get() = when (this) {
        NumberFilterOperator.LessThan -> Res.string.assets_filter_less_than.toUiText()
        NumberFilterOperator.EqualTo -> Res.string.assets_filter_equal_to.toUiText()
        NumberFilterOperator.GreaterThan -> Res.string.assets_filter_greater_than.toUiText()
    }

private val GroupFilterOperator.label: UiText
    get() = when (this) {
        GroupFilterOperator.Is -> Res.string.assets_filter_is.toUiText()
        GroupFilterOperator.IsNot -> Res.string.assets_filter_is_not.toUiText()
    }

private val TextFilterOperator.label: UiText
    get() = when (this) {
        TextFilterOperator.StartsWith -> Res.string.assets_filter_starts_with.toUiText()
        TextFilterOperator.DoesNotStartWith -> Res.string.assets_filter_does_not_start_with.toUiText()
        TextFilterOperator.Is -> Res.string.assets_filter_is.toUiText()
        TextFilterOperator.IsNot -> Res.string.assets_filter_is_not.toUiText()
        TextFilterOperator.Contains -> Res.string.assets_filter_contains.toUiText()
        TextFilterOperator.DoesNotContain -> Res.string.assets_filter_does_not_contain.toUiText()
    }
