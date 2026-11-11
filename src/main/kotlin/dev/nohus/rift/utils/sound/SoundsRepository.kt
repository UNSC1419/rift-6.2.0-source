package dev.nohus.rift.utils.sound

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.sounds_bonus_name
import dev.nohus.rift.generated.resources.sounds_ding_name
import dev.nohus.rift.generated.resources.sounds_magic_sparkle_name
import dev.nohus.rift.generated.resources.sounds_metal_whoosh_name
import dev.nohus.rift.generated.resources.sounds_notification_1_name
import dev.nohus.rift.generated.resources.sounds_notification_2_name
import dev.nohus.rift.generated.resources.sounds_notification_3_name
import dev.nohus.rift.generated.resources.sounds_notification_4_name
import dev.nohus.rift.generated.resources.sounds_pop_name
import dev.nohus.rift.generated.resources.sounds_power_up_name
import dev.nohus.rift.generated.resources.sounds_surprise_name
import dev.nohus.rift.generated.resources.sounds_swoosh_name
import dev.nohus.rift.localization.toUiText
import org.koin.core.annotation.Single

@Single
class SoundsRepository {

    private val soundResources = [
        Sound(id = 1, resource = "click.wav", name = Res.string.sounds_notification_1_name.toUiText()),
        Sound(id = 2, resource = "button.wav", name = Res.string.sounds_notification_2_name.toUiText()),
        Sound(id = 3, resource = "achieve.wav", Res.string.sounds_notification_3_name.toUiText()),
        Sound(id = 4, resource = "interface.wav", name = Res.string.sounds_notification_4_name.toUiText()),
        Sound(id = 5, resource = "pop.wav", name = Res.string.sounds_pop_name.toUiText()),
        Sound(id = 6, resource = "ding-idea.wav", name = Res.string.sounds_ding_name.toUiText()),
        Sound(id = 7, resource = "game-bonus.wav", name = Res.string.sounds_bonus_name.toUiText()),
        Sound(id = 8, resource = "swoosh.wav", name = Res.string.sounds_swoosh_name.toUiText()),
        Sound(id = 9, resource = "ambient-metal-whoosh.wav", Res.string.sounds_metal_whoosh_name.toUiText()),
        Sound(id = 10, resource = "bell-transition.wav", name = Res.string.sounds_magic_sparkle_name.toUiText()),
        Sound(id = 11, resource = "surprise.wav", name = Res.string.sounds_surprise_name.toUiText()),
        Sound(id = 12, resource = "power-up-sparkle.wav", name = Res.string.sounds_power_up_name.toUiText()),
    ]

    fun getSounds(): List<Sound> {
        return soundResources
    }
}
