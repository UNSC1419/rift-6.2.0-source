package dev.nohus.rift.utils.directories

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

private val logger = KotlinLogging.logger {}

@Single
class GetEveLauncherCacheDirectoriesUseCase {

    operator fun invoke(launcherDirectory: Path, wineDrive: Path? = null): List<Path> {
        val file = launcherDirectory.resolve("launcher-data.json")
        return try {
            if (!file.isRegularFile()) return emptyList()
            val state = (Json.parseToJsonElement(file.readText()) as? JsonObject)?.get("state") as? JsonObject
                ?: return emptyList()
            val caches = state["shared-cache"] as? JsonObject
            val eve = caches?.get("eve-online") as? JsonObject
            val location = eve?.get("location") as? JsonObject
            val settings = state["settings"] as? JsonObject
            listOfNotNull(location?.get("path"), settings?.get("sharedCacheDirectory"))
                .map { it.jsonPrimitive.content }
                .filter { it.isNotBlank() }
                .mapNotNull { path ->
                    if (wineDrive == null) {
                        Path.of(path)
                    } else if (path.length >= 3 && path[1] == ':' && (path[2] == '\\' || path[2] == '/')) {
                        val drive = if (path[0].equals('c', ignoreCase = true)) {
                            wineDrive
                        } else {
                            wineDrive.parent.resolve("dosdevices/${path[0].lowercaseChar()}:")
                        }
                        drive.resolve(path.substring(3).replace('\\', '/'))
                    } else {
                        null
                    }
                }.distinct()
        } catch (e: Exception) {
            logger.warn(e) { "Could not read EVE launcher cache location from $file" }
            emptyList()
        }
    }
}
