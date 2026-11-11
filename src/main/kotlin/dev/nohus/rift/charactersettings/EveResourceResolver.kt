package dev.nohus.rift.charactersettings

import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.OperatingSystem.Linux
import dev.nohus.rift.utils.OperatingSystem.MacOs
import dev.nohus.rift.utils.OperatingSystem.Windows
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.nio.file.Path
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isRegularFile
import kotlin.io.path.useLines

private val logger = KotlinLogging.logger {}

/** Resolves EVE's `res:/` paths to files in the locally installed shared cache */
@Single
class EveResourceResolver(
    private val settings: Settings,
    private val operatingSystem: OperatingSystem,
) {

    private data class ResourceIndex(val path: Path, val modified: Long, val size: Long)

    private var loadedIndex: ResourceIndex? = null
    private var resourcePaths: Map<String, Path> = emptyMap()

    @Synchronized
    operator fun invoke(resourcePath: String): Path? {
        val cacheDirectory = settings.eveSharedCacheDirectory ?: return null
        val indexPath = cacheDirectory.resolve(
            when (operatingSystem) {
                Linux, Windows -> "tq/resfileindex.txt"
                MacOs -> "tq/EVE.app/Contents/Resources/build/resfileindex.txt"
            },
        )
        return try {
            if (!indexPath.isRegularFile()) {
                loadedIndex = null
                resourcePaths = emptyMap()
                return null
            }
            val index = ResourceIndex(indexPath, indexPath.getLastModifiedTime().toMillis(), java.nio.file.Files.size(indexPath))
            if (index != loadedIndex) {
                resourcePaths = loadResourcePaths(cacheDirectory, indexPath)
                loadedIndex = index
            }
            resourcePaths[resourcePath.removePrefix("bytes:").lowercase()]
        } catch (e: Exception) {
            logger.warn(e) { "Could not read EVE resource index $indexPath" }
            loadedIndex = null
            resourcePaths = emptyMap()
            null
        }
    }

    private fun loadResourcePaths(cacheDirectory: Path, indexPath: Path): Map<String, Path> {
        val resourcesDirectory = cacheDirectory.resolve("ResFiles").toAbsolutePath().normalize()
        return indexPath.useLines { lines ->
            buildMap {
                for (line in lines) {
                    val columns = line.split(",", limit = 3)
                    if (columns.size < 2 || columns[0].isBlank() || columns[1].isBlank()) continue
                    val path = resourcesDirectory.resolve(columns[1]).normalize()
                    if (path.startsWith(resourcesDirectory)) put(columns[0].lowercase(), path)
                }
            }
        }
    }
}
