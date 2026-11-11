package dev.nohus.rift.notifications.eve

import dev.nohus.rift.compose.text.toFormattedText
import dev.nohus.rift.types.TypeDetails
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import java.time.Instant

private const val WINDOWS_EPOCH_OFFSET_MILLIS = 11_644_473_600_000L
private const val FILETIME_TICKS_PER_MILLISECOND = 10_000L
private val logger = KotlinLogging.logger {}

/**
 * Parses the type-specific YAML payload returned by ESI and delegates its presentation.
 */
@Single
class EveNotificationTextParser(
    private val eveNotificationTemplates: EveNotificationTemplates,
) {
    private val yamlLoader = Load(LoadSettings.builder().build())

    fun parse(
        type: String,
        text: String,
        typeDetails: TypeDetails,
        senderId: Long,
        receiverId: Long,
        createdTimestamp: Instant,
    ): ParsedEveNotification {
        return try {
            val normalizedType = type.filter(Char::isLetterOrDigit).lowercase()
            val parsedData = parseYaml(text) ?: return ParsedEveNotification(
                subject = null,
                body = "Notification details unavailable: $text".toFormattedText(),
                data = emptyMap(),
            )
            val data = parsedData + listOf(
                "notification_senderID" to senderId,
                "notification_receiverID" to receiverId,
                "notification_created" to (createdTimestamp.toEpochMilli() + WINDOWS_EPOCH_OFFSET_MILLIS) * FILETIME_TICKS_PER_MILLISECOND,
            ).toMap()

            eveNotificationTemplates.format(normalizedType, data, typeDetails)
        } catch (exception: Exception) {
            logger.error(exception) { "Could not display EVE notification of type $type" }
            ParsedEveNotification(
                subject = null,
                body = "Could not display this notification due to an error.".toFormattedText(),
                data = emptyMap(),
            )
        }
    }

    private fun parseYaml(text: String): Map<String, Any?>? {
        return try {
            when (val value = yamlLoader.loadFromString(text)) {
                is Map<*, *> -> {
                    value.entries.associate { [key, value] -> key.toString() to value }
                }
                is List<*> -> mapOf("rootArray" to value)
                else -> null
            }
        } catch (_: RuntimeException) {
            null
        }
    }
}
