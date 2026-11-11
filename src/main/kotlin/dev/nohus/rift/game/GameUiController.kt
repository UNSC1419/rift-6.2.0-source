package dev.nohus.rift.game

import androidx.compose.ui.text.font.FontWeight
import dev.nohus.rift.characters.repositories.ActiveCharacterRepository
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.characters.repositories.OnlineCharactersRepository
import dev.nohus.rift.compose.text.FormattedText
import dev.nohus.rift.compose.text.FormattedTextColor.Highlighted
import dev.nohus.rift.compose.text.FormattedTextColor.Secondary
import dev.nohus.rift.compose.text.buildFormattedText
import dev.nohus.rift.compose.text.toFormattedText
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.game_ui_link_close_hint
import dev.nohus.rift.generated.resources.game_ui_link_corporation_project_instruction
import dev.nohus.rift.generated.resources.game_ui_link_corporation_project_subject
import dev.nohus.rift.generated.resources.game_ui_link_freelance_project_instruction
import dev.nohus.rift.generated.resources.game_ui_link_freelance_project_subject
import dev.nohus.rift.generated.resources.game_ui_link_instruction
import dev.nohus.rift.generated.resources.game_ui_link_location_instruction
import dev.nohus.rift.generated.resources.game_ui_link_location_subject
import dev.nohus.rift.generated.resources.game_ui_link_mercenary_tactical_operation_instruction
import dev.nohus.rift.generated.resources.game_ui_link_mercenary_tactical_operation_subject
import dev.nohus.rift.generated.resources.game_ui_link_ship_instruction
import dev.nohus.rift.generated.resources.game_ui_link_ship_subject
import dev.nohus.rift.generated.resources.game_ui_link_subject
import dev.nohus.rift.generated.resources.game_ui_link_system_instruction
import dev.nohus.rift.generated.resources.game_ui_link_system_subject
import dev.nohus.rift.generated.resources.game_ui_link_type_instruction
import dev.nohus.rift.generated.resources.game_ui_link_type_subject
import dev.nohus.rift.generated.resources.game_ui_no_character_message
import dev.nohus.rift.generated.resources.game_ui_no_character_title
import dev.nohus.rift.generated.resources.game_ui_permission_message
import dev.nohus.rift.generated.resources.game_ui_permission_title
import dev.nohus.rift.generated.resources.game_ui_reauthenticate_prefix
import dev.nohus.rift.generated.resources.game_ui_reauthenticate_suffix
import dev.nohus.rift.generated.resources.game_ui_request_failed_message
import dev.nohus.rift.generated.resources.game_ui_request_failed_title
import dev.nohus.rift.generated.resources.game_ui_request_reason_prefix
import dev.nohus.rift.generated.resources.game_ui_unknown_error
import dev.nohus.rift.infodialog.InfoDialogInputModel
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.network.Result
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.esi.models.NewMailRequest
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.sso.scopes.ScopeGroups
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.windowing.WindowManager
import dev.nohus.rift.windowing.WindowManager.RiftWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.koin.core.annotation.Single

