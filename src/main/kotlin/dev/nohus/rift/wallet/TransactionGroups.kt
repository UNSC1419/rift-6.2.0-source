package dev.nohus.rift.wallet

import androidx.compose.ui.graphics.Color
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.wallet_reference_type_acceleration_gate_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_advertisement_listing_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_donation
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_location_services
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_miscellaneous
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_mission_collateral_paid
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_mission_collateral_refunded
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_mission_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_mission_reward_corporation_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_mission_time_bonus_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_mission_time_bonus_reward_corporation_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_security_services
import dev.nohus.rift.generated.resources.wallet_reference_type_agent_services_rendered
import dev.nohus.rift.generated.resources.wallet_reference_type_agents_preward
import dev.nohus.rift.generated.resources.wallet_reference_type_air_career_program_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_alignment_based_gate_toll
import dev.nohus.rift.generated.resources.wallet_reference_type_alliance_maintenance_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_alliance_registration_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_asset_safety_recovery_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_bounty
import dev.nohus.rift.generated.resources.wallet_reference_type_bounty_prize
import dev.nohus.rift.generated.resources.wallet_reference_type_bounty_prize_corporation_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_bounty_prizes
import dev.nohus.rift.generated.resources.wallet_reference_type_bounty_reimbursement
import dev.nohus.rift.generated.resources.wallet_reference_type_bounty_surcharge
import dev.nohus.rift.generated.resources.wallet_reference_type_brokers_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_clone_activation
import dev.nohus.rift.generated.resources.wallet_reference_type_clone_transfer
import dev.nohus.rift.generated.resources.wallet_reference_type_contraband_fine
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_auction_bid
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_auction_bid_corp
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_auction_bid_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_auction_sold
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_brokers_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_brokers_fee_corp
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_collateral
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_collateral_deposited_corp
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_collateral_payout
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_collateral_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_deposit
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_deposit_corp
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_deposit_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_deposit_sales_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_price
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_price_payment_corp
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_reversal
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_reward_deposited
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_reward_deposited_corp
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_reward_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_contract_sales_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_copying
import dev.nohus.rift.generated.resources.wallet_reference_type_corporate_reward_payout
import dev.nohus.rift.generated.resources.wallet_reference_type_corporate_reward_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_account_withdrawal
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_bulk_payment
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_dividend_payment
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_liquidation
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_logo_change_cost
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_payment
import dev.nohus.rift.generated.resources.wallet_reference_type_corporation_registration_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_cosmetic_market_component_item_purchase
import dev.nohus.rift.generated.resources.wallet_reference_type_cosmetic_market_skin_purchase
import dev.nohus.rift.generated.resources.wallet_reference_type_cosmetic_market_skin_sale
import dev.nohus.rift.generated.resources.wallet_reference_type_cosmetic_market_skin_sale_broker_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_cosmetic_market_skin_sale_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_cosmetic_market_skin_transaction
import dev.nohus.rift.generated.resources.wallet_reference_type_courier_mission_escrow
import dev.nohus.rift.generated.resources.wallet_reference_type_cspa
import dev.nohus.rift.generated.resources.wallet_reference_type_cspa_offline_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_daily_challenge_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_daily_goal_payouts
import dev.nohus.rift.generated.resources.wallet_reference_type_daily_goal_payouts_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_datacore_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_dna_modification_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_docking_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_duel_wager_escrow
import dev.nohus.rift.generated.resources.wallet_reference_type_duel_wager_payment
import dev.nohus.rift.generated.resources.wallet_reference_type_duel_wager_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_ess_escrow_transfer
import dev.nohus.rift.generated.resources.wallet_reference_type_external_trade_delivery
import dev.nohus.rift.generated.resources.wallet_reference_type_external_trade_freeze
import dev.nohus.rift.generated.resources.wallet_reference_type_external_trade_thaw
import dev.nohus.rift.generated.resources.wallet_reference_type_factory_slot_rental_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_flux_payout
import dev.nohus.rift.generated.resources.wallet_reference_type_flux_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_flux_ticket_repayment
import dev.nohus.rift.generated.resources.wallet_reference_type_flux_ticket_sale
import dev.nohus.rift.generated.resources.wallet_reference_type_freelance_jobs_broadcasting_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_freelance_jobs_duration_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_freelance_jobs_escrow_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_freelance_jobs_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_freelance_jobs_reward_corporation_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_freelance_jobs_reward_escrow
import dev.nohus.rift.generated.resources.wallet_reference_type_gm_cash_transfer
import dev.nohus.rift.generated.resources.wallet_reference_type_gm_plex_fee_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_industry_job_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_infrastructure_hub_maintenance
import dev.nohus.rift.generated.resources.wallet_reference_type_inheritance
import dev.nohus.rift.generated.resources.wallet_reference_type_insurance
import dev.nohus.rift.generated.resources.wallet_reference_type_insurgency_corruption_contribution_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_insurgency_suppression_contribution_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_item_trader_payment
import dev.nohus.rift.generated.resources.wallet_reference_type_jump_clone_activation_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_jump_clone_installation_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_kill_right_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_lp_store
import dev.nohus.rift.generated.resources.wallet_reference_type_manufacturing
import dev.nohus.rift.generated.resources.wallet_reference_type_market_escrow
import dev.nohus.rift.generated.resources.wallet_reference_type_market_fine_paid
import dev.nohus.rift.generated.resources.wallet_reference_type_market_provider_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_market_transaction
import dev.nohus.rift.generated.resources.wallet_reference_type_medal_creation
import dev.nohus.rift.generated.resources.wallet_reference_type_medal_issued
import dev.nohus.rift.generated.resources.wallet_reference_type_milestone_reward_payment
import dev.nohus.rift.generated.resources.wallet_reference_type_mission_completion
import dev.nohus.rift.generated.resources.wallet_reference_type_mission_cost
import dev.nohus.rift.generated.resources.wallet_reference_type_mission_expiration
import dev.nohus.rift.generated.resources.wallet_reference_type_mission_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_office_rental_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_operation_bonus
import dev.nohus.rift.generated.resources.wallet_reference_type_opportunity_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_planetary_construction
import dev.nohus.rift.generated.resources.wallet_reference_type_planetary_export_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_planetary_import_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_player_donation
import dev.nohus.rift.generated.resources.wallet_reference_type_player_trading
import dev.nohus.rift.generated.resources.wallet_reference_type_project_discovery_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_project_discovery_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_project_payouts
import dev.nohus.rift.generated.resources.wallet_reference_type_reaction
import dev.nohus.rift.generated.resources.wallet_reference_type_redeemed_isk_token
import dev.nohus.rift.generated.resources.wallet_reference_type_release_of_impounded_property
import dev.nohus.rift.generated.resources.wallet_reference_type_repair_bill
import dev.nohus.rift.generated.resources.wallet_reference_type_reprocessing_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_researching_material_productivity
import dev.nohus.rift.generated.resources.wallet_reference_type_researching_technology
import dev.nohus.rift.generated.resources.wallet_reference_type_researching_time_productivity
import dev.nohus.rift.generated.resources.wallet_reference_type_resource_wars_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_reverse_engineering
import dev.nohus.rift.generated.resources.wallet_reference_type_season_challenge_reward
import dev.nohus.rift.generated.resources.wallet_reference_type_security_processing_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_shares
import dev.nohus.rift.generated.resources.wallet_reference_type_skill_purchase
import dev.nohus.rift.generated.resources.wallet_reference_type_skyhook_claim_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_sovereignty_bill
import dev.nohus.rift.generated.resources.wallet_reference_type_store_purchase
import dev.nohus.rift.generated.resources.wallet_reference_type_store_purchase_refund
import dev.nohus.rift.generated.resources.wallet_reference_type_structure_gate_jump
import dev.nohus.rift.generated.resources.wallet_reference_type_transaction_tax
import dev.nohus.rift.generated.resources.wallet_reference_type_under_construction
import dev.nohus.rift.generated.resources.wallet_reference_type_upkeep_adjustment_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_war_ally_contract
import dev.nohus.rift.generated.resources.wallet_reference_type_war_fee
import dev.nohus.rift.generated.resources.wallet_reference_type_war_fee_surrender
import dev.nohus.rift.generated.resources.wallet_transaction_group_agents_missions
import dev.nohus.rift.generated.resources.wallet_transaction_group_bounties
import dev.nohus.rift.generated.resources.wallet_transaction_group_corporation_alliance
import dev.nohus.rift.generated.resources.wallet_transaction_group_hypernet
import dev.nohus.rift.generated.resources.wallet_transaction_group_industry
import dev.nohus.rift.generated.resources.wallet_transaction_group_miscellaneous
import dev.nohus.rift.generated.resources.wallet_transaction_group_trade
import dev.nohus.rift.generated.resources.wallet_transaction_group_transfers
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText

