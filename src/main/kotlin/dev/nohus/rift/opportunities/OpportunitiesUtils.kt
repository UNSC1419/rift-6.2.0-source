package dev.nohus.rift.opportunities

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import dev.nohus.rift.compose.RiftOpportunityCardCategory
import dev.nohus.rift.compose.RiftOpportunityCardType
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.contribution_method_attack_fw_complex_16px
import dev.nohus.rift.generated.resources.contribution_method_damage_ship_16px
import dev.nohus.rift.generated.resources.contribution_method_defend_fw_complex_16px
import dev.nohus.rift.generated.resources.contribution_method_deliver_item_16px
import dev.nohus.rift.generated.resources.contribution_method_destroy_npc_16px
import dev.nohus.rift.generated.resources.contribution_method_destroy_ship_16px
import dev.nohus.rift.generated.resources.contribution_method_earn_loyalty_point_16px
import dev.nohus.rift.generated.resources.contribution_method_lost_ship_16px
import dev.nohus.rift.generated.resources.contribution_method_manual_16px
import dev.nohus.rift.generated.resources.contribution_method_manufacture_item_16px
import dev.nohus.rift.generated.resources.contribution_method_mine_material_16px
import dev.nohus.rift.generated.resources.contribution_method_remote_boost_shields_16px
import dev.nohus.rift.generated.resources.contribution_method_remote_repair_armor_16px
import dev.nohus.rift.generated.resources.contribution_method_salvage_wreck_16px
import dev.nohus.rift.generated.resources.contribution_method_scan_signatures_16px
import dev.nohus.rift.generated.resources.contribution_method_ship_insurance_16px
import dev.nohus.rift.generated.resources.opportunities_career_enforcer_fallback_name
import dev.nohus.rift.generated.resources.opportunities_career_explorer_fallback_name
import dev.nohus.rift.generated.resources.opportunities_career_industrialist_fallback_name
import dev.nohus.rift.generated.resources.opportunities_career_soldier_of_fortune_fallback_name
import dev.nohus.rift.generated.resources.opportunities_career_unspecified_fallback_name
import dev.nohus.rift.generated.resources.opportunities_contribution_capture_fw_complex_description
import dev.nohus.rift.generated.resources.opportunities_contribution_capture_fw_complex_name
import dev.nohus.rift.generated.resources.opportunities_contribution_capture_fw_complex_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_capture_fw_complex_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_damage_ship_description
import dev.nohus.rift.generated.resources.opportunities_contribution_damage_ship_name
import dev.nohus.rift.generated.resources.opportunities_contribution_damage_ship_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_damage_ship_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_defend_fw_complex_description
import dev.nohus.rift.generated.resources.opportunities_contribution_defend_fw_complex_name
import dev.nohus.rift.generated.resources.opportunities_contribution_defend_fw_complex_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_defend_fw_complex_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_deliver_item_description
import dev.nohus.rift.generated.resources.opportunities_contribution_deliver_item_name
import dev.nohus.rift.generated.resources.opportunities_contribution_deliver_item_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_deliver_item_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_npc_description
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_npc_name
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_npc_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_npc_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_ship_description
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_ship_name
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_ship_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_destroy_ship_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_earn_loyalty_point_description
import dev.nohus.rift.generated.resources.opportunities_contribution_earn_loyalty_point_name
import dev.nohus.rift.generated.resources.opportunities_contribution_earn_loyalty_point_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_earn_loyalty_point_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_lost_ship_description
import dev.nohus.rift.generated.resources.opportunities_contribution_lost_ship_name
import dev.nohus.rift.generated.resources.opportunities_contribution_lost_ship_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_lost_ship_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_manual_description
import dev.nohus.rift.generated.resources.opportunities_contribution_manual_name
import dev.nohus.rift.generated.resources.opportunities_contribution_manual_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_manufacture_item_description
import dev.nohus.rift.generated.resources.opportunities_contribution_manufacture_item_name
import dev.nohus.rift.generated.resources.opportunities_contribution_manufacture_item_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_manufacture_item_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_mine_material_description
import dev.nohus.rift.generated.resources.opportunities_contribution_mine_material_name
import dev.nohus.rift.generated.resources.opportunities_contribution_mine_material_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_mine_material_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_boost_shield_description
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_boost_shield_name
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_boost_shield_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_boost_shield_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_repair_armor_description
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_repair_armor_name
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_repair_armor_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_remote_repair_armor_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_salvage_wreck_description
import dev.nohus.rift.generated.resources.opportunities_contribution_salvage_wreck_name
import dev.nohus.rift.generated.resources.opportunities_contribution_salvage_wreck_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_salvage_wreck_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_scan_signature_description
import dev.nohus.rift.generated.resources.opportunities_contribution_scan_signature_name
import dev.nohus.rift.generated.resources.opportunities_contribution_scan_signature_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_scan_signature_reward_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_ship_insurance_description
import dev.nohus.rift.generated.resources.opportunities_contribution_ship_insurance_name
import dev.nohus.rift.generated.resources.opportunities_contribution_ship_insurance_progress_unit
import dev.nohus.rift.generated.resources.opportunities_contribution_unknown_description
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.network.esi.models.BroadcastLocation
import dev.nohus.rift.network.esi.models.OpportunityCareer
import dev.nohus.rift.opportunities.GetOpportunityContributionAttributesUseCase.OpportunityContributionAttribute
import dev.nohus.rift.opportunities.GetOpportunityContributionAttributesUseCase.OpportunityContributionAttributeType
import dev.nohus.rift.repositories.GetSolarSystemChipStateUseCase
import dev.nohus.rift.repositories.SolarSystemChipLocation
import dev.nohus.rift.repositories.SolarSystemChipState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigInteger
import java.util.UUID