@Single
class GameUiController(
    private val esiApi: EsiApi,
    private val activeCharacterRepository: ActiveCharacterRepository,
    private val onlineCharactersRepository: OnlineCharactersRepository,
    private val localCharactersRepository: LocalCharactersRepository,
    private val windowManager: WindowManager,
    private val appLocale: AppLocale,
) {

    private val scope = CoroutineScope(SupervisorJob())

    enum class LinkCategory(val subject: StringResource, val instruction: StringResource) {
        Ship(Res.string.game_ui_link_ship_subject, Res.string.game_ui_link_ship_instruction),
        Type(Res.string.game_ui_link_type_subject, Res.string.game_ui_link_type_instruction),
        System(Res.string.game_ui_link_system_subject, Res.string.game_ui_link_system_instruction),
        Location(Res.string.game_ui_link_location_subject, Res.string.game_ui_link_location_instruction),
        CorporationProject(Res.string.game_ui_link_corporation_project_subject, Res.string.game_ui_link_corporation_project_instruction),
        FreelanceProject(Res.string.game_ui_link_freelance_project_subject, Res.string.game_ui_link_freelance_project_instruction),
        MercenaryTacticalOperation(Res.string.game_ui_link_mercenary_tactical_operation_subject, Res.string.game_ui_link_mercenary_tactical_operation_instruction),
    }

    /**
     * Opens the Show Info window in-game, only works for characters, corporations, and alliances
     */
    fun openInfoWindow(
        entityId: Int,
    ) {
        scope.launch {
            val characterId = getTargetCharacter() ?: return@launch
            esiApi.postUiOpenWindowInformation(Originator.GameUi, characterId, entityId.toLong()).handleError()
        }
    }

    /**
     * Switches the already opened Market Details window in-game to the specified type.
     * Does nothing if no Market Details window is open.
     */
    fun updateMarketDetailsWindow(
        typeId: Long,
    ) {
        scope.launch {
            val characterId = getTargetCharacter() ?: return@launch
            esiApi.postUiOpenWindowMarketDetails(Originator.GameUi, characterId, typeId).handleError()
        }
    }

    fun pushType(type: Type, category: LinkCategory, singletonId: Long? = null) {
        pushLink(GameLink.forType(type, singletonId), category)
    }

    fun pushSystem(system: MapSolarSystem) {
        pushLink(GameLink.forSystem(system), LinkCategory.System)
    }

    fun pushLocation(locationId: Long, locationTypeId: Int, locationName: String) {
        pushLink(GameLink.forLocation(locationId, locationTypeId, locationName), LinkCategory.Location)
    }

    fun pushCorporationProject(id: String, name: String) {
        pushLink(GameLink.forCorporationProject(id, name), LinkCategory.CorporationProject)
    }

    fun pushFreelanceProject(id: String, name: String) {
        pushLink(GameLink.forFreelanceJob(id, name), LinkCategory.FreelanceProject)
    }

    fun pushMercenaryTacticalOperation(id: String, name: String) {
        pushLink(GameLink.forMercenaryTacticalOperation(id, name), LinkCategory.MercenaryTacticalOperation)
    }

    fun pushUrl(link: String, name: String) {
        pushLink("<url=$link>$name</url>", null)
    }

    private fun pushLink(link: String, category: LinkCategory?) {
        scope.launch {
            val subject = (category?.subject?.toUiText() ?: Res.string.game_ui_link_subject.toUiText()).resolve(appLocale)
            val instruction = (category?.instruction?.toUiText() ?: Res.string.game_ui_link_instruction.toUiText()).resolve(appLocale)
            val closeHint = Res.string.game_ui_link_close_hint.toUiText().resolve(appLocale)
            val body = buildString {
                append("<font size=\"30\">")
                append("→ ")
                append(link)
                append(" ←")
                append("</font><br>")
                append("<b>$instruction</b><br>")
                append("<br><br><br><br>")
                append("<font size=\"12\" color=\"#ff4c4c4c\">$closeHint</font><br><br>")
                append("<b><font color=\"#ffC3E9FF\">R</font>IFT <font color=\"#ffC3E9FF\">I</font>ntel <font color=\"#ffC3E9FF\">F</font>usion <font color=\"#ffC3E9FF\">T</font>ool</b>")
            }
            openNewMailWindow(subject, body)
        }
    }

    fun openNewMailWindow(
        subject: String,
        body: String,
    ) {
        scope.launch {
            val characterId = getTargetCharacter() ?: return@launch
            val request = NewMailRequest(
                body = body,
                recipients = listOf(0),
                subject = subject,
            )
            esiApi.postUiOpenWindowNewMail(Originator.GameUi, characterId, request).handleError()
        }
    }

    private suspend fun getTargetCharacter(): Int? {
        val onlineCharacterIds = onlineCharactersRepository.onlineCharacters.value
        val targetCharacterId = activeCharacterRepository.activeCharacter.value.takeIf { it in onlineCharacterIds }
            ?: onlineCharactersRepository.onlineCharacters.value.firstOrNull()
        if (targetCharacterId == null) {
            showError(
                title = Res.string.game_ui_no_character_title.toUiText(),
                text = Res.string.game_ui_no_character_message.toUiText().resolve(appLocale).toFormattedText(),
            )
            return null
        }
        val targetCharacter = localCharactersRepository.characters.value
            .firstOrNull { it.characterId == targetCharacterId }
            ?: return null
        if (ScopeGroups.openWindow !in targetCharacter.scopes) {
            val permissionMessage = Res.string.game_ui_permission_message.toUiText().resolve(appLocale)
            val reauthenticatePrefix = Res.string.game_ui_reauthenticate_prefix.toUiText().resolve(appLocale)
            val reauthenticateSuffix = Res.string.game_ui_reauthenticate_suffix.toUiText().resolve(appLocale)
            showError(
                title = Res.string.game_ui_permission_title.toUiText(),
                text = buildFormattedText {
                    appendLine(permissionMessage)
                    appendLine()
                    append(reauthenticatePrefix)
                    withColor(Highlighted) {
                        withWeight(FontWeight.Bold) {
                            append(targetCharacter.info?.name ?: targetCharacter.characterId.toString())
                        }
                    }
                    append(reauthenticateSuffix)
                },
            )
            return null
        }
        return targetCharacterId
    }

    private suspend fun Result<Unit>.handleError() {
        if (this is Result.Failure) {
            val failureMessage = Res.string.game_ui_request_failed_message.toUiText().resolve(appLocale)
            val reasonPrefix = Res.string.game_ui_request_reason_prefix.toUiText().resolve(appLocale)
            val reason = cause?.message ?: Res.string.game_ui_unknown_error.toUiText().resolve(appLocale)
            showError(
                title = Res.string.game_ui_request_failed_title.toUiText(),
                text = buildFormattedText {
                    appendLine(failureMessage)
                    appendLine()
                    append(reasonPrefix)
                    withColor(Secondary) {
                        append(reason)
                    }
                },
            )
        }
    }

    private suspend fun showError(
        title: UiText,
        text: FormattedText,
    ) {
        val inputModel = InfoDialogInputModel(
            title = title.resolve(appLocale),
            text = text,
            isWarning = true,
        )
        windowManager.onWindowOpen(RiftWindow.InfoDialog, inputModel)
    }
}
