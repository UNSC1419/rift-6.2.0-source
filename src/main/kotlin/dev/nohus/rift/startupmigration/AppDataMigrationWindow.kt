package dev.nohus.rift.startupmigration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.migration_cleaning_up
import dev.nohus.rift.generated.resources.migration_close
import dev.nohus.rift.generated.resources.migration_description
import dev.nohus.rift.generated.resources.migration_failure
import dev.nohus.rift.generated.resources.migration_from
import dev.nohus.rift.generated.resources.migration_preparing
import dev.nohus.rift.generated.resources.migration_progress
import dev.nohus.rift.generated.resources.migration_retry
import dev.nohus.rift.generated.resources.migration_title
import dev.nohus.rift.generated.resources.migration_to
import dev.nohus.rift.generated.resources.window_rift_64
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.startupwarning.showAppDataAccessDeniedWindow
import dev.nohus.rift.utils.GetOperatingSystemUseCase
import dev.nohus.rift.utils.GetWindowsDocumentsFolderUseCase
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.isWindowsAccessDenied
import dev.nohus.rift.utils.osdirectories.WindowsDirectories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

fun migrateWindowsAppData(): Boolean {
    if (GetOperatingSystemUseCase()() != OperatingSystem.Windows) return true
    return try {
        migrateWindowsAppDataIfNeeded()
    } catch (e: IOException) {
        if (!isWindowsAccessDenied(e)) throw e
        showAppDataAccessDeniedWindow()
        false
    }
}

private fun migrateWindowsAppDataIfNeeded(): Boolean {
    val source = WindowsDirectories().getUserDirectory().resolve("Documents/RIFT")
    if (!source.isDirectory()) return true
    val documentsFolder = GetWindowsDocumentsFolderUseCase()()?.takeIf { it.isNotBlank() } ?: return true
    val destination = Path.of(documentsFolder).resolve("RIFT")
    if (source.toAbsolutePath().normalize() == destination.toAbsolutePath().normalize()) return true
    if (destination.isDirectory() && Files.isSameFile(source, destination)) return true
    return showAppDataMigrationWindow(source, destination)
}

private fun showAppDataMigrationWindow(source: Path, destination: Path): Boolean {
    var isSuccessful = false
    var accessDeniedException: Exception? = null
    application(exitProcessOnExit = false) {
        var progress by remember { mutableStateOf(MigrateAppDataUseCase.Progress()) }
        var error by remember { mutableStateOf<UiText?>(null) }
        var attempt by remember { mutableStateOf(0) }
        LaunchedEffect(attempt) {
            error = null
            progress = MigrateAppDataUseCase.Progress()
            try {
                withContext(Dispatchers.IO) {
                    MigrateAppDataUseCase()(source, destination) { update -> progress = update }
                }
                isSuccessful = true
                exitApplication()
            } catch (e: Exception) {
                if (isWindowsAccessDenied(e)) {
                    accessDeniedException = e
                    exitApplication()
                } else {
                    error = if (e is MigrateAppDataUseCase.MigrationException) e.reason else (e.message ?: e.toString()).toUiText()
                }
            }
        }
        Window(
            onCloseRequest = { if (error != null) exitApplication() },
            title = stringResource(Res.string.migration_title),
            icon = painterResource(Res.drawable.window_rift_64),
            state = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center), width = 560.dp, height = 300.dp),
            resizable = true,
        ) {
            RiftTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(RiftTheme.colors.windowBackground).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(stringResource(Res.string.migration_description))
                    Text(stringResource(Res.string.migration_from, source.toString()), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(stringResource(Res.string.migration_to, destination.toString()), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val totalFiles = progress.totalFiles
                    if (totalFiles == null) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = RiftTheme.colors.primary)
                        Text(stringResource(Res.string.migration_preparing))
                    } else {
                        LinearProgressIndicator(
                            progress = if (totalFiles == 0) 1f else progress.copiedFiles.toFloat() / totalFiles,
                            modifier = Modifier.fillMaxWidth(),
                            color = RiftTheme.colors.primary,
                        )
                        Text(
                            if (progress.isCleaningUp) {
                                stringResource(Res.string.migration_cleaning_up)
                            } else {
                                stringResource(Res.string.migration_progress, progress.copiedFiles, totalFiles)
                            },
                        )
                    }
                    progress.currentFile?.let {
                        Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    error?.let {
                        Text(stringResource(Res.string.migration_failure), color = RiftTheme.colors.textRed)
                        Text(it.resolve(), modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
                        Button(onClick = { attempt++ }) { Text(stringResource(Res.string.migration_retry)) }
                        Button(onClick = { exitApplication() }) { Text(stringResource(Res.string.migration_close)) }
                    }
                }
            }
        }
    }
    accessDeniedException?.let { throw it }
    return isSuccessful
}
