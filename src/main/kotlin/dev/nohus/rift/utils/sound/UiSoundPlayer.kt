package dev.nohus.rift.utils.sound

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.nohus.rift.di.koin
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.sound.SoundPlayer.SoundSource
import org.koin.core.annotation.Single

@Single
class UiSoundPlayer(
    private val soundPlayer: SoundPlayer,
) {
    fun play(sound: UiSound) {
        soundPlayer.play(
            soundResource = sound.resource,
            source = SoundSource.Ui,
            volume = sound.baseVolume,
        )
    }

    fun playAccordion(isExpanded: Boolean) {
        play(if (isExpanded) UiSound.Collapse else UiSound.Expand)
    }

    fun playSelect(isSelected: Boolean) {
        play(if (isSelected) UiSound.Deselect else UiSound.Select)
    }
}

@Composable
fun rememberUiSoundPlayer(): UiSoundPlayer {
    return remember { koin.get() }
}

enum class UiSound(val resource: String, val baseVolume: Float = 1.0f) {
    ButtonHover("eve-ui-button-hover.wav", 0.02f),
    ButtonClick("eve-ui-button-click.wav", 0.02f),
    ButtonClickSoft("eve-ui-button-click-soft.wav", 0.1f),
    ListSelect("eve-ui-list-select.wav", 0.2f),
    EntryHover("eve-ui-entry-hover.wav", 0.05f),
    EntrySelect("eve-ui-entry-select.wav", 0.1f),
    Expand("eve-ui-expand.wav", 0.05f),
    Collapse("eve-ui-collapse.wav", 0.05f),
    Close("eve-ui-close.wav", 0.2f),
    Select("eve-ui-select.wav", 0.15f),
    Deselect("eve-ui-deselect.wav", 0.01f),

    MapCurrentLocation("eve-ui-map-current-location.wav", 0.2f),
    MapLayout("eve-ui-map-layout.wav", 0.03f),
    MapFit("eve-ui-map-fit.wav", 0.1f),

    CameraShutter("camera-shutter.wav", 1.0f),
}
