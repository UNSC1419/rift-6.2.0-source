package dev.nohus.rift.startupwarning

import dev.nohus.rift.localization.DetectEveClientLanguageUseCase
import dev.nohus.rift.settings.persistence.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class HasNonEnglishEveClientUseCase(
    private val settings: Settings,
    private val detectEveClientLanguageUseCase: DetectEveClientLanguageUseCase,
) {

    suspend operator fun invoke(): Boolean {
        if (!settings.isSetupWizardFinished) return false
        return withContext(Dispatchers.IO) {
            val language = detectEveClientLanguageUseCase()
            if (language != null && !language.equals("EN", ignoreCase = true)) {
                logger.warn { "Client language is $language" }
                true
            } else {
                false
            }
        }
    }
}
