package dev.nohus.rift.localization

import dev.nohus.rift.settings.persistence.Settings
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import org.koin.core.annotation.Single
import java.util.Locale
import org.jetbrains.compose.resources.getPluralString as getResourcePluralString
import org.jetbrains.compose.resources.getString as getResourceString

@Single
class AppLocale(
    settings: Settings,
    detectAppLanguageUseCase: DetectAppLanguageUseCase,
) {
    val language: SupportedLanguage = settings.languageTag?.let { SupportedLanguage.findLanguage(it) ?: SupportedLanguage.English }
        ?: detectAppLanguageUseCase()
    val locale: Locale = Locale.forLanguageTag(language.languageTag)
    private lateinit var resourceEnvironment: ResourceEnvironment

    fun initialize() {
        // Keep the active locale fixed until restart so windows and background messages cannot mix languages
        Locale.setDefault(locale)
        resourceEnvironment = getSystemResourceEnvironment()
    }

    suspend fun getString(resource: StringResource, vararg arguments: Any): String {
        return getResourceString(resourceEnvironment, resource, *arguments)
    }

    suspend fun getPluralString(resource: PluralStringResource, quantity: Int, vararg arguments: Any): String {
        return getResourcePluralString(resourceEnvironment, resource, quantity, *arguments)
    }
}