object OpportunitiesUtils {

    fun getOpportunityCategory(opportunity: Opportunity): RiftOpportunityCardCategory {
        return when (opportunity.details.career) {
            OpportunityCareer.Unspecified -> RiftOpportunityCardCategory.Unclassified
            OpportunityCareer.Explorer -> RiftOpportunityCardCategory.Explorer
            OpportunityCareer.Industrialist -> RiftOpportunityCardCategory.Industrialist
            OpportunityCareer.Enforcer -> RiftOpportunityCardCategory.Enforcer
            OpportunityCareer.SoldierOfFortune -> RiftOpportunityCardCategory.SoldierOfFortune
        }
    }

    data class OpportunityCategoryMetadata(
        val name: UiText,
        val icon: DrawableResource? = null,
        val tooltip: UiText? = null,
        val progressUnit: UiText?,
        val rewardPer: UiText?,
    )

    @Composable
    fun getOpportunityType(opportunity: Opportunity): RiftOpportunityCardType {
        val metadata = getOpportunityTypeMetadata(opportunity)
        return if (metadata != null) {
            RiftOpportunityCardType(
                text = AnnotatedString(metadata.name.resolve()),
                icon = metadata.icon,
                tooltip = metadata.tooltip?.resolve(),
            )
        } else {
            RiftOpportunityCardType(
                text = when (opportunity.details.career) {
                    OpportunityCareer.Unspecified -> stringResource(Res.string.opportunities_career_unspecified_fallback_name)
                    OpportunityCareer.Explorer -> stringResource(Res.string.opportunities_career_explorer_fallback_name)
                    OpportunityCareer.Industrialist -> stringResource(Res.string.opportunities_career_industrialist_fallback_name)
                    OpportunityCareer.Enforcer -> stringResource(Res.string.opportunities_career_enforcer_fallback_name)
                    OpportunityCareer.SoldierOfFortune -> stringResource(Res.string.opportunities_career_soldier_of_fortune_fallback_name)
                },
                icon = null,
                tooltip = null,
            )
        }
    }

