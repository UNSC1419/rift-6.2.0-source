package dev.nohus.rift.network.esi.models

import dev.nohus.rift.network.IsoDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class WarsId(
    @SerialName("aggressor")
    val aggressor: WarParticipant,
    @SerialName("defender")
    val defender: WarParticipant,
    @SerialName("allies")
    val allies: List<WarParticipant> = emptyList(),
    @SerialName("started")
    @Serializable(with = IsoDateTimeSerializer::class)
    val started: Instant? = null,
    @SerialName("finished")
    @Serializable(with = IsoDateTimeSerializer::class)
    val finished: Instant? = null,
)

@Serializable
data class WarParticipant(
    @SerialName("corporation_id")
    val corporationId: Int? = null,
    @SerialName("alliance_id")
    val allianceId: Int? = null,
)
