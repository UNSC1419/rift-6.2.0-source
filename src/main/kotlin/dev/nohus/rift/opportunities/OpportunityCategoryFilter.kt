package dev.nohus.rift.opportunities

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.careerpaths_enforcer_16px
import dev.nohus.rift.generated.resources.careerpaths_explorer_16px
import dev.nohus.rift.generated.resources.careerpaths_industrialist_16px
import dev.nohus.rift.generated.resources.careerpaths_soldier_of_fortune_16px
import dev.nohus.rift.generated.resources.corporation_management_16px
import dev.nohus.rift.generated.resources.flag_16px
import dev.nohus.rift.generated.resources.freelance_projects_16px
import dev.nohus.rift.generated.resources.mercenary_den_16px
import dev.nohus.rift.generated.resources.mining_16px
import dev.nohus.rift.generated.resources.opportunities_category_combat_description
import dev.nohus.rift.generated.resources.opportunities_category_combat_name
import dev.nohus.rift.generated.resources.opportunities_category_corporation_projects_description
import dev.nohus.rift.generated.resources.opportunities_category_corporation_projects_name
import dev.nohus.rift.generated.resources.opportunities_category_cosmic_signatures_description
import dev.nohus.rift.generated.resources.opportunities_category_cosmic_signatures_name
import dev.nohus.rift.generated.resources.opportunities_category_enforcer_description
import dev.nohus.rift.generated.resources.opportunities_category_enforcer_name
import dev.nohus.rift.generated.resources.opportunities_category_explorer_description
import dev.nohus.rift.generated.resources.opportunities_category_explorer_name
import dev.nohus.rift.generated.resources.opportunities_category_factional_warfare_description
import dev.nohus.rift.generated.resources.opportunities_category_factional_warfare_name
import dev.nohus.rift.generated.resources.opportunities_category_fleet_description
import dev.nohus.rift.generated.resources.opportunities_category_fleet_name
import dev.nohus.rift.generated.resources.opportunities_category_freelance_jobs_description
import dev.nohus.rift.generated.resources.opportunities_category_freelance_jobs_name
import dev.nohus.rift.generated.resources.opportunities_category_hauling_description
import dev.nohus.rift.generated.resources.opportunities_category_hauling_name
import dev.nohus.rift.generated.resources.opportunities_category_industrialist_description
import dev.nohus.rift.generated.resources.opportunities_category_industrialist_name
import dev.nohus.rift.generated.resources.opportunities_category_logistics_description
import dev.nohus.rift.generated.resources.opportunities_category_logistics_name
import dev.nohus.rift.generated.resources.opportunities_category_manufacturing_description
import dev.nohus.rift.generated.resources.opportunities_category_manufacturing_name
import dev.nohus.rift.generated.resources.opportunities_category_mercenary_tactical_operations_description
import dev.nohus.rift.generated.resources.opportunities_category_mercenary_tactical_operations_name
import dev.nohus.rift.generated.resources.opportunities_category_mining_description
import dev.nohus.rift.generated.resources.opportunities_category_mining_name
import dev.nohus.rift.generated.resources.opportunities_category_soldier_of_fortune_description
import dev.nohus.rift.generated.resources.opportunities_category_soldier_of_fortune_name
import dev.nohus.rift.generated.resources.opportunities_category_type_activity
import dev.nohus.rift.generated.resources.opportunities_category_type_career_path
import dev.nohus.rift.generated.resources.opportunities_category_type_feature
import dev.nohus.rift.generated.resources.pinpoint_probe_formation_32px
import dev.nohus.rift.generated.resources.sword_16px
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import org.jetbrains.compose.resources.DrawableResource

sealed class OpportunityCategoryFilterType(val name: UiText) {
    data object Feature : OpportunityCategoryFilterType(Res.string.opportunities_category_type_feature.toUiText())
    data object CareerPath : OpportunityCategoryFilterType(Res.string.opportunities_category_type_career_path.toUiText())
    data object Activity : OpportunityCategoryFilterType(Res.string.opportunities_category_type_activity.toUiText())
}

