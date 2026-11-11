package dev.nohus.rift.whatsnew

import dev.nohus.rift.BuildConfig
import dev.nohus.rift.database.local.LocalDatabase
import dev.nohus.rift.settings.persistence.MapSystemInfoType
import dev.nohus.rift.settings.persistence.MapType
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.sso.authentication.EveSsoRepository
import dev.nohus.rift.utils.GetOperatingSystemUseCase
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.directories.AppDirectories
import dev.nohus.rift.windowing.WindowManager
import dev.nohus.rift.windowing.WindowManager.RiftWindow
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.io.IOException
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

private val logger = KotlinLogging.logger {}

@Single
class WhatsNewController(
    private val windowManager: WindowManager,
    private val settings: Settings,
    private val eveSsoRepository: EveSsoRepository,
    private val localDatabase: LocalDatabase,
    private val appDirectories: AppDirectories,
    private val getOperatingSystemUseCase: GetOperatingSystemUseCase,
) {

    fun showIfRequired() {
        val lastShownVersion = settings.whatsNewVersion
        val currentVersion = BuildConfig.version
        if (lastShownVersion != null && lastShownVersion != currentVersion) {
            runMigrations(lastShownVersion)
            val hasChangelog = WhatsNew.getVersions().any {
                VersionUtils.isNewer(lastShownVersion, it.version)
            }
            if (hasChangelog) {
                windowManager.onWindowOpen(RiftWindow.WhatsNew)
            }
        }
        resetWhatsNewVersion()
    }

    fun resetWhatsNewVersion() {
        settings.whatsNewVersion = BuildConfig.version
    }

    private fun runMigrations(lastVersion: String) {
        val migrations = listOf(
            "4.8.0" to {
                // New scope added
                eveSsoRepository.removeAllAuthentications()
            },
            "4.13.0" to {
                localDatabase.dropOldTable()
            },
            "5.20.0" to {
                settings.intelMap = settings.intelMap.let {
                    it.copy(
                        mapTypeInfoBoxInfoTypes = it.mapTypeInfoBoxInfoTypes +
                            (MapType.NewEden to (it.mapTypeInfoBoxInfoTypes[MapType.NewEden] ?: emptyList()) + MapSystemInfoType.RaidableSkyhooks) +
                            (MapType.Region to (it.mapTypeInfoBoxInfoTypes[MapType.Region] ?: emptyList()) + MapSystemInfoType.RaidableSkyhooks) +
                            (MapType.Distance to (it.mapTypeInfoBoxInfoTypes[MapType.Distance] ?: emptyList()) + MapSystemInfoType.RaidableSkyhooks),
                    )
                }
            },
            "5.30.0" to {
                settings.intelMap = settings.intelMap.let {
                    it.copy(
                        mapTypeInfoBoxInfoTypes = it.mapTypeInfoBoxInfoTypes +
                            (MapType.NewEden to (it.mapTypeInfoBoxInfoTypes[MapType.NewEden] ?: emptyList()) + MapSystemInfoType.SovereigntyCapitalSystem) +
                            (MapType.Region to (it.mapTypeInfoBoxInfoTypes[MapType.Region] ?: emptyList()) + MapSystemInfoType.SovereigntyCapitalSystem) +
                            (MapType.Distance to (it.mapTypeInfoBoxInfoTypes[MapType.Distance] ?: emptyList()) + MapSystemInfoType.SovereigntyCapitalSystem),
                    )
                }
            },
            "5.33.0" to {
                settings.windowSettings -= RiftWindow.Neocom
            },
            "6.0.0" to {
                settings.windowSettings -= RiftWindow.Neocom
            },
            "6.2.0" to {
                deleteOldWindowsCache()
            },
        )
        migrations.forEach { [version, migration] ->
            if (VersionUtils.isNewer(lastVersion, version)) {
                migration()
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun deleteOldWindowsCache() {
        if (getOperatingSystemUseCase() != OperatingSystem.Windows) return
        try {
            appDirectories.getAppDataDirectory().resolve("cache").deleteRecursively()
        } catch (e: Exception) {
            logger.warn(e) { "Could not delete the old Windows cache directory" }
        }
    }
}
