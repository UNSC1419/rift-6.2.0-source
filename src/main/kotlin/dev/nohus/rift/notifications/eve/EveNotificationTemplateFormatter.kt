package dev.nohus.rift.notifications.eve

import androidx.compose.ui.text.font.FontWeight
import dev.nohus.rift.compose.text.FormattedText
import dev.nohus.rift.compose.text.FormattedTextBuilder
import dev.nohus.rift.compose.text.ParseEveFormattedTextUseCase
import dev.nohus.rift.compose.text.buildFormattedText
import dev.nohus.rift.compose.text.toFormattedText
import dev.nohus.rift.repositories.CelestialsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.types.TypeDetails
import dev.nohus.rift.types.getDisplayName
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.CONSTELLATION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.REGION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.SOLAR_SYSTEM_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoLinkifier
import dev.nohus.rift.utils.entitylinks.StaticLocation
import dev.nohus.rift.utils.formatIsk
import dev.nohus.rift.utils.formatNumber
import org.koin.core.annotation.Single
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.abs

const val EVE_NOTIFICATION_TYPE = "__eveNotificationType"

@Single
class EveNotificationTemplateFormatter(
    private val parseEveFormattedTextUseCase: ParseEveFormattedTextUseCase,
    private val celestialsRepository: CelestialsRepository,
    private val solarSystemsRepository: SolarSystemsRepository,
    private val showInfoLinkifier: ShowInfoLinkifier,
) {

    fun formatGeneric(data: Map<String, Any?>): ParsedEveNotification {
        if (data.isEmpty()) return ParsedEveNotification(null, "No notification details".toFormattedText(), data)

        val body = buildFormattedText {
            data.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) append("\n")
                appendGenericValue(key, value)
            }
        }
        return ParsedEveNotification(null, body, data)
    }

    fun formatDocumented(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        subject: String,
        body: String? = null,
    ): ParsedEveNotification = ParsedEveNotification(
        subject = formatTemplate(subject, data, typeDetails),
        body = body?.let { formatTemplate(it, data, typeDetails) },
        data = data,
    )

    fun formatContact(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        subject: String,
        body: String? = null,
    ): ParsedEveNotification = formatDocumented(data.withStandingLevelName(), typeDetails, subject, body)

    fun formatNpcStandingChange(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
    ): ParsedEveNotification {
        val fallback by lazy { ParsedEveNotification("Notification details unavailable".toFormattedText(), FormattedText.Empty, data) }
        val changes = data["rootArray"] as? List<*> ?: return fallback
        val firstChange = changes.firstOrNull() as? List<*> ?: return fallback
        val rawChange = firstChange.getOrNull(2).asDouble() ?: return fallback
        val scaledChange = rawChange * 10.0
        val roundedChange = kotlin.math.round(scaledChange * 1000.0) / 1000.0
        val change = if (roundedChange < 0) {
            -maxOf(0.001, abs(roundedChange))
        } else {
            maxOf(0.001, roundedChange)
        }
        val direction = if (change > 0) "increased" else "decreased"
        val owner = data["notification_senderID"].asLong()?.let { typeDetails[it] }?.let {
            showInfoLinkifier.getLink(it)
        } ?: "Unknown"
        val otherChanges = (changes.size - 1).takeIf { it > 0 }?.let {
            " $it other ${if (it == 1) "standing has" else "standings have"} changed."
        } ?: ""
        val amount = NumberFormat.getInstance(Locale.ENGLISH).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 3
        }.format(abs(change))
        return ParsedEveNotification(
            subject = parseEveFormattedTextUseCase("Standing $direction", typeDetails),
            body = parseEveFormattedTextUseCase(
                "Your standing with $owner has $direction by $amount.$otherChanges",
                typeDetails,
            ),
            data = data,
        )
    }

    fun formatWarDeclared(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        subject: String,
        hostileWithHeadquartersBody: String,
        hostileBody: String,
        delayedWithHeadquartersBody: String,
        withHeadquartersBody: String,
        defaultBody: String,
    ): ParsedEveNotification {
        val hasHeadquarters = findValue(data, "warHQ") != null
        val hostile = findValue(data, "hostileState").isTruthy()
        val delayHours = findValue(data, "delayHours")
        val body = when {
            hostile && hasHeadquarters -> hostileWithHeadquartersBody
            hostile -> hostileBody
            delayHours != null && hasHeadquarters -> delayedWithHeadquartersBody
            hasHeadquarters -> withHeadquartersBody
            else -> defaultBody
        }
        return formatDocumented(data, typeDetails, subject, body)
    }

    private fun Map<String, Any?>.withStandingLevelName(): Map<String, Any?> {
        val level = findValue(this, "level").asDouble()?.toInt() ?: return this
        val name = when (level) {
            10 -> "Excellent Standing"
            5 -> "Good Standing"
            0 -> "Neutral Standing"
            -5 -> "Bad Standing"
            -10 -> "Terrible Standing"
            else -> return this
        }
        return this + ("level" to name)
    }

    /**
     * Takes a [template] with placeholder expressions, and inserts values from the [data] map.
     * When values are IDs, they are first resolved from [typeDetails].
     */
    private fun formatTemplate(template: String, data: Map<String, Any?>, typeDetails: TypeDetails): FormattedText {
        val normalizedTemplate = template
            .replace("\\r", "")
            .replace("+\\n", "\n")
            .replace("\\n", "\n")

        val formattedTemplate = buildString {
            var lastIndex = 0
            Regex("\\{([^{}]+)}").findAll(normalizedTemplate).forEach { match ->
                append(normalizedTemplate.substring(lastIndex, match.range.first))
                val expression = match.groupValues[1]
                val isInsideTag = normalizedTemplate.lastIndexOf('<', match.range.first) >
                    normalizedTemplate.lastIndexOf('>', match.range.first)
                val isInsideLink = normalizedTemplate.lastIndexOf("<a", match.range.first, ignoreCase = true) >
                    normalizedTemplate.lastIndexOf("</a>", match.range.first, ignoreCase = true)
                append(formatTemplateExpression(expression, data, typeDetails, linkify = !isInsideTag && !isInsideLink) ?: match.value)
                lastIndex = match.range.last + 1
            }
            append(normalizedTemplate.substring(lastIndex))
        }.trimWithBreaklines()
        return parseEveFormattedTextUseCase(formattedTemplate, typeDetails)
    }

    /**
     * Some templates have whitespace or breaklines at the beginning or end (sometimes because there are optional
     * expressions at the end). This trims them.
     */
    private tailrec fun String.trimWithBreaklines(): String {
        return if (isNotEmpty()) {
            if (first().isWhitespace()) {
                trimStart().trimWithBreaklines()
            } else if (startsWith("<br>")) {
                removePrefix("<br>").trimWithBreaklines()
            } else if (last().isWhitespace()) {
                trimEnd().trimWithBreaklines()
            } else if (endsWith("<br>")) {
                removeSuffix("<br>").trimWithBreaklines()
            } else this
        } else this
    }

    /**
     * Takes a template expression like `{[location]moonID.name, linkify}`, and returns its resolved value based on the
     * [data] key/value map. When the value is an ID, the entity name is resolved from [typeDetails].
     * When [linkify] is true, wraps the value in a `showinfo` link.
     */
    private fun formatTemplateExpression(
        expression: String,
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        linkify: Boolean,
    ): String? {
        val field = expression.substringAfter(']').substringBefore(',').substringBefore('.').trim()

        /**
         * The field is the key which appears in a template, and the aliases are the keys from the data map that should
         * be used to resolve it.
         * For example a template contains "char", but the ESI notification provides it as "characterID".
         */
        val aliases = when (field) {
            "alliance", "allianceName" -> listOf("allianceID", field)
            "corporation", "corporationName", "corporation_name", "corp_name" -> listOf("corpID", "corporationID", field)
            "aggressor" -> listOf("aggressorID")
            "defender" -> listOf("defenderID")
            "ally" -> listOf("allyID")
            "merc" -> listOf("mercID")
            "victim" -> listOf("victimID", "charRefID")
            "char" -> listOf("charID", "characterID")
            "bountyPlacer" -> listOf("bountyPlacerID")
            "availableToName" -> listOf("toEntityID", "availableToID")
            "station_id" -> listOf("stationID")
            "location" -> listOf("locationID")
            "item_type_id" -> listOf("typeID", field)
            "solarsystemID" -> listOf("solarSystemID", "systemID")
            "structureLink" -> listOf("structureID")
            "solarSystemLink", "solarsystemLink" -> listOf("solarSystemID", "systemID")
            "joinTime" -> listOf("time")
            "BattleTime" -> listOf("startDate")
            "amount" -> listOf("amount", "price", "bounty")
            "bountyPaid" -> listOf("bountyPaid", "bounty")
            "managerStation" -> listOf("corpID", "corporationID")
            "applicationText" -> listOf("applicationText", "customMessage", "message")
            "messageText" -> listOf("messageText", "message", "customMessage")
            "AttackingCorporation" -> listOf("attackerID")
            "ownerCorpName" -> listOf("ownerCorpID", "ownerID")
            "characterName" -> listOf("characterID", "charID", field)
            "charactername" -> listOf("receiverCharID", "senderCharID", field)
            "job_creator_name" -> listOf("creator_id", field)
            "issuing_corporation_name" -> listOf("corporation_id", field)
            "creator" -> listOf("creator_id", field)
            "goal_closer" -> listOf("closer_id", field)
            "closing_character_name" -> listOf("player_closer", field)
            "kicking_character_name" -> listOf("kicker", field)
            "parameterCorpName" -> listOf("parameter", field)
            "creditorsName" -> listOf("creditorID", field)
            "debtorsName" -> listOf("debtorID", field)
            "against" -> listOf("externalID", field)
            "faction" -> listOf("factionID", field)
            "aggressorCorporationName" -> listOf("aggressorCorpID", field)
            "aggressorAllianceName" -> listOf("aggressorAllianceID", field)
            "agressorCorp" -> listOf("aggressorCorpID", field)
            "agressorAlliance" -> listOf("aggressorAllianceID", field)
            "aggressorName" -> listOf("aggressorID", field)
            "moonName" -> listOf("moonID", field)
            "issuerCorp" -> listOf("issuerCorpID", "corpID", field)
            "project_name" -> listOf("project_id", field)
            "player" -> listOf("notification_receiverID")
            "moonLink" -> listOf("moonID")
            "startedByLink" -> listOf("startedBy")
            "cancelledByLink" -> listOf("cancelledBy")
            "firedByLink" -> listOf("firedBy")
            "typeID2" -> listOf("typeID2", "typeID")
            else -> when {
                field.equals("cloneTypeID", ignoreCase = true) -> listOf("cloneTypeID", "cloneType", "typeID")
                field.endsWith("TypeID", ignoreCase = true) -> listOf(field, "typeID", "itemID")
                field.endsWith("Name") -> listOf(field.removeSuffix("Name") + "ID", field)
                field == "owner" -> listOf("ownerID", "owner1ID")
                field == "owner1" -> listOf("owner1ID", "ownerID")
                field == "aggressorCorp" -> listOf("aggressorCorpID", "aggressorCorporationID")
                field == "aggressorAlliance" -> listOf("aggressorAllianceID")
                field == "iskOffer" || field == "iskOffered" -> listOf("iskValue", "iskOffer", "iskOffered")
                field == "declaredDate" -> listOf("declaredDate", "timeDeclared", "timeStarted")
                else -> listOf(field)
            }
        }

        // Date fields
        val dateValue = aliases.firstNotNullOfOrNull { findValue(data, it).asLong() }
        if (dateValue != null && expression.startsWith("[timeinterval]")) {
            return formatTimeInterval(dateValue, expression)
        }
        if (dateValue != null && expression.startsWith("[datetime]")) {
            return try {
                formatLocalizedDateTime(dateValue, expression)
            } catch (_: RuntimeException) {
                dateValue.toString()
            }
        }
        if (dateValue != null && field == "declaredDate") {
            return formatFullDateTime(dateValue, "yyyy.MM.dd HH:mm:ss")
        }

        // Corporation Project field
        if (field == "goal_name") {
            val projectId = findValue(data, "goal_id")?.toString()
            val projectName = findValue(data, field)?.toString()
            if (projectId != null && projectName != null) {
                return if (linkify) {
                    showInfoLinkifier.getLink(TypeDetail.CorporationProject(projectId, projectName))
                } else {
                    projectName
                }
            }
        }

        // Freelance Project field
        if (field == "project_name") {
            val projectId = findValue(data, "project_id")?.toString()
            val projectName = findValue(data, field)?.toString()
            if (projectId != null && projectName != null) {
                return if (linkify) {
                    showInfoLinkifier.getLink(TypeDetail.FreelanceProject(projectId, projectName))
                } else {
                    projectName
                }
            }
        }

        // HyperNet type link field
        if (field == "type_link") {
            val typeId = findValue(data, "type_id").asLong() ?: return ""
            val type = typeDetails[typeId] ?: return typeId.toString()
            return if (linkify) showInfoLinkifier.getLink(type) else type.getDisplayName()
        }

        // ID fields
        val isIdField = aliases.any { it.endsWith("ID", ignoreCase = true) } ||
            expression.startsWith("[character]") || expression.startsWith("[item]") ||
            expression.startsWith("[location]") || field in setOf(
                "closing_character_name", "kicking_character_name", "parameterCorpName",
                "startedByLink", "cancelledByLink", "firedByLink",
            )
        if (isIdField) {
            val id = aliases.firstNotNullOfOrNull { findValue(data, it).asLong() }
            if (id != null) {
                if (field == "moonName" && linkify) {
                    resolveStaticLocation(id)?.let { return showInfoLinkifier.getLink(it) }
                }
                typeDetails[id]?.let {
                    return if (linkify) showInfoLinkifier.getLink(it) else id.toString()
                }
                if (expression.startsWith("[location]") || field == "moonName") {
                    if (!linkify) return id.toString()
                    resolveStaticLocation(id)?.let { return showInfoLinkifier.getLink(it) }
                }
                if (linkify) {
                    getLinkInfoHref(expression, data)?.let { href ->
                        val itemId = href.substringAfter("//", "").toLongOrNull()
                        val value = itemId?.let { typeDetails[it]?.getDisplayName() } ?: id.toString()
                        return "<a href=\"$href\">$value</a>"
                    }
                }
                return id.toString()
            }
        }

        if (field == "corpList") {
            return formatEntityList(data, field, typeDetails)
        }

        // Other fields
        val value = aliases.firstNotNullOfOrNull { findValue(data, it) }
        if (value != null) {
            val numeric = value.asDouble()
            if (numeric != null && field in setOf("oldTaxRate", "newTaxRate")) {
                return formatTaxRate(numeric)
            }
            if (numeric != null && expression.startsWith("[numeric]")) {
                return formatNumeric(numeric, expression)
            }
            if (linkify) {
                getLinkInfoHref(expression, data)?.let { href ->
                    return "<a href=\"$href\">$value</a>"
                }
            }
            return value.toString()
        }

        // Static fields
        return formatStaticTemplateField(field, data, typeDetails)
    }

    /**
     * Finds a location (celestial, system, constellation, or region) by ID from static data
     */
    private fun resolveStaticLocation(id: Long): StaticLocation? {
        if (id !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) return null
        val intId = id.toInt()
        celestialsRepository.getCelestial(intId)?.let {
            return StaticLocation(it.name, it.type.id, it.id.toLong())
        }
        solarSystemsRepository.getSystem(intId)?.let {
            return StaticLocation(it.name, SOLAR_SYSTEM_TYPE_ID, it.id.toLong())
        }
        solarSystemsRepository.getConstellation(intId)?.let {
            return StaticLocation(it.name, CONSTELLATION_TYPE_ID, it.id.toLong())
        }
        solarSystemsRepository.getRegion(intId)?.let {
            return StaticLocation(it.name, REGION_TYPE_ID, it.id.toLong())
        }
        return null
    }

    /**
     * Some notification template expressions have a `linkinfo` parameter which points to another `key`
     * in the `data` map that should be used to construct the link for that expression.
     *
     * This function returns the href constructed based on that.
     */
    private fun getLinkInfoHref(expression: String, data: Map<String, Any?>): String? {
        val field = Regex("linkinfo=([^, ]+)").find(expression)?.groupValues?.get(1) ?: return null
        val linkInfo = findValue(data, field) ?: return null
        if (linkInfo is String && linkInfo.startsWith("showinfo:")) return linkInfo
        val values = extractLinkInfoValues(linkInfo)
        val typeId = values.getOrNull(0) ?: return null
        val itemId = values.getOrNull(1)
        return if (itemId != null) "showinfo:$typeId//$itemId" else "showinfo:$typeId"
    }

    private fun extractLinkInfoValues(value: Any?): List<Long> = when (value) {
        is List<*> -> value.flatMap(::extractLinkInfoValues)
        is Map<*, *> -> value.asStringMap()?.let { map ->
            val namedValues = listOfNotNull(
                findValue(map, "typeID").asLong(),
                findValue(map, "itemID").asLong(),
            )
            namedValues.ifEmpty {
                value.entries.flatMap { [key, nestedValue] ->
                    listOfNotNull(key.asLong(), nestedValue.asLong())
                }
            }
        } ?: emptyList()
        else -> listOfNotNull(value.asLong())
    }

    /**
     * Some template fields have hardcoded behavior or values, instead of the value coming from the `data` payload.
     * The `data` payload is required because it can still be used inside the hardcoded values.
     */
    private fun formatStaticTemplateField(field: String, data: Map<String, Any?>, typeDetails: TypeDetails): String? {
        return when (field) {
            "defaultPayoutText" -> if (findValue(data, "payout").isTruthy()) {
                "This payout is the default payout for an uninsured ship. If you are interested in better insurance then please visit a station with an Insurance Service for further details."
            } else ""
            "journalLink" -> "<b>Journal</b>"
            "implantListText" -> {
                val typeIds = findValue(data, "typeIDs") as? List<*> ?: return ""
                if (typeIds.isEmpty()) "Lost implants: No implants were lost"
                else "Lost implants:\n" + typeIds.joinToString("\n\n") { id ->
                    val numericId = id.asLong()
                    val name = numericId?.let { typeDetails[it] }?.let {
                        showInfoLinkifier.getLink(it)
                    } ?: id.toString()
                    "Type: $name"
                }
            }
            "topTenString" -> {
                val topTen = findValue(data, "topTen") as? List<*> ?: return ""
                topTen.mapIndexedNotNull { index, entry ->
                    val values = entry as? List<*> ?: return@mapIndexedNotNull null
                    val characterId = values.getOrNull(0).asLong()
                    val reward = values.getOrNull(1).asDouble()
                    val character = characterId?.let {
                        typeDetails[it]?.let { showInfoLinkifier.getLink(it) } ?: it.toString()
                    } ?: "Unknown"
                    "${index + 1}. $character${reward?.let { " — ${formatNumber(it, withDecimals = false)} LP" } ?: ""}"
                }.joinToString("\n")
            }
            "allianceText" -> findValue(data, "allianceID")?.asLong()?.let {
                " (Alliance: ${typeDetails[it]?.let { showInfoLinkifier.getLink(it) } ?: it})"
            } ?: ""
            "otherTowersText" -> formatOtherTowers(data, typeDetails)
            "currencyName" -> when (findValue(data, "currencyNameLabel")?.toString()) {
                null, "UI/Common/ISK" -> "ISK"
                else -> findValue(data, "currencyNameLabel")?.toString()?.substringAfterLast('/') ?: "ISK"
            }
            "whereText" -> formatOfficeLocation(data, typeDetails)
            "reasonText" -> if (findValue(data, "errorText").isTruthy()) {
                "The reason for this action is: ${findValue(data, "errorText")}"
            } else ""
            "cloneBoughtText" -> findValue(data, "cloneBought")?.let {
                "\nYour clone was purchased for ${formatIsk(it.asDouble() ?: 0.0, withCents = false)}."
            } ?: ""
            "cloningServiceText" -> formatCloningServiceText(data)
            "spLostText" -> findValue(data, "skillPointsLost")?.asLong()?.let {
                "\n\nYou have lost ${formatNumber(it.toDouble(), withDecimals = false)} skill points."
            } ?: ""
            "agressorText" -> formatStationAggressorText(data, typeDetails)
            "characterText" -> findValue(data, "charID")?.asLong()?.let {
                "Conquered by ${typeDetails[it]?.let(showInfoLinkifier::getLink) ?: it}."
            } ?: ""
            "oldCorpName" -> formatLinkedEntityValue(data, "oldOwnerID", typeDetails)
            "newCorpName" -> formatLinkedEntityValue(data, "newOwnerID", typeDetails)
            "subject" -> when (findValue(data, "state").asLong()) {
                0L -> "Outpost service re-enabled"
                1L -> "Outpost service disabled"
                else -> ""
            }
            "body" -> formatDynamicBody(data, typeDetails)
            "reason" -> when (findValue(data, "reason").asLong()) {
                1L -> "The insured ship was not owned by you."
                2L -> "The insurance contract had expired."
                3L -> "The insured ship had no value."
                else -> findValue(data, "reason")?.toString() ?: ""
            }
            "description" -> findValue(data, "description")?.toString()
                ?: "No description available for medal ${findValue(data, "medalID") ?: ""}."
            "title" -> findValue(data, "title")?.toString()
                ?: "Medal ${findValue(data, "medalID") ?: ""}"
            "locationName" -> formatContainerLocation(data, typeDetails)
            "confiscatedItems" -> formatConfiscatedItems(data, typeDetails)
            "summaryResponseText" -> when {
                findValue(data, "shouldAttack").isTruthy() -> "The contraband was confiscated and the offenders were attacked."
                findValue(data, "shouldConfiscate").isTruthy() -> "The contraband was confiscated."
                else -> ""
            }
            "billType" -> formatBillType(data)
            "securityDescription" -> formatSecurityDescription(data)
            "roleName" -> formatRoleName(data)
            "signOff" -> "Sincerely,\nYour friendly neighborhood CONCORD police squad"
            "typesAndQtyText" -> formatTypesAndQuantities(data, typeDetails)
            "listOfServices" -> formatTypeList(data, "listOfServiceModuleIDs", typeDetails)
            "listOfStructureLinks" -> formatStructureLinks(data)
            "warningText" -> if (
                findValue(data, EVE_NOTIFICATION_TYPE)?.toString() == "skyhookunderattack" &&
                findValue(data, "armorPercentage").asDouble() == 100.0 &&
                !findValue(data, "isActive").isTruthy()
            ) "Warning: this skyhook is vulnerable." else ""
            "allyText" -> if (findValue(data, "isAlly").isTruthy()) {
                "An allied party has joined this war."
            } else ""
            "costText" -> if (findValue(data, "cost").asDouble()?.let { it != 0.0 } == true) {
                "For your convenience we have included the appropriate CONCORD War Rules:\n\n" +
                        "The weekly cost is ${formatIsk(findValue(data, "cost").asDouble() ?: 0.0, withCents = false)} and the attacking organization will receive a bill which needs to be paid promptly to maintain the war. If the bill is not paid before it is due then the war will be cancelled.\n\n" +
                        "Both sides in a war must control at least one structure in space in order to be war eligible. If either side loses their last structure, they lose their war eligibility and the war is cancelled.\n" +
                        "The attacking side of a war must designate one highsec Upwell structure that they control as the War HQ for the conflict. If the War HQ is destroyed or removed from space the war will be cancelled.\n\n" +
                        "When an active war has been cancelled, there is a 24 hour cooldown period during which fighting can legally continue.\n\n" +
                        "If you are at war with a corporation that joins an alliance then the war will change from being against the corporation to being against the alliance.\n\n" +
                        "If you are at war with an alliance and a corporation leaves that alliance then you will still be at war with the alliance and a new war will be created with the corporation that left. This new war is subject to its own bills and all the normal war eligibility requirements."
            } else ""
            "lastCloneText" -> findValue(data, "lastCloned").asLong()?.let {
                "\n\nYour previous clone incept time was: ${formatFullDateTime(it)}"
            } ?: ""
            // These fields are optional, and if not included in the payload should be empty
            "applicationText", "messageText", "managerStation", "availableToName", "rentPeriod",
            "currentStanding", "current_standing", "requiredStanding", "required_standing",
            "newTaxRate", "oldTaxRate"  -> ""
            else -> null
        }
    }

    private fun formatOtherTowers(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        val corps = findValue(data, "corpsPresent") as? List<*> ?: return "No other towers are anchored in this system."
        val towers = corps.mapNotNull { it as? Map<*, *> }
            .mapNotNull corpEntry@{ rawCorp ->
                val corp = rawCorp.entries.associate { [key, value] -> key.toString() to value }
                val corpTowers = findValue(corp, "towers") as? List<*> ?: return@corpEntry null
                if (corpTowers.isEmpty()) return@corpEntry null
                val corpName = formatLinkedEntityValue(corp, "corpID", typeDetails)
                val alliance = findValue(corp, "allianceID")?.asLong()?.let {
                    " (Alliance: ${typeDetails[it]?.let(showInfoLinkifier::getLink) ?: it})"
                } ?: ""
                val towerText = corpTowers.mapNotNull towerEntry@{ rawTower ->
                    val tower = rawTower as? Map<*, *> ?: return@towerEntry null
                    val values = tower.entries.associate { [key, value] -> key.toString() to value }
                    val name = findValue(values, "towerName") ?: findValue(values, "typeName") ?: findValue(values, "typeID")
                    "  ${name ?: "Control tower"}"
                }.joinToString("\n")
                "$corpName$alliance:\n$towerText"
            }
        return if (towers.isEmpty()) "No other towers are anchored in this system."
        else "Other towers in this system:\n" + towers.joinToString("\n")
    }

    private fun formatOfficeLocation(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        val typeId = findValue(data, "typeID").asLong()
        val locationId = findValue(data, "stationID").asLong() ?: findValue(data, "locationID").asLong()
        val item = locationId?.let { typeDetails[it] }?.let(showInfoLinkifier::getLink)
            ?: locationId?.toString()
            ?: typeId?.let { typeDetails[it] }?.let(showInfoLinkifier::getLink)
            ?: typeId?.toString()
            ?: "the corporation office"
        return if (typeId == 27L) "Your corporation office at $item will expire." else "The items at $item will be impounded."
    }

    private fun formatCloningServiceText(data: Map<String, Any?>): String {
        val cloneStation = findValue(data, "cloneStationID").asLong()
        val corpStation = findValue(data, "corpStationID").asLong()
        return if (cloneStation != null && corpStation != null && cloneStation != corpStation) {
            "\nYour medical clone is located at a different station."
        } else ""
    }

    private fun formatStationAggressorText(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        val aggressor = formatLinkedEntityValue(data, "aggressorID", typeDetails)
        val corporation = formatLinkedEntityValue(data, "aggressorCorpID", typeDetails)
        return if (findValue(data, "aggressorID") != null) {
            "Aggressor: $aggressor\nCorporation: $corporation"
        } else ""
    }

    private fun formatContainerLocation(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        val stationId = findValue(data, "stationID").asLong()
        if (stationId != null) return formatLinkedEntityValue(data, "stationID", typeDetails)
        val systemId = findValue(data, "solarSystemID").asLong() ?: return "Unknown location"
        return resolveStaticLocation(systemId)?.let(showInfoLinkifier::getLink) ?: systemId.toString()
    }

    private fun formatConfiscatedItems(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        val lostItems = findValue(data, "lostList") as? List<*> ?: return ""
        return lostItems.mapNotNull { rawItem ->
            val item = rawItem.asStringMap() ?: return@mapNotNull null
            val typeId = findValue(item, "typeID").asLong()
            val quantity = findValue(item, "quantity").asLong() ?: findValue(item, "qty").asLong()
            val type = typeId?.let { typeDetails[it]?.let(showInfoLinkifier::getLink) ?: it.toString() } ?: "Unknown item"
            val response = when {
                findValue(data, "shouldAttack").isTruthy() -> "The owner was attacked."
                findValue(data, "shouldConfiscate").isTruthy() -> "The item was confiscated."
                else -> ""
            }
            "${quantity?.let { "$it x " } ?: ""}$type${if (response.isEmpty()) "" else " — $response"}"
        }.joinToString("\n")
    }

    private fun formatBillType(data: Map<String, Any?>): String = when (findValue(data, "billTypeID").asLong()) {
        1L -> "market fine"
        2L -> "rental bill"
        3L -> "broker bill"
        4L -> "war bill"
        5L -> "alliance maintenance bill"
        6L -> "sovereignty marker bill"
        7L -> "Infrastructure Hub bill"
        else -> findValue(data, "billType")?.toString() ?: "bill"
    }

    private fun formatSecurityDescription(data: Map<String, Any?>): String {
        val security = findValue(data, "security").asDouble()?.let { kotlin.math.round(it).toInt() }
            ?: return "Unknown security status"
        return when {
            security <= -10 -> "Terrible security status"
            security >= 10 -> "Excellent security status"
            security < 0 -> "Negative security status"
            security > 0 -> "Positive security status"
            else -> "Neutral security status"
        }
    }

    private fun formatRoleName(data: Map<String, Any?>): String {
        val roleName = findValue(data, "roleName")?.toString()
        if (!roleName.isNullOrBlank()) return roleName
        val roleNames = findValue(data, "roleNameIDs") as? List<*> ?: return "Person"
        return roleNames.joinToString(", ") { it.toString() }
    }

    private fun formatTypesAndQuantities(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        val values = findValue(data, "listOfTypesAndQty") as? List<*> ?: return ""
        return values.mapNotNull { rawValue ->
            val value = rawValue as? List<*> ?: return@mapNotNull null
            val quantity = value.getOrNull(0).asLong()
            val typeId = value.getOrNull(1).asLong()
            val type = typeId?.let { typeDetails[it]?.let(showInfoLinkifier::getLink) ?: it.toString() }
                ?: return@mapNotNull null
            "${quantity ?: 0} x $type"
        }.joinToString("\n")
    }

    private fun formatTypeList(data: Map<String, Any?>, key: String, typeDetails: TypeDetails): String {
        val values = findValue(data, key) as? List<*> ?: return ""
        return values.mapNotNull { value ->
            val typeId = value.asLong() ?: return@mapNotNull null
            typeDetails[typeId]?.let(showInfoLinkifier::getLink) ?: typeId.toString()
        }.joinToString("\n")
    }

    private fun formatEntityList(data: Map<String, Any?>, key: String, typeDetails: TypeDetails): String {
        val values = findValue(data, key) as? List<*> ?: return ""
        return values.joinToString(", ") { value ->
            val id = value.asLong()
            id?.let { typeDetails[it]?.let(showInfoLinkifier::getLink) ?: it.toString() } ?: value.toString()
        }
    }

    private fun formatStructureLinks(data: Map<String, Any?>): String {
        val values = findValue(data, "allStructureInfo") as? List<*> ?: return ""
        return values.mapNotNull { rawValue ->
            val value = rawValue as? List<*> ?: return@mapNotNull null
            val itemId = value.getOrNull(0).asLong() ?: return@mapNotNull null
            val name = value.getOrNull(1)?.toString() ?: return@mapNotNull null
            val typeId = value.getOrNull(2).asLong() ?: return@mapNotNull null
            "<a href=\"showinfo:$typeId//$itemId\">$name</a>"
        }.joinToString("\n")
    }

    private fun Any?.asStringMap(): Map<String, Any?>? {
        val map = this as? Map<*, *> ?: return null
        return map.entries.associate { [key, value] -> key.toString() to value }
    }

    private fun formatDynamicBody(data: Map<String, Any?>, typeDetails: TypeDetails): String {
        return when (findValue(data, EVE_NOTIFICATION_TYPE)?.toString()) {
            "corpdividendmsg" -> {
                val corporation = formatLinkedEntityValue(data, "corpID", typeDetails)
                if (findValue(data, "isMembers").isTruthy()) {
                    "$corporation has paid you a dividend."
                } else {
                    "You have received a dividend from $corporation."
                }
            }
            "sovcorpclaimfailmsg" -> when (findValue(data, "reason").asLong()) {
                1L -> "Your sovereignty claim was blocked by another corporation."
                2L -> "Your corporation is not a member of an alliance."
                3L -> "The sovereignty bill was not paid."
                else -> ""
            }
            else -> ""
        }
    }

    private fun formatLinkedEntityValue(data: Map<String, Any?>, key: String, typeDetails: TypeDetails): String {
        val id = findValue(data, key).asLong() ?: return "Unknown"
        return typeDetails[id]?.let(showInfoLinkifier::getLink) ?: id.toString()
    }

    private fun formatFullDateTime(value: Long, pattern: String = "EEEE, MMMM d, yyyy HH:mm"): String {
        return try {
            Instant.ofEpochMilli(value / 10_000 - 11_644_473_600_000)
                .atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH))
        } catch (_: RuntimeException) {
            value.toString()
        }
    }

    private fun formatLocalizedDateTime(value: Long, expression: String): String {
        val dateStyle = when (expression.substringAfter("date=", "full").substringBefore(',').trim()) {
            "short" -> FormatStyle.SHORT
            "medium" -> FormatStyle.MEDIUM
            "long" -> FormatStyle.LONG
            else -> FormatStyle.FULL
        }
        val time = expression.substringAfter("time=", "short").substringBefore(',').trim()
        val formatter = if (time == "none") {
            DateTimeFormatter.ofLocalizedDate(dateStyle)
        } else {
            val timeStyle = if (time == "long") FormatStyle.LONG else FormatStyle.SHORT
            DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
        }
        return Instant.ofEpochMilli(value / 10_000 - 11_644_473_600_000)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }

    private fun formatTimeInterval(value: Long, expression: String): String {
        var remainingSeconds = value / 10_000_000
        val units = listOf(
            "day" to 86_400L,
            "hour" to 3_600L,
            "minute" to 60L,
            "second" to 1L,
        )
        val from = expression.substringAfter("from=", "day").substringBefore(',').trim()
        val to = expression.substringAfter("to=", "second").substringBefore(',').trim()
        val firstIndex = units.indexOfFirst { [name] -> name == from }.takeIf { it >= 0 } ?: 0
        val lastIndex = units.indexOfFirst { [name] -> name == to }.takeIf { it >= firstIndex } ?: units.lastIndex
        return units.subList(firstIndex, lastIndex + 1).mapNotNull { [name, seconds] ->
            val count = remainingSeconds / seconds
            remainingSeconds %= seconds
            if (count == 0L) null else "$count $name${if (count == 1L) "" else "s"}"
        }.joinToString(" ").ifEmpty { "0 ${units[lastIndex].first}s" }
    }

    private fun formatNumeric(value: Double, expression: String): String {
        if (expression.endsWith(".isk")) return formatIsk(value, withCents = false)
        val decimalPlaces = Regex("decimalPlaces=(\\d+)").find(expression)?.groupValues?.get(1)?.toIntOrNull()
        val leadingZeroes = Regex("leadingZeroes=(\\d+)").find(expression)?.groupValues?.get(1)?.toIntOrNull()
        return if (decimalPlaces != null || leadingZeroes != null) {
            NumberFormat.getInstance(Locale.getDefault()).apply {
                minimumFractionDigits = decimalPlaces ?: 0
                maximumFractionDigits = decimalPlaces ?: 0
                minimumIntegerDigits = leadingZeroes ?: 1
            }.format(value)
        } else {
            formatNumber(value, withDecimals = false)
        }
    }

    private fun formatTaxRate(value: Double): String {
        return NumberFormat.getInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }.format(value)
    }

    private fun Any?.asLong(): Long? = when (this) {
        is Number -> toLong()
        is String -> toLongOrNull()
        else -> null
    }

    private fun Any?.asDouble(): Double? = when (this) {
        is Number -> toDouble()
        is String -> toDoubleOrNull()
        else -> null
    }

    private fun Any?.isTruthy(): Boolean = when (this) {
        is Boolean -> this
        is Number -> toDouble() != 0.0
        is String -> equals("true", ignoreCase = true) || toDoubleOrNull()?.let { it != 0.0 } == true
        else -> false
    }

    /**
     * Finds the value of the [key] in the [data] map, while trying key variations, different cases,
     * a normalized key text, and traversing nested maps and lists inside the [data] map.
     */
    private fun findValue(data: Map<*, *>, key: String): Any? {
        data[key]?.let { return it }
        data.entries.firstOrNull { [candidate, value] ->
            candidate is String && candidate.equals(key, ignoreCase = true) && value != null
        }?.value?.let { return it }
        val normalizedKey = key.filter(Char::isLetterOrDigit).lowercase()
        data.entries.firstOrNull { [candidate, value] ->
            value != null && candidate is String && candidate.filter(Char::isLetterOrDigit).lowercase() == normalizedKey
        }?.value?.let { return it }
        data.values.forEach { value ->
            when (value) {
                is Map<*, *> -> {
                    findValue(value, key)?.let { return it }
                }
                is List<*> -> value.forEach { item ->
                    if (item is Map<*, *>) {
                        findValue(item, key)?.let { return it }
                    }
                }
            }
        }
        return null
    }

    private fun FormattedTextBuilder.appendGenericValue(key: String, value: Any?, indent: String = "") {
        append(indent)
        withWeight(FontWeight.Bold) {
            append(key)
        }
        append(":")
        when (value) {
            is Map<*, *> -> {
                value.entries.forEach { [nestedKey, nestedValue] ->
                    append("\n")
                    appendGenericValue(nestedKey.toString(), nestedValue, "$indent  ")
                }
            }

            is List<*> -> {
                if (value.isEmpty()) {
                    append(" none")
                } else {
                    value.forEach { item ->
                        append("\n")
                        appendGenericValue("-", item, "$indent  ")
                    }
                }
            }

            else -> {
                append(" ")
                append(value?.toString() ?: "—")
            }
        }
    }
}
