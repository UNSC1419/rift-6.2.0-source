package dev.nohus.rift.planetaryindustry.models

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.planetary_industry_commodity_raw_resource
import dev.nohus.rift.generated.resources.planetary_industry_commodity_tier_1
import dev.nohus.rift.generated.resources.planetary_industry_commodity_tier_2
import dev.nohus.rift.generated.resources.planetary_industry_commodity_tier_3
import dev.nohus.rift.generated.resources.planetary_industry_commodity_tier_4
import dev.nohus.rift.generated.resources.planetary_industry_commodity_unknown
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText

object Commodities {

    private val p0Names = listOf(
        "Aqueous Liquids", "Autotrophs", "Base Metals", "Carbon Compounds",
        "Complex Organisms", "Felsic Magma", "Heavy Metals", "Ionic Solutions", "Microorganisms", "Noble Gas",
        "Noble Metals", "Non-CS Crystals", "Planktic Colonies", "Reactive Gas", "Suspended Plasma",
    )
    private val p1Names = listOf(
        "Water", "Industrial Fibers", "Reactive Metals", "Biofuels", "Proteins",
        "Silicon", "Toxic Metals", "Electrolytes", "Bacteria", "Oxygen", "Precious Metals", "Chiral Structures", "Biomass",
        "Oxidizing Compound", "Plasmoids",
    )
    private val p2Names = listOf(
        "Biocells", "Construction Blocks", "Consumer Electronics", "Coolant",
        "Enriched Uranium", "Fertilizer", "Genetically Enhanced Livestock", "Livestock", "Mechanical Parts",
        "Microfiber Shielding", "Miniature Electronics", "Nanites", "Oxides", "Polyaramids", "Polytextiles",
        "Rocket Fuel", "Silicate Glass", "Superconductors", "Supertensile Plastics", "Synthetic Oil", "Test Cultures",
        "Transmitter", "Viral Agent", "Water-Cooled CPU",
    )
    private val p3Names = listOf(
        "Biotech Research Reports", "Camera Drones", "Condensates",
        "Cryoprotectant Solution", "Data Chips", "Gel-Matrix Biopaste", "Guidance Systems", "Hazmat Detection Systems",
        "Hermetic Membranes", "High-Tech Transmitters", "Industrial Explosives", "Neocoms", "Nuclear Reactors",
        "Planetary Vehicles", "Robotics", "Smartfab Units", "Supercomputers", "Synthetic Synapses",
        "Transcranial Microcontrollers", "Ukomi Superconductors", "Vaccines",
    )
    private val p4Names = listOf(
        "Broadcast Node",
        "Integrity Response Drones",
        "Nano-Factory",
        "Organic Mortar Applicators",
        "Recursive Computing Module",
        "Self-Harmonizing Power Core",
        "Sterile Conduits",
        "Wetware Mainframe",
    )

    fun getTierName(commodity: String): UiText {
        return when (commodity) {
            in p0Names -> Res.string.planetary_industry_commodity_raw_resource.toUiText()
            in p1Names -> Res.string.planetary_industry_commodity_tier_1.toUiText()
            in p2Names -> Res.string.planetary_industry_commodity_tier_2.toUiText()
            in p3Names -> Res.string.planetary_industry_commodity_tier_3.toUiText()
            in p4Names -> Res.string.planetary_industry_commodity_tier_4.toUiText()
            else -> Res.string.planetary_industry_commodity_unknown.toUiText()
        }
    }
}
