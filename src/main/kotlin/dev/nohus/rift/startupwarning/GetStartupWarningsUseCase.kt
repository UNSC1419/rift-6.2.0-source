package dev.nohus.rift.startupwarning

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.startup_chat_logs_disabled
import dev.nohus.rift.generated.resources.startup_clock_ahead
import dev.nohus.rift.generated.resources.startup_clock_behind
import dev.nohus.rift.generated.resources.startup_clock_minutes
import dev.nohus.rift.generated.resources.startup_clock_seconds
import dev.nohus.rift.generated.resources.startup_warning_chat_logs_are_disabled
import dev.nohus.rift.generated.resources.startup_warning_fullscreen_eve_client
import dev.nohus.rift.generated.resources.startup_warning_fullscreen_eve_client_body
import dev.nohus.rift.generated.resources.startup_warning_incorrect_system_time
import dev.nohus.rift.generated.resources.startup_warning_msi_afterburner
import dev.nohus.rift.generated.resources.startup_warning_msi_afterburner_body
import dev.nohus.rift.generated.resources.startup_warning_non_english_eve_client
import dev.nohus.rift.generated.resources.startup_warning_non_english_eve_client_body
import dev.nohus.rift.generated.resources.startup_warning_outdated_version
import dev.nohus.rift.generated.resources.startup_warning_outdated_version_body
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.startupwarning.HasIncorrectSystemTimeUseCase.SystemTimeStatus.Incorrect
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class GetStartupWarningsUseCase(
    private val hasNonEnglishEveClient: HasNonEnglishEveClientUseCase,
    private val hasFullScreenEveClient: HasFullScreenEveClientUseCase,
    private val isRunningMsiAfterburner: IsRunningMsiAfterburnerUseCase,
    private val getAccountsWithDisabledChatLogs: GetAccountsWithDisabledChatLogsUseCase,
    private val hasIncorrectSystemTimeUseCase: HasIncorrectSystemTimeUseCase,
    private val isRunningOldVersionUseCase: IsRunningOldVersionUseCase,
    private val settings: Settings,
) {

    data class StartupWarning(
        val id: String,
        val title: UiText,
        val description: UiText,
        val detail: List<UiText> = emptyList(),
    )

    suspend operator fun invoke(): List<StartupWarning> {
        return buildList {
            val systemTimeStatus = hasIncorrectSystemTimeUseCase()
            if (systemTimeStatus is Incorrect) {
                val absoluteOffset = systemTimeStatus.offset.abs()
                val minutes = absoluteOffset.toMinutes()
                val seconds = absoluteOffset.toSecondsPart()
                val resource = if (systemTimeStatus.offset.isNegative) {
                    Res.string.startup_clock_behind
                } else {
                    Res.string.startup_clock_ahead
                }
                val text = resource.toUiText(
                    Res.plurals.startup_clock_minutes.toUiText(minutes.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), minutes.toUiTextArgument()).toUiTextArgument(),
                    Res.plurals.startup_clock_seconds.toUiText(seconds, seconds.toUiTextArgument()).toUiTextArgument(),
                )
                add(
                    StartupWarning(
                        id = "incorrect system time",
                        title = Res.string.startup_warning_incorrect_system_time.toUiText(),
                        description = text,
                    ),
                )
            }
            if (hasNonEnglishEveClient()) {
                add(
                    StartupWarning(
                        id = "non-english client",
                        title = Res.string.startup_warning_non_english_eve_client.toUiText(),
                        description = Res.string.startup_warning_non_english_eve_client_body.toUiText(),
                    ),
                )
            }
            if (hasFullScreenEveClient()) {
                add(
                    StartupWarning(
                        id = "fullscreen client",
                        title = Res.string.startup_warning_fullscreen_eve_client.toUiText(),
                        description = Res.string.startup_warning_fullscreen_eve_client_body.toUiText(),
                    ),
                )
            }
            if (isRunningMsiAfterburner()) {
                add(
                    StartupWarning(
                        id = "msi afterburner",
                        title = Res.string.startup_warning_msi_afterburner.toUiText(),
                        description = Res.string.startup_warning_msi_afterburner_body.toUiText(),
                    ),
                )
            }
            val accountMessages = getAccountsWithDisabledChatLogs()
            if (accountMessages.isNotEmpty()) {
                add(
                    StartupWarning(
                        id = "chat logs disabled v2",
                        title = Res.string.startup_warning_chat_logs_are_disabled.toUiText(),
                        description = Res.plurals.startup_chat_logs_disabled.toUiText(accountMessages.size, accountMessages.size.toUiTextArgument()),
                        detail = accountMessages,
                    ),
                )
            }
            if (isRunningOldVersionUseCase()) {
                add(
                    StartupWarning(
                        id = "old version",
                        title = Res.string.startup_warning_outdated_version.toUiText(),
                        description = Res.string.startup_warning_outdated_version_body.toUiText(),
                    ),
                )
            }
        }
            .also {
                if (it.isNotEmpty()) {
                    logger.warn { "Startup warnings: ${it.joinToString("\n") { warning -> warning.id }}" }
                }
            }
            .filter { it.id !in settings.dismissedWarnings }
    }
}