sealed class OpportunityCategoryFilter(
    val order: Int,
    val name: UiText,
    val type: OpportunityCategoryFilterType,
    val description: UiText,
    val icon: DrawableResource? = null,
) {
    data object CorporationProjects : OpportunityCategoryFilter(
        order = 0,
        name = Res.string.opportunities_category_corporation_projects_name.toUiText(),
        type = OpportunityCategoryFilterType.Feature,
        description = Res.string.opportunities_category_corporation_projects_description.toUiText(),
        icon = Res.drawable.corporation_management_16px,
    )

    data object FactionalWarfare : OpportunityCategoryFilter(
        order = 1,
        name = Res.string.opportunities_category_factional_warfare_name.toUiText(),
        type = OpportunityCategoryFilterType.Feature,
        description = Res.string.opportunities_category_factional_warfare_description.toUiText(),
        icon = Res.drawable.flag_16px,
    )

    data object FreelanceJobs : OpportunityCategoryFilter(
        order = 2,
        name = Res.string.opportunities_category_freelance_jobs_name.toUiText(),
        type = OpportunityCategoryFilterType.Feature,
        description = Res.string.opportunities_category_freelance_jobs_description.toUiText(),
        icon = Res.drawable.freelance_projects_16px,
    )

    data object MercenaryTacticalOperations : OpportunityCategoryFilter(
        order = 3,
        name = Res.string.opportunities_category_mercenary_tactical_operations_name.toUiText(),
        type = OpportunityCategoryFilterType.Feature,
        description = Res.string.opportunities_category_mercenary_tactical_operations_description.toUiText(),
        icon = Res.drawable.mercenary_den_16px,
    )

    data object Enforcer : OpportunityCategoryFilter(
        order = 4,
        name = Res.string.opportunities_category_enforcer_name.toUiText(),
        type = OpportunityCategoryFilterType.CareerPath,
        description = Res.string.opportunities_category_enforcer_description.toUiText(),
        icon = Res.drawable.careerpaths_enforcer_16px,
    )

    data object Explorer : OpportunityCategoryFilter(
        order = 5,
        name = Res.string.opportunities_category_explorer_name.toUiText(),
        type = OpportunityCategoryFilterType.CareerPath,
        description = Res.string.opportunities_category_explorer_description.toUiText(),
        icon = Res.drawable.careerpaths_explorer_16px,
    )

    data object Industrialist : OpportunityCategoryFilter(
        order = 6,
        name = Res.string.opportunities_category_industrialist_name.toUiText(),
        type = OpportunityCategoryFilterType.CareerPath,
        description = Res.string.opportunities_category_industrialist_description.toUiText(),
        icon = Res.drawable.careerpaths_industrialist_16px,
    )

    data object SoldierOfFortune : OpportunityCategoryFilter(
        order = 7,
        name = Res.string.opportunities_category_soldier_of_fortune_name.toUiText(),
        type = OpportunityCategoryFilterType.CareerPath,
        description = Res.string.opportunities_category_soldier_of_fortune_description.toUiText(),
        icon = Res.drawable.careerpaths_soldier_of_fortune_16px,
    )

    data object Combat : OpportunityCategoryFilter(
        order = 8,
        name = Res.string.opportunities_category_combat_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_combat_description.toUiText(),
        icon = Res.drawable.sword_16px,
    )

    data object CosmicSignatures : OpportunityCategoryFilter(
        order = 9,
        name = Res.string.opportunities_category_cosmic_signatures_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_cosmic_signatures_description.toUiText(),
        icon = Res.drawable.pinpoint_probe_formation_32px,
    )

    data object Fleet : OpportunityCategoryFilter(
        order = 10,
        name = Res.string.opportunities_category_fleet_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_fleet_description.toUiText(),
    )

    data object Hauling : OpportunityCategoryFilter(
        order = 11,
        name = Res.string.opportunities_category_hauling_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_hauling_description.toUiText(),
    )

    data object Logistics : OpportunityCategoryFilter(
        order = 13,
        name = Res.string.opportunities_category_logistics_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_logistics_description.toUiText(),
    )

    data object Manufacturing : OpportunityCategoryFilter(
        order = 14,
        name = Res.string.opportunities_category_manufacturing_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_manufacturing_description.toUiText(),
    )

    data object Mining : OpportunityCategoryFilter(
        order = 15,
        name = Res.string.opportunities_category_mining_name.toUiText(),
        type = OpportunityCategoryFilterType.Activity,
        description = Res.string.opportunities_category_mining_description.toUiText(),
        icon = Res.drawable.mining_16px,
    )
}
