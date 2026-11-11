package dev.nohus.rift.push

import dev.nohus.rift.ViewModel
import dev.nohus.rift.compose.DialogMessage
import dev.nohus.rift.compose.MessageDialogType
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.push_configuration_message
import dev.nohus.rift.generated.resources.push_failure_title
import dev.nohus.rift.generated.resources.push_no_key_message
import dev.nohus.rift.generated.resources.push_no_key_title
import dev.nohus.rift.generated.resources.push_no_token_message
import dev.nohus.rift.generated.resources.push_no_token_title
import dev.nohus.rift.generated.resources.push_no_topic_message
import dev.nohus.rift.generated.resources.push_no_topic_title
import dev.nohus.rift.generated.resources.push_success_message
import dev.nohus.rift.generated.resources.push_success_title
import dev.nohus.rift.generated.resources.push_unknown_error
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.settings.persistence.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.util.Locale

@Factory
class PushViewModel(
    private val pushNotificationController: PushNotificationController,
    private val settings: Settings,
) : ViewModel() {

    data class UiState(
        val dialogMessage: DialogMessage? = null,
        val pushoverApiToken: String,
        val pushoverApiKey: String,
        val ntfyTopic: String,
        val isLoading: Boolean = false,
    )

    private val _state = MutableStateFlow(
        UiState(
            pushoverApiToken = settings.pushover.apiToken ?: "",
            pushoverApiKey = settings.pushover.userKey ?: "",
            ntfyTopic = settings.ntfy.topic ?: "",
        ),
    )
    val state = _state.asStateFlow()

    fun onPushoverApiTokenChanged(token: String) {
        _state.update { it.copy(pushoverApiToken = token) }
        settings.pushover = settings.pushover.copy(apiToken = token)
    }

    fun onPushoverUserKeyChanged(key: String) {
        _state.update { it.copy(pushoverApiKey = key) }
        settings.pushover = settings.pushover.copy(userKey = key)
    }

    fun onNtfyTopicChanged(topic: String) {
        _state.update { it.copy(ntfyTopic = topic) }
        settings.ntfy = settings.ntfy.copy(topic = topic)
    }

    private fun showDialog(title: UiText, message: UiText) {
        _state.update {
            it.copy(
                dialogMessage = DialogMessage(
                    title = title,
                    message = message,
                    type = MessageDialogType.Info,
                ),
            )
        }
    }

    fun onNtfySendTest() {
        if (_state.value.isLoading) return

        val topic = settings.ntfy.topic
        if (topic.isNullOrEmpty()) {
            showDialog(Res.string.push_no_topic_title.toUiText(), Res.string.push_no_topic_message.toUiText())
            return
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = pushNotificationController.sendNtfyNotification(
                title = "RIFT Intel Fusion Tool".toUiText(),
                message = Res.string.push_configuration_message.toUiText(),
                iconUrl = null,
            )

            val response = result.success
            if (response != null) {
                showDialog(Res.string.push_success_title.toUiText(), Res.string.push_success_message.toUiText())
            } else {
                showDialog(Res.string.push_failure_title.toUiText(), result.failure?.message?.toUiText() ?: Res.string.push_unknown_error.toUiText())
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onPushoverSendTest() {
        if (_state.value.isLoading) return

        val token = settings.pushover.apiToken
        val key = settings.pushover.userKey
        if (token.isNullOrEmpty()) {
            showDialog(Res.string.push_no_token_title.toUiText(), Res.string.push_no_token_message.toUiText())
            return
        }
        if (key.isNullOrEmpty()) {
            showDialog(Res.string.push_no_key_title.toUiText(), Res.string.push_no_key_message.toUiText())
            return
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = pushNotificationController.sendPushoverNotification(
                title = "RIFT Intel Fusion Tool".toUiText(),
                message = Res.string.push_configuration_message.toUiText(),
            )

            val response = result.success
            if (response != null) {
                if (response.status == 1) {
                    showDialog(Res.string.push_success_title.toUiText(), Res.string.push_success_message.toUiText())
                } else {
                    val reason = response.errors
                        ?.joinToString("\n") { it.replaceFirstChar { it.titlecase(Locale.US) } }
                        ?.toUiText()
                        ?: Res.string.push_unknown_error.toUiText()
                    showDialog(Res.string.push_failure_title.toUiText(), reason)
                }
            } else {
                showDialog(Res.string.push_failure_title.toUiText(), result.failure?.message?.toUiText() ?: Res.string.push_unknown_error.toUiText())
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onCloseDialogMessage() {
        _state.update { it.copy(dialogMessage = null) }
    }
}
