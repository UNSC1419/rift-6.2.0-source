package dev.nohus.rift.opportunities

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.contribution_method_earn_loyalty_point_16px
import dev.nohus.rift.generated.resources.contribution_method_manual_16px
import dev.nohus.rift.generated.resources.contribution_method_scan_signatures_16px
import dev.nohus.rift.generated.resources.corporation_16px
import dev.nohus.rift.generated.resources.fw_complex_type_16px
import dev.nohus.rift.generated.resources.location_16px
import dev.nohus.rift.generated.resources.map_marker_resources_storage
import dev.nohus.rift.generated.resources.mercenary_den_16px
import dev.nohus.rift.generated.resources.mining_16px
import dev.nohus.rift.generated.resources.navigate_forward_16px
import dev.nohus.rift.generated.resources.opportunities_attribute_any_office_value
import dev.nohus.rift.generated.resources.opportunities_attribute_any_ships_implants_covered_value
import dev.nohus.rift.generated.resources.opportunities_attribute_any_ships_implants_not_covered_value
import dev.nohus.rift.generated.resources.opportunities_attribute_any_value
import dev.nohus.rift.generated.resources.opportunities_attribute_capture_fw_complex_complex_type_description
import dev.nohus.rift.generated.resources.opportunities_attribute_capture_fw_complex_complex_type_name
import dev.nohus.rift.generated.resources.opportunities_attribute_capture_fw_complex_faction_description
import dev.nohus.rift.generated.resources.opportunities_attribute_capture_fw_complex_faction_name
import dev.nohus.rift.generated.resources.opportunities_attribute_capture_fw_complex_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_capture_fw_complex_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_conflict_required_value
import dev.nohus.rift.generated.resources.opportunities_attribute_conflict_unrequired_value
import dev.nohus.rift.generated.resources.opportunities_attribute_damage_ship_identities_description
import dev.nohus.rift.generated.resources.opportunities_attribute_damage_ship_identities_name
import dev.nohus.rift.generated.resources.opportunities_attribute_damage_ship_locations_description
import dev.nohus.rift.generated.resources.opportunities_attribute_damage_ship_locations_name
import dev.nohus.rift.generated.resources.opportunities_attribute_damage_ship_ships_description
import dev.nohus.rift.generated.resources.opportunities_attribute_damage_ship_ships_name
import dev.nohus.rift.generated.resources.opportunities_attribute_defend_fw_complex_complex_type_description
import dev.nohus.rift.generated.resources.opportunities_attribute_defend_fw_complex_complex_type_name
import dev.nohus.rift.generated.resources.opportunities_attribute_defend_fw_complex_faction_description
import dev.nohus.rift.generated.resources.opportunities_attribute_defend_fw_complex_faction_name
import dev.nohus.rift.generated.resources.opportunities_attribute_defend_fw_complex_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_defend_fw_complex_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_deliver_item_destination_description
import dev.nohus.rift.generated.resources.opportunities_attribute_deliver_item_destination_name
import dev.nohus.rift.generated.resources.opportunities_attribute_deliver_item_item_description
import dev.nohus.rift.generated.resources.opportunities_attribute_deliver_item_item_name
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_npc_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_npc_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_ship_identities_description
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_ship_identities_name
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_ship_locations_description
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_ship_locations_name
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_ship_ships_description
import dev.nohus.rift.generated.resources.opportunities_attribute_destroy_ship_ships_name
import dev.nohus.rift.generated.resources.opportunities_attribute_earn_loyalty_point_corporations_description
import dev.nohus.rift.generated.resources.opportunities_attribute_earn_loyalty_point_corporations_name
import dev.nohus.rift.generated.resources.opportunities_attribute_large_complex_value
import dev.nohus.rift.generated.resources.opportunities_attribute_lost_ship_identities_description
import dev.nohus.rift.generated.resources.opportunities_attribute_lost_ship_identities_name
import dev.nohus.rift.generated.resources.opportunities_attribute_lost_ship_ship_description
import dev.nohus.rift.generated.resources.opportunities_attribute_lost_ship_ship_name
import dev.nohus.rift.generated.resources.opportunities_attribute_lost_ship_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_lost_ship_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_manufacture_item_facility_description
import dev.nohus.rift.generated.resources.opportunities_attribute_manufacture_item_facility_name
import dev.nohus.rift.generated.resources.opportunities_attribute_manufacture_item_item_description
import dev.nohus.rift.generated.resources.opportunities_attribute_manufacture_item_item_name
import dev.nohus.rift.generated.resources.opportunities_attribute_manufacture_item_owner_description
import dev.nohus.rift.generated.resources.opportunities_attribute_manufacture_item_owner_name
import dev.nohus.rift.generated.resources.opportunities_attribute_medium_complex_value
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_den_effects_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_den_effects_name
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_hostiles_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_hostiles_name
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_location_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_location_name
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_ship_restrictions_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_ship_restrictions_name
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_status_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mercenary_tactical_operation_status_name
import dev.nohus.rift.generated.resources.opportunities_attribute_mine_material_locations_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mine_material_locations_name
import dev.nohus.rift.generated.resources.opportunities_attribute_mine_material_materials_description
import dev.nohus.rift.generated.resources.opportunities_attribute_mine_material_materials_name
import dev.nohus.rift.generated.resources.opportunities_attribute_novice_complex_value
import dev.nohus.rift.generated.resources.opportunities_attribute_owner_any_value
import dev.nohus.rift.generated.resources.opportunities_attribute_owner_corporation_value
import dev.nohus.rift.generated.resources.opportunities_attribute_owner_self_value
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_boost_shield_identity_description
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_boost_shield_identity_name
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_boost_shield_ship_description
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_boost_shield_ship_name
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_boost_shield_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_boost_shield_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_repair_armor_identity_description
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_repair_armor_identity_name
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_repair_armor_ship_description
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_repair_armor_ship_name
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_repair_armor_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_remote_repair_armor_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_salvage_wreck_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_salvage_wreck_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_scan_signature_signature_description
import dev.nohus.rift.generated.resources.opportunities_attribute_scan_signature_signature_name
import dev.nohus.rift.generated.resources.opportunities_attribute_scan_signature_solar_system_description
import dev.nohus.rift.generated.resources.opportunities_attribute_scan_signature_solar_system_name
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_conflict_description
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_conflict_name
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_identities_description
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_identities_name
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_locations_description
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_locations_name
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_ships_description
import dev.nohus.rift.generated.resources.opportunities_attribute_ship_insurance_ships_name
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_combat_value
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_data_value
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_gas_value
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_ore_value
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_relic_value
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_unknown_value
import dev.nohus.rift.generated.resources.opportunities_attribute_signature_wormhole_value
import dev.nohus.rift.generated.resources.opportunities_attribute_small_complex_value
import dev.nohus.rift.generated.resources.opportunities_attribute_specific_office_value
import dev.nohus.rift.generated.resources.opportunities_attribute_unknown_complex_value
import dev.nohus.rift.generated.resources.opportunities_operation_anarchy_impact_value
import dev.nohus.rift.generated.resources.opportunities_operation_development_impact_value
import dev.nohus.rift.generated.resources.opportunities_operation_infomorph_bonus_value
import dev.nohus.rift.generated.resources.pilot_or_organization_16px
import dev.nohus.rift.generated.resources.solar_system_16px
import dev.nohus.rift.generated.resources.spaceship_command_16px
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.location.LocationRepository
import dev.nohus.rift.network.esi.models.Archetype
import dev.nohus.rift.network.esi.models.ConflictType
import dev.nohus.rift.network.esi.models.CorporationId
import dev.nohus.rift.network.esi.models.DockableLocation
import dev.nohus.rift.network.esi.models.Faction
import dev.nohus.rift.network.esi.models.Identity
import dev.nohus.rift.network.esi.models.Item
import dev.nohus.rift.network.esi.models.Location
import dev.nohus.rift.network.esi.models.OwnerType
import dev.nohus.rift.network.esi.models.SignatureTypeId
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.opportunities.TypeListsRepository.TypeList
import dev.nohus.rift.repositories.FactionsRepository
import dev.nohus.rift.repositories.ShipTreeGroups
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapConstellation
import dev.nohus.rift.repositories.SolarSystemsRepository.MapRegion
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.repositories.character.CharacterDetailsRepository
import dev.nohus.rift.repositories.character.CharacterDetailsRepository.CharacterDetails
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.utils.mapAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.annotation.Single

