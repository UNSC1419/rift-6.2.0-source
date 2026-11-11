package dev.nohus.rift.database

import dev.nohus.rift.compose.text.FormattedText
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.database_load_failure_message
import dev.nohus.rift.generated.resources.database_load_failure_title
import dev.nohus.rift.generated.resources.database_write_failure_message
import dev.nohus.rift.generated.resources.database_write_failure_title
import dev.nohus.rift.infodialog.InfoDialogInputModel
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.utils.directories.AppDirectories
import dev.nohus.rift.windowing.WindowManager
import dev.nohus.rift.windowing.WindowManager.RiftWindow
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.skiko.MainUIDispatcher
import org.koin.core.annotation.Single
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicBoolean

private val logger = KotlinLogging.logger {}

@Single
class ShowDatabaseFailureUseCase(
    private val windowManager: WindowManager,
    private val appLocale: AppLocale,
    private val appDirectories: AppDirectories,
) {

    enum class Failure {
        CharacterCacheWrite,
        StaticDatabaseLoad,
    }

    private data class FailureResources(
        val title: StringResource,
        val message: StringResource,
        val path: String,
    )

    private val hasShownWarning = AtomicBoolean(false)

    suspend operator fun invoke(exception: SQLException, failure: Failure) {
        if (!hasShownWarning.compareAndSet(false, true)) return
        val resources = when (failure) {
            Failure.CharacterCacheWrite -> {
                logger.error(exception) { "Could not save character lookup cache to the local database" }
                FailureResources(
                    Res.string.database_write_failure_title,
                    Res.string.database_write_failure_message,
                    appDirectories.getAppDataDirectory().resolve("local.db").toString(),
                )
            }
            Failure.StaticDatabaseLoad -> {
                logger.error(exception) { "Could not load the static database from temporary storage" }
                FailureResources(
                    Res.string.database_load_failure_title,
                    Res.string.database_load_failure_message,
                    System.getProperty("java.io.tmpdir"),
                )
            }
        }
        val inputModel = InfoDialogInputModel(
            title = appLocale.getString(resources.title),
            text = FormattedText.Plain(appLocale.getString(resources.message, resources.path)),
            isWarning = true,
        )
        withContext(MainUIDispatcher) {
            windowManager.onWindowOpen(RiftWindow.InfoDialog, inputModel)
        }
    }
}
