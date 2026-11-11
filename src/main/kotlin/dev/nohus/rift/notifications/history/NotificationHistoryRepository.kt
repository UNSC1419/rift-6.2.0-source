package dev.nohus.rift.notifications.history

import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.notifications.NotificationsController.Notification
import dev.nohus.rift.utils.directories.AppDirectories
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException
import java.nio.file.FileSystemException
import java.nio.file.NoSuchFileException
import java.time.Duration
import java.time.Instant
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val logger = KotlinLogging.logger {}

@Single
class NotificationHistoryRepository(
    appDirectories: AppDirectories,
    @Named("settings") private val json: Json,
    val serialization: NotificationHistoryMapper,
) {
    private val retention = Duration.ofDays(30)
    private val historyFile = appDirectories.getAppDataDirectory().resolve("notifications.json")
    private val scope = CoroutineScope(Job())
    private val mutex = Mutex()
    private val _notifications = MutableStateFlow<List<NotificationHistoryItem>>(emptyList())
    val notifications = _notifications.asStateFlow()

    init {
        historyFile.createParentDirectories()
        _notifications.value = removeExpired(load())
        save()
    }

    fun add(notification: Notification, alert: Alert?, reason: NotificationHistoryReason?) {
        val item = NotificationHistoryItem(Instant.now(), notification, alert, reason)
        _notifications.update { current -> removeExpired(current + item) }
        save()
    }

    private fun removeExpired(items: List<NotificationHistoryItem>): List<NotificationHistoryItem> {
        val cutoff = Instant.now() - retention
        return items.filter { it.timestamp.isAfter(cutoff) }
    }

    private fun load(): List<NotificationHistoryItem> {
        return try {
            json.decodeFromString<List<StoredNotificationHistoryItem>>(historyFile.readText()).mapNotNull(serialization::toDomain)
        } catch (e: NoSuchFileException) {
            logger.info { "Notifications history file not found" }
            emptyList()
        } catch (e: FileSystemException) {
            logger.error(e) { "Notifications history file could not be read" }
            historyFile.deleteIfExists()
            emptyList()
        } catch (e: SerializationException) {
            logger.error(e) { "Could not deserialize notifications history" }
            historyFile.deleteIfExists()
            emptyList()
        } catch (e: IOException) {
            logger.error(e) { "Notifications history file could not be read" }
            emptyList()
        }
    }

    private fun save() = scope.launch(Dispatchers.IO) {
        mutex.withLock {
            try {
                historyFile.writeText(json.encodeToString(_notifications.value.map(serialization::toDto)))
            } catch (e: FileSystemException) {
                logger.error(e) { "Could not write notifications history" }
            } catch (e: IOException) {
                logger.error(e) { "Could not write notifications history" }
            }
        }
    }
}
