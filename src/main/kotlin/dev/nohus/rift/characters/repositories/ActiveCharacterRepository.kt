package dev.nohus.rift.characters.repositories

import dev.nohus.rift.utils.evewindows.EveWindowsRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

/**
 * Keeps track of the most recent active character (by window focus)
 */
@Single
class ActiveCharacterRepository(
    private val onlineCharactersRepository: OnlineCharactersRepository,
    private val localCharactersRepository: LocalCharactersRepository,
    private val eveWindowsRepository: EveWindowsRepository,
) {

    private val _activeCharacter = MutableStateFlow<Int?>(null)
    val activeCharacter = _activeCharacter.asStateFlow()

    suspend fun start() = coroutineScope {
        launch {
            eveWindowsRepository.state
                .map { state ->
                    if (state.status == EveWindowsRepository.Status.Available) {
                        state.windows.firstOrNull { it.isActive }?.characterName
                    } else {
                        null
                    }
                }
                .distinctUntilChanged()
                .collect { activeWindowCharacter ->
                    checkActiveCharacter(activeWindowCharacter)
                }
        }
        @OptIn(FlowPreview::class)
        launch {
            onlineCharactersRepository.onlineCharacters.debounce(500.milliseconds).collect {
                // If we don't have an active character yet, take the first online character
                if (_activeCharacter.value == null) _activeCharacter.value = it.firstOrNull()
                eveWindowsRepository.refresh()
            }
        }

        launch {
            activeCharacter.collect {
                if (it != null) {
                    logger.info { "Active character changed: $it" }
                }
            }
        }
    }

    private suspend fun checkActiveCharacter(character: String?) = withContext(Dispatchers.IO) {
        val character = character ?: return@withContext
        val characterId = localCharactersRepository.characters.value.firstOrNull { characterItem ->
            characterItem.info?.name == character
        }?.characterId
        _activeCharacter.value = characterId
    }
}
