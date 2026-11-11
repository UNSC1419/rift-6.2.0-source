package dev.nohus.rift.localization

import dev.nohus.rift.characters.files.GetEveSettingsDirectoryUseCase
import dev.nohus.rift.settings.persistence.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.io.IOException
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readLines

private val logger = KotlinLogging.logger {}

@Single
class DetectEveClientLanguageUseCase(
    private val settings: Settings,
    private val getEveSettingsDirectoryUseCase: GetEveSettingsDirectoryUseCase,
) {

    operator fun invoke(): String? {
        return try {
            val directory = settings.eveSettingsDirectory ?: getEveSettingsDirectoryUseCase() ?: return null
            val preferences = directory.listDirectoryEntries()
                .filter { it.isDirectory() && it.name.startsWith("settings_") }
                .map { it.resolve("prefs.ini") }
                .filter { it.isRegularFile() }
                .sortedByDescending { it.getLastModifiedTime().toMillis() }
            preferences.firstNotNullOfOrNull { file ->
                try {
                    file.readLines().firstNotNullOfOrNull { line ->
                        if (line.substringBefore('=').trim() == "languageID") {
                            line.substringAfter('=', "").trim().takeIf { it.isNotEmpty() }
                        } else {
                            null
                        }
                    }
                } catch (e: IOException) {
                    logger.info(e) { "Could not read EVE language from $file" }
                    null
                }
            }
        } catch (e: IOException) {
            logger.info(e) { "Could not detect EVE client language" }
            null
        }
    }
}
