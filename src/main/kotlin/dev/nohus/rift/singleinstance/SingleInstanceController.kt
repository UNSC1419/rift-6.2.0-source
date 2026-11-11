package dev.nohus.rift.singleinstance

import dev.nohus.rift.utils.directories.AppDirectories
import dev.nohus.rift.utils.isWindowsAccessDenied
import org.koin.core.annotation.Single
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE

@Single
class SingleInstanceController(
    appDirectories: AppDirectories,
) {

    private val lockResult = acquireLock(appDirectories)

    private fun acquireLock(appDirectories: AppDirectories): Result<FileLock?> {
        var channel: FileChannel? = null
        return try {
            val lockFile = appDirectories.getAppDataDirectory().resolve(".lock")
            channel = FileChannel.open(lockFile, WRITE, CREATE)
            Result.success(channel.tryLock().also { if (it == null) channel.close() })
        } catch (e: IOException) {
            try {
                channel?.close()
            } catch (closeException: IOException) {
                e.addSuppressed(closeException)
            }
            if (!isWindowsAccessDenied(e)) throw e
            Result.failure(e)
        }
    }

    fun hasAppDataAccess(): Boolean {
        return lockResult.isSuccess
    }

    fun isInstanceRunning(): Boolean {
        return lockResult.getOrThrow() == null
    }
}
