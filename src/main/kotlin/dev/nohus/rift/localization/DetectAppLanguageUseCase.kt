package dev.nohus.rift.localization

import org.koin.core.annotation.Single
import java.util.Locale

@Single
class DetectAppLanguageUseCase(
    private val detectEveClientLanguageUseCase: DetectEveClientLanguageUseCase,
) {

    operator fun invoke(): SupportedLanguage {
        return detectEveClientLanguageUseCase()?.let { SupportedLanguage.findLanguage(it) }
            ?: SupportedLanguage.findLanguage(Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag())
            ?: SupportedLanguage.English
    }
}