    fun getOpportunityTypeMetadata(opportunity: Opportunity): OpportunityCategoryMetadata? {
        return when (val configuration = opportunity.details.configuration) {
            is OpportunityConfiguration.CaptureFwComplex -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_capture_fw_complex_name.toUiText(),
                icon = Res.drawable.contribution_method_attack_fw_complex_16px,
                tooltip = Res.string.opportunities_contribution_capture_fw_complex_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_capture_fw_complex_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_capture_fw_complex_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.DamageShip -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_damage_ship_name.toUiText(),
                icon = Res.drawable.contribution_method_damage_ship_16px,
                tooltip = Res.string.opportunities_contribution_damage_ship_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_damage_ship_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_damage_ship_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.DefendFwComplex -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_defend_fw_complex_name.toUiText(),
                icon = Res.drawable.contribution_method_defend_fw_complex_16px,
                tooltip = Res.string.opportunities_contribution_defend_fw_complex_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_defend_fw_complex_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_defend_fw_complex_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.DeliverItem -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_deliver_item_name.toUiText(),
                icon = Res.drawable.contribution_method_deliver_item_16px,
                tooltip = Res.string.opportunities_contribution_deliver_item_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_deliver_item_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_deliver_item_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.DestroyNpc -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_destroy_npc_name.toUiText(),
                icon = Res.drawable.contribution_method_destroy_npc_16px,
                tooltip = Res.string.opportunities_contribution_destroy_npc_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_destroy_npc_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_destroy_npc_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.DestroyShip -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_destroy_ship_name.toUiText(),
                icon = Res.drawable.contribution_method_destroy_ship_16px,
                tooltip = Res.string.opportunities_contribution_destroy_ship_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_destroy_ship_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_destroy_ship_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.EarnLoyaltyPoint -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_earn_loyalty_point_name.toUiText(),
                icon = Res.drawable.contribution_method_earn_loyalty_point_16px,
                tooltip = Res.string.opportunities_contribution_earn_loyalty_point_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_earn_loyalty_point_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_earn_loyalty_point_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.LostShip -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_lost_ship_name.toUiText(),
                icon = Res.drawable.contribution_method_lost_ship_16px,
                tooltip = Res.string.opportunities_contribution_lost_ship_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_lost_ship_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_lost_ship_reward_unit.toUiText(),
            )

            OpportunityConfiguration.Manual -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_manual_name.toUiText(),
                icon = Res.drawable.contribution_method_manual_16px,
                tooltip = Res.string.opportunities_contribution_manual_description.toUiText(),
                progressUnit = null,
                rewardPer = Res.string.opportunities_contribution_manual_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.ManufactureItem -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_manufacture_item_name.toUiText(),
                icon = Res.drawable.contribution_method_manufacture_item_16px,
                tooltip = Res.string.opportunities_contribution_manufacture_item_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_manufacture_item_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_manufacture_item_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.MineMaterial -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_mine_material_name.toUiText(),
                icon = Res.drawable.contribution_method_mine_material_16px,
                tooltip = Res.string.opportunities_contribution_mine_material_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_mine_material_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_mine_material_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.RemoteBoostShield -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_remote_boost_shield_name.toUiText(),
                icon = Res.drawable.contribution_method_remote_boost_shields_16px,
                tooltip = Res.string.opportunities_contribution_remote_boost_shield_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_remote_boost_shield_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_remote_boost_shield_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.RemoteRepairArmor -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_remote_repair_armor_name.toUiText(),
                icon = Res.drawable.contribution_method_remote_repair_armor_16px,
                tooltip = Res.string.opportunities_contribution_remote_repair_armor_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_remote_repair_armor_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_remote_repair_armor_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.SalvageWreck -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_salvage_wreck_name.toUiText(),
                icon = Res.drawable.contribution_method_salvage_wreck_16px,
                tooltip = Res.string.opportunities_contribution_salvage_wreck_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_salvage_wreck_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_salvage_wreck_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.ScanSignature -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_scan_signature_name.toUiText(),
                icon = Res.drawable.contribution_method_scan_signatures_16px,
                tooltip = Res.string.opportunities_contribution_scan_signature_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_scan_signature_progress_unit.toUiText(),
                rewardPer = Res.string.opportunities_contribution_scan_signature_reward_unit.toUiText(),
            )

            is OpportunityConfiguration.ShipInsurance -> OpportunityCategoryMetadata(
                name = Res.string.opportunities_contribution_ship_insurance_name.toUiText(),
                icon = Res.drawable.contribution_method_ship_insurance_16px,
                tooltip = Res.string.opportunities_contribution_ship_insurance_description.toUiText(),
                progressUnit = Res.string.opportunities_contribution_ship_insurance_progress_unit.toUiText(),
                rewardPer = null,
            )

            is OpportunityConfiguration.Unknown -> OpportunityCategoryMetadata(
                name = configuration.type,
                tooltip = Res.string.opportunities_contribution_unknown_description.toUiText(),
                progressUnit = null,
                rewardPer = null,
            )

