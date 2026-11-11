package dev.nohus.rift.sso.authentication

import androidx.compose.ui.graphics.asSkiaBitmap
import dev.nohus.rift.compose.theme.EveFontsRepository
import dev.nohus.rift.dynamicportraits.DynamicPortraitRepository
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.sso_callback_action_add_character
import dev.nohus.rift.generated.resources.sso_callback_action_finish
import dev.nohus.rift.generated.resources.sso_callback_action_retry
import dev.nohus.rift.generated.resources.sso_callback_characters_label
import dev.nohus.rift.generated.resources.sso_callback_document_title
import dev.nohus.rift.generated.resources.sso_callback_failure_message
import dev.nohus.rift.generated.resources.sso_callback_failure_title
import dev.nohus.rift.generated.resources.sso_callback_finished_message
import dev.nohus.rift.generated.resources.sso_callback_finished_title
import dev.nohus.rift.generated.resources.sso_callback_success_message
import dev.nohus.rift.generated.resources.sso_callback_success_title
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.requests.Originator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.koin.core.annotation.Factory
import kotlin.io.encoding.Base64

private val logger = KotlinLogging.logger {}

@Factory
class CallbackPageRenderer(
    private val dynamicPortraitRepository: DynamicPortraitRepository,
    private val esiApi: Lazy<EsiApi>,
    private val appLocale: AppLocale,
    private val eveFontsRepository: EveFontsRepository,
) {

    private data class CallbackPage(
        val stateClass: String,
        val title: String,
        val message: String,
        val actions: String,
    )

    private data class AuthenticatedCharacter(
        val id: Int,
        val name: String,
        val corporationId: Int? = null,
        val allianceId: Int? = null,
    )

    private val authenticatedCharacters = linkedMapOf<Int, AuthenticatedCharacter>()

    fun reset() {
        synchronized(authenticatedCharacters) {
            authenticatedCharacters.clear()
        }
    }

    suspend fun render(result: CallbackServer.CallbackResult): String {
        val html = Res.readBytes("files/sso-callback.html").inputStream().bufferedReader().readText()
        val riftIcon = Base64.encode(Res.readBytes("drawable/window_rift_128.png"))
        val riftFont = eveFontsRepository.state.value.regularFontBytes?.let { Base64.encode(it) } ?: ""
        if (result is CallbackServer.CallbackResult.Success) {
            val character = result.getAuthenticatedCharacter()
            synchronized(authenticatedCharacters) {
                authenticatedCharacters.remove(character.id)
                authenticatedCharacters[character.id] = character
            }
        }
        val characters = synchronized(authenticatedCharacters) {
            authenticatedCharacters.values.toList()
        }
        val page = when (result) {
            is CallbackServer.CallbackResult.Success -> CallbackPage(
                stateClass = "state-success",
                title = Res.string.sso_callback_success_title.toUiText().resolve(appLocale).escapeHtml(),
                message = Res.string.sso_callback_success_message.toUiText(result.characterName.escapeHtml().toUiTextArgument()).resolve(appLocale),
                actions = getActionsHtml(Res.string.sso_callback_action_add_character.toUiText()),
            )
            CallbackServer.CallbackResult.Failure -> CallbackPage(
                stateClass = "state-failure",
                title = Res.string.sso_callback_failure_title.toUiText().resolve(appLocale).escapeHtml(),
                message = Res.string.sso_callback_failure_message.toUiText().resolve(appLocale).escapeHtml(),
                actions = getActionsHtml(Res.string.sso_callback_action_retry.toUiText()),
            )
        }
        val focusedCharacter = characters.lastOrNull().takeUnless { result == CallbackServer.CallbackResult.Failure }
        val templateValues = mapOf(
            "languageTag" to appLocale.language.languageTag.escapeHtml(),
            "documentTitle" to Res.string.sso_callback_document_title.toUiText().resolve(appLocale).escapeHtml(),
            "charactersLabel" to Res.string.sso_callback_characters_label.toUiText().resolve(appLocale).escapeHtml(),
            "finishedTitle" to Res.string.sso_callback_finished_title.toUiText().resolve(appLocale).escapeHtml(),
            "finishedMessage" to Res.string.sso_callback_finished_message.toUiText().resolve(appLocale).escapeHtml(),
            "stateClass" to page.stateClass,
            "title" to page.title,
            "message" to page.message,
            "actions" to page.actions,
            "riftIcon" to riftIcon,
            "riftFont" to riftFont,
            "identityBanners" to focusedCharacter?.getGetIdentityBannersHtml().orEmpty(),
            "portraitBackdrop" to focusedCharacter?.getPortraitBackdropHtml().orEmpty(),
            "foregroundPortrait" to focusedCharacter?.getForegroundPortraitHtml().orEmpty(),
            "characters" to characters.getCharactersHtml(),
        )
        return callbackTemplatePlaceholder.replace(html) { match ->
            templateValues.getValue(match.groupValues[1])
        }
    }

    private suspend fun CallbackServer.CallbackResult.Success.getAuthenticatedCharacter(): AuthenticatedCharacter {
        return try {
            val details = esiApi.value.getCharactersId(Originator.LocalCharacters, characterId).success
                ?: return AuthenticatedCharacter(characterId, characterName)
            AuthenticatedCharacter(
                id = characterId,
                name = characterName,
                corporationId = details.corporationId,
                allianceId = details.allianceId,
            )
        } catch (e: Exception) {
            logger.warn(e) { "Unable to prepare organization details for character $characterId" }
            AuthenticatedCharacter(characterId, characterName)
        }
    }

    private fun List<AuthenticatedCharacter>.getCharactersHtml(): String {
        if (isEmpty()) return ""
        return mapIndexed { index, character ->
            val latestClass = if (index == lastIndex) " latest-character" else ""
            """
                    <div
                        class="character-node$latestClass"
                        style="--order: $index"
                        aria-label="${character.name.escapeHtml()}"
                    >
                        <img src="${character.getPortraitUrl(128)}" alt="" width="64" height="64" />
                        <span class="character-name">${character.name.escapeHtml()}</span>
                    </div>
            """.trimIndent()
        }.joinToString("")
    }

    private fun AuthenticatedCharacter.getPortraitBackdropHtml(): String = """
            <div class="focused-character" aria-hidden="true">
                <img src="${getPortraitUrl(256)}" alt="" />
            </div>
    """.trimIndent()

    private fun AuthenticatedCharacter.getGetIdentityBannersHtml(): String = buildList {
        corporationId?.let { add(getIdentityBannerHtml("identity-corporation", "corporations", it)) }
        allianceId?.let { add(getIdentityBannerHtml("identity-alliance", "alliances", it)) }
    }.joinToString("")

    private suspend fun AuthenticatedCharacter.getForegroundPortraitHtml(): String {
        return try {
            val portrait = dynamicPortraitRepository.getCharacterPortrait(
                characterId = id,
                portraitSize = 768,
                backgroundSize = 768,
            )?.portrait ?: return ""
            val encodedPortrait = Image.makeFromBitmap(portrait.asSkiaBitmap())
                .encodeToData(EncodedImageFormat.PNG)
                ?.bytes
                ?.let(Base64::encode)
                ?: return ""
            """
                <div class="foreground-character" aria-hidden="true">
                    <img class="foreground-character-image" src="data:image/png;base64,$encodedPortrait" alt="" />
                </div>
            """.trimIndent()
        } catch (e: Exception) {
            logger.warn(e) { "Unable to prepare foreground portrait for character $id" }
            ""
        }
    }

    private fun AuthenticatedCharacter.getPortraitUrl(size: Int): String {
        return "https://images.evetech.net/characters/$id/portrait?size=$size"
    }

    private fun getIdentityBannerHtml(
        className: String,
        organizationType: String,
        organizationId: Int,
    ): String {
        return """
            <div class="identity-emblem $className">
                <img src="https://images.evetech.net/$organizationType/$organizationId/logo?size=256" alt="" />
            </div>
        """.trimIndent()
    }

    private fun String.escapeHtml(): String = buildString(length) {
        for (character in this@escapeHtml) {
            append(
                when (character) {
                    '&' -> "&amp;"
                    '<' -> "&lt;"
                    '>' -> "&gt;"
                    '"' -> "&quot;"
                    '\'' -> "&#39;"
                    else -> character
                },
            )
        }
    }

    private suspend fun getActionsHtml(authenticateLabel: UiText): String {
        val label = authenticateLabel.resolve(appLocale).escapeHtml()
        val finishLabel = Res.string.sso_callback_action_finish.toUiText().resolve(appLocale).escapeHtml()
        return """
            <div class="actions">
                <a class="button button-primary" href="/authenticate"><span>$label</span></a>
                <a id="finish-button" class="button button-secondary" href="/finish"><span>$finishLabel</span></a>
            </div>
        """.trimIndent()
    }

    private companion object {
        val callbackTemplatePlaceholder = Regex("""\{\{(\w+)}}""")
    }
}
