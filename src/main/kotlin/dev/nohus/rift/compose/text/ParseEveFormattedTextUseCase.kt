package dev.nohus.rift.compose.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.MulticolorIconType
import dev.nohus.rift.compose.RiftMulticolorIcon
import dev.nohus.rift.game.GameUiController
import dev.nohus.rift.repositories.ExternalServiceRepository
import dev.nohus.rift.types.TypeDetails
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.utils.entitylinks.EntityInteractionProvider
import dev.nohus.rift.utils.entitylinks.EntityInteractionProvider.Interaction
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.ALLIANCE_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.CHARACTER_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.CONSTELLATION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.CORPORATION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.FACTION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.REGION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.SOLAR_SYSTEM_TYPE_ID
import dev.nohus.rift.utils.get
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.toURIOrNull
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.parser.Parser
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class ParseEveFormattedTextUseCase(
    private val gameUiController: GameUiController,
    private val entityInteractionProvider: EntityInteractionProvider,
    private val typesRepository: TypesRepository,
) {

    private val colors = mapOf(
        "black" to "FF000000",
        "green" to "FF008000",
        "silver" to "FFC0C0C0",
        "lime" to "FF00FF00",
        "gray" to "FF808080",
        "grey" to "FF808080",
        "olive" to "FF808000",
        "white" to "FFFFFFFF",
        "yellow" to "FFFFFF00",
        "maroon" to "FF800000",
        "navy" to "FF000080",
        "red" to "FFFF0000",
        "blue" to "FF0000FF",
        "purple" to "FF800080",
        "teal" to "FF008080",
        "fuchsia" to "FFFF00FF",
        "aqua" to "FF00FFFF",
        "orange" to "FFFF8000",
        "transparent" to "00000000",
        "lightred" to "FFCC3333",
        "lightblue" to "FF7777FF",
        "lightgreen" to "FF80ff80",
        // Not used in EVE, but useful for internal RIFT use
        "textprimary" to "BFFFFFFF",
        "texthighlighted" to "E5FFFFFF",
    )

    /**
     * If provided, `typeDetails` will be used to construct links with context menus
     */
    operator fun invoke(text: String, typeDetails: TypeDetails? = null, decodeEntities: Boolean = false): FormattedText {
        val tokens = parse(emptyList(), text).let { tokens ->
            if (decodeEntities) {
                tokens.map { token ->
                    if (token is Token.Text) Token.Text(Parser.unescapeEntities(token.text, false)) else token
                }
            } else {
                tokens
            }
        }
        return format(tokens, typeDetails = typeDetails)
    }

    /**
     * Returns formatted text from tokens
     */
    context(typeDetails: TypeDetails?)
    private fun format(remaining: List<Token>): FormattedText {
        if (remaining.isEmpty()) return FormattedText.Empty
        when (val nextToken = remaining.first()) {
            is Token.OpenTag -> {
                // Find the corresponding close tag
                var depth = 1
                var index = 1
                while (index <= remaining.lastIndex) {
                    remaining[index].let {
                        if (it is Token.OpenTag && it.tag == nextToken.tag) depth++
                        if (it is Token.CloseTag && it.tag == nextToken.tag) depth--
                        if (depth == 0) {
                            return applyTag(nextToken, remaining.subList(1, index)) + format(remaining.drop(index + 1))
                        }
                        index++
                    }
                }
                // There was no close tag, apply until the end
                return applyTag(nextToken, remaining.drop(1))
            }
            is Token.CloseTag -> {
                // Close tag without a corresponding open tag, ignore it
                return format(remaining.drop(1))
            }
            is Token.Text -> {
                return nextToken.text.toFormattedText() + format(remaining.drop(1))
            }
        }
    }

    /**
     * Returns formatted text from tokens, applying the given tag as the formatting
     */
    context(typeDetails: TypeDetails?)
    private fun applyTag(tag: Token.OpenTag, content: List<Token>): FormattedText {
        return when (tag.tag.lowercase()) {
            "b" -> FormattedText.Formatted(
                text = format(content),
                spans = listOf(Span.Weight(SpanTarget.Full, FontWeight.Bold)),
            )
            "i" -> FormattedText.Formatted(
                text = format(content),
                spans = listOf(Span.Italics(SpanTarget.Full)),
            )
            "u" -> FormattedText.Formatted(
                text = format(content),
                spans = listOf(Span.Underline(SpanTarget.Full)),
            )
            "font", "color" -> {
                val spans = mutableListOf<Span>()
                tag.parameters["size"]?.toIntOrNull()?.let {
                    spans += Span.Size(SpanTarget.Full, it)
                }
                tag.parameters["color"]?.let {
                    parseColor(it)?.let { spans += it }
                }
                FormattedText.Formatted(format(content), spans)
            }
            "a" -> {
                tag.parameters["href"].let { href ->
                    when (val scheme = href?.substringBefore(":")) {
                        null -> {
                            format(content)
                        }
                        "showinfo", "killReport", "opportunity", "localsvc", "helpPointer", "joinChannel" -> {
                            val text = format(content)
                            val style = when (scheme) {
                                "helpPointer" -> LinkStyle.Help
                                "joinChannel" -> LinkStyle.Invite
                                else -> LinkStyle.Default
                            }
                            val interaction = if (scheme == "killReport") {
                                getKillReportInteraction(href, text.toPlainString())
                            } else {
                                getLinkInteraction(href, text.toPlainString())
                            }
                            val span = Span.CustomLink(
                                target = SpanTarget.Full,
                                link = Link(
                                    style = style,
                                    onClick = { interaction.onClick() },
                                    contextMenuItems = interaction.contextMenuItems,
                                ),
                            )
                            FormattedText.Formatted(text, listOf(span))
                        }
                        else -> {
                            val span = Span.CustomLink(
                                target = SpanTarget.Full,
                                link = Link(
                                    style = LinkStyle.External,
                                    onClick = { href.toURIOrNull()?.openBrowser() },
                                    contextMenuItems = null,
                                ),
                            )
                            FormattedText.Formatted(format(content), listOf(span))
                        }
                    }
                }
            }
            "loc" -> format(content)
            "br" -> "\n".toFormattedText() + format(content)
            else -> {
                logger.warn { "Unimplemented tag: $tag" }
                format(content)
            }
        }
    }

    context(typeDetails: TypeDetails?)
    private fun getLinkInteraction(href: String, text: String): Interaction {
        val fallbackInteraction by lazy {
            Interaction(
                onClick = { gameUiController.pushUrl(href, text) },
                contextMenuItems = emptyList(),
            )
        }
        val showInfo = parseShowInfoHref(href) ?: return fallbackInteraction
        val typeDetail = showInfo.itemId?.let { typeDetails?.get(it) }

        return typeDetail
            ?.let { entityInteractionProvider.getFrom(typeDetail, showInfo.itemId) }
            ?: when (showInfo.typeId) {
                // These fallbacks are used when the parser is used without TypeDetails,
                // or when a referenced entity could not be loaded
                CHARACTER_TYPE_ID -> showInfo.itemId?.toInt()?.let(entityInteractionProvider::getCharacter)
                CORPORATION_TYPE_ID -> showInfo.itemId?.toInt()?.let(entityInteractionProvider::getCorporation)
                ALLIANCE_TYPE_ID -> showInfo.itemId?.toInt()?.let(entityInteractionProvider::getAlliance)
                SOLAR_SYSTEM_TYPE_ID -> showInfo.itemId?.toInt()?.let { entityInteractionProvider.getLocation(it) }
                else -> typesRepository.getType(showInfo.typeId)?.let {
                    entityInteractionProvider.getType(it, showInfo.itemId)
                }
            }
            ?: fallbackInteraction
    }

    private fun getKillReportInteraction(href: String, text: String): Interaction {
        val killReport = parseKillReportHref(href) ?: return Interaction(
            onClick = { gameUiController.pushUrl(href, text) },
            contextMenuItems = emptyList(),
        )
        return entityInteractionProvider.getKillmail(killReport.killmailId, killReport.killmailHash)
    }

    private fun parseKillReportHref(href: String): KillReport? {
        val value = href.substringAfter("killReport:", missingDelimiterValue = "")
        val parts = value.split(":")
        if (parts.size != 2) return null
        val killmailId = parts[0].toLongOrNull() ?: return null
        val killmailHash = parts[1].takeIf { it.isNotEmpty() } ?: return null
        return KillReport(killmailId, killmailHash)
    }

    private fun parseShowInfoHref(href: String): ShowInfo? {
        val value = href.substringAfter("showinfo:", missingDelimiterValue = "")
        if (value.isEmpty()) return null
        val typeId = value.substringBefore("//").toIntOrNull() ?: return null
        val itemId = value.substringAfter("//", missingDelimiterValue = "").substringBefore('/').toLongOrNull()
        return ShowInfo(typeId, itemId)
    }

    private data class ShowInfo(val typeId: Int, val itemId: Long?)

    private data class KillReport(val killmailId: Long, val killmailHash: String)

    private fun parseColor(color: String): Span.CustomColor? {
        val hex = when {
            color.startsWith("#") -> color.removePrefix("#")
            color.startsWith("0x") -> color.removePrefix("0x")
            else -> colors[color.lowercase()]
        } ?: return null
        return if (hex.length == 8) {
            val alpha = hex.take(2).toInt(16)
            val red = hex.substring(2, 4).toInt(16)
            val green = hex.substring(4, 6).toInt(16)
            val blue = hex.substring(6, 8).toInt(16)
            Span.CustomColor(SpanTarget.Full, Color(red, green, blue, alpha))
        } else {
            null
        }
    }

    private val tagName = """^[A-z]+""".toRegex()
    private val parameter = """(?<key>[A-z]+)="(?<value>[^"]*)"""".toRegex()
    private val unquotedParameter = """(?<key>[A-z]+)=(?!")(?<value>[^ ]*)""".toRegex()

    sealed interface Token {
        data class Text(val text: String) : Token
        data class OpenTag(val tag: String, val parameters: Map<String, String>) : Token
        data class CloseTag(val tag: String) : Token
    }

    /**
     * Parses an EVE formatted text into a list of [Token]s.
     */
    private tailrec fun parse(current: List<Token>, remaining: String): List<Token> {
        if (remaining.isEmpty()) return current
        when (val nextTagIndex = remaining.indexOf('<')) {
            -1 -> {
                // Text is tag-free, just add it
                return current + Token.Text(remaining)
            }
            0 -> {
                // Text starts with a tag
                val tagEndIndex = remaining.indexOf('>')
                val tag = remaining.take(tagEndIndex + 1).removeSurrounding("<", ">").trim()
                val name = tagName.find(tag.removePrefix("/").trim())?.value
                    ?: return parse(current + Token.Text("<"), remaining.drop(1))
                return if (tag.startsWith('/')) {
                    parse(current + Token.CloseTag(name), remaining.drop(tagEndIndex + 1))
                } else {
                    val parameters = parameter.findAll(tag).map { it["key"] to it["value"] }.toMap()
                    val unquotedParameters = unquotedParameter.findAll(tag).map { it["key"] to it["value"] }.toMap()
                    parse(current + Token.OpenTag(name, parameters + unquotedParameters), remaining.drop(tagEndIndex + 1))
                }
            }
            else -> {
                // Text has a tag later on
                val plain = remaining.take(nextTagIndex)
                return parse(current + Token.Text(plain), remaining.drop(nextTagIndex))
            }
        }
    }
}
