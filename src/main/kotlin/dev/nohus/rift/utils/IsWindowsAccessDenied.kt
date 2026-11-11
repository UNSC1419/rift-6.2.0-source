package dev.nohus.rift.utils

import com.sun.jna.LastErrorException
import com.sun.jna.platform.win32.Kernel32Util
import com.sun.jna.platform.win32.WinError
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.nio.file.AccessDeniedException
import java.nio.file.FileSystemException

private val logger = KotlinLogging.logger {}

fun isWindowsAccessDenied(exception: Throwable): Boolean {
    if (GetOperatingSystemUseCase()() != OperatingSystem.Windows) return false
    val causes = generateSequence(exception) { it.cause }.toList()
    if (causes.any { it is AccessDeniedException }) return true
    val ioExceptions = causes.filterIsInstance<IOException>()
    if (ioExceptions.isEmpty()) return false

    // Java's native lock errors retain only the system-localized message, not the Windows error code
    val accessDeniedMessage = try {
        Kernel32Util.formatMessage(WinError.ERROR_ACCESS_DENIED).trim().removeSuffix(".")
    } catch (e: LastErrorException) {
        logger.warn(e) { "Could not obtain the Windows access denied message" }
        return false
    }
    if (accessDeniedMessage.isBlank()) return false
    return ioExceptions.any {
        val message = if (it is FileSystemException) it.reason else it.message
        // Some JVM versions append an operation description in parentheses
        val normalizedMessage = message?.substringBefore(" (")?.trim()?.removeSuffix(".")
        normalizedMessage == accessDeniedMessage
    }
}
