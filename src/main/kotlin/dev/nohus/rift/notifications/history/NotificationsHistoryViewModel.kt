package dev.nohus.rift.notifications.history

import dev.nohus.rift.ViewModel
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.characters.repositories.LocalCharactersRepository.LocalCharacter
import dev.nohus.rift.contacts.ContactsRepository
import dev.nohus.rift.contacts.ContactsRepository.Label
import dev.nohus.rift.notifications.eve.EveNotificationsRepository
import dev.nohus.rift.notifications.eve.EveNotificationsRepository.EveNotifications
import dev.nohus.rift.repositories.NotificationTypeGroup
import dev.nohus.rift.repositories.NotificationTypesRepository
import dev.nohus.rift.settings.persistence.NotificationsTab
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.sound.Sound
import dev.nohus.rift.utils.sound.SoundsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.time.ZoneId

@Factory
class NotificationsHistoryViewModel(
    notificationHistoryRepository: NotificationHistoryRepository,
    localCharactersRepository: LocalCharactersRepository,
    contactsRepository: ContactsRepository,
    soundsRepository: SoundsRepository,
    private val settings: Settings,
    notificationTypesRepository: NotificationTypesRepository,
    private val eveNotificationsRepository: EveNotificationsRepository,
) : ViewModel() {

    data class UiState(
        val selectedTab: NotificationsTab = NotificationsTab.Rift,
        val isUsingCompactMode: Boolean = false,
        val notifications: List<NotificationHistoryItem> = emptyList(),
        val characters: List<LocalCharacter> = emptyList(),
        val labels: List<Label> = emptyList(),
        val sounds: List<Sound> = emptyList(),
        val alertIds: Set<String> = emptySet(),
        val eveNotifications: EveNotifications = EveNotifications(),
        val eveNotificationTypeTitles: Map<String, String> = emptyMap(),
        val eveNotificationTypeGroups: List<NotificationTypeGroup> = emptyList(),
        val excludedEveNotificationTypes: Set<String> = emptySet(),
        val excludedEveNotificationAlertTypes: Set<String> = emptySet(),
        val displayTimezone: ZoneId,
    )

    val state = MutableStateFlow(
        UiState(
            selectedTab = settings.notificationsTab,
            isUsingCompactMode = settings.isUsingCompactNotificationsHistoryMode,
            sounds = soundsRepository.getSounds(),
            alertIds = settings.alerts.mapTo(mutableSetOf()) { it.id },
            eveNotificationTypeTitles = notificationTypesRepository.getTitles(),
            eveNotificationTypeGroups = notificationTypesRepository.getGroups(),
            excludedEveNotificationTypes = settings.excludedEveNotificationTypes,
            excludedEveNotificationAlertTypes = settings.excludedEveNotificationAlertTypes,
            displayTimezone = settings.displayTimeZone,
        ),
    )

    init {
        viewModelScope.launch {
            notificationHistoryRepository.notifications.collect { notifications ->
                state.update { it.copy(notifications = notifications.reversed()) }
            }
        }
        viewModelScope.launch {
            localCharactersRepository.characters.collect { characters ->
                state.update { it.copy(characters = characters) }
            }
        }
        viewModelScope.launch {
            contactsRepository.contacts.collect { contacts ->
                state.update { it.copy(labels = contacts.labels.values.flatten()) }
            }
        }
        viewModelScope.launch {
            settings.updateFlow.collect {
                state.update {
                    it.copy(
                        isUsingCompactMode = settings.isUsingCompactNotificationsHistoryMode,
                        alertIds = settings.alerts.mapTo(mutableSetOf()) { alert -> alert.id },
                        excludedEveNotificationTypes = settings.excludedEveNotificationTypes,
                        excludedEveNotificationAlertTypes = settings.excludedEveNotificationAlertTypes,
                        displayTimezone = settings.displayTimeZone,
                    )
                }
            }
        }
        viewModelScope.launch {
            eveNotificationsRepository.notifications.collect { notifications ->
                state.update { it.copy(eveNotifications = notifications) }
            }
        }
    }

    fun onEveNotificationTypesChecked(types: Set<String>, isChecked: Boolean) {
        settings.excludedEveNotificationTypes = if (isChecked) {
            settings.excludedEveNotificationTypes - types
        } else {
            settings.excludedEveNotificationTypes + types
        }
    }

    fun onEveNotificationAlertTypesChecked(types: Set<String>, isChecked: Boolean) {
        settings.excludedEveNotificationAlertTypes = if (isChecked) {
            settings.excludedEveNotificationAlertTypes - types
        } else {
            settings.excludedEveNotificationAlertTypes + types
        }
    }

    fun onTabSelected(tab: NotificationsTab) {
        settings.notificationsTab = tab
        state.update { it.copy(selectedTab = tab) }
    }

    fun onIsUsingCompactModeChange(enabled: Boolean) {
        settings.isUsingCompactNotificationsHistoryMode = enabled
    }
}
