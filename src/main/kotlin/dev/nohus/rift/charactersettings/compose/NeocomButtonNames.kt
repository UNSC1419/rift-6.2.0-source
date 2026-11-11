package dev.nohus.rift.charactersettings.compose

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.character_settings_neocom_button_access_lists_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_achievements_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_air_career_program_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_aura_guidance_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_bounty_office_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_calculator_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_calendar_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_character_sheet_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_chat_channels_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_chat_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_compare_tool_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_contacts_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_contracts_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_corporation_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_eve_help_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_factional_warfare_enlistment_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_factional_warfare_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_fitting_management_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_fitting_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_fleet_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_hypernet_relay_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_industry_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_insurgency_dashboard_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_inventory_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_journal_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_locations_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_log_and_messages_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_log_off_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_mail_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_map_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_market_orders_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_mining_ledger_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_multibuy_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_my_mercenary_dens_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_new_eden_store_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_notepad_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_omega_subscription_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_opportunities_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_personal_assets_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_planetary_industry_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_plex_vault_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_pointer_window_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_project_discovery_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_quit_game_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_regional_market_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_settings_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_ship_skinr_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_ship_tree_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_structure_browser_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_the_agency_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_the_proving_grounds_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_theaters_of_war_name
import dev.nohus.rift.generated.resources.character_settings_neocom_button_wallet_name
import org.jetbrains.compose.resources.StringResource
data class NeocomButtonDefinition(
    val id: String,
    val name: String,
    val displayName: StringResource,
    val iconPath: String,
    val btnType: Int = 1,
    val isAddable: Boolean = true,
)

