package dev.nohus.rift.utils.evewindows

import dev.nohus.rift.BuildConfig
import org.koin.core.annotation.Single

@Single
class EveWindowTitleParser {

    private val prefix = "EVE - "
    private val characterNameLength = 3..37
    private val getMock = if (BuildConfig.isDevEnvironment) ::getOverlayMock else null

    fun getCharacterName(title: String): String? {
        getMock?.invoke(title)?.let { return it }
        if (!title.startsWith(prefix)) return null
        val characterName = title.removePrefix(prefix).trim()
        return characterName.takeIf { it.length in characterNameLength }
    }

    private fun getOverlayMock(title: String): String? {
        return if (title.contains("overlay-mock-")) {
            title.substringAfterLast("-").substringBefore(".")
        } else {
            null
        }
    }
}
