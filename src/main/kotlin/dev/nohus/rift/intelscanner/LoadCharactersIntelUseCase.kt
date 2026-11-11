package dev.nohus.rift.intelscanner

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_scanner_isk_range_five_billion
import dev.nohus.rift.generated.resources.intel_scanner_isk_range_one_billion
import dev.nohus.rift.generated.resources.intel_scanner_isk_range_one_hundred_billion
import dev.nohus.rift.generated.resources.intel_scanner_isk_range_one_trillion
import dev.nohus.rift.generated.resources.intel_scanner_isk_range_ten_billion
import dev.nohus.rift.generated.resources.intel_scanner_isk_range_under_one_billion
import dev.nohus.rift.generated.resources.intel_scanner_space_abyssal
import dev.nohus.rift.generated.resources.intel_scanner_space_highsec
import dev.nohus.rift.generated.resources.intel_scanner_space_lowsec
import dev.nohus.rift.generated.resources.intel_scanner_space_nullsec
import dev.nohus.rift.generated.resources.intel_scanner_space_wormhole_space
import dev.nohus.rift.generated.resources.intel_scanner_tag_bait_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_black_ops_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_capital_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_cyno_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_fleet_commander_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_ganker_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_highsec_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_logistics_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_lowsec_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_rookie_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_supercarrier_name
import dev.nohus.rift.generated.resources.intel_scanner_tag_titan_name
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.zkillboard.ZkillCharacterStats
import dev.nohus.rift.network.zkillboard.ZkillboardApi
import dev.nohus.rift.repositories.FactionsRepository
import dev.nohus.rift.repositories.character.CharacterDetailsRepository
import dev.nohus.rift.repositories.character.CharacterDetailsRepository.CharacterDetails
import dev.nohus.rift.repositories.character.CharacterStatus
import dev.nohus.rift.repositories.character.CharactersRepository
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.types.TypesRepository.Type
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt

@Single
class LoadCharactersIntelUseCase(
    private val charactersRepository: CharactersRepository,
    private val characterDetailsRepository: CharacterDetailsRepository,
    private val factionsRepository: FactionsRepository,
    private val zKillboardApi: ZkillboardApi,
    private val typesRepository: TypesRepository,
) {

    data class CharacterItem(
        val name: String,
        val state: CharacterState = CharacterState.Loading,
    )

    sealed interface CharacterState {
        data object Loading : CharacterState
        data object NotFound : CharacterState
        data object Error : CharacterState
        data class Loaded(
            val characterDetails: CharacterDetails,
            val characterId: Int,
            val characterName: String,
            val corporationId: Int,
            val corporationName: String,
            val corporationTicker: String,
            val allianceId: Int?,
            val allianceName: String?,
            val allianceTicker: String?,
            val standing: Float,
            val standingLevel: Standing,
            val factionId: Int?,
            val factionName: String?,
            val securityStatus: Double?,
            val zKillboardDetails: ZkillboardDetails? = null,
            val associateGroup: AssociateGroup? = null,
            val isZKillboardLoading: Boolean = true,
        ) : CharacterState
    }

    data class AssociateGroup(
        val name: String?,
        val size: Int,
    )

    data class ZkillboardDetails(
        val tags: List<CharacterTag>,
        val dangerRatio: Int?,
        val gangRatio: Int?,
        val avgGangSize: Int?,
        val shipsLost: Int?,
        val shipsDestroyed: Int?,
        val soloKills: Int?,
        val cyno: Cyno?,
        val groupSizes: List<GroupSize>?,
        val space: List<Space>?,
        val isk: List<Isk>?,
        val recentShips: List<UsedShip>?,
        val topShips: List<UsedShip>?,
        val fleetCommander: FleetCommander?,
        val baitCount: Int?,
        val gankerCount: Int?,
        val awoxCount: Int?,
        val associates: List<Associate>?,
        val affiliates: List<Affiliate>?,
    ) {

        data class Affiliate(
            val allianceId: Int,
            val sharedKills: Int,
        )

        data class Associate(
            val characterId: Int,
            val sharedKills: Int,
        )

        data class Cyno(
            val count: Int,
            val standard: Int,
            val covert: Int,
            val industrial: Int,
        )

        data class FleetCommander(
            val score: Int,
            val monitorAppearances: Int,
            val commandShipAppearances: Int,
            val largeFleetAppearances: Int,
        )

        data class GroupSize(
            val name: String,
            val size: Int,
            val shipsLost: Int?,
            val shipsDestroyed: Int?,
        )

        data class Space(
            val type: SpaceType,
            val shipsLost: Int?,
            val shipsDestroyed: Int?,
        )

        enum class SpaceType(
            val label: String,
            val displayName: UiText,
        ) {
            Highsec("loc:highsec", Res.string.intel_scanner_space_highsec.toUiText()),
            Lowsec("loc:lowsec", Res.string.intel_scanner_space_lowsec.toUiText()),
            Nullsec("loc:nullsec", Res.string.intel_scanner_space_nullsec.toUiText()),
            WormholeSpace("loc:w-space", Res.string.intel_scanner_space_wormhole_space.toUiText()),
            Pochven("loc:pochven", "Pochven".toUiText()),
            Abyssal("loc:abyssal", Res.string.intel_scanner_space_abyssal.toUiText()),
        }

        data class Isk(
            val type: IskType,
            val shipsLost: Int?,
            val shipsDestroyed: Int?,
        )

        enum class IskType(
            val label: String,
            val displayName: UiText,
        ) {
            UnderOneBillion("isk:under1b", Res.string.intel_scanner_isk_range_under_one_billion.toUiText()),
            OneBillion("isk:1b+", Res.string.intel_scanner_isk_range_one_billion.toUiText()),
            FiveBillion("isk:5b+", Res.string.intel_scanner_isk_range_five_billion.toUiText()),
            TenBillion("isk:10b+", Res.string.intel_scanner_isk_range_ten_billion.toUiText()),
            OneHundredBillion("isk:100b+", Res.string.intel_scanner_isk_range_one_hundred_billion.toUiText()),
            OneTrillion("isk:1t+", Res.string.intel_scanner_isk_range_one_trillion.toUiText()),
        }
    }

    data class UsedShip(
        val type: Type,
        val kills: Int,
        val losses: Int,
    )

    operator fun invoke(
        characters: List<String>,
        shouldIgnoreCharacter: (CharacterState.Loaded) -> Boolean,
    ): Flow<List<CharacterItem>> = channelFlow {
        if (characters.isEmpty()) return@channelFlow

        val mutex = Mutex()
        var current: List<CharacterItem> = emptyList()

        suspend fun updateImpl(new: List<CharacterItem>) {
            current = new
            send(new)
        }

        suspend fun update(new: List<CharacterItem>) {
            mutex.withLock {
                updateImpl(new)
            }
        }

        suspend fun updateCharacterItem(character: String, characterState: CharacterState) {
            mutex.withLock {
                val new = current.map {
                    if (it.name == character) it.copy(state = characterState) else it
                }
                updateImpl(new)
            }
        }

        suspend fun removeCharacterItem(character: String) {
            mutex.withLock {
                updateImpl(current.filterNot { it.name == character })
            }
        }

        suspend fun updateCharacterItem(character: String, update: (CharacterState.Loaded) -> CharacterState.Loaded) {
            mutex.withLock {
                val new = current.map { item ->
                    if (item.name == character && item.state is CharacterState.Loaded) item.copy(state = update(item.state)) else item
                }
                updateImpl(new)
            }
        }

        suspend fun updateZkillboardDetails(character: String, details: ZkillboardDetails?) {
            mutex.withLock {
                val updated = current.map { item ->
                    if (item.name == character && item.state is CharacterState.Loaded) {
                        item.copy(
                            state = item.state.copy(
                                zKillboardDetails = details,
                                isZKillboardLoading = false,
                            ),
                        )
                    } else {
                        item
                    }
                }
                updateImpl(updated.withAssociateGroups())
            }
        }

        if (characters.size >= 2) {
            // We have at least 2 characters, show them immediately
            update(characters.map { name -> CharacterItem(name) })
        }

        val statuses = charactersRepository.getCharacterNamesStatus(Originator.IntelScanner, characters)
        val isAnyCharacterExisting = statuses.values.any { it is CharacterStatus.Exists }
        if (characters.size < 2) {
            if (isAnyCharacterExisting) {
                // We didn't have 2 characters, so only show them if at least 1 exists, otherwise it could've been an unrelated clipboard
                update(characters.map { name -> CharacterItem(name) })
            } else {
                return@channelFlow
            }
        }

        coroutineScope {
            characters.forEach { character ->
                launch {
                    when (val status = statuses[character]) {
                        is CharacterStatus.Exists -> {
                            val details = async { loadCharacterDetails(status.characterId) }
                            val zKillboardDetails = async { loadZkillboardDetails(status.characterId) }
                            // Wait for basic details to load first
                            val characterState = details.await()
                            if (characterState is CharacterState.Loaded && shouldIgnoreCharacter(characterState)) {
                                zKillboardDetails.cancel()
                                removeCharacterItem(character)
                                return@launch
                            }
                            updateCharacterItem(character, characterState)
                            // Then update additional details as they load
                            launch {
                                val details = zKillboardDetails.await()
                                updateZkillboardDetails(character, details)
                            }
                        }
                        CharacterStatus.DoesNotExist -> {
                            updateCharacterItem(character, CharacterState.NotFound)
                        }
                        null -> {
                            updateCharacterItem(character, CharacterState.Error)
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadCharacterDetails(characterId: Int): CharacterState {
        val details = characterDetailsRepository.getCharacterDetails(Originator.IntelScanner, characterId) ?: return CharacterState.Error
        return CharacterState.Loaded(
            characterDetails = details,
            characterId = details.characterId,
            characterName = details.name,
            corporationId = details.corporationId,
            corporationName = details.corporationName,
            corporationTicker = details.corporationTicker,
            allianceId = details.allianceId,
            allianceName = details.allianceName,
            allianceTicker = details.allianceTicker,
            standing = details.standing,
            standingLevel = details.standingLevel,
            factionId = details.factionId,
            factionName = details.factionId?.let { factionsRepository.getFaction(it) },
            securityStatus = details.securityStatus,
        )
    }

    private suspend fun loadZkillboardDetails(characterId: Int): ZkillboardDetails? {
        val result = zKillboardApi.getCharacterStats(Originator.IntelScanner, characterId)
        val stats = result.success ?: return null
        val labels = stats.labels.orEmpty()
        return ZkillboardDetails(
            tags = getTags(stats),
            dangerRatio = stats.dangerRatio,
            gangRatio = stats.gangRatio,
            avgGangSize = stats.avgGangSize?.roundToInt()?.takeIf { it > 0 },
            shipsLost = stats.shipsLost,
            shipsDestroyed = stats.shipsDestroyed,
            soloKills = stats.soloKills,
            cyno = stats.cyno.let {
                ZkillboardDetails.Cyno(
                    count = it?.count ?: 0,
                    standard = it?.standard ?: 0,
                    covert = it?.covert ?: 0,
                    industrial = it?.industrial ?: 0,
                )
            },
            recentShips = stats.recentShips.orEmpty().mapNotNull { ship ->
                UsedShip(
                    type = ship.shipTypeId?.let { typesRepository.getType(it) } ?: return@mapNotNull null,
                    kills = ship.kills ?: 0,
                    losses = ship.losses ?: 0,
                )
            }.takeIf { it.isNotEmpty() },
            topShips = stats.topShips.orEmpty().mapNotNull { ship ->
                UsedShip(
                    type = ship.shipTypeId?.let { typesRepository.getType(it) } ?: return@mapNotNull null,
                    kills = ship.kills ?: 0,
                    losses = ship.losses ?: 0,
                )
            }.takeIf { it.isNotEmpty() },
            groupSizes = labels.mapNotNull { (name, details) ->
                ZkillboardDetails.GroupSize(
                    name = name,
                    size = name.getGroupSize() ?: return@mapNotNull null,
                    shipsLost = details.shipsLost,
                    shipsDestroyed = details.shipsDestroyed,
                )
            }.sortedBy { it.size }.takeIf { it.isNotEmpty() },
            space = labels.mapNotNull { (name, details) ->
                val type = ZkillboardDetails.SpaceType.entries.firstOrNull { it.label == name }
                    ?: return@mapNotNull null
                ZkillboardDetails.Space(
                    type = type,
                    shipsLost = details.shipsLost,
                    shipsDestroyed = details.shipsDestroyed,
                )
            }.sortedBy { it.type.ordinal }.takeIf { it.isNotEmpty() },
            isk = labels.mapNotNull { (name, details) ->
                val type = ZkillboardDetails.IskType.entries.firstOrNull { it.label == name }
                    ?: return@mapNotNull null
                ZkillboardDetails.Isk(
                    type = type,
                    shipsLost = details.shipsLost,
                    shipsDestroyed = details.shipsDestroyed,
                )
            }.sortedBy { it.type.ordinal }.takeIf { it.isNotEmpty() },
            fleetCommander = stats.fleetCommander.let {
                ZkillboardDetails.FleetCommander(
                    score = it?.score ?: 0,
                    monitorAppearances = it?.monitorAppearances ?: 0,
                    commandShipAppearances = it?.commandShipAppearances ?: 0,
                    largeFleetAppearances = it?.largeFleetAppearances ?: 0,
                )
            },
            baitCount = stats.bait?.count ?: 0,
            gankerCount = stats.gankerCount ?: 0,
            awoxCount = stats.awoxCount ?: labels["awox"]?.shipsDestroyed ?: 0,
            associates = stats.associates.orEmpty().mapNotNull { associate ->
                ZkillboardDetails.Associate(
                    characterId = associate.characterId ?: return@mapNotNull null,
                    sharedKills = associate.sharedKills ?: 0,
                )
            }.takeIf { it.isNotEmpty() },
            affiliates = stats.affiliates.orEmpty().mapNotNull { affiliate ->
                ZkillboardDetails.Affiliate(
                    allianceId = affiliate.allianceId ?: return@mapNotNull null,
                    sharedKills = affiliate.sharedKills ?: 0,
                )
            }.takeIf { it.isNotEmpty() },
        )
    }

    data class CharacterTag(
        val name: String,
        val count: Int?,
    ) {
        val displayName: UiText
            get() = when (name) {
                "FC" -> Res.string.intel_scanner_tag_fleet_commander_name.toUiText()
                "LOGI" -> Res.string.intel_scanner_tag_logistics_name.toUiText()
                "BLOPS" -> Res.string.intel_scanner_tag_black_ops_name.toUiText()
                "SUPER" -> Res.string.intel_scanner_tag_supercarrier_name.toUiText()
                "TITAN" -> Res.string.intel_scanner_tag_titan_name.toUiText()
                "CAPITAL" -> Res.string.intel_scanner_tag_capital_name.toUiText()
                "HIGHSEC" -> Res.string.intel_scanner_tag_highsec_name.toUiText()
                "LOWSEC" -> Res.string.intel_scanner_tag_lowsec_name.toUiText()
                "GANKER" -> Res.string.intel_scanner_tag_ganker_name.toUiText()
                "CYNO" -> Res.string.intel_scanner_tag_cyno_name.toUiText()
                "BAIT" -> Res.string.intel_scanner_tag_bait_name.toUiText()
                "ROOKIE" -> Res.string.intel_scanner_tag_rookie_name.toUiText()
                else -> name.toUiText()
            }
    }

    private fun getTags(stats: ZkillCharacterStats): List<CharacterTag> {
        return buildList {
            val fleetCommander = stats.fleetCommander?.score ?: 0
            if (fleetCommander >= 61) {
                add(CharacterTag("FC", fleetCommander))
            }

            val logi = stats.activityTags?.logi ?: 0
            if (logi > 0) {
                add(CharacterTag("LOGI", logi))
            }
            val blops = stats.activityTags?.blops ?: 0
            if (blops > 0) {
                add(CharacterTag("BLOPS", blops))
            }
            val superCapital = stats.activityTags?.superCapital ?: 0
            if (superCapital > 0) {
                add(CharacterTag("SUPER", superCapital))
            }
            val titan = stats.activityTags?.titan ?: 0
            if (titan > 0) {
                add(CharacterTag("TITAN", titan))
            }
            val capital = stats.activityTags?.capital ?: 0
            if (capital > 0 && none { it.name in ["SUPER", "TITAN"] }) {
                add(CharacterTag("CAPITAL", capital))
            }

            val highsecKills = stats.labels?.get(ZkillboardDetails.SpaceType.Highsec.label)?.shipsDestroyed ?: 0
            val lowsecKills = stats.labels?.get(ZkillboardDetails.SpaceType.Lowsec.label)?.shipsDestroyed ?: 0
            val pochvenKills = stats.labels?.get(ZkillboardDetails.SpaceType.Pochven.label)?.shipsDestroyed ?: 0
            val allKills = stats.shipsDestroyed ?: 0
            if (allKills >= 10 && highsecKills / allKills.toFloat() >= 0.75f) {
                add(CharacterTag("HIGHSEC", highsecKills))
            }
            if (allKills >= 10 && lowsecKills / allKills.toFloat() >= 0.75f) {
                add(CharacterTag("LOWSEC", lowsecKills))
            }
            if (allKills >= 10 && pochvenKills / allKills.toFloat() >= 0.75f) {
                add(CharacterTag("POCHVEN", pochvenKills))
            }
            val gankerCount = stats.gankerCount ?: 0
            if (gankerCount >= 3) {
                add(CharacterTag("GANKER", gankerCount))
            }
            val cynoCount = stats.cyno?.count ?: 0
            if (cynoCount >= 2) {
                add(CharacterTag("CYNO", cynoCount))
            }
            val baitCount = stats.bait?.count ?: 0
            if (baitCount >= 2) {
                add(CharacterTag("BAIT", baitCount))
            }
            val isNewCharacter = stats.info?.birthday != null && Duration.between(stats.info.birthday, Instant.now()) < Duration.ofDays(180)
            val hasMoreLosses = stats.shipsDestroyed == 0 || (stats.shipsLost ?: 0) > (stats.shipsDestroyed ?: 0)
            if (isNewCharacter && hasMoreLosses && none { it.name in ["SUPER", "TITAN", "CAPITAL", "CYNO", "BAIT"] }) {
                add(CharacterTag("ROOKIE", null))
            }
        }
    }

    private fun String.getGroupSize(): Int? {
        return groupSizeLabelRegex.matchEntire(this)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun List<CharacterItem>.withAssociateGroups(): List<CharacterItem> {
        val loadedCharacters = mapIndexedNotNull { index, item ->
            (item.state as? CharacterState.Loaded)?.let { index to it }
        }
        val parent = loadedCharacters.associate { (_, state) -> state.characterId to state.characterId }.toMutableMap()

        fun find(characterId: Int): Int {
            val currentParent = parent.getValue(characterId)
            if (currentParent == characterId) return characterId
            return find(currentParent).also { parent[characterId] = it }
        }

        fun union(first: Int, second: Int) {
            val firstRoot = find(first)
            val secondRoot = find(second)
            if (firstRoot != secondRoot) parent[secondRoot] = firstRoot
        }

        loadedCharacters.forEach { (_, state) ->
            state.zKillboardDetails?.associates.orEmpty().forEach { associate ->
                if (associate.characterId in parent) union(state.characterId, associate.characterId)
            }
        }

        val groups = loadedCharacters
            .groupBy { (_, state) -> find(state.characterId) }
            .values
            .filter { it.size > 1 }
            .sortedBy { group -> group.minOf { it.first } }
        val hasMultipleGroups = groups.size > 1
        val groupsByCharacterId = groups.flatMapIndexed { groupIndex, group ->
            val associateGroup = AssociateGroup(
                name = getGroupName(groupIndex).takeIf { hasMultipleGroups },
                size = group.size,
            )
            group.map { (_, state) -> state.characterId to associateGroup }
        }.toMap()

        return map { item ->
            val state = item.state as? CharacterState.Loaded ?: return@map item
            item.copy(state = state.copy(associateGroup = groupsByCharacterId[state.characterId]))
        }
    }

    private fun getGroupName(index: Int): String {
        var value = index + 1
        return buildString {
            while (value > 0) {
                value--
                insert(0, 'A' + value % 26)
                value /= 26
            }
        }
    }

    companion object {
        private val groupSizeLabelRegex = Regex("^#:?([0-9]+)\\+?$")
    }
}