sealed class TransactionGroup(
    val name: UiText,
    val color: Color,
) {
    data object CorporationAlliance : TransactionGroup(name = Res.string.wallet_transaction_group_corporation_alliance.toUiText(), color = Color(0xFF5c59d8))
    data object AgentsAndMissions : TransactionGroup(name = Res.string.wallet_transaction_group_agents_missions.toUiText(), color = Color(0xFFe68348))
    data object Trade : TransactionGroup(name = Res.string.wallet_transaction_group_trade.toUiText(), color = Color(0xFF31c4a1))
    data object Bounty : TransactionGroup(name = Res.string.wallet_transaction_group_bounties.toUiText(), color = Color(0xFFe53a3a))
    data object Industry : TransactionGroup(name = Res.string.wallet_transaction_group_industry.toUiText(), color = Color(0xFF3de53a))
    data object Transfer : TransactionGroup(name = Res.string.wallet_transaction_group_transfers.toUiText(), color = Color(0xFFe6e048))
    data object Misc : TransactionGroup(name = Res.string.wallet_transaction_group_miscellaneous.toUiText(), color = Color(0xFF808080))
    data object HypernetRelay : TransactionGroup(name = Res.string.wallet_transaction_group_hypernet.toUiText(), color = Color(0xFF3aa8e5))

    companion object {
        fun byReferenceType(referenceType: String) = when (referenceType) {
            "acceleration_gate_fee" -> Misc
            "advertisement_listing_fee" -> CorporationAlliance
            "agent_donation" -> AgentsAndMissions
            "agent_location_services" -> AgentsAndMissions
            "agent_miscellaneous" -> AgentsAndMissions
            "agent_mission_collateral_paid" -> AgentsAndMissions
            "agent_mission_collateral_refunded" -> AgentsAndMissions
            "agent_mission_reward" -> AgentsAndMissions
            "agent_mission_reward_corporation_tax" -> CorporationAlliance
            "agent_mission_time_bonus_reward" -> AgentsAndMissions
            "agent_mission_time_bonus_reward_corporation_tax" -> CorporationAlliance
            "agent_security_services" -> AgentsAndMissions
            "agent_services_rendered" -> AgentsAndMissions
            "agents_preward" -> AgentsAndMissions
            "air_career_program_reward" -> AgentsAndMissions
            "alliance_maintainance_fee" -> CorporationAlliance
            "alliance_registration_fee" -> CorporationAlliance
            "allignment_based_gate_toll" -> Misc
            "asset_safety_recovery_tax" -> Misc
            "bounty" -> Bounty
            "bounty_prize" -> Bounty
            "bounty_prize_corporation_tax" -> Bounty
            "bounty_prizes" -> Bounty
            "bounty_reimbursement" -> Bounty
            "bounty_surcharge" -> Bounty
            "brokers_fee" -> Trade
            "clone_activation" -> Misc
            "clone_transfer" -> Misc
            "contraband_fine" -> Misc
            "contract_auction_bid" -> Trade
            "contract_auction_bid_corp" -> Trade
            "contract_auction_bid_refund" -> Trade
            "contract_auction_sold" -> Trade
            "contract_brokers_fee" -> Trade
            "contract_brokers_fee_corp" -> Trade
            "contract_collateral" -> Trade
            "contract_collateral_deposited_corp" -> Trade
            "contract_collateral_payout" -> Trade
            "contract_collateral_refund" -> Trade
            "contract_deposit" -> Trade
            "contract_deposit_corp" -> Trade
            "contract_deposit_refund" -> Trade
            "contract_deposit_sales_tax" -> Trade
            "contract_price" -> Trade
            "contract_price_payment_corp" -> Trade
            "contract_reversal" -> Trade
            "contract_reward" -> Trade
            "contract_reward_deposited" -> Trade
            "contract_reward_deposited_corp" -> Trade
            "contract_reward_refund" -> Trade
            "contract_sales_tax" -> Trade
            "copying" -> Industry
            "corporate_reward_payout" -> CorporationAlliance
            "corporate_reward_tax" -> CorporationAlliance
            "corporation_account_withdrawal" -> CorporationAlliance
            "corporation_bulk_payment" -> CorporationAlliance
            "corporation_dividend_payment" -> CorporationAlliance
            "corporation_liquidation" -> CorporationAlliance
            "corporation_logo_change_cost" -> CorporationAlliance
            "corporation_payment" -> CorporationAlliance
            "corporation_registration_fee" -> CorporationAlliance
            "cosmetic_market_component_item_purchase" -> Trade
            "cosmetic_market_skin_purchase" -> Trade
            "cosmetic_market_skin_sale" -> Trade
            "cosmetic_market_skin_sale_broker_fee" -> Trade
            "cosmetic_market_skin_sale_tax" -> Trade
            "cosmetic_market_skin_transaction" -> Trade
            "courier_mission_escrow" -> AgentsAndMissions
            "cspa" -> Misc
            "cspaofflinerefund" -> Misc
            "daily_challenge_reward" -> AgentsAndMissions
            "daily_goal_payouts" -> AgentsAndMissions
            "daily_goal_payouts_tax" -> AgentsAndMissions
            "datacore_fee" -> Industry
            "dna_modification_fee" -> Misc
            "docking_fee" -> Misc
            "duel_wager_escrow" -> Misc
            "duel_wager_payment" -> Misc
            "duel_wager_refund" -> Misc
            "ess_escrow_transfer" -> Bounty
            "external_trade_delivery" -> Trade
            "external_trade_freeze" -> Trade
            "external_trade_thaw" -> Trade
            "factory_slot_rental_fee" -> Industry
            "flux_payout" -> HypernetRelay
            "flux_tax" -> HypernetRelay
            "flux_ticket_repayment" -> HypernetRelay
            "flux_ticket_sale" -> HypernetRelay
            "freelance_jobs_broadcasting_fee" -> Misc
            "freelance_jobs_duration_fee" -> Misc
            "freelance_jobs_escrow_refund" -> Misc
            "freelance_jobs_reward" -> Misc
            "freelance_jobs_reward_corporation_tax" -> Misc
            "freelance_jobs_reward_escrow" -> Misc
            "gm_cash_transfer" -> Misc
            "gm_plex_fee_refund" -> Misc
            "industry_job_tax" -> Industry
            "infrastructure_hub_maintenance" -> CorporationAlliance
            "inheritance" -> Misc
            "insurance" -> Misc
            "insurgency_corruption_contribution_reward" -> Misc
            "insurgency_suppression_contribution_reward" -> Misc
            "item_trader_payment" -> Trade
            "jump_clone_activation_fee" -> Misc
            "jump_clone_installation_fee" -> Misc
            "kill_right_fee" -> Misc
            "lp_store" -> Trade
            "manufacturing" -> Industry
            "market_escrow" -> Trade
            "market_fine_paid" -> Trade
            "market_provider_tax" -> Trade
            "market_transaction" -> Trade
            "medal_creation" -> CorporationAlliance
            "medal_issued" -> CorporationAlliance
            "milestone_reward_payment" -> Misc
            "mission_completion" -> AgentsAndMissions
            "mission_cost" -> AgentsAndMissions
            "mission_expiration" -> AgentsAndMissions
            "mission_reward" -> AgentsAndMissions
            "office_rental_fee" -> CorporationAlliance
            "operation_bonus" -> Misc
            "opportunity_reward" -> Misc
            "planetary_construction" -> Industry
            "planetary_export_tax" -> Industry
            "planetary_import_tax" -> Industry
            "player_donation" -> Transfer
            "player_trading" -> Trade
            "project_discovery_reward" -> Misc
            "project_discovery_tax" -> Misc
            "project_payouts" -> Misc
            "reaction" -> Industry
            "redeemed_isk_token" -> Misc
            "release_of_impounded_property" -> Misc
            "repair_bill" -> Misc
            "reprocessing_tax" -> Misc
            "researching_material_productivity" -> Industry
            "researching_technology" -> Industry
            "researching_time_productivity" -> Industry
            "resource_wars_reward" -> Misc
            "reverse_engineering" -> Industry
            "season_challenge_reward" -> Misc
            "security_processing_fee" -> Misc
            "shares" -> CorporationAlliance
            "skill_purchase" -> Trade
            "skyhook_claim_fee" -> CorporationAlliance
            "sovereignity_bill" -> CorporationAlliance
            "store_purchase" -> Trade
            "store_purchase_refund" -> Trade
            "structure_gate_jump" -> Misc
            "transaction_tax" -> Trade
            "under_construction" -> Misc
            "upkeep_adjustment_fee" -> Misc
            "war_ally_contract" -> CorporationAlliance
            "war_fee" -> CorporationAlliance
            "war_fee_surrender" -> CorporationAlliance
            else -> Misc
        }
    }
}

