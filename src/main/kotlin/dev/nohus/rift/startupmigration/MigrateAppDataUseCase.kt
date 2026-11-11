package dev.nohus.rift.startupmigration

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.migration_error_close_instances
import dev.nohus.rift.generated.resources.migration_error_conflicting_data
import dev.nohus.rift.generated.resources.migration_error_nested_folders
import dev.nohus.rift.generated.resources.migration_error_symbolic_link
import dev.nohus.rift.generated.resources.migration_error_unsupported_file
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.isSymbolicLink

class MigrateAppDataUseCase {

    operator fun invoke(source: Path, destination: Path, onProgress: (Progress) -> Unit) {
        if (destination.isSymbolicLink()) {
            throw MigrationException(Res.string.migration_error_symbolic_link.toUiText(destination.toString().toUiTextArgument()))
        }
        destination.createDirectories()
        val sourcePath = source.toRealPath()
        val destinationPath = destination.toRealPath()
        if (sourcePath.startsWith(destinationPath) || destinationPath.startsWith(sourcePath)) {
            throw MigrationException(Res.string.migration_error_nested_folders.toUiText())
        }
        FileChannel.open(destination.resolve(".lock"), CREATE, WRITE).use { targetChannel ->
            val targetLock = targetChannel.tryLock() ?: throw MigrationException(Res.string.migration_error_close_instances.toUiText())
            targetLock.use {
                val sourceLockFile = source.resolve(".lock")
                FileChannel.open(sourceLockFile, CREATE, WRITE).use { sourceChannel ->
                    val sourceLock = sourceChannel.tryLock() ?: throw MigrationException(Res.string.migration_error_close_instances.toUiText())
                    sourceLock.use {
                        copyAndVerifyData(source, destination, sourceLockFile, onProgress)
                    }
                }
                sourceLockFile.deleteExisting()
                source.deleteExisting()
            }
        }
    }

    private fun copyAndVerifyData(source: Path, destination: Path, lockFile: Path, onProgress: (Progress) -> Unit) {
        onProgress(Progress())
        val entries = Files.walk(source).use { paths -> paths.filter { it != source && it != lockFile }.toList() }
        val files = entries.filter { !it.isDirectory(NOFOLLOW_LINKS) }
        onProgress(Progress(totalFiles = files.size))
        entries.forEach { path ->
            if (path.isSymbolicLink() || (!path.isDirectory(NOFOLLOW_LINKS) && !path.isRegularFile(NOFOLLOW_LINKS))) {
                throw MigrationException(Res.string.migration_error_unsupported_file.toUiText(path.toString().toUiTextArgument()))
            }
            val target = destination.resolve(source.relativize(path))
            if (target.isSymbolicLink()) {
                throw MigrationException(Res.string.migration_error_symbolic_link.toUiText(target.toString().toUiTextArgument()))
            }
            if (path.isDirectory(NOFOLLOW_LINKS)) target.createDirectories()
        }
        files.forEachIndexed { index, path ->
            val relativePath = source.relativize(path)
            val target = destination.resolve(relativePath)
            onProgress(Progress(index, files.size, relativePath.toString()))
            if (target.exists(NOFOLLOW_LINKS)) {
                verifyCopiedFile(path, target)
            } else {
                val temporaryFile = Files.createTempFile(target.parent, ".rift-migration-", ".tmp")
                try {
                    Files.copy(path, temporaryFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                    verifyCopiedFile(path, temporaryFile)
                    // Do not overwrite data created by another installation
                    Files.move(temporaryFile, target)
                } finally {
                    temporaryFile.deleteIfExists()
                }
            }
            onProgress(Progress(index + 1, files.size, relativePath.toString()))
        }
        // Keep all originals until every file has been copied and verified successfully
        onProgress(Progress(files.size, files.size, isCleaningUp = true))
        entries.asReversed().forEach { path ->
            if (!path.isDirectory(NOFOLLOW_LINKS)) {
                verifyCopiedFile(path, destination.resolve(source.relativize(path)))
            }
            path.deleteExisting()
        }
    }

    private fun verifyCopiedFile(source: Path, destination: Path) {
        if (!destination.isRegularFile(NOFOLLOW_LINKS) || Files.mismatch(source, destination) != -1L) {
            throw MigrationException(Res.string.migration_error_conflicting_data.toUiText(destination.toString().toUiTextArgument()))
        }
    }

    class MigrationException(val reason: UiText) : IOException()

    data class Progress(
        val copiedFiles: Int = 0,
        val totalFiles: Int? = null,
        val currentFile: String? = null,
        val isCleaningUp: Boolean = false,
    )
}
