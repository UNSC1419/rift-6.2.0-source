package dev.nohus.rift.notifications.eve

import dev.nohus.rift.network.esi.models.NotificationSenderType
import dev.nohus.rift.repositories.IdRanges
import org.koin.core.annotation.Single
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings

@Single
class EveNotificationReferencedIdsExtractor {

    private val yamlLoader = Load(LoadSettings.builder().build())

    data class ReferencedIds(
        val uncategorizedIds: Set<Long> = emptySet(),
        val structureIdsWithCharacterId: Set<Pair<Long, Int>> = emptySet(),
        val characterIds: Set<Long> = emptySet(),
        val corporationIds: Set<Long> = emptySet(),
        val allianceIds: Set<Long> = emptySet(),
        val factionIds: Set<Long> = emptySet(),
        val systemIds: Set<Long> = emptySet(),
        val moonIds: Set<Long> = emptySet(),
        val typeIds: Set<Long> = emptySet(),
        val stationIds: Set<Long> = emptySet(),
    ) {
        operator fun plus(other: ReferencedIds) = ReferencedIds(
            uncategorizedIds + other.uncategorizedIds,
            structureIdsWithCharacterId + other.structureIdsWithCharacterId,
            characterIds + other.characterIds,
            corporationIds + other.corporationIds,
            allianceIds + other.allianceIds,
            factionIds + other.factionIds,
            systemIds + other.systemIds,
            moonIds + other.moonIds,
            typeIds + other.typeIds,
            stationIds + other.stationIds,
        )
    }

    fun extract(notification: EveNotification): ReferencedIds {
        val data = notification.text?.let(::parseYaml)
        val sender = when (notification.senderType) {
            NotificationSenderType.Character -> ReferencedIds(characterIds = setOf(notification.senderId))
            NotificationSenderType.Corporation -> ReferencedIds(corporationIds = setOf(notification.senderId))
            NotificationSenderType.Alliance -> ReferencedIds(allianceIds = setOf(notification.senderId))
            NotificationSenderType.Faction -> ReferencedIds(factionIds = setOf(notification.senderId))
            NotificationSenderType.Other -> ReferencedIds(uncategorizedIds = setOf(notification.senderId))
        }
        return (data?.let { extract(it, notification.characterIds.firstOrNull()) } ?: ReferencedIds()) +
            sender + ReferencedIds(characterIds = notification.characterIds.map { it.toLong() }.toSet())
    }

    private fun extract(data: Map<*, *>, characterId: Int? = null): ReferencedIds {
        val ids = ReferencedIdsBuilder(characterId)
        fun Any?.asLong(): Long? = when (this) {
            is Number -> toLong()
            is String -> toLongOrNull()
            else -> null
        }

        data["parameter"].asLong()?.let { parameter ->
            when (data["voteType"].asLong()) {
                1L -> ids.add("corporationID", parameter)
                3L -> ids.add("characterID", parameter)
            }
        }

        fun visit(value: Any?, key: String? = null) {
            when (value) {
                is Map<*, *> -> {
                    if (key.equals("oreVolumeByType", ignoreCase = true)) {
                        value.keys.forEach { typeId ->
                            typeId.toString().toLongOrNull()?.let { ids.add("typeID", it) }
                        }
                    }
                    if (key.equals("agentLocation", ignoreCase = true) || key.equals("targetLocation", ignoreCase = true)) {
                        value.forEach { [groupId, locationId] ->
                            val id = when (locationId) {
                                is Number -> locationId.toLong()
                                is String -> locationId.toLongOrNull()
                                else -> null
                            } ?: return@forEach
                            when (groupId.toString().toIntOrNull()) {
                                5 -> ids.add("solarSystemID", id)
                                15 -> ids.add("stationID", id)
                            }
                        }
                    }
                    value.forEach { [nestedKey, nestedValue] -> visit(nestedValue, nestedKey.toString()) }
                }
                is List<*> -> {
                    when {
                        key.equals("listOfTypesAndQty", ignoreCase = true) -> {
                            value.forEach { item ->
                                (item as? List<*>)?.getOrNull(1).asLong()?.let { ids.add("typeID", it) }
                            }
                        }
                        key.equals("typeIDs", ignoreCase = true) ||
                            key.equals("listOfServiceModuleIDs", ignoreCase = true) -> {
                            value.forEach { item ->
                                item.asLong()?.let { ids.add("typeID", it) }
                            }
                        }
                        key.equals("topTen", ignoreCase = true) -> {
                            value.forEach { entry ->
                                (entry as? List<*>)?.getOrNull(0).asLong()?.let { ids.add("characterID", it) }
                            }
                        }
                        key.equals("corpList", ignoreCase = true) -> {
                            value.forEach { corporationId ->
                                corporationId.asLong()?.let { ids.add("corporationID", it) }
                            }
                        }
                    }
                    value.forEach { visit(it, key) }
                }
                is Number -> if (key?.isReferencedIdKey() == true) ids.add(key, value.toLong())
                is String -> if (key?.isReferencedIdKey() == true) {
                    value.toLongOrNull()?.let { ids.add(key, it) }
                }
            }
        }
        data.forEach { [key, value] -> visit(value, key.toString()) }
        return ids.build()
    }

