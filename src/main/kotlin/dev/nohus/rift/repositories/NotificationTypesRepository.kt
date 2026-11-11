package dev.nohus.rift.repositories

import dev.nohus.rift.database.static.NotificationTypes
import dev.nohus.rift.database.static.StaticDatabase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.koin.core.annotation.Single

@Single
class NotificationTypesRepository(
    staticDatabase: StaticDatabase,
) {

    private val scope = CoroutineScope(Job())
    private lateinit var namesByInternalName: Map<String, String>
    private lateinit var internalNamesById: Map<Int, String>
    private val hasLoaded = CompletableDeferred<Unit>()

    init {
        scope.launch(Dispatchers.IO) {
            staticDatabase.transaction {
                val notificationTypes = NotificationTypes.selectAll().toList()
                namesByInternalName = notificationTypes.associate { it[NotificationTypes.internalName] to it[NotificationTypes.name] }
                internalNamesById = notificationTypes.associate { it[NotificationTypes.id] to it[NotificationTypes.internalName] }
            }
            hasLoaded.complete(Unit)
        }
    }

    private fun blockUntilLoaded() {
        runBlocking {
            hasLoaded.await()
        }
    }

    fun getTitle(internalName: String): String? {
        blockUntilLoaded()
        return namesByInternalName[internalName]
    }

    fun getTitles(): Map<String, String> {
        blockUntilLoaded()
        return namesByInternalName
    }

    fun getGroups(): List<NotificationTypeGroup> {
        blockUntilLoaded()
        return notificationTypeGroups.map { (name, ids) ->
            NotificationTypeGroup(
                name = name,
                types = ids.mapNotNull(internalNamesById::get).toSet(),
            )
        }
    }

    private companion object {
        val notificationTypeGroups = listOf(
            "Agents" to setOf(66, 69, 70, 71, 72, 73, 74, 247, 248),
            "Bills" to setOf(4, 9, 10, 11, 12, 13, 50, 250),
            "CCP Notifications" to setOf(170, 171, 172, 173, 4001, 4002),
            "Contacts" to setOf(89, 90, 2001, 2002, 166, 251),
            "Corporate" to setOf(2, 3, 16, 17, 142, 18, 139, 19, 20, 21, 22, 23, 25, 26, 85, 92, 128, 129, 130, 131, 143, 144, 145, 146, 260, 262, 261, 263, 264, 265, 290, 292, 296, 297, 293, 294, 295, 270),
            "Faction Warfare" to setOf(58, 59, 60, 61, 62, 63, 64, 65, 81, 82, 83, 84, 96, 97, 105, 106, 107, 108, 289, 298, 299, 300, 301, 302, 303),
            "HyperNet" to setOf(243, 244, 245),
            "Insurance" to setOf(34, 35, 36, 54, 55),
            "Insurgencies" to setOf(1110, 1111, 1112, 1113, 1114, 1115, 1116),
            "Miscellaneous" to setOf(138, 32, 33, 51, 52, 53, 56, 57, 67, 68, 91, 115, 116, 117, 118, 119, 120, 133, 1003, 1004, 1006, 1008, 1007, 1021, 1022, 1030, 1031, 1032, 200, 199, 201, 1050, 3001, 1060, 1100, 1101, 226, 227, 228, 1015, 1016, 1017, 1018, 253, 252, 6001, 6002, 6010, 6011, 6012, 276, 275, 280, 6013, 6032),
            "Old" to setOf(1),
            "Skill Training" to setOf(1000, 1001, 1002, 1005, 1033, 6020),
            "Sovereignty" to setOf(37, 38, 39, 40, 41, 42, 43, 44, 48, 46, 47, 86, 87, 88, 147, 148, 149, 150, 152, 160, 161, 162, 163, 164, 165, 167, 168, 169),
            "Structures" to setOf(45, 49, 75, 76, 77, 78, 79, 80, 93, 94, 95, 132, 181, 182, 183, 184, 185, 186, 187, 188, 190, 191, 192, 198, 207, 208, 209, 210, 211, 249, 202, 203, 204, 205, 206, 254, 6030, 6031, 6040, 6041, 281, 282, 283, 284, 285, 286, 287, 288),
            "War" to setOf(5, 225, 6, 7, 8, 27, 224, 28, 29, 30, 31, 98, 99, 100, 101, 102, 103, 104, 109, 110, 121, 122, 123, 124, 125, 126, 127, 140, 141, 221, 222, 223, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 246),
        )
    }
}

data class NotificationTypeGroup(
    val name: String,
    val types: Set<String>,
)
