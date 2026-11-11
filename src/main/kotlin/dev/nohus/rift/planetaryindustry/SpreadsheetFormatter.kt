package dev.nohus.rift.planetaryindustry

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.planetary_industry_export_character_header
import dev.nohus.rift.generated.resources.planetary_industry_export_character_id_header
import dev.nohus.rift.generated.resources.planetary_industry_export_character_name_header
import dev.nohus.rift.generated.resources.planetary_industry_export_expires_at_date_header
import dev.nohus.rift.generated.resources.planetary_industry_export_expires_at_header
import dev.nohus.rift.generated.resources.planetary_industry_export_expiry_reason_header
import dev.nohus.rift.generated.resources.planetary_industry_export_extraction_rate_header
import dev.nohus.rift.generated.resources.planetary_industry_export_extractor_expired_status
import dev.nohus.rift.generated.resources.planetary_industry_export_extractor_inactive_status
import dev.nohus.rift.generated.resources.planetary_industry_export_factory_idle_status
import dev.nohus.rift.generated.resources.planetary_industry_export_final_product_header
import dev.nohus.rift.generated.resources.planetary_industry_export_final_products_header
import dev.nohus.rift.generated.resources.planetary_industry_export_input_not_routed_status
import dev.nohus.rift.generated.resources.planetary_industry_export_is_working_header
import dev.nohus.rift.generated.resources.planetary_industry_export_output_not_routed_status
import dev.nohus.rift.generated.resources.planetary_industry_export_planet_name_header
import dev.nohus.rift.generated.resources.planetary_industry_export_planet_type_header
import dev.nohus.rift.generated.resources.planetary_industry_export_product_capacity_header
import dev.nohus.rift.generated.resources.planetary_industry_export_static_status
import dev.nohus.rift.generated.resources.planetary_industry_export_status_header
import dev.nohus.rift.generated.resources.planetary_industry_export_storage_full_status
import dev.nohus.rift.generated.resources.planetary_industry_export_stored_quantity_header
import dev.nohus.rift.generated.resources.planetary_industry_export_system_header
import dev.nohus.rift.generated.resources.planetary_industry_export_system_id_header
import dev.nohus.rift.generated.resources.planetary_industry_export_system_name_header
import dev.nohus.rift.generated.resources.planetary_industry_export_used_capacity_header
import dev.nohus.rift.generated.resources.planetary_industry_export_used_other_capacity_header
import dev.nohus.rift.generated.resources.planetary_industry_export_used_product_capacity_header
import dev.nohus.rift.generated.resources.planetary_industry_extracting_status
import dev.nohus.rift.generated.resources.planetary_industry_idle_status
import dev.nohus.rift.generated.resources.planetary_industry_needs_attention_status
import dev.nohus.rift.generated.resources.planetary_industry_not_setup_status
import dev.nohus.rift.generated.resources.planetary_industry_producing_status
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.planetaryindustry.CopyType.Excel
import dev.nohus.rift.planetaryindustry.CopyType.ExcelWithAddin
import dev.nohus.rift.planetaryindustry.CopyType.GoogleSheets
import dev.nohus.rift.planetaryindustry.PlanetaryIndustryRepository.ColonyItem
import dev.nohus.rift.planetaryindustry.models.Colony
import dev.nohus.rift.planetaryindustry.models.ColonyStatus
import dev.nohus.rift.planetaryindustry.models.ColonyStatus.Extracting
import dev.nohus.rift.planetaryindustry.models.ColonyStatus.Idle
import dev.nohus.rift.planetaryindustry.models.ColonyStatus.NeedsAttention
import dev.nohus.rift.planetaryindustry.models.ColonyStatus.NotSetup
import dev.nohus.rift.planetaryindustry.models.ColonyStatus.Producing
import dev.nohus.rift.planetaryindustry.models.Pin
import dev.nohus.rift.planetaryindustry.models.PinStatus
import dev.nohus.rift.planetaryindustry.simulation.ExtractionSimulation.Companion.getProgramOutputPrediction
import dev.nohus.rift.types.TypesRepository.Type
import java.time.Duration
import java.util.Locale
import kotlin.math.roundToInt

