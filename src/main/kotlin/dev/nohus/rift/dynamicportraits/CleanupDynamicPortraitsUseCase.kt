package dev.nohus.rift.dynamicportraits

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.io.IOException
import java.time.Duration
import java.time.Instant
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

private val logger = KotlinLogging.logger {}

@Single
class CleanupDynamicPortraitsUseCase(
    dynamicPortraitsDirectory: DynamicPortraitsDirectory,
) {

    private val portraitsDirectory = dynamicPortraitsDirectory.path

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        if (!portraitsDirectory.exists()) return@withContext

        val maxAge = Instant.now() - Duration.ofDays(14)
        try {
            portraitsDirectory.listDirectoryEntries().filter { it.isDirectory() }.forEach { characterDirectory ->
                characterDirectory
                    .listDirectoryEntries()
                    .filter { it.getLastModifiedTime().toInstant().isBefore(maxAge) }
                    .forEach { portrait ->
                        logger.debug { "Deleting old cached portrait: $portrait (${portrait.getLastModifiedTime()})" }
                        portrait.deleteIfExists()
                    }
                if (characterDirectory.listDirectoryEntries().isEmpty()) {
                    characterDirectory.deleteIfExists()
                }
            }
        } catch (e: IOException) {
            logger.error(e) { "Could not cleanup old cached portraits" }
        }
    }
}
