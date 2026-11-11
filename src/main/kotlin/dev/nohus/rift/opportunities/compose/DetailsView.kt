package dev.nohus.rift.opportunities.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.nohus.rift.characters.repositories.LocalCharactersRepository.LocalCharacter
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCharacterPortrait
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.CharacterStack
import dev.nohus.rift.compose.CharacterTooltip
import dev.nohus.rift.compose.ClickableAlliance
import dev.nohus.rift.compose.ClickableCharacter
import dev.nohus.rift.compose.ClickableCorporation
import dev.nohus.rift.compose.CorporationColorsSwatch
import dev.nohus.rift.compose.LinkText
import dev.nohus.rift.compose.MulticolorIconType
import dev.nohus.rift.compose.PointerInteractionStateHolder
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCircularCharacterPortrait
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftMulticolorIcon
import dev.nohus.rift.compose.RiftOpportunityContainerFlair
import dev.nohus.rift.compose.RiftOpportunityTypeIcon
import dev.nohus.rift.compose.RiftSolarSystemChip
import dev.nohus.rift.compose.RiftTable
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.Sort
import dev.nohus.rift.compose.SortingColumn
import dev.nohus.rift.compose.TableCell.RichTableCell
import dev.nohus.rift.compose.TableCell.TextTableCell
import dev.nohus.rift.compose.TableRow
import dev.nohus.rift.compose.VerticalDivider
import dev.nohus.rift.compose.fadingRightEdge
import dev.nohus.rift.compose.getNow
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.produceCorporationColors
import dev.nohus.rift.compose.sharedTransitionElement
import dev.nohus.rift.compose.text.LinkedText
import dev.nohus.rift.compose.text.toPlainString
import dev.nohus.rift.compose.theme.EveColors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.opportunities_age_range_description
import dev.nohus.rift.generated.resources.opportunities_age_requirements_met_heading
import dev.nohus.rift.generated.resources.opportunities_age_requirements_unmet_heading
import dev.nohus.rift.generated.resources.opportunities_age_unspecified_description
import dev.nohus.rift.generated.resources.opportunities_alliance_column
import dev.nohus.rift.generated.resources.opportunities_allocated_reward_tooltip_heading
import dev.nohus.rift.generated.resources.opportunities_available_reward_label
import dev.nohus.rift.generated.resources.opportunities_contribution_method_label
import dev.nohus.rift.generated.resources.opportunities_contributions_empty_description
import dev.nohus.rift.generated.resources.opportunities_contributions_permission_description
import dev.nohus.rift.generated.resources.opportunities_contributor_column
import dev.nohus.rift.generated.resources.opportunities_contributors_error_description
import dev.nohus.rift.generated.resources.opportunities_corporation_column
import dev.nohus.rift.generated.resources.opportunities_coverage_limit_label
import dev.nohus.rift.generated.resources.opportunities_coverage_ratio_label
import dev.nohus.rift.generated.resources.opportunities_coverage_ratio_tooltip
import dev.nohus.rift.generated.resources.opportunities_created_date_heading
import dev.nohus.rift.generated.resources.opportunities_creator_heading
import dev.nohus.rift.generated.resources.opportunities_description_heading
import dev.nohus.rift.generated.resources.opportunities_details_title
import dev.nohus.rift.generated.resources.opportunities_end_date_prefix
import dev.nohus.rift.generated.resources.opportunities_expiry_prefix
import dev.nohus.rift.generated.resources.opportunities_isk_payout_column
import dev.nohus.rift.generated.resources.opportunities_job_type_label
import dev.nohus.rift.generated.resources.opportunities_maximum_age_clause
import dev.nohus.rift.generated.resources.opportunities_maximum_age_description
import dev.nohus.rift.generated.resources.opportunities_minimum_age_clause
import dev.nohus.rift.generated.resources.opportunities_minimum_age_description
import dev.nohus.rift.generated.resources.opportunities_not_participating_description
import dev.nohus.rift.generated.resources.opportunities_objectives_heading
import dev.nohus.rift.generated.resources.opportunities_operation_type_label
import dev.nohus.rift.generated.resources.opportunities_operational_intel_heading
import dev.nohus.rift.generated.resources.opportunities_own_contribution_heading
import dev.nohus.rift.generated.resources.opportunities_own_earnings_tooltip_heading
import dev.nohus.rift.generated.resources.opportunities_paid_compensation_tooltip_heading
import dev.nohus.rift.generated.resources.opportunities_paid_reward_tooltip_heading
import dev.nohus.rift.generated.resources.opportunities_participant_column
import dev.nohus.rift.generated.resources.opportunities_participants_heading
import dev.nohus.rift.generated.resources.opportunities_participation_accepted_status
import dev.nohus.rift.generated.resources.opportunities_participation_limit_tooltip
import dev.nohus.rift.generated.resources.opportunities_participation_removed_status
import dev.nohus.rift.generated.resources.opportunities_participation_resigned_status
import dev.nohus.rift.generated.resources.opportunities_participation_unspecified_status
import dev.nohus.rift.generated.resources.opportunities_payout_column
import dev.nohus.rift.generated.resources.opportunities_progress_heading
import dev.nohus.rift.generated.resources.opportunities_project_target_column
import dev.nohus.rift.generated.resources.opportunities_project_type_label
import dev.nohus.rift.generated.resources.opportunities_remaining_compensation_label
import dev.nohus.rift.generated.resources.opportunities_remaining_compensation_tooltip_heading
import dev.nohus.rift.generated.resources.opportunities_remaining_reward_label
import dev.nohus.rift.generated.resources.opportunities_remaining_reward_tooltip_heading
import dev.nohus.rift.generated.resources.opportunities_reward_per_contribution_label
import dev.nohus.rift.generated.resources.opportunities_state_column
import dev.nohus.rift.generated.resources.opportunities_target_value_column
import dev.nohus.rift.generated.resources.opportunities_total_amount_column
import dev.nohus.rift.generated.resources.opportunities_total_earnings_label
import dev.nohus.rift.generated.resources.opportunities_unknown_alliance_name
import dev.nohus.rift.generated.resources.opportunities_view_in_game_action
import dev.nohus.rift.generated.resources.calendar_16px
import dev.nohus.rift.generated.resources.checkmark_16px
import dev.nohus.rift.generated.resources.contribution_16px
import dev.nohus.rift.generated.resources.corporation_project_state_time_16px
import dev.nohus.rift.generated.resources.isk
import dev.nohus.rift.generated.resources.navigate_back_16px
import dev.nohus.rift.generated.resources.open_window_16px
import dev.nohus.rift.generated.resources.ratio_16px
import dev.nohus.rift.generated.resources.spaceship_command_16px
import dev.nohus.rift.generated.resources.stripes_tile
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.network.Result
import dev.nohus.rift.network.esi.models.OpportunityState
import dev.nohus.rift.network.esi.models.ParticipationState
import dev.nohus.rift.opportunities.AgeRequirement
import dev.nohus.rift.opportunities.Contributors
import dev.nohus.rift.opportunities.Corporation
import dev.nohus.rift.opportunities.Creator
import dev.nohus.rift.opportunities.GetOpportunityContributionAttributesUseCase.OpportunityContributionAttribute
import dev.nohus.rift.opportunities.OpportunitiesUtils
import dev.nohus.rift.opportunities.OpportunitiesUtils.getOpportunityCategory
import dev.nohus.rift.opportunities.OpportunitiesUtils.getOpportunityTypeMetadata
import dev.nohus.rift.opportunities.Opportunity
import dev.nohus.rift.opportunities.OpportunityCategoryFilter
import dev.nohus.rift.opportunities.OpportunityConfiguration
import dev.nohus.rift.opportunities.OpportunityType
import dev.nohus.rift.utils.formatDate
import dev.nohus.rift.utils.formatDateTime2
import dev.nohus.rift.utils.formatDuration
import dev.nohus.rift.utils.formatIsk
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.withColor
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun DetailsView(
    opportunity: Opportunity,
    onBackClick: () -> Unit,
    onCategoryFilterClick: (OpportunityCategoryFilter) -> Unit,
    onViewInGameClick: () -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.medium),
        ) {
            RiftImageButton(
                resource = Res.drawable.navigate_back_16px,
                size = 16.dp,
                tint = RiftTheme.colors.textPrimary,
                onClick = onBackClick,
            )

            Text(
                text = stringResource(Res.string.opportunities_details_title),
                style = RiftTheme.typography.detailSecondary,
            )
        }

        OpportunityStateInfo(opportunity.state)

        ProjectHeader(
            opportunity = opportunity,
            onCategoryFilterClick = onCategoryFilterClick,
            onViewInGameClick = onViewInGameClick,
            modifier = Modifier.sharedTransitionElement("card-${opportunity.id}"),
        )

        ScrollbarColumn(
            contentPadding = PaddingValues(start = Spacing.large, end = Spacing.medium, bottom = Spacing.large),
            hasScrollbarBackground = true,
            modifier = Modifier.padding(top = Spacing.large, end = Spacing.large, bottom = Spacing.large),
        ) {
            val metadata = getOpportunityTypeMetadata(opportunity)
            val progressColor = when (opportunity.state) {
                OpportunityState.Active, OpportunityState.Completed -> {
                    if (opportunity.currentProgress >= opportunity.desiredProgress) EveColors.successGreen else Color(0xFFA9DBE9)
                }
                OpportunityState.Closed -> EveColors.gunmetalGrey
                else -> EveColors.hotRed
            }
            val opportunityType = when (opportunity.type) {
                OpportunityType.CorporationProject -> stringResource(Res.string.opportunities_project_type_label)
                OpportunityType.FreelanceJob -> stringResource(Res.string.opportunities_job_type_label)
                OpportunityType.MercenaryTacticalOperation -> stringResource(Res.string.opportunities_operation_type_label)
            }

            EndDateRow(opportunity)

            if (opportunity.details.ageRequirement != null) {
                AgeRequirementBanners(opportunity.details.ageRequirement, opportunity.eligibleCharacters)
                Spacer(Modifier.height(Spacing.veryLarge))
            }

            if (opportunity.type in listOf(OpportunityType.CorporationProject, OpportunityType.FreelanceJob)) {
                ProgressSection(opportunity, progressColor, metadata, opportunityType)
                Spacer(Modifier.height(Spacing.veryLarge))
                YourContributionSection(opportunity, progressColor, metadata)
                Spacer(Modifier.height(Spacing.veryLarge))
                ParticipantsSection(opportunity)
                Spacer(Modifier.height(Spacing.veryLarge))
                ObjectivesSection(metadata, opportunity)
                Spacer(Modifier.height(Spacing.veryLarge))
            }
            if (opportunity.details.description.toPlainString().isNotEmpty()) {
                DescriptionSection(opportunity)
                Spacer(Modifier.height(Spacing.veryLarge))
            }
            if (opportunity.type == OpportunityType.MercenaryTacticalOperation) {
                MercenaryTacticalOperationSections(opportunity)
                Spacer(Modifier.height(Spacing.veryLarge))
            }
            CreatorSection(opportunity)
        }
    }
}

