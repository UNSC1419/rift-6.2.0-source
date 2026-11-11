package dev.nohus.rift.assets

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.assets_location_flag_ammo_hold
import dev.nohus.rift.generated.resources.assets_location_flag_asteroid_hold
import dev.nohus.rift.generated.resources.assets_location_flag_bonus
import dev.nohus.rift.generated.resources.assets_location_flag_booster
import dev.nohus.rift.generated.resources.assets_location_flag_booster_hold
import dev.nohus.rift.generated.resources.assets_location_flag_capsule
import dev.nohus.rift.generated.resources.assets_location_flag_cargo
import dev.nohus.rift.generated.resources.assets_location_flag_command_center_hold
import dev.nohus.rift.generated.resources.assets_location_flag_corporation_deliveries
import dev.nohus.rift.generated.resources.assets_location_flag_corpse_hold
import dev.nohus.rift.generated.resources.assets_location_flag_crate_loot
import dev.nohus.rift.generated.resources.assets_location_flag_deliveries
import dev.nohus.rift.generated.resources.assets_location_flag_drone_bay
import dev.nohus.rift.generated.resources.assets_location_flag_expedition_hold
import dev.nohus.rift.generated.resources.assets_location_flag_fighter_bay
import dev.nohus.rift.generated.resources.assets_location_flag_fighter_tube
import dev.nohus.rift.generated.resources.assets_location_flag_fleet_hangar
import dev.nohus.rift.generated.resources.assets_location_flag_frigate_escape_bay
import dev.nohus.rift.generated.resources.assets_location_flag_fuel_hold
import dev.nohus.rift.generated.resources.assets_location_flag_gas_hold
import dev.nohus.rift.generated.resources.assets_location_flag_hidden_modifiers
import dev.nohus.rift.generated.resources.assets_location_flag_high_slot
import dev.nohus.rift.generated.resources.assets_location_flag_ice_hold
import dev.nohus.rift.generated.resources.assets_location_flag_implant
import dev.nohus.rift.generated.resources.assets_location_flag_impounded
import dev.nohus.rift.generated.resources.assets_location_flag_industrial_ship_hold
import dev.nohus.rift.generated.resources.assets_location_flag_infrastructure_hangar
import dev.nohus.rift.generated.resources.assets_location_flag_junkyard_reprocessed
import dev.nohus.rift.generated.resources.assets_location_flag_junkyard_trashed
import dev.nohus.rift.generated.resources.assets_location_flag_large_ship_hold
import dev.nohus.rift.generated.resources.assets_location_flag_locked
import dev.nohus.rift.generated.resources.assets_location_flag_low_slot
import dev.nohus.rift.generated.resources.assets_location_flag_material_hold
import dev.nohus.rift.generated.resources.assets_location_flag_medium_ship_hold
import dev.nohus.rift.generated.resources.assets_location_flag_medium_slot
import dev.nohus.rift.generated.resources.assets_location_flag_mineral_hold
import dev.nohus.rift.generated.resources.assets_location_flag_mobile_depot_hold
import dev.nohus.rift.generated.resources.assets_location_flag_moon_materials_hold
import dev.nohus.rift.generated.resources.assets_location_flag_ore_hold
import dev.nohus.rift.generated.resources.assets_location_flag_pilot
import dev.nohus.rift.generated.resources.assets_location_flag_planet_surface
import dev.nohus.rift.generated.resources.assets_location_flag_planetary_commodities_hold
import dev.nohus.rift.generated.resources.assets_location_flag_quafe_hold
import dev.nohus.rift.generated.resources.assets_location_flag_quantum_core_room
import dev.nohus.rift.generated.resources.assets_location_flag_reward
import dev.nohus.rift.generated.resources.assets_location_flag_rig_slot
import dev.nohus.rift.generated.resources.assets_location_flag_salvage_hold
import dev.nohus.rift.generated.resources.assets_location_flag_secondary_storage
import dev.nohus.rift.generated.resources.assets_location_flag_service_slot
import dev.nohus.rift.generated.resources.assets_location_flag_ship_hangar
import dev.nohus.rift.generated.resources.assets_location_flag_ship_hold
import dev.nohus.rift.generated.resources.assets_location_flag_ship_offline
import dev.nohus.rift.generated.resources.assets_location_flag_skill
import dev.nohus.rift.generated.resources.assets_location_flag_skill_in_training
import dev.nohus.rift.generated.resources.assets_location_flag_small_ship_hold
import dev.nohus.rift.generated.resources.assets_location_flag_structure_active
import dev.nohus.rift.generated.resources.assets_location_flag_structure_fuel
import dev.nohus.rift.generated.resources.assets_location_flag_structure_inactive
import dev.nohus.rift.generated.resources.assets_location_flag_structure_offline
import dev.nohus.rift.generated.resources.assets_location_flag_subsystem
import dev.nohus.rift.generated.resources.assets_location_flag_subsystem_hold
import dev.nohus.rift.generated.resources.assets_location_flag_wallet
import dev.nohus.rift.generated.resources.assets_location_flag_wardrobe
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument

