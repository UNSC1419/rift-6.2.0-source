package dev.nohus.rift.utils.directories

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.SystemUtils
import org.koin.core.annotation.Single
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

@Single
class GetLinuxSteamLibrariesUseCase(
    private val getLinuxPartitionsUseCase: GetLinuxPartitionsUseCase,
    private val getSteamLibrariesUseCase: GetSteamLibrariesUseCase,
) {

    private val libraryDirectoryName = "SteamLibrary"
    private val homeLibraryPath = ".steam/steam"

    /**
     * Returns a list of Steam library directories
     */
    operator fun invoke(): List<Path> {
        val partitions = try {
            getLinuxPartitionsUseCase()
        } catch (e: Exception) {
            logger.warn(e) { "Could not discover mounted Steam libraries" }
            emptyList()
        }
        val homeDirectory = SystemUtils.getUserHome().toPath()
        val homeLibrary = homeDirectory.resolve(homeLibraryPath)
        val additionalLibraries = partitions.map { partition ->
            partition.resolve(libraryDirectoryName)
        }
        val steamDirectories = listOf(
            homeLibrary,
            homeDirectory.resolve(".local/share/Steam"),
            homeDirectory.resolve(".var/app/com.valvesoftware.Steam/.local/share/Steam"),
        )
        return getSteamLibrariesUseCase(steamDirectories + additionalLibraries).distinctBy { path ->
            try {
                path.toRealPath()
            } catch (_: Exception) {
                path.toAbsolutePath().normalize()
            }
        }
    }
}
