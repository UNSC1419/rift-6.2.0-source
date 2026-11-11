package dev.nohus.rift.network.zkillboard

import dev.nohus.rift.network.IsoDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ZkillCharacterStats(
    @SerialName("dangerRatio")
    val dangerRatio: Int? = null,
    @SerialName("gangRatio")
    val gangRatio: Int? = null,
    @SerialName("avgGangSize")
    val avgGangSize: Double? = null,
    @SerialName("shipsLost")
    val shipsLost: Int? = null,
    @SerialName("shipsDestroyed")
    val shipsDestroyed: Int? = null,
    @SerialName("shipsDestroyedSolo")
    val shipsDestroyedSolo: Int? = null,
    @SerialName("soloKills")
    val soloKills: Int? = null,
    @SerialName("topLists")
    val topLists: List<TopList>? = null,
    @SerialName("labels")
    val labels: Map<String, Label>? = null,
    @SerialName("recentShips")
    val recentShips: List<UsedShip>? = null,
    @SerialName("topShips")
    val topShips: List<UsedShip>? = null,
    @SerialName("fc")
    val fleetCommander: FleetCommander? = null,
    @SerialName("cyno")
    val cyno: Cyno? = null,
    @SerialName("bait")
    val bait: Bait? = null,
    @SerialName("gankerCount")
    val gankerCount: Int? = null,
    @SerialName("awoxCount")
    val awoxCount: Int? = null,
    @SerialName("associates")
    val associates: List<Associate>? = null,
    @SerialName("affiliates")
    val affiliates: List<Affiliate>? = null,
    @SerialName("activityTags")
    val activityTags: ActivityTags? = null,
    @SerialName("info")
    val info: CharacterInfo? = null,
) {
    @Serializable
    data class CharacterInfo(
        @SerialName("birthday")
        @Serializable(with = IsoDateTimeSerializer::class)
        val birthday: Instant? = null,
    )

    @Serializable
    data class ActivityTags(
        @SerialName("blops")
        val blops: Int? = null,
        @SerialName("logi")
        val logi: Int? = null,
        @SerialName("capital")
        val capital: Int? = null,
        @SerialName("super")
        val superCapital: Int? = null,
        @SerialName("titan")
        val titan: Int? = null,
    )

    @Serializable
    data class Affiliate(
        @SerialName("allianceID")
        val allianceId: Int? = null,
        @SerialName("sharedKills")
        val sharedKills: Int? = null,
    )

    @Serializable
    data class Associate(
        @SerialName("characterID")
        val characterId: Int? = null,
        @SerialName("sharedKills")
        val sharedKills: Int? = null,
    )

    @Serializable
    data class Bait(
        @SerialName("count")
        val count: Int? = null,
    )

    @Serializable
    data class Cyno(
        @SerialName("count")
        val count: Int? = null,
        @SerialName("standard")
        val standard: Int? = null,
        @SerialName("covert")
        val covert: Int? = null,
        @SerialName("industrial")
        val industrial: Int? = null,
    )

    @Serializable
    data class FleetCommander(
        @SerialName("score")
        val score: Int? = null,
        @SerialName("monitorAppearances")
        val monitorAppearances: Int? = null,
        @SerialName("commandShipAppearances")
        val commandShipAppearances: Int? = null,
        @SerialName("largeFleetAppearances")
        val largeFleetAppearances: Int? = null,
    )

    @Serializable
    data class UsedShip(
        @SerialName("shipTypeID")
        val shipTypeId: Int? = null,
        @SerialName("kills")
        val kills: Int? = null,
        @SerialName("losses")
        val losses: Int? = null,
    )

    @Serializable
    data class TopList(
        @SerialName("type")
        val type: String? = null,
        @SerialName("values")
        val values: List<TopListValue>? = null,
    )

    // "shipType" TopList type
    @Serializable
    data class TopListValue(
        @SerialName("kills")
        val kills: Int? = null,
        @SerialName("shipTypeID")
        val shipTypeID: Int? = null,
        @SerialName("shipName")
        val shipName: String? = null,
    )

    @Serializable
    data class Label(
        @SerialName("shipsLost")
        val shipsLost: Int? = null,
        @SerialName("shipsDestroyed")
        val shipsDestroyed: Int? = null,
    )
}