object LocationFlags {

    private val flags = mapOf(
        "AssetSafety" to null, // Known from location
        "AutoFit" to null, // Inside a container
        "Bonus" to Res.string.assets_location_flag_bonus.toUiText(),
        "Booster" to Res.string.assets_location_flag_booster.toUiText(),
        "BoosterBay" to Res.string.assets_location_flag_booster_hold.toUiText(),
        "Capsule" to Res.string.assets_location_flag_capsule.toUiText(),
        "CapsuleerDeliveries" to null, // Shown inside a corporation container
        "Cargo" to Res.string.assets_location_flag_cargo.toUiText(),
        "CorpDeliveries" to Res.string.assets_location_flag_corporation_deliveries.toUiText(),
        "CorpSAG1" to null, // Shown inside a corporation container
        "CorpSAG2" to null,
        "CorpSAG3" to null,
        "CorpSAG4" to null,
        "CorpSAG5" to null,
        "CorpSAG6" to null,
        "CorpSAG7" to null,
        "CorporationGoalDeliveries" to null,
        "CrateLoot" to Res.string.assets_location_flag_crate_loot.toUiText(),
        "CorpseBay" to Res.string.assets_location_flag_corpse_hold.toUiText(),
        "Deliveries" to Res.string.assets_location_flag_deliveries.toUiText(),
        "DroneBay" to Res.string.assets_location_flag_drone_bay.toUiText(),
        "ExpeditionHold" to Res.string.assets_location_flag_expedition_hold.toUiText(),
        "FighterBay" to Res.string.assets_location_flag_fighter_bay.toUiText(),
        "FighterTube0" to Res.string.assets_location_flag_fighter_tube.toUiText(1.toUiTextArgument()),
        "FighterTube1" to Res.string.assets_location_flag_fighter_tube.toUiText(2.toUiTextArgument()),
        "FighterTube2" to Res.string.assets_location_flag_fighter_tube.toUiText(3.toUiTextArgument()),
        "FighterTube3" to Res.string.assets_location_flag_fighter_tube.toUiText(4.toUiTextArgument()),
        "FighterTube4" to Res.string.assets_location_flag_fighter_tube.toUiText(5.toUiTextArgument()),
        "FleetHangar" to Res.string.assets_location_flag_fleet_hangar.toUiText(),
        "FrigateEscapeBay" to Res.string.assets_location_flag_frigate_escape_bay.toUiText(),
        "Hangar" to null, // Inside a hangar
        "HangarAll" to null,
        "HiSlot0" to Res.string.assets_location_flag_high_slot.toUiText(1.toUiTextArgument()),
        "HiSlot1" to Res.string.assets_location_flag_high_slot.toUiText(2.toUiTextArgument()),
        "HiSlot2" to Res.string.assets_location_flag_high_slot.toUiText(3.toUiTextArgument()),
        "HiSlot3" to Res.string.assets_location_flag_high_slot.toUiText(4.toUiTextArgument()),
        "HiSlot4" to Res.string.assets_location_flag_high_slot.toUiText(5.toUiTextArgument()),
        "HiSlot5" to Res.string.assets_location_flag_high_slot.toUiText(6.toUiTextArgument()),
        "HiSlot6" to Res.string.assets_location_flag_high_slot.toUiText(7.toUiTextArgument()),
        "HiSlot7" to Res.string.assets_location_flag_high_slot.toUiText(8.toUiTextArgument()),
        "HiddenModifiers" to Res.string.assets_location_flag_hidden_modifiers.toUiText(),
        "Implant" to Res.string.assets_location_flag_implant.toUiText(),
        "Impounded" to Res.string.assets_location_flag_impounded.toUiText(),
        "InfrastructureHangar" to Res.string.assets_location_flag_infrastructure_hangar.toUiText(),
        "JunkyardReprocessed" to Res.string.assets_location_flag_junkyard_reprocessed.toUiText(),
        "JunkyardTrashed" to Res.string.assets_location_flag_junkyard_trashed.toUiText(),
        "LoSlot0" to Res.string.assets_location_flag_low_slot.toUiText(1.toUiTextArgument()),
        "LoSlot1" to Res.string.assets_location_flag_low_slot.toUiText(2.toUiTextArgument()),
        "LoSlot2" to Res.string.assets_location_flag_low_slot.toUiText(3.toUiTextArgument()),
        "LoSlot3" to Res.string.assets_location_flag_low_slot.toUiText(4.toUiTextArgument()),
        "LoSlot4" to Res.string.assets_location_flag_low_slot.toUiText(5.toUiTextArgument()),
        "LoSlot5" to Res.string.assets_location_flag_low_slot.toUiText(6.toUiTextArgument()),
        "LoSlot6" to Res.string.assets_location_flag_low_slot.toUiText(7.toUiTextArgument()),
        "LoSlot7" to Res.string.assets_location_flag_low_slot.toUiText(8.toUiTextArgument()),
        "Locked" to Res.string.assets_location_flag_locked.toUiText(),
        "MedSlot0" to Res.string.assets_location_flag_medium_slot.toUiText(1.toUiTextArgument()),
        "MedSlot1" to Res.string.assets_location_flag_medium_slot.toUiText(2.toUiTextArgument()),
        "MedSlot2" to Res.string.assets_location_flag_medium_slot.toUiText(3.toUiTextArgument()),
        "MedSlot3" to Res.string.assets_location_flag_medium_slot.toUiText(4.toUiTextArgument()),
        "MedSlot4" to Res.string.assets_location_flag_medium_slot.toUiText(5.toUiTextArgument()),
        "MedSlot5" to Res.string.assets_location_flag_medium_slot.toUiText(6.toUiTextArgument()),
        "MedSlot6" to Res.string.assets_location_flag_medium_slot.toUiText(7.toUiTextArgument()),
        "MedSlot7" to Res.string.assets_location_flag_medium_slot.toUiText(8.toUiTextArgument()),
        "MobileDepotHold" to Res.string.assets_location_flag_mobile_depot_hold.toUiText(),
        "MoonMaterialBay" to Res.string.assets_location_flag_moon_materials_hold.toUiText(),
        "OfficeFolder" to null,
        "Pilot" to Res.string.assets_location_flag_pilot.toUiText(),
        "PlanetSurface" to Res.string.assets_location_flag_planet_surface.toUiText(),
        "QuafeBay" to Res.string.assets_location_flag_quafe_hold.toUiText(),
        "QuantumCoreRoom" to Res.string.assets_location_flag_quantum_core_room.toUiText(),
        "Reward" to Res.string.assets_location_flag_reward.toUiText(),
        "RigSlot0" to Res.string.assets_location_flag_rig_slot.toUiText(1.toUiTextArgument()),
        "RigSlot1" to Res.string.assets_location_flag_rig_slot.toUiText(2.toUiTextArgument()),
        "RigSlot2" to Res.string.assets_location_flag_rig_slot.toUiText(3.toUiTextArgument()),
        "RigSlot3" to Res.string.assets_location_flag_rig_slot.toUiText(4.toUiTextArgument()),
        "RigSlot4" to Res.string.assets_location_flag_rig_slot.toUiText(5.toUiTextArgument()),
        "RigSlot5" to Res.string.assets_location_flag_rig_slot.toUiText(6.toUiTextArgument()),
        "RigSlot6" to Res.string.assets_location_flag_rig_slot.toUiText(7.toUiTextArgument()),
        "RigSlot7" to Res.string.assets_location_flag_rig_slot.toUiText(8.toUiTextArgument()),
        "SecondaryStorage" to Res.string.assets_location_flag_secondary_storage.toUiText(),
        "ServiceSlot0" to Res.string.assets_location_flag_service_slot.toUiText(1.toUiTextArgument()),
        "ServiceSlot1" to Res.string.assets_location_flag_service_slot.toUiText(2.toUiTextArgument()),
        "ServiceSlot2" to Res.string.assets_location_flag_service_slot.toUiText(3.toUiTextArgument()),
        "ServiceSlot3" to Res.string.assets_location_flag_service_slot.toUiText(4.toUiTextArgument()),
        "ServiceSlot4" to Res.string.assets_location_flag_service_slot.toUiText(5.toUiTextArgument()),
        "ServiceSlot5" to Res.string.assets_location_flag_service_slot.toUiText(6.toUiTextArgument()),
        "ServiceSlot6" to Res.string.assets_location_flag_service_slot.toUiText(7.toUiTextArgument()),
        "ServiceSlot7" to Res.string.assets_location_flag_service_slot.toUiText(8.toUiTextArgument()),
        "ShipHangar" to Res.string.assets_location_flag_ship_hangar.toUiText(),
        "ShipOffline" to Res.string.assets_location_flag_ship_offline.toUiText(),
        "Skill" to Res.string.assets_location_flag_skill.toUiText(),
        "SkillInTraining" to Res.string.assets_location_flag_skill_in_training.toUiText(),
        "SpecializedAmmoHold" to Res.string.assets_location_flag_ammo_hold.toUiText(),
        "SpecializedAsteroidHold" to Res.string.assets_location_flag_asteroid_hold.toUiText(),
        "SpecializedCommandCenterHold" to Res.string.assets_location_flag_command_center_hold.toUiText(),
        "SpecializedFuelBay" to Res.string.assets_location_flag_fuel_hold.toUiText(),
        "SpecializedGasHold" to Res.string.assets_location_flag_gas_hold.toUiText(),
        "SpecializedIceHold" to Res.string.assets_location_flag_ice_hold.toUiText(),
        "SpecializedIndustrialShipHold" to Res.string.assets_location_flag_industrial_ship_hold.toUiText(),
        "SpecializedLargeShipHold" to Res.string.assets_location_flag_large_ship_hold.toUiText(),
        "SpecializedMaterialBay" to Res.string.assets_location_flag_material_hold.toUiText(),
        "SpecializedMediumShipHold" to Res.string.assets_location_flag_medium_ship_hold.toUiText(),
        "SpecializedMineralHold" to Res.string.assets_location_flag_mineral_hold.toUiText(),
        "SpecializedOreHold" to Res.string.assets_location_flag_ore_hold.toUiText(),
        "SpecializedPlanetaryCommoditiesHold" to Res.string.assets_location_flag_planetary_commodities_hold.toUiText(),
        "SpecializedSalvageHold" to Res.string.assets_location_flag_salvage_hold.toUiText(),
        "SpecializedShipHold" to Res.string.assets_location_flag_ship_hold.toUiText(),
        "SpecializedSmallShipHold" to Res.string.assets_location_flag_small_ship_hold.toUiText(),
        "StructureActive" to Res.string.assets_location_flag_structure_active.toUiText(),
        "StructureFuel" to Res.string.assets_location_flag_structure_fuel.toUiText(),
        "StructureInactive" to Res.string.assets_location_flag_structure_inactive.toUiText(),
        "StructureOffline" to Res.string.assets_location_flag_structure_offline.toUiText(),
        "SubSystemBay" to Res.string.assets_location_flag_subsystem_hold.toUiText(),
        "SubSystemSlot0" to Res.string.assets_location_flag_subsystem.toUiText(1.toUiTextArgument()),
        "SubSystemSlot1" to Res.string.assets_location_flag_subsystem.toUiText(2.toUiTextArgument()),
        "SubSystemSlot2" to Res.string.assets_location_flag_subsystem.toUiText(3.toUiTextArgument()),
        "SubSystemSlot3" to Res.string.assets_location_flag_subsystem.toUiText(4.toUiTextArgument()),
        "SubSystemSlot4" to Res.string.assets_location_flag_subsystem.toUiText(5.toUiTextArgument()),
        "SubSystemSlot5" to Res.string.assets_location_flag_subsystem.toUiText(6.toUiTextArgument()),
        "SubSystemSlot6" to Res.string.assets_location_flag_subsystem.toUiText(7.toUiTextArgument()),
        "SubSystemSlot7" to Res.string.assets_location_flag_subsystem.toUiText(8.toUiTextArgument()),
        "Unlocked" to null, // Inside an unlocked container
        "Wallet" to Res.string.assets_location_flag_wallet.toUiText(),
        "Wardrobe" to Res.string.assets_location_flag_wardrobe.toUiText(),
    )

    fun getName(flag: String): UiText? {
        return flags[flag]
    }
}
