package dev.nohus.rift.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import dev.nohus.rift.compose.text.parseEveFormattedText
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.alerts_game_action_asteroid_depleted_body
import dev.nohus.rift.generated.resources.alerts_game_action_asteroid_depleted_title
import dev.nohus.rift.generated.resources.alerts_game_action_attacking_body
import dev.nohus.rift.generated.resources.alerts_game_action_attacking_title
import dev.nohus.rift.generated.resources.alerts_game_action_combat_stopped_body
import dev.nohus.rift.generated.resources.alerts_game_action_combat_stopped_title
import dev.nohus.rift.generated.resources.alerts_game_action_conversation_invite_body
import dev.nohus.rift.generated.resources.alerts_game_action_conversation_invite_title
import dev.nohus.rift.generated.resources.alerts_game_action_decloaked_body
import dev.nohus.rift.generated.resources.alerts_game_action_decloaked_title
import dev.nohus.rift.generated.resources.alerts_game_action_out_of_charges_body
import dev.nohus.rift.generated.resources.alerts_game_action_out_of_charges_title
import dev.nohus.rift.generated.resources.alerts_game_action_under_attack_body
import dev.nohus.rift.generated.resources.alerts_game_action_under_attack_title
import dev.nohus.rift.generated.resources.alerts_game_action_warp_scrambled_body
import dev.nohus.rift.generated.resources.alerts_game_action_warp_scrambled_title
import dev.nohus.rift.generated.resources.alerts_planet_attention_body
import dev.nohus.rift.generated.resources.alerts_planet_attention_future_body
import dev.nohus.rift.generated.resources.alerts_planet_attention_future_title
import dev.nohus.rift.generated.resources.alerts_planet_attention_title
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.utils.formatDurationLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Entities
import java.time.Duration

sealed interface NotificationText {
    data class Literal(val title: String, val message: AnnotatedString) : NotificationText
    data class GameAction(val type: GameActionNotificationType, val subject: String) : NotificationText
    data class PlanetaryIndustry(val planetName: String, val attentionIn: Duration?) : NotificationText
}

@Serializable
enum class GameActionNotificationType {
    @SerialName("under_attack")
    UnderAttack,

    @SerialName("attacking")
    Attacking,

    @SerialName("warp_scrambled")
    WarpScrambled,

    @SerialName("decloaked")
    Decloaked,

    @SerialName("combat_stopped")
    CombatStopped,

    @SerialName("out_of_charges")
    OutOfCharges,

    @SerialName("conversation_invite")
    ConversationInvite,

    @SerialName("asteroid_depleted")
    AsteroidDepleted,
}

fun NotificationText.getTitleText(): UiText = when (this) {
    is NotificationText.Literal -> title.toUiText()
    is NotificationText.PlanetaryIndustry -> if (attentionIn != null) {
        Res.string.alerts_planet_attention_future_title.toUiText()
    } else {
        Res.string.alerts_planet_attention_title.toUiText()
    }
    is NotificationText.GameAction -> when (type) {
        GameActionNotificationType.UnderAttack -> Res.string.alerts_game_action_under_attack_title.toUiText()
        GameActionNotificationType.Attacking -> Res.string.alerts_game_action_attacking_title.toUiText()
        GameActionNotificationType.WarpScrambled -> Res.string.alerts_game_action_warp_scrambled_title.toUiText()
        GameActionNotificationType.Decloaked -> Res.string.alerts_game_action_decloaked_title.toUiText()
        GameActionNotificationType.CombatStopped -> Res.string.alerts_game_action_combat_stopped_title.toUiText()
        GameActionNotificationType.OutOfCharges -> Res.string.alerts_game_action_out_of_charges_title.toUiText()
        GameActionNotificationType.ConversationInvite -> Res.string.alerts_game_action_conversation_invite_title.toUiText()
        GameActionNotificationType.AsteroidDepleted -> Res.string.alerts_game_action_asteroid_depleted_title.toUiText()
    }
}

fun NotificationText.getMessageText(): UiText = when (this) {
    is NotificationText.Literal -> message.toString().toUiText()
    is NotificationText.PlanetaryIndustry -> if (attentionIn != null) {
        Res.string.alerts_planet_attention_future_body.toUiText(
            planetName.toUiTextArgument(),
            formatDurationLong(attentionIn).toUiTextArgument(),
        )
    } else {
        Res.string.alerts_planet_attention_body.toUiText(planetName.toUiTextArgument())
    }
    is NotificationText.GameAction -> when (type) {
        GameActionNotificationType.UnderAttack -> Res.string.alerts_game_action_under_attack_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.Attacking -> Res.string.alerts_game_action_attacking_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.WarpScrambled -> Res.string.alerts_game_action_warp_scrambled_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.Decloaked -> Res.string.alerts_game_action_decloaked_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.CombatStopped -> Res.string.alerts_game_action_combat_stopped_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.OutOfCharges -> Res.string.alerts_game_action_out_of_charges_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.ConversationInvite -> Res.string.alerts_game_action_conversation_invite_body.toUiText(Entities.escape(subject).toUiTextArgument())
        GameActionNotificationType.AsteroidDepleted -> Res.string.alerts_game_action_asteroid_depleted_body.toUiText(Entities.escape(subject).toUiTextArgument())
    }
}

@Composable
fun NotificationText.getAnnotatedMessage(): AnnotatedString = when (this) {
    is NotificationText.Literal -> message
    is NotificationText.PlanetaryIndustry -> AnnotatedString(getMessageText().resolve())
    is NotificationText.GameAction -> parseEveFormattedText(getMessageText().resolve(), decodeEntities = true)
}
