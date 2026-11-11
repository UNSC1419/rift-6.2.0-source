package dev.nohus.rift.sso.scopes

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.sso_scope_open_window_name
import dev.nohus.rift.generated.resources.sso_scope_open_window_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_assets_name
import dev.nohus.rift.generated.resources.sso_scope_read_assets_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_assets_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_name
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_3
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_4
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_5
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_6
import dev.nohus.rift.generated.resources.sso_scope_read_character_location_reason_7
import dev.nohus.rift.generated.resources.sso_scope_read_clones_name
import dev.nohus.rift.generated.resources.sso_scope_read_clones_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_contacts_name
import dev.nohus.rift.generated.resources.sso_scope_read_contacts_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_contacts_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_corporation_assets_name
import dev.nohus.rift.generated.resources.sso_scope_read_corporation_assets_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_corporation_wallet_name
import dev.nohus.rift.generated.resources.sso_scope_read_corporation_wallet_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_current_ship_name
import dev.nohus.rift.generated.resources.sso_scope_read_current_ship_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_jobs_name
import dev.nohus.rift.generated.resources.sso_scope_read_jobs_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_jobs_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_loyalty_points_name
import dev.nohus.rift.generated.resources.sso_scope_read_loyalty_points_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_notifications_name
import dev.nohus.rift.generated.resources.sso_scope_read_notifications_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_online_status_name
import dev.nohus.rift.generated.resources.sso_scope_read_online_status_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_planetary_industry_colonies_name
import dev.nohus.rift.generated.resources.sso_scope_read_planetary_industry_colonies_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_planetary_industry_colonies_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_projects_name
import dev.nohus.rift.generated.resources.sso_scope_read_projects_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_roles_name
import dev.nohus.rift.generated.resources.sso_scope_read_roles_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_roles_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_roles_reason_3
import dev.nohus.rift.generated.resources.sso_scope_read_structures_name
import dev.nohus.rift.generated.resources.sso_scope_read_structures_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_structures_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_structures_reason_3
import dev.nohus.rift.generated.resources.sso_scope_read_structures_reason_4
import dev.nohus.rift.generated.resources.sso_scope_read_wallet_name
import dev.nohus.rift.generated.resources.sso_scope_read_wallet_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_wallet_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_your_structures_name
import dev.nohus.rift.generated.resources.sso_scope_read_your_structures_reason_1
import dev.nohus.rift.generated.resources.sso_scope_read_your_structures_reason_2
import dev.nohus.rift.generated.resources.sso_scope_read_your_structures_reason_3
import dev.nohus.rift.generated.resources.sso_scope_update_autopilot_name
import dev.nohus.rift.generated.resources.sso_scope_update_autopilot_reason_1
import dev.nohus.rift.generated.resources.sso_scope_write_contacts_name
import dev.nohus.rift.generated.resources.sso_scope_write_contacts_reason_1
import dev.nohus.rift.localization.toUiText

