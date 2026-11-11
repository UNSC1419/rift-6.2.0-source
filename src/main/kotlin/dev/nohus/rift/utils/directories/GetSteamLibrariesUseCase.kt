package dev.nohus.rift.utils.directories

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

private val logger = KotlinLogging.logger {}

@Single
class GetSteamLibrariesUseCase {

    operator fun invoke(steamDirectories: List<Path>): List<Path> {
        val libraries = steamDirectories.toMutableList()
        for (directory in steamDirectories) {
            for (relativePath in listOf("steamapps/libraryfolders.vdf", "config/libraryfolders.vdf")) {
                val file = directory.resolve(relativePath)
                try {
                    if (!file.isRegularFile()) continue
                    val paths = Regex(""""path"\s*"((?:\\.|[^"\\])*)"""")
                        .findAll(file.readText())
                        .map { it.groupValues[1].replace("\\\\", "\\").replace("\\\"", "\"") }
                    for (path in paths) libraries.add(Path.of(path))
                } catch (e: Exception) {
                    logger.warn(e) { "Could not read Steam libraries from $file" }
                }
            }
        }
        return libraries.distinct().filter { it.isDirectory() }
    }
}