fun getReferenceTypeName(referenceType: String): UiText {
    return when (referenceType) {
        "acceleration_gate_fee" -> Res.string.wallet_reference_type_acceleration_gate_fee.toUiText()
        "advertisement_listing_fee" -> Res.string.wallet_reference_type_advertisement_listing_fee.toUiText()
        "agent_donation" -> Res.string.wallet_reference_type_agent_donation.toUiText()
        "agent_location_services" -> Res.string.wallet_reference_type_agent_location_services.toUiText()
        "agent_miscellaneous" -> Res.string.wallet_reference_type_agent_miscellaneous.toUiText()
        "agent_mission_collateral_paid" -> Res.string.wallet_reference_type_agent_mission_collateral_paid.toUiText()
        "agent_mission_collateral_refunded" -> Res.string.wallet_reference_type_agent_mission_collateral_refunded.toUiText()
        "agent_mission_reward" -> Res.string.wallet_reference_type_agent_mission_reward.toUiText()
        "agent_mission_reward_corporation_tax" -> Res.string.wallet_reference_type_agent_mission_reward_corporation_tax.toUiText()
        "agent_mission_time_bonus_reward" -> Res.string.wallet_reference_type_agent_mission_time_bonus_reward.toUiText()
        "agent_mission_time_bonus_reward_corporation_tax" -> Res.string.wallet_reference_type_agent_mission_time_bonus_reward_corporation_tax.toUiText()
        "agent_security_services" -> Res.string.wallet_reference_type_agent_security_services.toUiText()
        "agent_services_rendered" -> Res.string.wallet_reference_type_agent_services_rendered.toUiText()
        "agents_preward" -> Res.string.wallet_reference_type_agents_preward.toUiText()
        "air_career_program_reward" -> Res.string.wallet_reference_type_air_career_program_reward.toUiText()
        "alliance_maintainance_fee" -> Res.string.wallet_reference_type_alliance_maintenance_fee.toUiText()
        "alliance_registration_fee" -> Res.string.wallet_reference_type_alliance_registration_fee.toUiText()
        "allignment_based_gate_toll" -> Res.string.wallet_reference_type_alignment_based_gate_toll.toUiText()
        "asset_safety_recovery_tax" -> Res.string.wallet_reference_type_asset_safety_recovery_tax.toUiText()
        "bounty" -> Res.string.wallet_reference_type_bounty.toUiText()
        "bounty_prize" -> Res.string.wallet_reference_type_bounty_prize.toUiText()
        "bounty_prize_corporation_tax" -> Res.string.wallet_reference_type_bounty_prize_corporation_tax.toUiText()
        "bounty_prizes" -> Res.string.wallet_reference_type_bounty_prizes.toUiText()
        "bounty_reimbursement" -> Res.string.wallet_reference_type_bounty_reimbursement.toUiText()
        "bounty_surcharge" -> Res.string.wallet_reference_type_bounty_surcharge.toUiText()
        "brokers_fee" -> Res.string.wallet_reference_type_brokers_fee.toUiText()
        "clone_activation" -> Res.string.wallet_reference_type_clone_activation.toUiText()
        "clone_transfer" -> Res.string.wallet_reference_type_clone_transfer.toUiText()
        "contraband_fine" -> Res.string.wallet_reference_type_contraband_fine.toUiText()
        "contract_auction_bid" -> Res.string.wallet_reference_type_contract_auction_bid.toUiText()
        "contract_auction_bid_corp" -> Res.string.wallet_reference_type_contract_auction_bid_corp.toUiText()
        "contract_auction_bid_refund" -> Res.string.wallet_reference_type_contract_auction_bid_refund.toUiText()
        "contract_auction_sold" -> Res.string.wallet_reference_type_contract_auction_sold.toUiText()
        "contract_brokers_fee" -> Res.string.wallet_reference_type_contract_brokers_fee.toUiText()
        "contract_brokers_fee_corp" -> Res.string.wallet_reference_type_contract_brokers_fee_corp.toUiText()
        "contract_collateral" -> Res.string.wallet_reference_type_contract_collateral.toUiText()
        "contract_collateral_deposited_corp" -> Res.string.wallet_reference_type_contract_collateral_deposited_corp.toUiText()
        "contract_collateral_payout" -> Res.string.wallet_reference_type_contract_collateral_payout.toUiText()
        "contract_collateral_refund" -> Res.string.wallet_reference_type_contract_collateral_refund.toUiText()
        "contract_deposit" -> Res.string.wallet_reference_type_contract_deposit.toUiText()
        "contract_deposit_corp" -> Res.string.wallet_reference_type_contract_deposit_corp.toUiText()
        "contract_deposit_refund" -> Res.string.wallet_reference_type_contract_deposit_refund.toUiText()
        "contract_deposit_sales_tax" -> Res.string.wallet_reference_type_contract_deposit_sales_tax.toUiText()
        "contract_price" -> Res.string.wallet_reference_type_contract_price.toUiText()
        "contract_price_payment_corp" -> Res.string.wallet_reference_type_contract_price_payment_corp.toUiText()
        "contract_reversal" -> Res.string.wallet_reference_type_contract_reversal.toUiText()
        "contract_reward" -> Res.string.wallet_reference_type_contract_reward.toUiText()
        "contract_reward_deposited" -> Res.string.wallet_reference_type_contract_reward_deposited.toUiText()
        "contract_reward_deposited_corp" -> Res.string.wallet_reference_type_contract_reward_deposited_corp.toUiText()
        "contract_reward_refund" -> Res.string.wallet_reference_type_contract_reward_refund.toUiText()
        "contract_sales_tax" -> Res.string.wallet_reference_type_contract_sales_tax.toUiText()
        "copying" -> Res.string.wallet_reference_type_copying.toUiText()
        "corporate_reward_payout" -> Res.string.wallet_reference_type_corporate_reward_payout.toUiText()
        "corporate_reward_tax" -> Res.string.wallet_reference_type_corporate_reward_tax.toUiText()
        "corporation_account_withdrawal" -> Res.string.wallet_reference_type_corporation_account_withdrawal.toUiText()
        "corporation_bulk_payment" -> Res.string.wallet_reference_type_corporation_bulk_payment.toUiText()
        "corporation_dividend_payment" -> Res.string.wallet_reference_type_corporation_dividend_payment.toUiText()
        "corporation_liquidation" -> Res.string.wallet_reference_type_corporation_liquidation.toUiText()
        "corporation_logo_change_cost" -> Res.string.wallet_reference_type_corporation_logo_change_cost.toUiText()
        "corporation_payment" -> Res.string.wallet_reference_type_corporation_payment.toUiText()
        "corporation_registration_fee" -> Res.string.wallet_reference_type_corporation_registration_fee.toUiText()
        "cosmetic_market_component_item_purchase" -> Res.string.wallet_reference_type_cosmetic_market_component_item_purchase.toUiText()
        "cosmetic_market_skin_purchase" -> Res.string.wallet_reference_type_cosmetic_market_skin_purchase.toUiText()
        "cosmetic_market_skin_sale" -> Res.string.wallet_reference_type_cosmetic_market_skin_sale.toUiText()
        "cosmetic_market_skin_sale_broker_fee" -> Res.string.wallet_reference_type_cosmetic_market_skin_sale_broker_fee.toUiText()
        "cosmetic_market_skin_sale_tax" -> Res.string.wallet_reference_type_cosmetic_market_skin_sale_tax.toUiText()
        "cosmetic_market_skin_transaction" -> Res.string.wallet_reference_type_cosmetic_market_skin_transaction.toUiText()
        "courier_mission_escrow" -> Res.string.wallet_reference_type_courier_mission_escrow.toUiText()
        "cspa" -> Res.string.wallet_reference_type_cspa.toUiText()
        "cspaofflinerefund" -> Res.string.wallet_reference_type_cspa_offline_refund.toUiText()
        "daily_challenge_reward" -> Res.string.wallet_reference_type_daily_challenge_reward.toUiText()
        "daily_goal_payouts" -> Res.string.wallet_reference_type_daily_goal_payouts.toUiText()
        "daily_goal_payouts_tax" -> Res.string.wallet_reference_type_daily_goal_payouts_tax.toUiText()
        "datacore_fee" -> Res.string.wallet_reference_type_datacore_fee.toUiText()
        "dna_modification_fee" -> Res.string.wallet_reference_type_dna_modification_fee.toUiText()
        "docking_fee" -> Res.string.wallet_reference_type_docking_fee.toUiText()
        "duel_wager_escrow" -> Res.string.wallet_reference_type_duel_wager_escrow.toUiText()
        "duel_wager_payment" -> Res.string.wallet_reference_type_duel_wager_payment.toUiText()
        "duel_wager_refund" -> Res.string.wallet_reference_type_duel_wager_refund.toUiText()
        "ess_escrow_transfer" -> Res.string.wallet_reference_type_ess_escrow_transfer.toUiText()
        "external_trade_delivery" -> Res.string.wallet_reference_type_external_trade_delivery.toUiText()
        "external_trade_freeze" -> Res.string.wallet_reference_type_external_trade_freeze.toUiText()
        "external_trade_thaw" -> Res.string.wallet_reference_type_external_trade_thaw.toUiText()
        "factory_slot_rental_fee" -> Res.string.wallet_reference_type_factory_slot_rental_fee.toUiText()
        "flux_payout" -> Res.string.wallet_reference_type_flux_payout.toUiText()
        "flux_tax" -> Res.string.wallet_reference_type_flux_tax.toUiText()
        "flux_ticket_repayment" -> Res.string.wallet_reference_type_flux_ticket_repayment.toUiText()
        "flux_ticket_sale" -> Res.string.wallet_reference_type_flux_ticket_sale.toUiText()
        "freelance_jobs_broadcasting_fee" -> Res.string.wallet_reference_type_freelance_jobs_broadcasting_fee.toUiText()
        "freelance_jobs_duration_fee" -> Res.string.wallet_reference_type_freelance_jobs_duration_fee.toUiText()
        "freelance_jobs_escrow_refund" -> Res.string.wallet_reference_type_freelance_jobs_escrow_refund.toUiText()
        "freelance_jobs_reward" -> Res.string.wallet_reference_type_freelance_jobs_reward.toUiText()
        "freelance_jobs_reward_corporation_tax" -> Res.string.wallet_reference_type_freelance_jobs_reward_corporation_tax.toUiText()
        "freelance_jobs_reward_escrow" -> Res.string.wallet_reference_type_freelance_jobs_reward_escrow.toUiText()
        "gm_cash_transfer" -> Res.string.wallet_reference_type_gm_cash_transfer.toUiText()
        "gm_plex_fee_refund" -> Res.string.wallet_reference_type_gm_plex_fee_refund.toUiText()
        "industry_job_tax" -> Res.string.wallet_reference_type_industry_job_tax.toUiText()
        "infrastructure_hub_maintenance" -> Res.string.wallet_reference_type_infrastructure_hub_maintenance.toUiText()
        "inheritance" -> Res.string.wallet_reference_type_inheritance.toUiText()
        "insurance" -> Res.string.wallet_reference_type_insurance.toUiText()
        "insurgency_corruption_contribution_reward" -> Res.string.wallet_reference_type_insurgency_corruption_contribution_reward.toUiText()
        "insurgency_suppression_contribution_reward" -> Res.string.wallet_reference_type_insurgency_suppression_contribution_reward.toUiText()
        "item_trader_payment" -> Res.string.wallet_reference_type_item_trader_payment.toUiText()
        "jump_clone_activation_fee" -> Res.string.wallet_reference_type_jump_clone_activation_fee.toUiText()
        "jump_clone_installation_fee" -> Res.string.wallet_reference_type_jump_clone_installation_fee.toUiText()
        "kill_right_fee" -> Res.string.wallet_reference_type_kill_right_fee.toUiText()
        "lp_store" -> Res.string.wallet_reference_type_lp_store.toUiText()
        "manufacturing" -> Res.string.wallet_reference_type_manufacturing.toUiText()
        "market_escrow" -> Res.string.wallet_reference_type_market_escrow.toUiText()
        "market_fine_paid" -> Res.string.wallet_reference_type_market_fine_paid.toUiText()
        "market_provider_tax" -> Res.string.wallet_reference_type_market_provider_tax.toUiText()
        "market_transaction" -> Res.string.wallet_reference_type_market_transaction.toUiText()
        "medal_creation" -> Res.string.wallet_reference_type_medal_creation.toUiText()
        "medal_issued" -> Res.string.wallet_reference_type_medal_issued.toUiText()
        "milestone_reward_payment" -> Res.string.wallet_reference_type_milestone_reward_payment.toUiText()
        "mission_completion" -> Res.string.wallet_reference_type_mission_completion.toUiText()
        "mission_cost" -> Res.string.wallet_reference_type_mission_cost.toUiText()
        "mission_expiration" -> Res.string.wallet_reference_type_mission_expiration.toUiText()
        "mission_reward" -> Res.string.wallet_reference_type_mission_reward.toUiText()
        "office_rental_fee" -> Res.string.wallet_reference_type_office_rental_fee.toUiText()
        "operation_bonus" -> Res.string.wallet_reference_type_operation_bonus.toUiText()
        "opportunity_reward" -> Res.string.wallet_reference_type_opportunity_reward.toUiText()
        "planetary_construction" -> Res.string.wallet_reference_type_planetary_construction.toUiText()
        "planetary_export_tax" -> Res.string.wallet_reference_type_planetary_export_tax.toUiText()
        "planetary_import_tax" -> Res.string.wallet_reference_type_planetary_import_tax.toUiText()
        "player_donation" -> Res.string.wallet_reference_type_player_donation.toUiText()
        "player_trading" -> Res.string.wallet_reference_type_player_trading.toUiText()
        "project_discovery_reward" -> Res.string.wallet_reference_type_project_discovery_reward.toUiText()
        "project_discovery_tax" -> Res.string.wallet_reference_type_project_discovery_tax.toUiText()
        "project_payouts" -> Res.string.wallet_reference_type_project_payouts.toUiText()
        "reaction" -> Res.string.wallet_reference_type_reaction.toUiText()
        "redeemed_isk_token" -> Res.string.wallet_reference_type_redeemed_isk_token.toUiText()
        "release_of_impounded_property" -> Res.string.wallet_reference_type_release_of_impounded_property.toUiText()
        "repair_bill" -> Res.string.wallet_reference_type_repair_bill.toUiText()
        "reprocessing_tax" -> Res.string.wallet_reference_type_reprocessing_tax.toUiText()
        "researching_material_productivity" -> Res.string.wallet_reference_type_researching_material_productivity.toUiText()
        "researching_technology" -> Res.string.wallet_reference_type_researching_technology.toUiText()
        "researching_time_productivity" -> Res.string.wallet_reference_type_researching_time_productivity.toUiText()
        "resource_wars_reward" -> Res.string.wallet_reference_type_resource_wars_reward.toUiText()
        "reverse_engineering" -> Res.string.wallet_reference_type_reverse_engineering.toUiText()
        "season_challenge_reward" -> Res.string.wallet_reference_type_season_challenge_reward.toUiText()
        "security_processing_fee" -> Res.string.wallet_reference_type_security_processing_fee.toUiText()
        "shares" -> Res.string.wallet_reference_type_shares.toUiText()
        "skill_purchase" -> Res.string.wallet_reference_type_skill_purchase.toUiText()
        "skyhook_claim_fee" -> Res.string.wallet_reference_type_skyhook_claim_fee.toUiText()
        "sovereignity_bill" -> Res.string.wallet_reference_type_sovereignty_bill.toUiText()
        "store_purchase" -> Res.string.wallet_reference_type_store_purchase.toUiText()
        "store_purchase_refund" -> Res.string.wallet_reference_type_store_purchase_refund.toUiText()
        "structure_gate_jump" -> Res.string.wallet_reference_type_structure_gate_jump.toUiText()
        "transaction_tax" -> Res.string.wallet_reference_type_transaction_tax.toUiText()
        "under_construction" -> Res.string.wallet_reference_type_under_construction.toUiText()
        "upkeep_adjustment_fee" -> Res.string.wallet_reference_type_upkeep_adjustment_fee.toUiText()
        "war_ally_contract" -> Res.string.wallet_reference_type_war_ally_contract.toUiText()
        "war_fee" -> Res.string.wallet_reference_type_war_fee.toUiText()
        "war_fee_surrender" -> Res.string.wallet_reference_type_war_fee_surrender.toUiText()
        else ->
            referenceType
                .split('_')
                .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
                .replace("Ess", "ESS")
                .toUiText()
    }
}