@Composable
private fun MercenaryTacticalOperationSections(
    opportunity: Opportunity,
) {
    val operation = opportunity.details.configuration as OpportunityConfiguration.MercenaryTacticalOperation
    TitledSection(stringResource(Res.string.opportunities_operational_intel_heading)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            opportunity.details.contributionAttributes.forEach { attribute ->
                OpportunityAttribute(
                    caption = attribute.name.resolve(),
                    icon = attribute.icon,
                    values = attribute.values,
                    tooltip = attribute.description.resolve(),
                )
            }
        }
    }
    Spacer(Modifier.height(Spacing.veryLarge))
    TitledSection(operation.archetypeTitle) {
        Text(
            text = operation.archetypeDescription,
            style = RiftTheme.typography.bodyPrimary,
        )
    }
}

@Composable
private fun CreatorSection(opportunity: Opportunity) {
    val title = if (opportunity.details.created != null) {
        stringResource(Res.string.opportunities_created_date_heading, formatDate(opportunity.details.created))
    } else {
        stringResource(Res.string.opportunities_creator_heading)
    }
    TitledSection(title) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(50.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Creator(opportunity.creator)
            if (opportunity.creator.corporation != null) {
                Corporation(opportunity.creator.corporation)
            }
        }
    }
}

@Composable
private fun DescriptionSection(opportunity: Opportunity) {
    TitledSection(title = stringResource(Res.string.opportunities_description_heading)) {
        LinkedText(
            text = opportunity.details.description,
            style = RiftTheme.typography.bodyPrimary,
        )
    }
}