object ScopeGroups {
    val readContacts = ScopeGroup(
        name = Res.string.sso_scope_read_contacts_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_contacts_reason_1.toUiText(),
            Res.string.sso_scope_read_contacts_reason_2.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Characters.ReadContacts,
            EsiScope.Corporations.ReadContacts,
            EsiScope.Alliances.ReadContacts,
        ),
    )
    val writeContacts = ScopeGroup(
        name = Res.string.sso_scope_write_contacts_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_write_contacts_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Characters.WriteContacts,
        ),
    )
    val readNotifications = ScopeGroup(
        name = Res.string.sso_scope_read_notifications_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_notifications_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Characters.ReadNotifications,
        ),
    )
    val readClones = ScopeGroup(
        name = Res.string.sso_scope_read_clones_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_clones_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Clones.ReadClones,
            EsiScope.Clones.ReadImplants,
        ),
    )
    val readOnlineStatus = ScopeGroup(
        name = Res.string.sso_scope_read_online_status_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_online_status_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Locations.ReadOnline,
        ),
        isRequired = true,
    )
    val readCharacterLocation = ScopeGroup(
        name = Res.string.sso_scope_read_character_location_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_character_location_reason_1.toUiText(),
            Res.string.sso_scope_read_character_location_reason_2.toUiText(),
            Res.string.sso_scope_read_character_location_reason_3.toUiText(),
            Res.string.sso_scope_read_character_location_reason_4.toUiText(),
            Res.string.sso_scope_read_character_location_reason_5.toUiText(),
            Res.string.sso_scope_read_character_location_reason_6.toUiText(),
            Res.string.sso_scope_read_character_location_reason_7.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Locations.ReadLocation,
        ),
    )
    val readCurrentShip = ScopeGroup(
        name = Res.string.sso_scope_read_current_ship_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_current_ship_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Locations.ReadShipType,
        ),
    )
    val readPlanetaryIndustryColonies = ScopeGroup(
        name = Res.string.sso_scope_read_planetary_industry_colonies_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_planetary_industry_colonies_reason_1.toUiText(),
            Res.string.sso_scope_read_planetary_industry_colonies_reason_2.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Planets.ManagePlanets,
        ),
    )
    val readWallet = ScopeGroup(
        name = Res.string.sso_scope_read_wallet_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_wallet_reason_1.toUiText(),
            Res.string.sso_scope_read_wallet_reason_2.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Wallet.ReadCharacterWallet,
        ),
    )
    val readCorporationWallet = ScopeGroup(
        name = Res.string.sso_scope_read_corporation_wallet_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_corporation_wallet_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Wallet.ReadCorporationWallets,
            EsiScope.Corporations.ReadDivisions,
        ),
    )
    val readLoyaltyPoints = ScopeGroup(
        name = Res.string.sso_scope_read_loyalty_points_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_loyalty_points_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Characters.ReadLoyalty,
        ),
    )
    val readStructures = ScopeGroup(
        name = Res.string.sso_scope_read_structures_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_structures_reason_1.toUiText(),
            Res.string.sso_scope_read_structures_reason_2.toUiText(),
            Res.string.sso_scope_read_structures_reason_3.toUiText(),
            Res.string.sso_scope_read_structures_reason_4.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Search.SearchStructures,
            EsiScope.Universe.ReadStructures,
        ),
    )
    val readYourStructures = ScopeGroup(
        name = Res.string.sso_scope_read_your_structures_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_your_structures_reason_1.toUiText(),
            Res.string.sso_scope_read_your_structures_reason_2.toUiText(),
            Res.string.sso_scope_read_your_structures_reason_3.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Structures.ReadCharacter,
            EsiScope.Structures.ReadCorporation,
            EsiScope.Activities.ReadCharacter,
        ),
    )
    val updateAutopilot = ScopeGroup(
        name = Res.string.sso_scope_update_autopilot_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_update_autopilot_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Ui.WriteWaypoint,
        ),
    )
    val openWindow = ScopeGroup(
        name = Res.string.sso_scope_open_window_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_open_window_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Ui.OpenWindow,
        ),
    )
    val readAssets = ScopeGroup(
        name = Res.string.sso_scope_read_assets_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_assets_reason_1.toUiText(),
            Res.string.sso_scope_read_assets_reason_2.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Assets.ReadAssets,
        ),
    )
    val readCorporationAssets = ScopeGroup(
        name = Res.string.sso_scope_read_corporation_assets_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_corporation_assets_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Assets.ReadCorporationAssets,
        ),
    )
    val readProjects = ScopeGroup(
        name = Res.string.sso_scope_read_projects_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_projects_reason_1.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Corporations.ReadProjects,
        ),
    )
    val readJobs = ScopeGroup(
        name = Res.string.sso_scope_read_jobs_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_jobs_reason_1.toUiText(),
            Res.string.sso_scope_read_jobs_reason_2.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Characters.ReadFreelanceJobs,
            EsiScope.Corporations.ReadFreelanceJobs,
        ),
    )
    val readRoles = ScopeGroup(
        name = Res.string.sso_scope_read_roles_name.toUiText(),
        reasons = listOf(
            Res.string.sso_scope_read_roles_reason_1.toUiText(),
            Res.string.sso_scope_read_roles_reason_2.toUiText(),
            Res.string.sso_scope_read_roles_reason_3.toUiText(),
        ),
        scopes = listOf(
            EsiScope.Characters.ReadCorporationRoles,
        ),
    )

    val all = [
        readContacts,
        writeContacts,
        readNotifications,
        readClones,
        readOnlineStatus,
        readCharacterLocation,
        readCurrentShip,
        readPlanetaryIndustryColonies,
        readWallet,
        readCorporationWallet,
        readLoyaltyPoints,
        readStructures,
        readYourStructures,
        updateAutopilot,
        openWindow,
        readAssets,
        readCorporationAssets,
        readProjects,
        readJobs,
        readRoles,
    ]

    fun getByIds(scopes: List<String>): List<ScopeGroup> {
        return all.filter { group ->
            val ids = group.scopes.map { it.id }
            scopes.containsAll(ids)
        }
    }
}
