package dev.nohus.rift.intelscanner.compose

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_category
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_celestial_bodies
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_celestials
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_corpses
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_drones
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_ships
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_ships_by_class
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_stargates
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_stations
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_structures
import dev.nohus.rift.generated.resources.intel_scanner_dscan_heading_wrecks
import dev.nohus.rift.generated.resources.intel_scanner_dscan_unknown_category
import dev.nohus.rift.intelscanner.LoadDScanIntelUseCase.DScanItem
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.settings.persistence.IntelScannerDScanSection

data class DScanPaneModel(
    val title: UiText,
    val section: IntelScannerDScanSection,
    val content: DScanPaneContent,
    val id: String = section.name,
)

sealed interface DScanPaneContent {
    data class ShipTypes(val items: List<DScanItem>) : DScanPaneContent
    data class Ships(val items: List<DScanItem>) : DScanPaneContent
    data class Structures(val items: List<DScanItem>) : DScanPaneContent
    data class Stations(val items: List<DScanItem>) : DScanPaneContent
    data class CelestialBodies(val items: List<DScanItem>) : DScanPaneContent
    data class Category(val items: List<DScanItem>, val showGroup: Boolean = true) : DScanPaneContent
    data class Corpses(val items: List<DScanItem>) : DScanPaneContent
}

fun getDScanPanes(
    items: List<DScanItem>,
    enabledSections: Set<IntelScannerDScanSection>,
): List<DScanPaneModel> {
    val ships = items.filter { it.typeCategory == "Ship" }
    val structures = items.filter { it.typeCategory == "Structure" }
    val wrecks = items.filter { it.typeGroup == "Wreck" }
    val corpses = items.filter { it.typeGroup == "Biomass" }
    val celestialBodies = items.filter { it.typeGroup in listOf("Moon", "Planet", "Sun") }
    val stargates = items.filter { it.typeGroup == "Stargate" }
    val celestials = items.filter { it.typeCategory == "Celestial" } - celestialBodies.toSet() - stargates.toSet() - wrecks.toSet() - corpses.toSet()
    val stations = items.filter { it.typeCategory == "Station" }
    val drones = items.filter { it.typeCategory == "Drone" }
    val otherItems = items - ships.toSet() - structures.toSet() - wrecks.toSet() - corpses.toSet() - celestialBodies.toSet() - stargates.toSet() - celestials.toSet() - stations.toSet() - drones.toSet()

    return buildList {
        fun addPane(title: UiText, section: IntelScannerDScanSection, content: DScanPaneContent, id: String = section.name) {
            if (section in enabledSections) add(DScanPaneModel(title, section, content, id))
        }

        if (ships.isNotEmpty()) {
            addPane(Res.string.intel_scanner_dscan_heading_ships_by_class.toUiText(ships.size.toUiTextArgument()), IntelScannerDScanSection.ShipsByClass, DScanPaneContent.ShipTypes(ships))
            addPane(Res.string.intel_scanner_dscan_heading_ships.toUiText(ships.size.toUiTextArgument()), IntelScannerDScanSection.Ships, DScanPaneContent.Ships(ships))
        }
        if (structures.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_structures.toUiText(structures.size.toUiTextArgument()), IntelScannerDScanSection.Structures, DScanPaneContent.Structures(structures))
        if (stations.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_stations.toUiText(stations.size.toUiTextArgument()), IntelScannerDScanSection.Stations, DScanPaneContent.Stations(stations))
        if (celestialBodies.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_celestial_bodies.toUiText(celestialBodies.size.toUiTextArgument()), IntelScannerDScanSection.CelestialBodies, DScanPaneContent.CelestialBodies(celestialBodies))
        if (stargates.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_stargates.toUiText(stargates.size.toUiTextArgument()), IntelScannerDScanSection.Stargates, DScanPaneContent.CelestialBodies(stargates))
        if (celestials.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_celestials.toUiText(celestials.size.toUiTextArgument()), IntelScannerDScanSection.Celestials, DScanPaneContent.Category(celestials))
        if (wrecks.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_wrecks.toUiText(wrecks.size.toUiTextArgument()), IntelScannerDScanSection.Wrecks, DScanPaneContent.Category(wrecks, showGroup = false))
        if (drones.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_drones.toUiText(drones.size.toUiTextArgument()), IntelScannerDScanSection.Drones, DScanPaneContent.Category(drones))
        if (corpses.isNotEmpty()) addPane(Res.string.intel_scanner_dscan_heading_corpses.toUiText(corpses.size.toUiTextArgument()), IntelScannerDScanSection.Corpses, DScanPaneContent.Corpses(corpses))
        otherItems.groupBy { it.typeCategory }.forEach { [category, categoryItems] ->
            val title = category?.toUiText() ?: Res.string.intel_scanner_dscan_unknown_category.toUiText()
            addPane(Res.string.intel_scanner_dscan_heading_category.toUiText(title.toUiTextArgument(), categoryItems.size.toUiTextArgument()), IntelScannerDScanSection.Other, DScanPaneContent.Category(categoryItems), "Other:${category.orEmpty()}")
        }
    }
}