@Composable
private fun ObjectivesSection(
    metadata: OpportunitiesUtils.OpportunityCategoryMetadata?,
    opportunity: Opportunity,
) {
    TitledSection(title = stringResource(Res.string.opportunities_objectives_heading)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            OpportunityAttribute(
                caption = stringResource(Res.string.opportunities_contribution_method_label),
                icon = metadata?.icon,
                values = listOfNotNull(
                    metadata?.name?.let {
                        OpportunityContributionAttribute.Text(
                            metadata.name,
                            isPlain = true,
                        )
                    },
                ),
                tooltip = metadata?.tooltip?.resolve(),
            )

            opportunity.details.contributionAttributes.forEach { attribute ->
                OpportunityAttribute(
                    caption = attribute.name.resolve(),
                    icon = attribute.icon,
                    values = attribute.values,
                    tooltip = attribute.description.resolve(),
                )
            }
        }
    }
}

@Composable
private fun ParticipantsSection(
    opportunity: Opportunity,
) {
    TitledSection(title = stringResource(Res.string.opportunities_participants_heading)) {
        when (val result = opportunity.contributors) {
            is Contributors.Available -> {
                when (opportunity.type) {
                    OpportunityType.CorporationProject -> {
                        val rows = result.contributors.map { contributor ->
                            val percent = contributor.contributed / opportunity.desiredProgress.toFloat()
                            val payout = contributor.contributed * (opportunity.details.rewardPerContribution ?: 0.0)
                            TableRow(
                                id = contributor.characterId.toString(),
                                cells = listOf(
                                    TextTableCell(contributor.details?.name ?: "${contributor.characterId}"),
                                    TextTableCell(
                                        formatNumber(contributor.contributed),
                                        sortingAmount = contributor.contributed.toDouble(),
                                    ),
                                    TextTableCell(
                                        String.format("%.1f%%", percent * 100),
                                        sortingAmount = percent.toDouble(),
                                    ),
                                    TextTableCell(formatIsk(payout, withCents = false), sortingAmount = payout),
                                ),
                                characterId = contributor.characterId,
                            )
                        }
                        RiftTable(
                            columns = listOf(stringResource(Res.string.opportunities_contributor_column), stringResource(Res.string.opportunities_total_amount_column), stringResource(Res.string.opportunities_project_target_column), stringResource(Res.string.opportunities_payout_column)),
                            rows = rows,
                            extraSpacing = 30.dp,
                            defaultSort = SortingColumn(1, Sort.Descending),
                        )
                    }

                    OpportunityType.FreelanceJob -> {
                        val rows = result.contributors.map { contributor ->
                            val percent = contributor.contributed / opportunity.desiredProgress.toFloat()
                            val payout = contributor.contributed * (opportunity.details.rewardPerContribution ?: 0.0)
                            TableRow(
                                id = contributor.characterId.toString(),
                                cells = listOf(
                                    RichTableCell(
                                        contributor.details?.name ?: "${contributor.characterId}",
                                        32.dp,
                                        120.dp,
                                    ) {
                                        ClickableCharacter(contributor.characterId) {
                                            CharacterTooltip(contributor.details) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                                ) {
                                                    AsyncCharacterPortrait(
                                                        characterId = contributor.characterId,
                                                        size = 32,
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape),
                                                    )
                                                    LinkText(
                                                        text = contributor.details?.name
                                                            ?: contributor.characterId.toString(),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Clip,
                                                        softWrap = false,
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    RichTableCell(
                                        contributor.details?.corporationName ?: "${contributor.details?.corporationId}",
                                        32.dp,
                                        120.dp,
                                    ) {
                                        if (contributor.details?.corporationId != null) {
                                            ClickableCorporation(contributor.details.corporationId) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                                ) {
                                                    AsyncCorporationLogo(
                                                        corporationId = contributor.details.corporationId,
                                                        size = 32,
                                                        modifier = Modifier
                                                            .size(24.dp),
                                                    )
                                                    LinkText(
                                                        text = contributor.details.corporationName,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Clip,
                                                        softWrap = false,
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(text = "-")
                                        }
                                    },
                                    RichTableCell(
                                        contributor.details?.allianceName ?: "${contributor.details?.allianceId}",
                                        32.dp,
                                        120.dp,
                                    ) {
                                        if (contributor.details?.allianceId != null) {
                                            ClickableAlliance(contributor.details.allianceId) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                                ) {
                                                    AsyncAllianceLogo(
                                                        allianceId = contributor.details.allianceId,
                                                        size = 32,
                                                        modifier = Modifier
                                                            .size(24.dp),
                                                    )
                                                    LinkText(
                                                        text = contributor.details.allianceName ?: stringResource(Res.string.opportunities_unknown_alliance_name),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Clip,
                                                        softWrap = false,
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(text = "-")
                                        }
                                    },
                                    TextTableCell(
                                        formatNumber(contributor.contributed),
                                        sortingAmount = contributor.contributed.toDouble(),
                                    ),
                                    TextTableCell(
                                        String.format("%.1f%%", percent * 100),
                                        sortingAmount = percent.toDouble(),
                                    ),
                                    TextTableCell(formatIsk(payout, withCents = false), sortingAmount = payout),
                                    RichTableCell(
                                        contributor.details?.allianceName ?: "${contributor.details?.allianceId}",
                                        32.dp,
                                        120.dp,
                                    ) {
                                        val [text, color] = when (contributor.participationState) {
                                            ParticipationState.Unspecified -> stringResource(Res.string.opportunities_participation_unspecified_status) to RiftTheme.colors.textSecondary
                                            ParticipationState.Commited -> stringResource(Res.string.opportunities_participation_accepted_status) to EveColors.successGreen
                                            ParticipationState.Kicked -> stringResource(Res.string.opportunities_participation_removed_status) to EveColors.dangerRed
                                            ParticipationState.Resigned -> stringResource(Res.string.opportunities_participation_resigned_status) to EveColors.warningOrange
                                        }
                                        Text(
                                            text = text,
                                            style = RiftTheme.typography.bodyPrimary.copy(color = color),
                                        )
                                    },
                                ),
                                characterId = null,
                            )
                        }
                        RiftTable(
                            columns = listOf(
                                stringResource(Res.string.opportunities_participant_column),
                                stringResource(Res.string.opportunities_corporation_column),
                                stringResource(Res.string.opportunities_alliance_column),
                                stringResource(Res.string.opportunities_total_amount_column),
                                stringResource(Res.string.opportunities_target_value_column),
                                stringResource(Res.string.opportunities_isk_payout_column),
                                stringResource(Res.string.opportunities_state_column),
                            ),
                            rows = rows,
                            extraSpacing = 30.dp,
                            defaultSort = SortingColumn(1, Sort.Descending),
                        )
                    }

                    OpportunityType.MercenaryTacticalOperation -> {
                        // MTO's don't have contributors
                    }
                }
            }

            Contributors.Empty -> Text(
                text = stringResource(Res.string.opportunities_contributions_empty_description),
                style = RiftTheme.typography.displaySecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
            )

            Contributors.NoAccess -> Text(
                text = stringResource(Res.string.opportunities_contributions_permission_description),
                style = RiftTheme.typography.bodySecondary,
            )

            is Contributors.Error -> Text(
                text = stringResource(Res.string.opportunities_contributors_error_description, result.message.resolve()),
                style = RiftTheme.typography.bodySecondary,
            )
        }
    }
}

@Composable
private fun YourContributionSection(
    opportunity: Opportunity,
    progressColor: Color,
    metadata: OpportunitiesUtils.OpportunityCategoryMetadata?,
) {
    TitledSection(
        title = stringResource(Res.string.opportunities_own_contribution_heading),
        tooltip = if (opportunity.details.participationLimit != null) {
            stringResource(Res.string.opportunities_participation_limit_tooltip, formatNumber(opportunity.details.participationLimit))
        } else {
            null
        },
    ) {
        opportunity.contributions.forEach { contribution ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(50.dp),
            ) {
                ProgressGauge(
                    progress = contribution.contribution.success ?: 0,
                    maxProgress = opportunity.details.participationLimit ?: opportunity.desiredProgress,
                    color = progressColor,
                    iconResource = null,
                    progressUnit = metadata?.progressUnit?.resolve(),
                    characterId = contribution.characterId,
                    characterName = contribution.characterName,
                    isIskProgress = opportunity.details.configuration is OpportunityConfiguration.ShipInsurance,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                ) {
                    if (contribution.contribution is Result.Success && opportunity.details.rewardPerContribution != null) {
                        if (opportunity.details.configuration is OpportunityConfiguration.ShipInsurance) {
                            if (opportunity.details.participationLimit != null) {
                                val availableProgress =
                                    opportunity.details.participationLimit - contribution.contribution.data
                                RewardInfo(
                                    text = formatIsk(
                                        availableProgress * opportunity.details.rewardPerContribution,
                                        withCents = false,
                                    ),
                                    caption = stringResource(Res.string.opportunities_remaining_compensation_label),
                                    icon = Res.drawable.isk,
                                )
                            }
                        } else {
                            if (opportunity.details.participationLimit != null) {
                                val availableProgress =
                                    opportunity.details.participationLimit - contribution.contribution.data
                                RewardInfo(
                                    text = formatIsk(
                                        availableProgress * opportunity.details.rewardPerContribution,
                                        withCents = false,
                                    ),
                                    caption = stringResource(Res.string.opportunities_available_reward_label),
                                    icon = Res.drawable.isk,
                                )
                            }

                            val totalEarnings =
                                contribution.contribution.data * opportunity.details.rewardPerContribution
                            contribution.contribution.success?.let {
                                RewardInfo(
                                    text = formatIsk(totalEarnings, withCents = false),
                                    caption = stringResource(Res.string.opportunities_total_earnings_label),
                                    icon = Res.drawable.checkmark_16px,
                                    tooltip = {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                                            modifier = Modifier
                                                .width(IntrinsicSize.Max)
                                                .padding(Spacing.large),
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.opportunities_own_earnings_tooltip_heading),
                                                style = RiftTheme.typography.detailSecondary,
                                            )
                                            Text(
                                                text = formatIsk(totalEarnings, withCents = true),
                                                style = RiftTheme.typography.bodyPrimary,
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        if (opportunity.contributions.isEmpty()) {
            Text(
                text = stringResource(Res.string.opportunities_not_participating_description),
                style = RiftTheme.typography.displaySecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ProgressSection(
    opportunity: Opportunity,
    progressColor: Color,
    metadata: OpportunitiesUtils.OpportunityCategoryMetadata?,
    opportunityType: String,
) {
    TitledSection(title = stringResource(Res.string.opportunities_progress_heading)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(50.dp),
        ) {
            ProgressGauge(
                progress = opportunity.currentProgress,
                maxProgress = opportunity.desiredProgress,
                color = progressColor,
                iconResource = metadata?.icon,
                progressUnit = metadata?.progressUnit?.resolve(),
                characterId = null,
                characterName = null,
                isIskProgress = opportunity.details.configuration is OpportunityConfiguration.ShipInsurance,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.large),
            ) {
                if (opportunity.details.configuration is OpportunityConfiguration.ShipInsurance) {
                    opportunity.reward?.let { reward ->
                        val tooltip: @Composable () -> Unit = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .padding(Spacing.large),
                            ) {
                                Text(
                                    text = stringResource(Res.string.opportunities_allocated_reward_tooltip_heading),
                                    style = RiftTheme.typography.detailSecondary,
                                )
                                Text(
                                    text = formatIsk(reward.initial, withCents = true),
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                                Text(
                                    text = stringResource(Res.string.opportunities_paid_compensation_tooltip_heading),
                                    style = RiftTheme.typography.detailSecondary,
                                )
                                Text(
                                    text = formatIsk(reward.initial - reward.remaining, withCents = true),
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                                Divider(
                                    color = RiftTheme.colors.textSecondary,
                                )
                                Text(
                                    text = stringResource(Res.string.opportunities_remaining_compensation_tooltip_heading),
                                    style = RiftTheme.typography.detailSecondary,
                                )
                                Text(
                                    text = formatIsk(reward.remaining, withCents = true),
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                            }
                        }
                        if (opportunity.details.submissionLimit != null) {
                            RewardInfo(
                                text = formatIsk(opportunity.details.submissionLimit, withCents = false),
                                caption = stringResource(Res.string.opportunities_coverage_limit_label),
                                icon = Res.drawable.spaceship_command_16px,
                                tooltip = tooltip,
                            )
                        } else {
                            RewardInfo(
                                text = formatIsk(reward.remaining, withCents = false),
                                caption = stringResource(Res.string.opportunities_remaining_reward_label, opportunityType),
                                icon = Res.drawable.spaceship_command_16px,
                                tooltip = tooltip,
                            )
                        }
                    }

                    if (opportunity.details.submissionMultiplier != null) {
                        RewardInfo(
                            text = String.format("%.0f%%", opportunity.details.submissionMultiplier * 100),
                            caption = stringResource(Res.string.opportunities_coverage_ratio_label),
                            icon = Res.drawable.ratio_16px,
                            tooltip = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                                    modifier = Modifier
                                        .width(IntrinsicSize.Max)
                                        .padding(Spacing.large),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.opportunities_coverage_ratio_tooltip),
                                        style = RiftTheme.typography.bodyPrimary,
                                    )
                                }
                            },
                        )
                    }
                } else {
                    opportunity.reward?.let { reward ->
                        RewardInfo(
                            text = formatIsk(reward.remaining, withCents = false),
                            caption = stringResource(Res.string.opportunities_remaining_reward_label, opportunityType),
                            icon = Res.drawable.isk,
                            tooltip = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                                    modifier = Modifier
                                        .width(IntrinsicSize.Max)
                                        .padding(Spacing.large),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.opportunities_allocated_reward_tooltip_heading),
                                        style = RiftTheme.typography.detailSecondary,
                                    )
                                    Text(
                                        text = formatIsk(reward.initial, withCents = true),
                                        style = RiftTheme.typography.bodyPrimary,
                                    )
                                    Text(
                                        text = stringResource(Res.string.opportunities_paid_reward_tooltip_heading),
                                        style = RiftTheme.typography.detailSecondary,
                                    )
                                    Text(
                                        text = formatIsk(reward.initial - reward.remaining, withCents = true),
                                        style = RiftTheme.typography.bodyPrimary,
                                    )
                                    Divider(
                                        color = RiftTheme.colors.textSecondary,
                                    )
                                    Text(
                                        text = stringResource(Res.string.opportunities_remaining_reward_tooltip_heading),
                                        style = RiftTheme.typography.detailSecondary,
                                    )
                                    Text(
                                        text = formatIsk(reward.remaining, withCents = true),
                                        style = RiftTheme.typography.bodyPrimary,
                                    )
                                }
                            },
                        )
                        opportunity.details.rewardPerContribution?.let {
                            RewardInfo(
                                text = formatIsk(it, withCents = false),
                                caption = stringResource(Res.string.opportunities_reward_per_contribution_label, metadata?.rewardPer?.resolve().toString()),
                                icon = Res.drawable.contribution_16px,
                            )
                            // TODO: Deliver jobs in-game shows value warnings / universal price %
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgeRequirementBanners(
    ageRequirement: AgeRequirement,
    characters: List<LocalCharacter>,
) {
    val (metCharacters, unmetCharacters) = remember(ageRequirement, characters) {
        val now = Instant.now()
        characters
            .filter { it.info != null }
            .partition {
                val ageDays = Duration.between(it.info!!.birthday, now).toDays()
                val isMinimumMet = ageRequirement.minimumAge == null || ageDays >= ageRequirement.minimumAge
                val isMaximumMet = ageRequirement.maximumAge == null || ageDays <= ageRequirement.maximumAge
                isMinimumMet && isMaximumMet
            }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        if (metCharacters.isNotEmpty()) {
            AgeRequirementBanner(ageRequirement, true, metCharacters)
        }
        if (unmetCharacters.isNotEmpty()) {
            AgeRequirementBanner(ageRequirement, false, unmetCharacters)
        }
    }
}

@Composable
private fun AgeRequirementBanner(
    ageRequirement: AgeRequirement,
    hasMetRequirements: Boolean,
    characters: List<LocalCharacter>,
) {
    val backgroundColor = if (hasMetRequirements) EveColors.copperOxideGreen.copy(alpha = 0.4f) else EveColors.cherryRed.copy(alpha = 0.4f)
    val headingColor = if (hasMetRequirements) EveColors.successGreen else EveColors.dangerRed
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(backgroundColor),
    ) {
        val stripesBitmap = imageResource(Res.drawable.stripes_tile)
        val brush = remember(stripesBitmap) { ShaderBrush(ImageShader(stripesBitmap, TileMode.Repeated, TileMode.Repeated)) }
        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
                .graphicsLayer {
                    colorFilter = BlendModeColorFilter(color = headingColor, blendMode = BlendMode.SrcIn)
                }
                .background(brush),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.padding(Spacing.medium).weight(2f),
        ) {
            val title = if (hasMetRequirements) stringResource(Res.string.opportunities_age_requirements_met_heading) else stringResource(Res.string.opportunities_age_requirements_unmet_heading)
            Text(
                text = title,
                style = RiftTheme.typography.bodyPrimary.copy(color = headingColor),
            )
            val minimumAge = ageRequirement.minimumAge?.let { pluralStringResource(Res.plurals.opportunities_minimum_age_clause, it, it) }
            val maximumAge = ageRequirement.maximumAge?.let { pluralStringResource(Res.plurals.opportunities_maximum_age_clause, it, it) }
            val text = when {
                minimumAge != null && maximumAge != null -> stringResource(Res.string.opportunities_age_range_description, minimumAge, maximumAge)
                minimumAge != null -> stringResource(Res.string.opportunities_minimum_age_description, minimumAge)
                maximumAge != null -> stringResource(Res.string.opportunities_maximum_age_description, maximumAge)
                else -> stringResource(Res.string.opportunities_age_unspecified_description)
            }
            Text(
                text = text,
                style = RiftTheme.typography.bodyHighlighted,
            )
        }

        CharacterStack(characters, Modifier.weight(1f))
    }
}

@Composable
private fun EndDateRow(opportunity: Opportunity) {
    if (opportunity.details.expires != null || opportunity.details.finished != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.height(IntrinsicSize.Min).wrapContentWidth(align = Alignment.Start, unbounded = true),
        ) {
            if (opportunity.details.expires != null && opportunity.state == OpportunityState.Active) {
                val expiresIn = Duration.between(getNow(), opportunity.details.expires).coerceAtLeast(Duration.ZERO)
                val iconColor = when {
                    expiresIn <= Duration.ofHours(6) -> EveColors.cherryRed
                    expiresIn <= Duration.ofDays(1) -> EveColors.duskyOrange
                    else -> RiftTheme.colors.textDisabled
                }
                val textColor = when {
                    expiresIn <= Duration.ofHours(6) -> EveColors.cherryRed
                    expiresIn <= Duration.ofDays(1) -> EveColors.duskyOrange
                    else -> RiftTheme.colors.textSecondary
                }
                val textHighlightColor = when {
                    expiresIn <= Duration.ofHours(6) -> EveColors.dangerRed
                    expiresIn <= Duration.ofDays(1) -> EveColors.warningOrange
                    else -> RiftTheme.colors.textPrimary
                }
                Image(
                    painter = painterResource(Res.drawable.corporation_project_state_time_16px),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(iconColor),
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.opportunities_expiry_prefix))
                        val style = RiftTheme.typography.bodySecondary.copy(color = textHighlightColor, fontWeight = FontWeight.Bold)
                        withStyle(style.toSpanStyle()) {
                            append(formatDuration(expiresIn).resolve())
                        }
                    },
                    style = RiftTheme.typography.bodySecondary.copy(color = textColor),
                )
                VerticalDivider(
                    color = RiftTheme.colors.textDisabled,
                    modifier = Modifier.padding(horizontal = Spacing.small),
                )
            }

            val finishDate = if (opportunity.state == OpportunityState.Active) {
                opportunity.details.expires ?: opportunity.details.finished
            } else {
                opportunity.details.finished
            }
            if (finishDate != null) {
                Image(
                    painter = painterResource(Res.drawable.calendar_16px),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(RiftTheme.colors.textDisabled),
                    modifier = Modifier.size(16.dp),
                )

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.opportunities_end_date_prefix))
                        withColor(RiftTheme.colors.textPrimary) {
                            append(formatDateTime2(finishDate))
                        }
                    },
                    style = RiftTheme.typography.bodySecondary,
                )
            }
        }
        Spacer(Modifier.height(Spacing.medium))
    }
}

@Composable
private fun ProgressGauge(
    progress: Long,
    maxProgress: Long,
    color: Color,
    iconResource: DrawableResource?,
    progressUnit: String?,
    characterId: Int?,
    characterName: String?,
    isIskProgress: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.large),
    ) {
        val progressPercent = progress / maxProgress.toFloat()
        CorporationProjectProgressGauge(
            progress = progressPercent,
            color = color,
            iconResource = iconResource,
            characterId = characterId,
            characterName = characterName,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            val suffix = if (isIskProgress) " ISK" else ""
            Text(
                text = "${formatNumber(progress)}$suffix",
                style = RiftTheme.typography.headerPrimary.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "/ ${formatNumber(maxProgress)}$suffix",
                style = RiftTheme.typography.detailPrimary,
            )
            Text(
                text = buildAnnotatedString {
                    append("${(progressPercent * 100).roundToInt()}%")
                    append(" ")
                    withColor(RiftTheme.colors.textSecondary) {
                        if (progressUnit != null) append(progressUnit)
                    }
                },
                style = RiftTheme.typography.detailPrimary,
            )
        }
    }
}

