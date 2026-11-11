package dev.nohus.rift.network.evekill

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EveKillIntel(
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("ships_flown")
    val shipsFlown: List<Ship>? = null,
    @SerialName("ships_lost")
    val shipsLost: List<Ship>? = null,
    @SerialName("fleet_partners")
    val fleetPartners: List<FleetPartner>? = null,
    @SerialName("groups_flown_with")
    val groupsFlownWith: List<Entity>? = null,
) {
    @Serializable
    data class Ship(
        @SerialName("id")
        val id: Int? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("count")
        val count: Int? = null,
    )

    @Serializable
    data class Entity(
        @SerialName("id")
        val id: Int? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("count")
        val count: Int? = null,
    )

    @Serializable
    data class FleetPartner(
        @SerialName("id")
        val id: Int? = null,
        @SerialName("name")
        val name: String? = null,
    )
}
