package dev.nohus.rift.opportunities.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.LoadingSpinner
import dev.nohus.rift.compose.RiftSideNavigation
import dev.nohus.rift.compose.RiftSideNavigationHeader
import dev.nohus.rift.compose.RiftSideNavigationItem
import dev.nohus.rift.compose.RiftToggleButton
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ToggleButtonType
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.contact_tag
import dev.nohus.rift.generated.resources.house_16px
import dev.nohus.rift.generated.resources.opportunities_all_filter_name
import dev.nohus.rift.generated.resources.opportunities_career_paths_heading
import dev.nohus.rift.generated.resources.opportunities_features_heading
import dev.nohus.rift.generated.resources.opportunities_lifecycle_current_action
import dev.nohus.rift.generated.resources.opportunities_lifecycle_history_action
import dev.nohus.rift.generated.resources.opportunities_loading_tooltip
import dev.nohus.rift.generated.resources.opportunities_other_tags_heading
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.opportunities.OpportunitiesViewModel
import dev.nohus.rift.opportunities.OpportunitiesViewModel.OpportunityLifecycleFilter
import dev.nohus.rift.opportunities.OpportunityCategoryFilter
import dev.nohus.rift.opportunities.OpportunityCategoryFilterType
import org.jetbrains.compose.resources.stringResource

@Composable
fun SideNavigation(
    state: OpportunitiesViewModel.UiState,
    onLifecycleFilterChange: (OpportunityLifecycleFilter) -> Unit,
    onClick: (OpportunityCategoryFilter?) -> Unit,
) {
    RiftSideNavigation(
        footer = {
            LoadingFooter(isLoading = state.isLoading)
        },
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier.padding(end = 8.dp, top = 8.dp),
            ) {
                RiftToggleButton(
                    text = stringResource(Res.string.opportunities_lifecycle_current_action),
                    isSelected = state.lifecycleFilter == OpportunityLifecycleFilter.Active,
                    type = ToggleButtonType.Left,
                    onClick = { onLifecycleFilterChange(OpportunityLifecycleFilter.Active) },
                    modifier = Modifier.weight(1f),
                )
                RiftToggleButton(
                    text = stringResource(Res.string.opportunities_lifecycle_history_action),
                    isSelected = state.lifecycleFilter == OpportunityLifecycleFilter.History,
                    type = ToggleButtonType.Right,
                    onClick = { onLifecycleFilterChange(OpportunityLifecycleFilter.History) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        val allFilters = OpportunityCategoryFilter::class.sealedSubclasses.map { it.objectInstance!! }
        val primaryFilter = if (state.participatingFilter) null else state.primaryFilter

        item {
            RiftSideNavigationHeader(stringResource(Res.string.opportunities_features_heading))
        }
        item {
            RiftSideNavigationItem(
                text = stringResource(Res.string.opportunities_all_filter_name),
                count = state.categoryFilters.opportunityCount[null] ?: 0,
                icon = Res.drawable.house_16px,
                isSelected = primaryFilter == null,
                onClick = { onClick(null) },
            )
        }
        val features = allFilters.filter { it.type == OpportunityCategoryFilterType.Feature }
        items(features, key = { it }) {
            Item(state.categoryFilters, primaryFilter, it, onClick)
        }

        item {
            RiftSideNavigationHeader(stringResource(Res.string.opportunities_career_paths_heading))
        }
        val careerPaths = allFilters.filter { it.type == OpportunityCategoryFilterType.CareerPath }
        items(careerPaths, key = { it }) {
            Item(state.categoryFilters, primaryFilter, it, onClick)
        }

        item {
            RiftSideNavigationHeader(stringResource(Res.string.opportunities_other_tags_heading))
        }
        val activities = allFilters
            .filter { it.type == OpportunityCategoryFilterType.Activity }
            .filter { (state.categoryFilters.opportunityCount[it] ?: 0) > 0 || it == state.primaryFilter }
        items(activities, key = { it }) {
            Item(state.categoryFilters, primaryFilter, it, onClick)
        }
    }
}

@Composable
private fun LazyItemScope.Item(
    categoryFilters: OpportunitiesViewModel.CategoryFilters,
    primaryFilter: OpportunityCategoryFilter?,
    filter: OpportunityCategoryFilter,
    onClick: (OpportunityCategoryFilter) -> Unit,
) {
    val showIcon = filter.type in listOf(OpportunityCategoryFilterType.Feature, OpportunityCategoryFilterType.CareerPath)
    val isSelected = primaryFilter == filter
    RiftSideNavigationItem(
        text = filter.name.resolve(),
        count = categoryFilters.opportunityCount[filter] ?: 0,
        icon = filter.icon.takeIf { showIcon } ?: Res.drawable.contact_tag.takeIf { isSelected },
        isSelected = isSelected,
        onClick = { onClick(filter) },
    )
}

@Composable
private fun LoadingFooter(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
    ) {
        AnimatedVisibility(isLoading) {
            RiftTooltipArea(stringResource(Res.string.opportunities_loading_tooltip)) {
                LoadingSpinner(
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}