@Composable
private fun RewardInfo(
    text: String,
    caption: String,
    icon: DrawableResource,
    tooltip: @Composable (() -> Unit)? = null,
) {
    RiftTooltipArea(
        tooltip = tooltip,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CutCornerShape(bottomEnd = 8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .size(32.dp),
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = RiftTheme.colors.textSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column {
                Text(
                    text = text,
                    style = RiftTheme.typography.bodyPrimary,
                )
                Text(
                    text = caption,
                    style = RiftTheme.typography.detailSecondary,
                )
            }
        }
    }
}

@Composable
private fun ProjectHeader(
    opportunity: Opportunity,
    onCategoryFilterClick: (OpportunityCategoryFilter) -> Unit,
    onViewInGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pointerInteractionStateHolder = remember { PointerInteractionStateHolder() }
    Box(
        modifier = modifier
            .pointerInteraction(pointerInteractionStateHolder)
            .height(IntrinsicSize.Min),
    ) {
        Surface(
            color = RiftTheme.colors.backgroundPrimaryDark,
        ) {
            val category = getOpportunityCategory(opportunity)
            val alpha by animateFloatAsState(
                if (pointerInteractionStateHolder.isHovered) {
                    0.75f
                } else {
                    0.5f
                },
            )
            RiftOpportunityContainerFlair(
                isHovered = pointerInteractionStateHolder.isHovered,
                image = category.flair,
                offset = DpOffset(26.dp, 26.dp),
                alpha = alpha,
            )

            CorporationColorsSwatch(opportunity, pointerInteractionStateHolder)

            Column(
                modifier = Modifier.padding(Spacing.large),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = Spacing.medium),
                ) {
                    RiftOpportunityTypeIcon(category.icon, null)
                    Spacer(Modifier.width(Spacing.medium))
                    Text(
                        text = category.name.resolve(),
                        style = RiftTheme.typography.headerSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        softWrap = false,
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                            .fadingRightEdge(),
                    )
                }

                opportunity.details.solarSystemChipState?.let {
                    RiftSolarSystemChip(it, isOnlyShowingClosest = true)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.heightIn(max = 96.dp),
                ) {
                    Column(Modifier.weight(2f)) {
                        Spacer(Modifier.height(Spacing.large))
                        Text(
                            text = opportunity.name,
                            style = RiftTheme.typography.headlinePrimary.copy(fontWeight = FontWeight.Bold),
                        )
                        Spacer(Modifier.height(Spacing.large))
                    }
                    if (opportunity.creator.corporation != null) {
                        Box(Modifier.weight(1f)) {
                            ClickableCorporation(opportunity.creator.corporation.id) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.veryLarge),
                                ) {
                                    AsyncCorporationLogo(
                                        corporationId = opportunity.creator.corporation.id,
                                        size = 64,
                                        modifier = Modifier.size(64.dp),
                                    )
                                    Text(
                                        text = opportunity.creator.corporation.name,
                                        style = RiftTheme.typography.headlinePrimary,
                                    )
                                }
                            }
                        }
                    }
                }

                OpportunityCategoryFilterChips(
                    filters = opportunity.details.matchingFilters.sortedBy { it.order },
                    enabledFilters = emptySet(),
                    onCategoryFilterChange = { onCategoryFilterClick(it) },
                )

                Spacer(Modifier.height(Spacing.large))

                RiftButton(
                    text = stringResource(Res.string.opportunities_view_in_game_action),
                    icon = Res.drawable.open_window_16px,
                    cornerCut = ButtonCornerCut.Both,
                    onClick = onViewInGameClick,
                    modifier = Modifier.width(400.dp),
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CorporationColorsSwatch(
    opportunity: Opportunity,
    pointerInteractionStateHolder: PointerInteractionStateHolder,
) {
    if (opportunity.creator.corporation == null) return
    val colors by produceCorporationColors(opportunity.creator.corporation.id, opportunity.creator.corporation.palette)
    CorporationColorsSwatch(
        colors = colors,
        backgroundWidth = null,
        size = 96.dp,
        pointerInteractionStateHolder = pointerInteractionStateHolder,
    )
}

@Composable
private fun Creator(creator: Creator) {
    ClickableCharacter(creator.characterId) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.veryLarge),
        ) {
            RiftCircularCharacterPortrait(
                characterId = creator.characterId,
                characterDetails = creator.characterDetails,
                name = creator.characterName,
                hasPadding = false,
                size = 64.dp,
            )
            Column {
                Text(
                    text = stringResource(Res.string.opportunities_creator_heading),
                    style = RiftTheme.typography.bodySecondary,
                )
                LinkText(
                    text = creator.characterName,
                    normalStyle = RiftTheme.typography.headerPrimary.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    hoveredStyle = RiftTheme.typography.headerPrimary.copy(
                        color = RiftTheme.colors.textLink,
                        fontWeight = FontWeight.Bold,
                    ),
                    hasHoverCursor = false,
                )
            }
        }
    }
}

@Composable
private fun Corporation(corporation: Corporation) {
    ClickableCorporation(corporation.id) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.veryLarge),
        ) {
            AsyncCorporationLogo(
                corporationId = corporation.id,
                size = 64,
                modifier = Modifier.size(64.dp),
            )
            LinkText(
                text = corporation.name,
                normalStyle = RiftTheme.typography.headerPrimary.copy(
                    fontWeight = FontWeight.Bold,
                ),
                hoveredStyle = RiftTheme.typography.headerPrimary.copy(
                    color = RiftTheme.colors.textLink,
                    fontWeight = FontWeight.Bold,
                ),
                hasHoverCursor = false,
            )
        }
    }
}

@Composable
private fun TitledSection(
    title: String,
    tooltip: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Text(
                text = title,
                style = RiftTheme.typography.headlineSecondary,
            )
            if (tooltip != null) {
                RiftTooltipArea(tooltip) {
                    RiftMulticolorIcon(MulticolorIconType.Info)
                }
            }
        }
        content()
    }
}