@Single
class GetOpportunityContributionAttributesUseCase(
    private val solarSystemsRepository: SolarSystemsRepository,
    private val typesRepository: TypesRepository,
    private val characterDetailsRepository: CharacterDetailsRepository,
    private val locationRepository: LocationRepository,
    private val factionsRepository: FactionsRepository,
) {

    data class OpportunityContributionAttributeType(
        val name: UiText,
        val description: UiText,
        val icon: DrawableResource,
        val values: List<OpportunityContributionAttribute>,
    )

    sealed interface OpportunityContributionAttribute {
        data class Text(val text: UiText, val isPlain: Boolean = false) : OpportunityContributionAttribute
        data class Type(val type: TypesRepository.Type) : OpportunityContributionAttribute
        data class TypeGroup(val name: String) : OpportunityContributionAttribute
        data class Ship(val type: TypesRepository.Type) : OpportunityContributionAttribute
        data class ShipGroup(val id: Int, val name: String, val icon: DrawableResource) : OpportunityContributionAttribute
        data class SolarSystem(val solarSystem: MapSolarSystem) : OpportunityContributionAttribute
        data class Constellation(val constellation: MapConstellation) : OpportunityContributionAttribute
        data class Region(val region: MapRegion) : OpportunityContributionAttribute
        data class Station(val station: LocationRepository.Station?, val solarSystem: MapSolarSystem?) : OpportunityContributionAttribute
        data class Structure(val structure: LocationRepository.Structure?, val solarSystem: MapSolarSystem?) : OpportunityContributionAttribute
        data class Faction(val id: Int, val name: String) : OpportunityContributionAttribute
        data class Character(val id: Int, val character: CharacterDetails?) : OpportunityContributionAttribute
        data class Corporation(val id: Int, val name: String?) : OpportunityContributionAttribute
        data class Alliance(val id: Int, val name: String?) : OpportunityContributionAttribute
    }

    /**
     * characterId is only used to fetch structure details if an attribute contains a structure
     */
    suspend operator fun invoke(originator: Originator, configuration: OpportunityConfiguration, characterId: Int): List<OpportunityContributionAttributeType> = withContext(Dispatchers.Default) {
        when (val configuration = configuration) {
            is OpportunityConfiguration.CaptureFwComplex -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_capture_fw_complex_complex_type_name.toUiText(),
                    description = Res.string.opportunities_attribute_capture_fw_complex_complex_type_description.toUiText(),
                    icon = Res.drawable.fw_complex_type_16px,
                    values = configuration.archetypes?.map(::mapArchetype) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_capture_fw_complex_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_capture_fw_complex_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_capture_fw_complex_faction_name.toUiText(),
                    description = Res.string.opportunities_attribute_capture_fw_complex_faction_description.toUiText(),
                    icon = Res.drawable.corporation_16px,
                    values = configuration.factions?.map(::mapFaction) ?: any(),
                ),
            )
            is OpportunityConfiguration.DamageShip -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_damage_ship_identities_name.toUiText(),
                    description = Res.string.opportunities_attribute_damage_ship_identities_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = configuration.identities?.mapAsync { mapIdentity(originator, it) }?.filterNotNull() ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_damage_ship_ships_name.toUiText(),
                    description = Res.string.opportunities_attribute_damage_ship_ships_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.ships?.mapNotNull(::mapShip) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_damage_ship_locations_name.toUiText(),
                    description = Res.string.opportunities_attribute_damage_ship_locations_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.DefendFwComplex -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_defend_fw_complex_complex_type_name.toUiText(),
                    description = Res.string.opportunities_attribute_defend_fw_complex_complex_type_description.toUiText(),
                    icon = Res.drawable.fw_complex_type_16px,
                    values = configuration.archetypes?.map(::mapArchetype) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_defend_fw_complex_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_defend_fw_complex_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_defend_fw_complex_faction_name.toUiText(),
                    description = Res.string.opportunities_attribute_defend_fw_complex_faction_description.toUiText(),
                    icon = Res.drawable.corporation_16px,
                    values = configuration.factions?.map(::mapFaction) ?: any(),
                ),
            )
            is OpportunityConfiguration.DeliverItem -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_deliver_item_item_name.toUiText(),
                    description = Res.string.opportunities_attribute_deliver_item_item_description.toUiText(),
                    icon = Res.drawable.map_marker_resources_storage,
                    values = configuration.items?.mapNotNull(::mapItem) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_deliver_item_destination_name.toUiText(),
                    description = Res.string.opportunities_attribute_deliver_item_destination_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.dockingLocations?.mapNotNull { mapDockableLocation(originator, it, characterId) }
                        ?: configuration.officeId?.let { listOf(mapOfficeId(it)) }
                        ?: any(Res.string.opportunities_attribute_any_office_value.toUiText()),
                ),
            )
            is OpportunityConfiguration.DestroyNpc -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_destroy_npc_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_destroy_npc_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.DestroyShip -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_destroy_ship_identities_name.toUiText(),
                    description = Res.string.opportunities_attribute_destroy_ship_identities_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = configuration.identities?.mapAsync { mapIdentity(originator, it) }?.filterNotNull() ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_destroy_ship_ships_name.toUiText(),
                    description = Res.string.opportunities_attribute_destroy_ship_ships_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.ships?.mapNotNull(::mapShip) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_destroy_ship_locations_name.toUiText(),
                    description = Res.string.opportunities_attribute_destroy_ship_locations_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.EarnLoyaltyPoint -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_earn_loyalty_point_corporations_name.toUiText(),
                    description = Res.string.opportunities_attribute_earn_loyalty_point_corporations_description.toUiText(),
                    icon = Res.drawable.contribution_method_earn_loyalty_point_16px,
                    values = configuration.corporations?.mapAsync { mapCorporationId(originator, it) } ?: any(),
                ),
            )
            is OpportunityConfiguration.LostShip -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_lost_ship_identities_name.toUiText(),
                    description = Res.string.opportunities_attribute_lost_ship_identities_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = configuration.identities?.mapAsync { mapIdentity(originator, it) }?.filterNotNull() ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_lost_ship_ship_name.toUiText(),
                    description = Res.string.opportunities_attribute_lost_ship_ship_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.ships?.mapNotNull(::mapShip) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_lost_ship_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_lost_ship_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            OpportunityConfiguration.Manual -> listOf()
            is OpportunityConfiguration.ManufactureItem -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_manufacture_item_owner_name.toUiText(),
                    description = Res.string.opportunities_attribute_manufacture_item_owner_description.toUiText(),
                    icon = Res.drawable.contribution_method_manual_16px,
                    values = listOf(mapOwnerType(configuration.owner)),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_manufacture_item_item_name.toUiText(),
                    description = Res.string.opportunities_attribute_manufacture_item_item_description.toUiText(),
                    icon = Res.drawable.map_marker_resources_storage,
                    values = configuration.items?.mapNotNull(::mapItem) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_manufacture_item_facility_name.toUiText(),
                    description = Res.string.opportunities_attribute_manufacture_item_facility_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.dockingLocations?.mapAsync {
                        mapDockableLocation(originator, it, characterId)
                    }?.filterNotNull() ?: any(),
                ),
            )
            is OpportunityConfiguration.MineMaterial -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mine_material_locations_name.toUiText(),
                    description = Res.string.opportunities_attribute_mine_material_locations_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mine_material_materials_name.toUiText(),
                    description = Res.string.opportunities_attribute_mine_material_materials_description.toUiText(),
                    icon = Res.drawable.mining_16px,
                    values = configuration.materials?.mapNotNull(::mapItem) ?: any(),
                ),
            )
            is OpportunityConfiguration.RemoteBoostShield -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_remote_boost_shield_identity_name.toUiText(),
                    description = Res.string.opportunities_attribute_remote_boost_shield_identity_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = configuration.identities?.mapAsync { mapIdentity(originator, it) }?.filterNotNull() ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_remote_boost_shield_ship_name.toUiText(),
                    description = Res.string.opportunities_attribute_remote_boost_shield_ship_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.ships?.mapNotNull(::mapShip) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_remote_boost_shield_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_remote_boost_shield_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.RemoteRepairArmor -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_remote_repair_armor_identity_name.toUiText(),
                    description = Res.string.opportunities_attribute_remote_repair_armor_identity_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = configuration.identities?.mapAsync { mapIdentity(originator, it) }?.filterNotNull() ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_remote_repair_armor_ship_name.toUiText(),
                    description = Res.string.opportunities_attribute_remote_repair_armor_ship_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.ships?.mapNotNull(::mapShip) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_remote_repair_armor_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_remote_repair_armor_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.SalvageWreck -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_salvage_wreck_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_salvage_wreck_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.ScanSignature -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_scan_signature_signature_name.toUiText(),
                    description = Res.string.opportunities_attribute_scan_signature_signature_description.toUiText(),
                    icon = Res.drawable.contribution_method_scan_signatures_16px,
                    values = configuration.signatures?.map(::mapSignatureTypeId) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_scan_signature_solar_system_name.toUiText(),
                    description = Res.string.opportunities_attribute_scan_signature_solar_system_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.ShipInsurance -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_ship_insurance_conflict_name.toUiText(),
                    description = Res.string.opportunities_attribute_ship_insurance_conflict_description.toUiText(),
                    icon = Res.drawable.contribution_method_manual_16px,
                    values = listOf(mapConflictType(configuration.conflictType)),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_ship_insurance_identities_name.toUiText(),
                    description = Res.string.opportunities_attribute_ship_insurance_identities_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = configuration.identities?.mapAsync { mapIdentity(originator, it) }?.filterNotNull() ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_ship_insurance_ships_name.toUiText(),
                    description = Res.string.opportunities_attribute_ship_insurance_ships_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.ships?.mapNotNull(::mapShip) ?: any(
                        if (configuration.reimburseImplants) {
                            Res.string.opportunities_attribute_any_ships_implants_covered_value.toUiText()
                        } else {
                            Res.string.opportunities_attribute_any_ships_implants_not_covered_value.toUiText()
                        },
                    ),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_ship_insurance_locations_name.toUiText(),
                    description = Res.string.opportunities_attribute_ship_insurance_locations_description.toUiText(),
                    icon = Res.drawable.location_16px,
                    values = configuration.locations?.mapNotNull(::mapLocation) ?: any(),
                ),
            )
            is OpportunityConfiguration.MercenaryTacticalOperation -> listOf(
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mercenary_tactical_operation_hostiles_name.toUiText(),
                    description = Res.string.opportunities_attribute_mercenary_tactical_operation_hostiles_description.toUiText(),
                    icon = Res.drawable.pilot_or_organization_16px,
                    values = listOf(
                        OpportunityContributionAttribute.Text(configuration.hostiles),
                    ),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mercenary_tactical_operation_status_name.toUiText(),
                    description = Res.string.opportunities_attribute_mercenary_tactical_operation_status_description.toUiText(),
                    icon = Res.drawable.navigate_forward_16px,
                    values = listOf(
                        OpportunityContributionAttribute.Text(configuration.status, isPlain = true),
                    ),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mercenary_tactical_operation_location_name.toUiText(),
                    description = Res.string.opportunities_attribute_mercenary_tactical_operation_location_description.toUiText(),
                    icon = Res.drawable.solar_system_16px,
                    values = listOfNotNull(
                        configuration.solarSystem?.let { OpportunityContributionAttribute.SolarSystem(it) },
                    ),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mercenary_tactical_operation_ship_restrictions_name.toUiText(),
                    description = Res.string.opportunities_attribute_mercenary_tactical_operation_ship_restrictions_description.toUiText(),
                    icon = Res.drawable.spaceship_command_16px,
                    values = configuration.allowedShipsLists?.let(::mapShipLists) ?: any(),
                ),
                OpportunityContributionAttributeType(
                    name = Res.string.opportunities_attribute_mercenary_tactical_operation_den_effects_name.toUiText(),
                    description = Res.string.opportunities_attribute_mercenary_tactical_operation_den_effects_description.toUiText(),
                    icon = Res.drawable.mercenary_den_16px,
                    values = listOf(
                        OpportunityContributionAttribute.Text(Res.string.opportunities_operation_development_impact_value.toUiText(configuration.developmentImpact.toUiTextArgument()), isPlain = true),
                        OpportunityContributionAttribute.Text(Res.string.opportunities_operation_anarchy_impact_value.toUiText(configuration.anarchyImpact.toUiTextArgument()), isPlain = true),
                        OpportunityContributionAttribute.Text(Res.string.opportunities_operation_infomorph_bonus_value.toUiText(configuration.infomorphBonus.toUiTextArgument()), isPlain = true),
                    ),
                ),
            )
            is OpportunityConfiguration.Unknown -> listOf()
        }
    }

    private fun mapLocation(location: Location): OpportunityContributionAttribute? {
        return when {
            location.solarSystemId != null -> solarSystemsRepository.getSystem(location.solarSystemId.toInt())
                ?.let { OpportunityContributionAttribute.SolarSystem(it) }
            location.constellationId != null -> solarSystemsRepository.getConstellation(location.constellationId.toInt())
                ?.let { OpportunityContributionAttribute.Constellation(it) }
            location.regionId != null -> solarSystemsRepository.getRegion(location.regionId.toInt())
                ?.let { OpportunityContributionAttribute.Region(it) }
            else -> null
        }
    }

    private fun mapItem(item: Item): OpportunityContributionAttribute? {
        return when {
            item.groupId != null -> {
                typesRepository.getGroupName(item.groupId.toInt())?.let {
                    OpportunityContributionAttribute.TypeGroup(it)
                }
            }
            item.typeId != null -> {
                typesRepository.getType(item.typeId.toInt())?.let {
                    OpportunityContributionAttribute.Type(it)
                }
            }
            else -> null
        }
    }

    private fun mapShipLists(lists: List<TypeList>): List<OpportunityContributionAttribute> {
        val allShips = lists.flatMap { list ->
            buildList {
                addAll(list.includedCategoryIDs?.flatMap { typesRepository.getTypesInCategory(it) } ?: emptyList())
                removeAll(list.excludedCategoryIDs?.flatMap { typesRepository.getTypesInCategory(it) } ?: emptyList())
                addAll(list.includedGroupIDs?.flatMap { typesRepository.getTypesInGroup(it) } ?: emptyList())
                removeAll(list.excludedGroupIDs?.flatMap { typesRepository.getTypesInGroup(it) } ?: emptyList())
                addAll(list.includedTypeIDs?.map { typesRepository.getType(it) } ?: emptyList())
                removeAll(list.excludedTypeIDs?.map { typesRepository.getType(it) } ?: emptyList())
            }.filterNotNull()
        }
        val includedShips = allShips.toMutableList()
        val includedGroups = buildList {
            allShips.map { it.groupId }.distinct().forEach { groupId ->
                val shipsInGroup = typesRepository.getTypesInGroup(groupId)
                if (shipsInGroup.all { it in allShips }) {
                    // The entire group is included
                    add(groupId)
                    includedShips.removeAll(shipsInGroup)
                }
            }
        }
        val groups = includedGroups.mapNotNull { groupId ->
            val name = typesRepository.getGroupName(groupId) ?: return@mapNotNull null
            ShipTreeGroups[name]
        }.sortedBy {
            it.id
        }.map {
            OpportunityContributionAttribute.ShipGroup(it.id, it.name, it.icon)
        }
        val ships = includedShips.map { ship ->
            OpportunityContributionAttribute.Ship(ship)
        }
        return groups + ships
    }

    private fun mapShip(item: Item): OpportunityContributionAttribute? {
        return when {
            item.groupId != null -> {
                ShipTreeGroups[item.groupId.toInt()]?.let {
                    OpportunityContributionAttribute.ShipGroup(it.id, it.name, it.icon)
                }
            }
            item.typeId != null -> {
                typesRepository.getType(item.typeId.toInt())?.let {
                    OpportunityContributionAttribute.Ship(it)
                }
            }
            else -> null
        }
    }

    private fun mapFaction(faction: Faction): OpportunityContributionAttribute {
        val name = factionsRepository.getFaction(faction.factionId.toInt()) ?: ""
        return OpportunityContributionAttribute.Faction(faction.factionId.toInt(), name)
    }

    private fun mapArchetype(archetype: Archetype): OpportunityContributionAttribute {
        return when (archetype.archetypeId.toInt()) {
            33 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_novice_complex_value.toUiText())
            34 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_small_complex_value.toUiText())
            35 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_medium_complex_value.toUiText())
            36 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_large_complex_value.toUiText())
            else -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_unknown_complex_value.toUiText())
        }
    }

    private suspend fun mapIdentity(originator: Originator, identity: Identity): OpportunityContributionAttribute? {
        return when {
            identity.characterId != null -> {
                val details = characterDetailsRepository.getCharacterDetails(originator, identity.characterId.toInt())
                OpportunityContributionAttribute.Character(identity.characterId.toInt(), details)
            }
            identity.corporationId != null -> {
                val name = characterDetailsRepository.getCorporationName(originator, identity.corporationId.toInt()).success?.name
                OpportunityContributionAttribute.Corporation(identity.corporationId.toInt(), name)
            }
            identity.allianceId != null -> {
                val name = characterDetailsRepository.getAllianceName(originator, identity.allianceId.toInt()).success?.name
                OpportunityContributionAttribute.Alliance(identity.allianceId.toInt(), name)
            }
            identity.factionId != null -> {
                val name = factionsRepository.getFaction(identity.factionId.toInt()) ?: ""
                OpportunityContributionAttribute.Faction(identity.factionId.toInt(), name)
            }
            else -> null
        }
    }

    private suspend fun mapCorporationId(originator: Originator, corporationId: CorporationId): OpportunityContributionAttribute {
        val name = characterDetailsRepository.getCorporationName(originator, corporationId.corporationId.toInt()).success?.name
        return OpportunityContributionAttribute.Corporation(corporationId.corporationId.toInt(), name)
    }

    private suspend fun mapDockableLocation(originator: Originator, dockableLocation: DockableLocation, characterId: Int): OpportunityContributionAttribute? {
        return when {
            dockableLocation.stationId != null -> {
                val station = locationRepository.getStation(originator, dockableLocation.stationId.toInt())
                val solarSystem = station?.solarSystemId?.let { solarSystemsRepository.getSystem(it) }
                OpportunityContributionAttribute.Station(station, solarSystem)
            }
            dockableLocation.structureId != null -> {
                if (originator == Originator.CorporationProjects) {
                    val structure = locationRepository.getStructure(originator, dockableLocation.structureId, characterId)
                    val solarSystem = structure?.solarSystemId?.let { solarSystemsRepository.getSystem(it) }
                    OpportunityContributionAttribute.Structure(structure, solarSystem)
                } else {
                    // For Freelance Jobs, we probably don't have access to the references structure, so there
                    // is no point trying to fetch it
                    OpportunityContributionAttribute.Structure(null, null)
                }
            }
            else -> null
        }
    }

    private fun mapOwnerType(ownerType: OwnerType): OpportunityContributionAttribute {
        return when (ownerType) {
            OwnerType.Any -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_owner_any_value.toUiText())
            OwnerType.Corporation -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_owner_corporation_value.toUiText())
            OwnerType.Character -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_owner_self_value.toUiText())
        }
    }

    private fun mapSignatureTypeId(signatureTypeId: SignatureTypeId): OpportunityContributionAttribute {
        return when (signatureTypeId.signatureTypeId.toInt()) {
            208 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_data_value.toUiText())
            209 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_gas_value.toUiText())
            210 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_relic_value.toUiText())
            211 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_ore_value.toUiText())
            1136 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_combat_value.toUiText())
            1908 -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_wormhole_value.toUiText())
            else -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_signature_unknown_value.toUiText())
        }
    }

    private fun mapConflictType(conflictType: ConflictType): OpportunityContributionAttribute {
        return when (conflictType) {
            ConflictType.Pvp -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_conflict_required_value.toUiText())
            ConflictType.Pve, ConflictType.Any -> OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_conflict_unrequired_value.toUiText())
        }
    }

    /**
     * Currently, ESI doesn't provide any way to resolve an office ID
     */
    @Suppress("unused")
    private fun mapOfficeId(officeId: Long): OpportunityContributionAttribute {
        return OpportunityContributionAttribute.Text(Res.string.opportunities_attribute_specific_office_value.toUiText())
    }

    private fun any(text: UiText = Res.string.opportunities_attribute_any_value.toUiText()): List<OpportunityContributionAttribute.Text> {
        return listOf(OpportunityContributionAttribute.Text(text, isPlain = true))
    }
}
