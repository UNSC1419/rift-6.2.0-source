package dev.nohus.rift.utils.osdirectories

import dev.nohus.rift.utils.GetWindowsDocumentsFolderUseCase
import org.apache.commons.lang3.SystemUtils
import java.nio.file.Path
import kotlin.io.path.createDirectories

class WindowsDirectories(
    private val getWindowsDocumentsFolderUseCase: GetWindowsDocumentsFolderUseCase = GetWindowsDocumentsFolderUseCase(),
) : OperatingSystemDirectories {

    override fun getUserDirectory(): Path {
        return Path.of(SystemUtils.USER_HOME)
    }

    override fun getAppConfigDirectory(applicationName: String): Path {
        return getUserDocumentsDirectory().resolve(applicationName).also { it.createDirectories() }
    }

    override fun getAppCacheDirectory(applicationName: String): Path {
        return getLocalAppDataDirectory().resolve(applicationName).resolve("cache").also { it.createDirectories() }
    }

    private fun getLocalAppDataDirectory(): Path {
        val localAppData = System.getenv("LOCALAPPDATA")?.trim()?.takeIf { it.isNotEmpty() }
        if (localAppData != null) return Path.of(localAppData)
        return getUserDirectory().resolve("AppData").resolve("Local")
    }

    private fun getUserDocumentsDirectory(): Path {
        val documentsFolder = getWindowsDocumentsFolderUseCase()?.takeIf { it.isNotBlank() }
        if (documentsFolder != null) return Path.of(documentsFolder)
        return getUserDirectory().resolve("Documents")
    }
}
