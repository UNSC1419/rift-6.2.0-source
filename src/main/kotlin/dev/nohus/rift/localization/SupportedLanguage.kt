package dev.nohus.rift.localization

import dev.nohus.rift.BuildConfig
import java.util.Locale

enum class SupportedLanguage(
    val languageTag: String,
    val nativeName: String,
    val isPublished: Boolean,
) {
    English("en", "English", isPublished = true),
    Polish("pl", "Polski", isPublished = false),
    ;

    companion object {
        fun findLanguage(languageTag: String): SupportedLanguage? {
            val locale = Locale.forLanguageTag(languageTag.trim().replace('_', '-'))
            val published = entries.filter { it.isPublished || BuildConfig.isDevEnvironment }
            return published.firstOrNull { it.languageTag == locale.toLanguageTag() }
                ?: published.firstOrNull { it.languageTag == locale.language }
        }
    }
}
