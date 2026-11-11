package dev.nohus.rift.network.requests

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.Headers
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

/**
 * A deserialized response body and response headers
 */
data class Reply<T>(
    val body: T,
    val headers: Headers,
)

val Headers.expires: Instant? get() = get("Expires")?.let(::parseExpires)

private fun parseExpires(value: String): Instant? {
    return try {
        ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()
    } catch (_: Exception) {
        logger.error { "Could not parse expiry header: $value" }
        null
    }
}
