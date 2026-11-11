package dev.nohus.rift.utils.directories

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.osdirectories.OperatingSystemDirectories
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.io.File
import java.nio.file.Path
import kotlin.io.path.isRegularFile

private val logger = KotlinLogging.logger {}

@Single
class GetEveSharedCacheDirectoryUseCase(
    private val settings: Settings,
    private val operatingSystem: OperatingSystem,
    private val operatingSystemDirectories: OperatingSystemDirectories,
    private val getLinuxSteamLibrariesUseCase: GetLinuxSteamLibrariesUseCase,
    private val getSteamLibrariesUseCase: GetSteamLibrariesUseCase,
    private val getEveLauncherCacheDirectoriesUseCase: GetEveLauncherCacheDirectoriesUseCase,
    private val isEveSharedCacheDirectoryValidUseCase: IsEveSharedCacheDirectoryValidUseCase,
) {

    operator fun invoke(): Path? {
        settings.eveSharedCacheDirectory?.takeIf { isCacheDirectoryValid(it) }?.let { return it }
        val home = operatingSystemDirectories.getUserDirectory()
        val candidates = when (operatingSystem) {
            OperatingSystem.Linux -> getLinuxSteamLibrariesUseCase().flatMap { library ->
                val prefix = library.resolve("steamapps/compatdata/8500/pfx/drive_c")
                getWineLauncherCandidates(prefix) + getWineCandidates(prefix) + listOf(library.resolve("steamapps/common/Eve Online/SharedCache"))
            } + listOf(home.resolve(".wine/drive_c"), home.resolve("Games/eve-online/drive_c")).flatMap {
                getWineLauncherCandidates(it) + getWineCandidates(it)
            }
            OperatingSystem.Windows -> getWindowsCandidates(home)
            OperatingSystem.MacOs -> getEveLauncherCacheDirectoriesUseCase(
                home.resolve("Library/Application Support/EVE Online"),
            ) + listOf(
                home.resolve("Library/Application Support/EVE Online/SharedCache"),
                Path.of("/Users/Shared/EVE Online/SharedCache"),
            )
        }
        return candidates.distinct().firstOrNull { isCacheDirectoryValid(it) }
    }

    private fun isCacheDirectoryValid(directory: Path): Boolean {
        return isEveSharedCacheDirectoryValidUseCase(directory) && getIndexPath(directory).isRegularFile()
    }

    private fun getIndexPath(directory: Path): Path = directory.resolve(
        when (operatingSystem) {
            OperatingSystem.MacOs -> "tq/EVE.app/Contents/Resources/build/resfileindex.txt"
            else -> "tq/resfileindex.txt"
        },
    )

    private fun getWineLauncherCandidates(drive: Path): List<Path> {
        return listOf("steamuser", System.getProperty("user.name"))
            .distinct()
            .flatMap { user ->
                getEveLauncherCacheDirectoriesUseCase(
                    launcherDirectory = drive.resolve("users/$user/AppData/Roaming/EVE Online"),
                    wineDrive = drive,
                )
            }
    }

    private fun getWineCandidates(drive: Path): List<Path> = listOf(
        drive.resolve("CCP/EVE Online"),
        drive.resolve("CCP/EVE/SharedCache"),
        drive.resolve("EVE/SharedCache"),
        drive.resolve("Program Files/CCP/EVE/SharedCache"),
        drive.resolve("Program Files (x86)/CCP/EVE/SharedCache"),
    )

    private fun getWindowsCandidates(home: Path): List<Path> {
        val steamDirectories = listOfNotNull(
            getWindowsSteamDirectory(),
            System.getenv("ProgramFiles(x86)")?.let { Path.of(it).resolve("Steam") },
            System.getenv("ProgramFiles")?.let { Path.of(it).resolve("Steam") },
        )
        val roots = (
            listOfNotNull(settings.eveSettingsDirectory?.root, home.root) +
                File.listRoots().map { it.toPath() }
            ).distinct()
        val roamingDirectory = System.getenv("APPDATA")?.let { Path.of(it) } ?: home.resolve("AppData/Roaming")
        return getEveLauncherCacheDirectoriesUseCase(roamingDirectory.resolve("EVE Online")) +
            roots.flatMap { getWineCandidates(it) } +
            listOf(home.resolve("AppData/Local/CCP/EVE/SharedCache")) +
            getSteamLibrariesUseCase(steamDirectories).flatMap { library ->
                listOf(
                    library.resolve("steamapps/common/Eve Online/SharedCache"),
                    library.resolve("steamapps/common/Eve Online"),
                )
            }
    }

    private fun getWindowsSteamDirectory(): Path? = try {
        if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SteamPath")) {
            Path.of(Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SteamPath"))
        } else {
            null
        }
    } catch (e: Exception) {
        logger.warn(e) { "Could not read Steam installation directory" }
        null
    }
}