            is OpportunityConfiguration.MercenaryTacticalOperation -> null
        }
    }

    fun getMatchingFilters(
        baseType: OpportunityCategoryFilter,
        career: OpportunityCareer,
        configuration: OpportunityConfiguration?,
    ): List<OpportunityCategoryFilter> {
        return buildList {
            add(baseType)

            career.let {
                when (it) {
                    OpportunityCareer.Explorer -> OpportunityCategoryFilter.Explorer
                    OpportunityCareer.Industrialist -> OpportunityCategoryFilter.Industrialist
                    OpportunityCareer.Enforcer -> OpportunityCategoryFilter.Enforcer
                    OpportunityCareer.SoldierOfFortune -> OpportunityCategoryFilter.SoldierOfFortune
                    else -> null
                }
            }?.let { add(it) }

            when (configuration) {
                is OpportunityConfiguration.CaptureFwComplex -> listOf(OpportunityCategoryFilter.FactionalWarfare, OpportunityCategoryFilter.Combat)
                is OpportunityConfiguration.DamageShip -> listOf(OpportunityCategoryFilter.Combat)
                is OpportunityConfiguration.DefendFwComplex -> listOf(OpportunityCategoryFilter.FactionalWarfare, OpportunityCategoryFilter.Combat)
                is OpportunityConfiguration.DeliverItem -> listOf(OpportunityCategoryFilter.Hauling)
                is OpportunityConfiguration.DestroyNpc -> listOf(OpportunityCategoryFilter.Combat)
                is OpportunityConfiguration.DestroyShip -> listOf(OpportunityCategoryFilter.Combat)
                is OpportunityConfiguration.EarnLoyaltyPoint -> listOf()
                is OpportunityConfiguration.LostShip -> listOf(OpportunityCategoryFilter.Combat, OpportunityCategoryFilter.Fleet, OpportunityCategoryFilter.Logistics)
                OpportunityConfiguration.Manual -> listOf()
                is OpportunityConfiguration.ManufactureItem -> listOf(OpportunityCategoryFilter.Manufacturing)
                is OpportunityConfiguration.MineMaterial -> listOf(OpportunityCategoryFilter.Mining)
                is OpportunityConfiguration.RemoteBoostShield -> listOf(OpportunityCategoryFilter.Combat, OpportunityCategoryFilter.Fleet, OpportunityCategoryFilter.Logistics)
                is OpportunityConfiguration.RemoteRepairArmor -> listOf(OpportunityCategoryFilter.Combat, OpportunityCategoryFilter.Fleet, OpportunityCategoryFilter.Logistics)
                is OpportunityConfiguration.SalvageWreck -> listOf()
                is OpportunityConfiguration.ScanSignature -> listOf(OpportunityCategoryFilter.CosmicSignatures)
                is OpportunityConfiguration.ShipInsurance -> listOf(OpportunityCategoryFilter.Combat, OpportunityCategoryFilter.Fleet, OpportunityCategoryFilter.Logistics)
                is OpportunityConfiguration.MercenaryTacticalOperation -> listOf(OpportunityCategoryFilter.Combat)
                is OpportunityConfiguration.Unknown -> listOf()
                null -> listOf()
            }.let { addAll(it) }
        }
    }

    fun getSolarSystemChipState(
        getSolarSystemChipStateUseCase: GetSolarSystemChipStateUseCase,
        contributionAttributes: List<OpportunityContributionAttributeType>,
        broadcastLocations: List<BroadcastLocation>? = null,
    ): SolarSystemChipState? {
        val attributeLocations = contributionAttributes.flatMap { it.values }.mapNotNull { value ->
            when (value) {
                is OpportunityContributionAttribute.SolarSystem -> SolarSystemChipLocation.SolarSystem(value.solarSystem.id)
                is OpportunityContributionAttribute.Constellation -> SolarSystemChipLocation.Constellation(value.constellation.id)
                is OpportunityContributionAttribute.Region -> SolarSystemChipLocation.Region(value.region.id)
                is OpportunityContributionAttribute.Station -> value.solarSystem?.id?.let { SolarSystemChipLocation.SolarSystem(it) }
                is OpportunityContributionAttribute.Structure -> value.solarSystem?.id?.let { SolarSystemChipLocation.SolarSystem(it) }
                else -> null
            }
        }
        val broadcastLocations = broadcastLocations?.map {
            SolarSystemChipLocation.SolarSystem(it.id.toInt())
        }
        val solarSystemChipLocations = attributeLocations + broadcastLocations.orEmpty()
        if (solarSystemChipLocations.isEmpty()) return null
        return getSolarSystemChipStateUseCase(solarSystemChipLocations)
    }

    fun uuidToInt128(uuid: String): String {
        val uuid = UUID.fromString(uuid)
        val mostSigBits = uuid.mostSignificantBits
        val leastSigBits = uuid.leastSignificantBits

        // Create an unsigned mask (2^64 - 1 using BigInteger)
        @Suppress("SpellCheckingInspection")
        val mask = BigInteger("FFFFFFFFFFFFFFFF", 16)

        // Apply the mask to convert signed long to unsigned BigInteger
        val high = BigInteger.valueOf(mostSigBits).and(mask)
        val low = BigInteger.valueOf(leastSigBits).and(mask)

        // Combine the high and low bits into a 128-bit integer
        return high.shiftLeft(64).or(low).toString()
    }

    fun int128ToUuid(int128: String): String {
        // Parse the 128-bit integer from the string
        val bigInt = BigInteger(int128)

        // Split into the most significant and least significant bits
        @Suppress("SpellCheckingInspection")
        val mask = BigInteger("FFFFFFFFFFFFFFFF", 16)
        val leastSigBits = bigInt.and(mask).toLong() // Extract lower 64 bits
        val mostSigBits = bigInt.shiftRight(64).and(mask).toLong() // Extract upper 64 bits

        // Create the UUID from the most and least significant bits
        return UUID(mostSigBits, leastSigBits).toString()
    }
}
