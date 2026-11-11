package dev.nohus.rift.database.staticdata.sde.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationType(
    @SerialName("_key")
    val id: Int,

    val displayName: TranslatedString? = null,
    val internalName: String,
)

@Serializable
data class TranslatedString(
    val de: String,
    val en: String,
    val es: String,
    val fr: String,
    val ja: String,
    val ko: String,
    val ru: String,
    val zh: String,
)
