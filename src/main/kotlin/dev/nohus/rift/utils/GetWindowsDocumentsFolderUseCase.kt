package dev.nohus.rift.utils

import com.sun.jna.platform.win32.KnownFolders
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.Shell32
import com.sun.jna.platform.win32.ShlObj
import com.sun.jna.platform.win32.W32Errors
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.ptr.PointerByReference
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

private const val KF_FLAG_NO_PACKAGE_REDIRECTION = 0x00010000

@Single
class GetWindowsDocumentsFolderUseCase {

    operator fun invoke(): String? {
        return try {
            val outPath = PointerByReference()
            try {
                val flags = KF_FLAG_NO_PACKAGE_REDIRECTION or ShlObj.KNOWN_FOLDER_FLAG.DONT_VERIFY.flag
                val result = Shell32.INSTANCE.SHGetKnownFolderPath(KnownFolders.FOLDERID_Documents, flags, null, outPath)
                if (!W32Errors.SUCCEEDED(result.toInt())) throw Win32Exception(result)
                outPath.value.getWideString(0)
            } finally {
                outPath.value?.let { Ole32.INSTANCE.CoTaskMemFree(it) }
            }
        } catch (e: RuntimeException) {
            logger.error { "Failed checking Windows documents folder location: ${e.message}" }
            null
        }
    }
}