val NeocomButtonDefinitions = listOf(
    NeocomButtonDefinition("job_board", "Opportunities", Res.string.character_settings_neocom_button_opportunities_name, windowIcon("opportunities.png")),
    NeocomButtonDefinition("journal", "Journal", Res.string.character_settings_neocom_button_journal_name, windowIcon("journal.png")),
    NeocomButtonDefinition(
        id = "airCareerProgram",
        name = "AIR Career Program",
        displayName = Res.string.character_settings_neocom_button_air_career_program_name,
        iconPath = windowIcon("airCareerProgram.png"),
        btnType = 21,
    ),
    NeocomButtonDefinition("agency", "The Agency", Res.string.character_settings_neocom_button_the_agency_name, windowIcon("theAgency.png")),
    NeocomButtonDefinition("map_beta", "Map", Res.string.character_settings_neocom_button_map_name, windowIcon("map.png")),
    NeocomButtonDefinition(
        id = "ProjectDiscovery",
        name = "Project Discovery",
        displayName = Res.string.character_settings_neocom_button_project_discovery_name,
        iconPath = windowIcon("projectdiscovery.png"),
    ),
    NeocomButtonDefinition("militia", "Factional Warfare", Res.string.character_settings_neocom_button_factional_warfare_name, windowIcon("factionalwarfare.png")),
    NeocomButtonDefinition("fwEnlistmentWnd", "Factional Warfare Enlistment", Res.string.character_settings_neocom_button_factional_warfare_enlistment_name, windowIcon("factionalwarfare.png")),
    NeocomButtonDefinition("insurgentsDashboard", "Insurgency Dashboard", Res.string.character_settings_neocom_button_insurgency_dashboard_name, windowIcon("insurgencies.png")),
    NeocomButtonDefinition("bountyoffice", "Bounty Office", Res.string.character_settings_neocom_button_bounty_office_name, windowIcon("bountyoffice.png")),
    NeocomButtonDefinition("pvp_filament_event_window", "The Proving Grounds", Res.string.character_settings_neocom_button_the_proving_grounds_name, windowIcon("provingGrounds.png")),
    NeocomButtonDefinition("market", "Regional Market", Res.string.character_settings_neocom_button_regional_market_name, windowIcon("market.png")),
    NeocomButtonDefinition("marketOrders", "Market Orders", Res.string.character_settings_neocom_button_market_orders_name, windowIcon("marketOrders.png")),
    NeocomButtonDefinition("wallet", "Wallet", Res.string.character_settings_neocom_button_wallet_name, windowIcon("wallet.png")),
    NeocomButtonDefinition("contracts", "Contracts", Res.string.character_settings_neocom_button_contracts_name, windowIcon("contracts.png")),
    NeocomButtonDefinition("multibuy", "Multibuy", Res.string.character_settings_neocom_button_multibuy_name, texture("classes/MultiSell/multiBuy.png")),
    NeocomButtonDefinition("raffle_window", "HyperNet Relay", Res.string.character_settings_neocom_button_hypernet_relay_name, windowIcon("hypernet.png")),
    NeocomButtonDefinition("industry", "Industry", Res.string.character_settings_neocom_button_industry_name, windowIcon("Industry.png")),
    NeocomButtonDefinition("planets", "Planetary Industry", Res.string.character_settings_neocom_button_planetary_industry_name, windowIcon("planets.png")),
    NeocomButtonDefinition(
        id = "mercenary_den",
        name = "My Mercenary Dens",
        displayName = Res.string.character_settings_neocom_button_my_mercenary_dens_name,
        iconPath = texture("eveicon/product_icons/mercenary_den_product_icon_64px.png"),
    ),
    NeocomButtonDefinition("ledger", "Mining Ledger", Res.string.character_settings_neocom_button_mining_ledger_name, windowIcon("miningLedger.png")),
    NeocomButtonDefinition("inventory", "Inventory", Res.string.character_settings_neocom_button_inventory_name, windowIcon("items.png"), btnType = 4),
    NeocomButtonDefinition("assets", "Personal Assets", Res.string.character_settings_neocom_button_personal_assets_name, windowIcon("assets.png")),
    NeocomButtonDefinition("charactersheet", "Character Sheet", Res.string.character_settings_neocom_button_character_sheet_name, windowIcon("charactersheet.png")),
    NeocomButtonDefinition("locations", "Locations", Res.string.character_settings_neocom_button_locations_name, windowIcon("locations.png")),
    NeocomButtonDefinition("fitting", "Fitting", Res.string.character_settings_neocom_button_fitting_name, windowIcon("fitting.png")),
    NeocomButtonDefinition("shipTree", "Ship Tree", Res.string.character_settings_neocom_button_ship_tree_name, windowIcon("ISIS.png")),
    NeocomButtonDefinition("fittingMgmt", "Fitting Management", Res.string.character_settings_neocom_button_fitting_management_name, windowIcon("fittingManagement.png")),
    NeocomButtonDefinition("chatchannels", "Chat Channels", Res.string.character_settings_neocom_button_chat_channels_name, windowIcon("chatchannels.png")),
    NeocomButtonDefinition("fleet", "Fleet", Res.string.character_settings_neocom_button_fleet_name, windowIcon("fleet.png")),
    NeocomButtonDefinition("mail", "Mail", Res.string.character_settings_neocom_button_mail_name, windowIcon("evemail.png")),
    NeocomButtonDefinition("corporation", "Corporation", Res.string.character_settings_neocom_button_corporation_name, windowIcon("corporation.png")),
    NeocomButtonDefinition("calendar", "Calendar", Res.string.character_settings_neocom_button_calendar_name, windowIcon("calendar.png")),
    NeocomButtonDefinition("accessgroups", "Access Lists", Res.string.character_settings_neocom_button_access_lists_name, windowIcon("accessGroups.png")),
    NeocomButtonDefinition("addressbook", "Contacts", Res.string.character_settings_neocom_button_contacts_name, windowIcon("peopleandplaces.png")),
    NeocomButtonDefinition("ship_skinr", "Ship SKINR", Res.string.character_settings_neocom_button_ship_skinr_name, windowIcon("paint_tool.png")),
    NeocomButtonDefinition("notepad", "Notepad", Res.string.character_settings_neocom_button_notepad_name, windowIcon("notepad.png")),
    NeocomButtonDefinition("compareTool", "Compare Tool", Res.string.character_settings_neocom_button_compare_tool_name, windowIcon("comparetool.png")),
    NeocomButtonDefinition("log", "Log and Messages", Res.string.character_settings_neocom_button_log_and_messages_name, windowIcon("log.png")),
    NeocomButtonDefinition("structurebrowser", "Structure Browser", Res.string.character_settings_neocom_button_structure_browser_name, windowIcon("structureBrowser.png")),
    NeocomButtonDefinition("calculator", "Calculator", Res.string.character_settings_neocom_button_calculator_name, windowIcon("calculator.png")),
    NeocomButtonDefinition("pointerWnd", "Pointer Window", Res.string.character_settings_neocom_button_pointer_window_name, windowIcon("UIHelper.png")),
    NeocomButtonDefinition("aurumStore", "New Eden Store", Res.string.character_settings_neocom_button_new_eden_store_name, windowIcon("NES.png")),
    NeocomButtonDefinition("PLEXvault", "PLEX Vault", Res.string.character_settings_neocom_button_plex_vault_name, texture("Plex/plex_128_gradient_white.png")),
    NeocomButtonDefinition("omega_upsell", "Omega Subscription", Res.string.character_settings_neocom_button_omega_subscription_name, windowIcon("upsell.png")),
    NeocomButtonDefinition("help", "EVE Help", Res.string.character_settings_neocom_button_eve_help_name, windowIcon("help.png")),
    NeocomButtonDefinition("auraguidance", "Aura Guidance", Res.string.character_settings_neocom_button_aura_guidance_name, texture("classes/careerPortal/aura/aura_neocom_64.png")),
    NeocomButtonDefinition("settings", "Settings", Res.string.character_settings_neocom_button_settings_name, windowIcon("settings.png")),
    NeocomButtonDefinition("logoff", "Log off", Res.string.character_settings_neocom_button_log_off_name, windowIcon("logOut.png")),
    NeocomButtonDefinition("quitGame", "Quit Game", Res.string.character_settings_neocom_button_quit_game_name, windowIcon("quitGame.png")),
    NeocomButtonDefinition(
        id = "chat",
        name = "Chat",
        displayName = Res.string.character_settings_neocom_button_chat_name,
        iconPath = windowIcon("chatchannel.png"),
        btnType = 10,
    ),
    NeocomButtonDefinition(
        id = "theaters_of_war",
        name = "Theaters of War",
        displayName = Res.string.character_settings_neocom_button_theaters_of_war_name,
        iconPath = texture("eveicon/product_icons/theaters_of_war_product_64px.png"),
        isAddable = false,
    ),
    NeocomButtonDefinition("achievements", "Achievements", Res.string.character_settings_neocom_button_achievements_name, windowIcon("achievements.png"), isAddable = false),
)

val NeocomButtonDefinitionsById = NeocomButtonDefinitions.associateBy { it.id }

private fun windowIcon(filename: String): String = texture("WindowIcons/$filename")

private fun texture(path: String): String = "bytes:res:/UI/Texture/$path"
