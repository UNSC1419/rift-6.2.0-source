package dev.nohus.rift.intelscanner

import dev.nohus.rift.intelscanner.DScanParser.DScanEntity
import dev.nohus.rift.repositories.GetSolarSystemChipStateUseCase
import dev.nohus.rift.repositories.SolarSystemChipLocation
import dev.nohus.rift.repositories.SolarSystemChipState
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.types.TypesRepository.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class LoadDScanIntelUseCase(
    private val typesRepository: TypesRepository,
    private val solarSystemsRepository: SolarSystemsRepository,
    private val getSolarSystemChipStateUseCase: GetSolarSystemChipStateUseCase,
) {

    data class DScan(
        val systemChip: SolarSystemChipState?,
        val items: List<DScanItem>,
    )

    data class DScanItem(
        val type: Type,
        val typeCategory: String?,
        val typeGroup: String?,
        val name: String,
        val distance: Long?,
    )

    suspend operator fun invoke(items: List<DScanEntity>): DScan = withContext(Dispatchers.Default) {
        val items = items.map {
            val type = typesRepository.getTypeOrPlaceholder(it.typeId)
            DScanItem(
                type = type,
                typeCategory = typesRepository.getCategoryName(type.categoryId),
                typeGroup = typesRepository.getGroupName(type.groupId),
                name = it.name,
                distance = it.distance,
            )
        }
        DScan(
            systemChip = detectSystem(items),
            items = items,
        )
    }

    private fun detectSystem(items: List<DScanItem>): SolarSystemChipState? {
        val planets = items.filter { it.typeGroup == "Planet" }.map { it.name.substringBeforeLast(" ") }
        val moons = items.filter { it.typeGroup == "Moon" }.map { it.name.substringBefore(" - ").substringBeforeLast(" ") }
        val structures = items.filter { it.typeCategory == "Structure" }.map { it.name.substringBefore(" - ").substringBefore(" » ") }
        val name = (planets + moons + structures).groupBy { it }.entries.maxByOrNull { it.value.size }?.key
        if (name != null) {
            solarSystemsRepository.getSystemId(name)?.let {
                return getSolarSystemChipStateUseCase(SolarSystemChipLocation.SolarSystem(it), isShowingRegion = true)
            }
        }
        return null
    }
}
