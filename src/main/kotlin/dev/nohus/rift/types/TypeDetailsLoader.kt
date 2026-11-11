package dev.nohus.rift.types

import dev.nohus.rift.location.LocationRepository
import dev.nohus.rift.network.esi.models.UniverseNamesCategory
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.repositories.CelestialsRepository
import dev.nohus.rift.repositories.FactionsRepository
import dev.nohus.rift.repositories.NamesRepository
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.character.CharacterDetailsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single

@Single
class TypeDetailsLoader(
    private val locationRepository: LocationRepository,
    private val characterDetailsRepository: CharacterDetailsRepository,
    private val solarSystemsRepository: SolarSystemsRepository,
    private val celestialsRepository: CelestialsRepository,
    private val typesRepository: TypesRepository,
    private val namesRepository: NamesRepository,
    private val factionsRepository: FactionsRepository,
) {

    data class LoadingTypeDetails(
        val structureIds: Int = 0,
        val characterIds: Int = 0,
        val groupIds: Int = 0,
        val systemIds: Int = 0,
        val typeIds: Int = 0,
        val isStructuresLoaded: Boolean = false,
        val isCharactersLoaded: Boolean = false,
        val isGroupsLoaded: Boolean = false,
    )

    suspend fun loadTypeDetails(
        originator: Originator,
        uncategorizedIds: Set<Long> = emptySet(),
        structureIdsWithCharacterId: Set<Pair<Long, Int>> = emptySet(),
        stationIds: Set<Long> = emptySet(),
        characterIds: Set<Long> = emptySet(),
        corporationIds: Set<Long> = emptySet(),
        allianceIds: Set<Long> = emptySet(),
        factionIds: Set<Long> = emptySet(),
        systemIds: Set<Long> = emptySet(),
        moonIds: Set<Long> = emptySet(),
        typeIds: Set<Long> = emptySet(),
        onLoadingStateChanged: (LoadingTypeDetails) -> Unit = {},
    ): TypeDetails {
        val uncategorizedIds = uncategorizedIds.toMutableSet()
        val structureIdsWithCharacterId = structureIdsWithCharacterId.toMutableSet()
        val stationIds = stationIds.toMutableSet()
        val characterIds = characterIds.toMutableSet()
        val corporationIds = corporationIds.toMutableSet()
        val allianceIds = allianceIds.toMutableSet()
        val factionIds = factionIds.toMutableSet()
        val systemIds = systemIds.toMutableSet()
        val moonIds = moonIds.toMutableSet()
        val typeIds = typeIds.toMutableSet()

        var loadingState = LoadingTypeDetails()

        fun updateLoadingState(update: (LoadingTypeDetails) -> LoadingTypeDetails) {
            loadingState = update(loadingState)
            onLoadingStateChanged(loadingState)
        }

        updateLoadingState {
            LoadingTypeDetails(
                structureIds = structureIdsWithCharacterId.size + stationIds.size,
                characterIds = characterIds.size,
                groupIds = corporationIds.size + allianceIds.size,
                systemIds = systemIds.size,
                typeIds = typeIds.size,
            )
        }

        // For those IDs where we don't know the category, fetch the categories from ESI
        namesRepository.resolveNames(originator, uncategorizedIds)
        uncategorizedIds.forEach { id ->
            when (namesRepository.getCategory(id)) {
                UniverseNamesCategory.Character -> characterIds += id
                UniverseNamesCategory.Constellation -> {}
                UniverseNamesCategory.Corporation -> corporationIds += id
                UniverseNamesCategory.InventoryType -> typeIds += id
                UniverseNamesCategory.Region -> {}
                UniverseNamesCategory.SolarSystem -> systemIds += id
                UniverseNamesCategory.Station -> stationIds += id
                UniverseNamesCategory.Faction -> factionIds += id
                UniverseNamesCategory.Alliance -> allianceIds += id
                null -> {}
            }
        }

        updateLoadingState {
            LoadingTypeDetails(
                structureIds = structureIdsWithCharacterId.size + stationIds.size,
                characterIds = characterIds.size,
                groupIds = corporationIds.size + allianceIds.size + factionIds.size,
                systemIds = systemIds.size,
                typeIds = typeIds.size,
            )
        }

        // Fetch details about all the IDs
        val details = mutableMapOf<Long, TypeDetail>()
        coroutineScope {
            val deferredStructures = structureIdsWithCharacterId.distinct().map { (structureId, characterId) ->
                async {
                    locationRepository.getStructure(originator, structureId, characterId, fetchOwner = true)
                }
            }
            val deferredStations = stationIds.distinct().map {
                async {
                    locationRepository.getStation(originator, it.toInt(), fetchOwner = true)
                }
            }
            deferredStructures.awaitAll().forEach { structure ->
                structure?.let { details[it.structureId] = TypeDetail.Structure(it) }
            }
            deferredStations.awaitAll().forEach { station ->
                station?.let { details[it.stationId.toLong()] = TypeDetail.Station(it) }
            }
            updateLoadingState {
                it.copy(isStructuresLoaded = true)
            }

            val deferredCharacters = characterIds.distinct().let {
                async {
                    characterDetailsRepository.getCharacterDetails(originator, it.map(Long::toInt)).values
                }
            }
            deferredCharacters.await().forEach { character ->
                character?.let { details[it.characterId.toLong()] = TypeDetail.Character(it) }
            }
            updateLoadingState {
                it.copy(isCharactersLoaded = true)
            }

            val deferredCorporations = corporationIds.distinct().map { corporationId ->
                async {
                    characterDetailsRepository.getCorporationDetails(originator, corporationId.toInt())
                }
            }
            val deferredAlliances = allianceIds.distinct().map {
                async {
                    characterDetailsRepository.getAllianceDetails(originator, it.toInt())
                }
            }

            deferredCorporations.awaitAll().forEach { corporation ->
                corporation?.let { details[corporation.corporationId.toLong()] = TypeDetail.Corporation(corporation) }
            }
            deferredAlliances.awaitAll().forEach { alliance ->
                alliance?.let { details[alliance.allianceId.toLong()] = TypeDetail.Alliance(alliance) }
            }
            updateLoadingState {
                it.copy(isGroupsLoaded = true)
            }

            factionIds.distinct().forEach { id ->
                details[id] = TypeDetail.Faction(id, factionsRepository.getFaction(id.toInt()) ?: "")
            }
            systemIds.distinct().forEach { id ->
                solarSystemsRepository.getSystem(id.toInt())?.let { details[id] = TypeDetail.SolarSystem(it) }
            }
            moonIds.distinct().forEach { id ->
                celestialsRepository.getCelestial(id.toInt())?.let { details[id] = TypeDetail.Celestial(it) }
            }
            typeIds.distinct().forEach { id ->
                typesRepository.getType(id.toInt())?.let { details[id] = TypeDetail.Type(it) }
            }
        }

        return TypeDetails(details)
    }
}