    private fun String.isReferencedIdKey(): Boolean {
        return !(equals("killMailID", ignoreCase = true) ||
                equals("project_id", ignoreCase = true) ||
                equals("goal_id", ignoreCase = true) ||
                equals("itemRefID", ignoreCase = true)) && (endsWith("ID", ignoreCase = true) || equals("player_closer", ignoreCase = true) ||
                equals("kicker", ignoreCase = true) || equals("startedBy", ignoreCase = true) ||
                equals("cancelledBy", ignoreCase = true) || equals("firedBy", ignoreCase = true))
    }

    private fun parseYaml(text: String): Map<*, *>? {
        return try {
            yamlLoader.loadFromString(text) as? Map<*, *>
        } catch (_: RuntimeException) {
            null
        }
    }

    private class ReferencedIdsBuilder(private val characterId: Int?) {
        private val uncategorizedIds = mutableSetOf<Long>()
        private val structureIdsWithCharacterId = mutableSetOf<Pair<Long, Int>>()
        private val characterIds = mutableSetOf<Long>()
        private val corporationIds = mutableSetOf<Long>()
        private val allianceIds = mutableSetOf<Long>()
        private val factionIds = mutableSetOf<Long>()
        private val systemIds = mutableSetOf<Long>()
        private val moonIds = mutableSetOf<Long>()
        private val typeIds = mutableSetOf<Long>()
        private val stationIds = mutableSetOf<Long>()

        fun add(key: String, id: Long) {
            when {
                key.equals("charID", true) || key.equals("characterID", true) -> characterIds += id
                key.equals("player_closer", true) || key.equals("kicker", true) ||
                    key.equals("startedBy", true) || key.equals("cancelledBy", true) ||
                    key.equals("firedBy", true) -> characterIds += id
                key.equals("corpID", true) || key.equals("corporationID", true) -> corporationIds += id
                key.equals("allianceID", true) -> allianceIds += id
                key.equals("factionID", true) -> factionIds += id
                key.equals("solarSystemID", true) || key.equals("systemID", true) -> systemIds += id
                key.equals("moonID", true) -> moonIds += id
                key.equals("typeID", true) || key.equals("itemID", true) || key.equals("cloneTypeID", true) -> typeIds += id
                key.equals("stationID", true) || IdRanges.isStation(id) -> stationIds += id
                key.equals("locationID", true) && id in IdRanges.solarSystem -> systemIds += id
                (key.equals("locationID", true) || key.equals("structureID", true)) && characterId != null -> structureIdsWithCharacterId += id to characterId
                IdRanges.isNpcCorporation(id.toInt()) -> corporationIds += id
                else -> uncategorizedIds += id
            }
        }

        fun build() = ReferencedIds(
            uncategorizedIds, structureIdsWithCharacterId, characterIds, corporationIds, allianceIds,
            factionIds, systemIds, moonIds, typeIds, stationIds,
        )
    }
}