object SpreadsheetFormatter {

    suspend fun format(type: CopyType, items: List<ColonyItem>, appLocale: AppLocale): String {
        return when (type) {
            GoogleSheets -> formatForGoogleSheets(items, appLocale)
            Excel -> formatForExcel(items, appLocale)
            ExcelWithAddin -> formatForExcelWithAddin(items, appLocale)
        }
    }

    private suspend fun formatForGoogleSheets(items: List<ColonyItem>, appLocale: AppLocale): String {
        val types = getAllTypes(items)
        val extractedTypes = getAllExtractedTypes(items)
        val rows = items.map { item ->
            val colony = item.colony
            val contents = getColonyContents(colony)
            val finalProducts = colony.overview.finalProducts.joinToString(",") { it.name }
            val status = colony.status.getDisplayName(appLocale)
            val totalUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.finalProductsUsedCapacity + colony.overview.otherUsedCapacity)
            val finalProductsUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.finalProductsUsedCapacity)
            val otherUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.otherUsedCapacity)
            val expiryTimestamp = getGoogleSheetsExpiryTimestamp(item)
            val expiryReasons = getExpiryReason(item.ffwdColony.status, appLocale)
            val averagesPerHourExtracted = getAveragesPerHourExtracted(colony)
            listOf(
                item.characterName,
                colony.characterId,
                colony.planet.name,
                colony.type.name,
                colony.system.name,
                colony.system.id,
                status,
                colony.status.isWorking,
                finalProducts,
                colony.overview.capacity,
                totalUsedCapacity,
                finalProductsUsedCapacity,
                otherUsedCapacity,
                expiryTimestamp,
                expiryReasons,
                *extractedTypes.map { averagesPerHourExtracted[it] ?: 0 }.toTypedArray(),
                *types.map { contents[it] ?: 0 }.toTypedArray(),
            ).joinToString(separator = "\t")
        }.joinToString("\n")
        val headers = listOf(
            Res.string.planetary_industry_export_character_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_character_id_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_planet_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_planet_type_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_system_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_system_id_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_status_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_is_working_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_final_products_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_product_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_product_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_other_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_expires_at_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_expiry_reason_header.toUiText().resolve(appLocale),
            *extractedTypes.map { Res.string.planetary_industry_export_extraction_rate_header.toUiText(it.name.toUiTextArgument()).resolve(appLocale) }.toTypedArray(),
            *types.map { Res.string.planetary_industry_export_stored_quantity_header.toUiText(it.name.toUiTextArgument()).resolve(appLocale) }.toTypedArray(),
        ).joinToString("\t")
        return "$headers\n$rows"
    }

    private suspend fun formatForExcel(items: List<ColonyItem>, appLocale: AppLocale): String {
        val types = getAllTypes(items)
        val extractedTypes = getAllExtractedTypes(items)
        val rows = items.map { item ->
            val colony = item.colony
            val contents = getColonyContents(colony)
            val finalProducts = colony.overview.finalProducts.joinToString(",") { it.name }
            val status = colony.status.getDisplayName(appLocale)
            val totalUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.finalProductsUsedCapacity + colony.overview.otherUsedCapacity)
            val finalProductsUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.finalProductsUsedCapacity)
            val otherUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.otherUsedCapacity)
            val expiryTimestamp = getExcelExpiryTimestamp(item)
            val expiryReasons = getExpiryReason(item.ffwdColony.status, appLocale)
            val averagesPerHourExtracted = getAveragesPerHourExtracted(colony)
            listOf(
                item.characterName,
                colony.characterId,
                colony.planet.name,
                colony.type.name,
                colony.system.name,
                colony.system.id,
                status,
                colony.status.isWorking,
                finalProducts,
                colony.overview.capacity,
                totalUsedCapacity,
                finalProductsUsedCapacity,
                otherUsedCapacity,
                expiryTimestamp,
                expiryReasons,
                *extractedTypes.map { averagesPerHourExtracted[it] ?: 0 }.toTypedArray(),
                *types.map { contents[it] ?: 0 }.toTypedArray(),
            ).joinToString(separator = "\t")
        }.joinToString("\n")
        val headers = listOf(
            Res.string.planetary_industry_export_character_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_character_id_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_planet_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_planet_type_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_system_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_system_id_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_status_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_is_working_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_final_products_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_product_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_product_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_other_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_expires_at_date_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_expiry_reason_header.toUiText().resolve(appLocale),
            *extractedTypes.map { Res.string.planetary_industry_export_extraction_rate_header.toUiText(it.name.toUiTextArgument()).resolve(appLocale) }.toTypedArray(),
            *types.map { Res.string.planetary_industry_export_stored_quantity_header.toUiText(it.name.toUiTextArgument()).resolve(appLocale) }.toTypedArray(),
        ).joinToString("\t")
        return "$headers\n$rows"
    }

    private suspend fun formatForExcelWithAddin(items: List<ColonyItem>, appLocale: AppLocale): String {
        val types = getAllTypes(items)
        val extractedTypes = getAllExtractedTypes(items)
        val maxFinalProducts = items.maxOf { it.colony.overview.finalProducts.size }
        val rows = items.map { item ->
            val colony = item.colony
            val contents = getColonyContents(colony)
            val finalProducts = colony.overview.finalProducts.map {
                "=EVEONLINE.TYPE(${it.id})"
            }.toTypedArray()
            val status = colony.status.getDisplayName(appLocale)
            val totalUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.finalProductsUsedCapacity + colony.overview.otherUsedCapacity)
            val finalProductsUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.finalProductsUsedCapacity)
            val otherUsedCapacity = String.format(Locale.ROOT, "%.02f", colony.overview.otherUsedCapacity)
            val expiryTimestamp = getExcelExpiryTimestamp(item)
            val expiryReasons = getExpiryReason(item.ffwdColony.status, appLocale)
            val averagesPerHourExtracted = getAveragesPerHourExtracted(colony)
            listOf(
                "=EVEONLINE.CHARACTER(${colony.characterId})",
                colony.planet.name,
                "=EVEONLINE.TYPE(${colony.planet.type.typeId})",
                "=EVEONLINE.SOLARSYSTEM(${colony.system.id})",
                status,
                colony.status.isWorking,
                *finalProducts,
                *List(maxFinalProducts - finalProducts.size) { "" }.toTypedArray(),
                colony.overview.capacity,
                totalUsedCapacity,
                finalProductsUsedCapacity,
                otherUsedCapacity,
                expiryTimestamp,
                expiryReasons,
                *extractedTypes.map { averagesPerHourExtracted[it] ?: 0 }.toTypedArray(),
                *types.map { contents[it] ?: 0 }.toTypedArray(),
            ).joinToString(separator = "\t")
        }.joinToString("\n")
        val headers = listOf(
            Res.string.planetary_industry_export_character_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_planet_name_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_planet_type_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_system_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_status_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_is_working_header.toUiText().resolve(appLocale),
            *List(maxFinalProducts) { Res.string.planetary_industry_export_final_product_header.toUiText().resolve(appLocale) }.toTypedArray(),
            Res.string.planetary_industry_export_product_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_product_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_used_other_capacity_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_expires_at_date_header.toUiText().resolve(appLocale),
            Res.string.planetary_industry_export_expiry_reason_header.toUiText().resolve(appLocale),
            *extractedTypes.map { Res.string.planetary_industry_export_extraction_rate_header.toUiText(it.name.toUiTextArgument()).resolve(appLocale) }.toTypedArray(),
            *types.map { Res.string.planetary_industry_export_stored_quantity_header.toUiText(it.name.toUiTextArgument()).resolve(appLocale) }.toTypedArray(),
        ).joinToString("\t")
        return "$headers\n$rows"
    }

    private fun getAllTypes(items: List<ColonyItem>): List<Type> {
        return items.flatMap { item ->
            item.colony.pins.flatMap { it.contents.keys }
        }.distinct().sortedBy { it.name }
    }

    private fun getColonyContents(colony: Colony): Map<Type, Long> {
        return colony.pins
            .flatMap { it.contents.entries }
            .groupingBy({ it.key })
            .fold(0L) { accumulator, element -> accumulator + element.value }
    }

    private fun getAllExtractedTypes(items: List<ColonyItem>): List<Type> {
        return items.flatMap { item ->
            item.colony.pins.filterIsInstance<Pin.Extractor>().mapNotNull { it.productType }
        }.distinct().sortedBy { it.name }
    }

    private fun getAveragesPerHourExtracted(colony: Colony): Map<Type, Int> {
        return colony.pins.filterIsInstance<Pin.Extractor>().mapNotNull { extractor ->
            extractor.productType?.let { it to (extractor.getAveragePerHour() ?: 0) }
        }.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }
    }

    private suspend fun ColonyStatus.getDisplayName(appLocale: AppLocale) = when (this) {
        is Extracting -> Res.string.planetary_industry_extracting_status.toUiText().resolve(appLocale)
        is Producing -> Res.string.planetary_industry_producing_status.toUiText().resolve(appLocale)
        is NotSetup -> Res.string.planetary_industry_not_setup_status.toUiText().resolve(appLocale)
        is NeedsAttention -> Res.string.planetary_industry_needs_attention_status.toUiText().resolve(appLocale)
        is Idle -> Res.string.planetary_industry_idle_status.toUiText().resolve(appLocale)
    }

    private suspend fun PinStatus.getDisplayName(appLocale: AppLocale) = when (this) {
        PinStatus.Extracting -> Res.string.planetary_industry_extracting_status.toUiText().resolve(appLocale)
        PinStatus.ExtractorExpired -> Res.string.planetary_industry_export_extractor_expired_status.toUiText().resolve(appLocale)
        PinStatus.ExtractorInactive -> Res.string.planetary_industry_export_extractor_inactive_status.toUiText().resolve(appLocale)
        PinStatus.FactoryIdle -> Res.string.planetary_industry_export_factory_idle_status.toUiText().resolve(appLocale)
        PinStatus.InputNotRouted -> Res.string.planetary_industry_export_input_not_routed_status.toUiText().resolve(appLocale)
        PinStatus.NotSetup -> Res.string.planetary_industry_not_setup_status.toUiText().resolve(appLocale)
        PinStatus.OutputNotRouted -> Res.string.planetary_industry_export_output_not_routed_status.toUiText().resolve(appLocale)
        PinStatus.Producing -> Res.string.planetary_industry_producing_status.toUiText().resolve(appLocale)
        PinStatus.Static -> Res.string.planetary_industry_export_static_status.toUiText().resolve(appLocale)
        PinStatus.StorageFull -> Res.string.planetary_industry_export_storage_full_status.toUiText().resolve(appLocale)
    }

    private fun getGoogleSheetsExpiryTimestamp(item: ColonyItem): String {
        val expiryTimestamp = item.ffwdColony.currentSimTime.toEpochMilli() / 1000
        return "=EPOCHTODATE($expiryTimestamp)"
    }

    private fun getExcelExpiryTimestamp(item: ColonyItem): String {
        val expiryTimestamp = item.ffwdColony.currentSimTime.toEpochMilli() / 1000
        return "=($expiryTimestamp/86400)+25569"
    }

    private suspend fun getExpiryReason(status: ColonyStatus, appLocale: AppLocale): String {
        return if (status is Idle) {
            Res.string.planetary_industry_idle_status.toUiText().resolve(appLocale)
        } else {
            status.pins.map { it.status.getDisplayName(appLocale) }.distinct().joinToString(",")
        }
    }

    private fun Pin.Extractor.getAveragePerHour(): Int? {
        if (isActive) {
            if (installTime != null && expiryTime != null && baseValue != null && cycleTime != null) {
                val totalProgramDuration = Duration.between(installTime, expiryTime)
                val totalCycles = (totalProgramDuration.toSeconds() / cycleTime.toSeconds()).toInt()
                val prediction = getProgramOutputPrediction(baseValue, cycleTime, totalCycles)
                val totalMined = prediction.sum()
                val averagePerHour = (totalMined / totalProgramDuration.toHours().toFloat()).roundToInt()
                return averagePerHour
            }
        }
        return null
    }
}
