package dev.nohus.rift.characters.repositories

import dev.nohus.rift.charactersettings.io.AccountAssociationsRepository
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.sso.scopes.ScopeGroups
import dev.nohus.rift.utils.evewindows.EveWindow
import dev.nohus.rift.utils.evewindows.EveWindowsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

@Single
class OnlineCharactersRepository(
    private val eveWindowsRepository: EveWindowsRepository,
    private val localCharactersRepository: LocalCharactersRepository,
    private val accountAssociationsRepository: AccountAssociationsRepository,
    private val esiApi: EsiApi,
) {

    private val _onlineCharacters = MutableStateFlow<List<Int>>(emptyList())
    val onlineCharacters = _onlineCharacters.asStateFlow()

    private val lastSeen = mutableMapOf<Int, Instant>()
    private val lastSeenMutex = Mutex()
    private val onlineSinceSeenDuration = Duration.ofMinutes(1)
    private var onlineGameClients = setOf<Int>()

    suspend fun start() = coroutineScope {
        launch {
            while (true) {
                updateOnlineCharacters()
                delay(1000.milliseconds)
            }
        }
        launch {
            // Check online status of offline characters every 5 minutes
            val offlineOnlinePollDuration = Duration.ofMinutes(5)
            while (true) {
                delay(offlineOnlinePollDuration.toMillis().milliseconds)
                checkEsiOnlineCharacters(checkOnline = false)
            }
        }
        launch {
            eveWindowsRepository.state
                .map { state ->
                    val windows = if (state.status == EveWindowsRepository.Status.Available) state.windows else null
                    windows?.map { it.characterName }?.toSet()
                }
                .distinctUntilChanged()
                .collect { characterNames ->
                    if (characterNames != null) updateOpenEveClients(characterNames)
                }
        }
        launch {
            // Check online status of online characters every 20 seconds
            // This will only check the ones for which we cannot see the game client
            val onlineOnlinePollDuration = Duration.ofSeconds(20)
            while (true) {
                delay(onlineOnlinePollDuration.toMillis().milliseconds)
                checkEsiOnlineCharacters(checkOnline = true)
            }
        }
        launch {
            @OptIn(FlowPreview::class)
            localCharactersRepository.characters.debounce(500.milliseconds).collect {
                checkOpenEveClients()
                checkEsiOnlineCharacters(checkOnline = false)
                checkEsiOnlineCharacters(checkOnline = true)
            }
        }
    }

    suspend fun onCharacterLogin(characterId: Int) {
        lastSeenMutex.withLock {
            lastSeen[characterId] = Instant.now()
        }
        updateOnlineCharacters()
        accountAssociationsRepository.onCharacterLogin(characterId)
    }

    private suspend fun updateOnlineCharacters() {
        val now = Instant.now()
        lastSeenMutex.withLock {
            onlineGameClients.forEach { characterId ->
                lastSeen[characterId] = now
            }
            val onlineIds = lastSeen.toMap()
                .filter { (_, timestamp) -> Duration.between(timestamp, now) < onlineSinceSeenDuration }
                .map { (id, _) -> id }
            _onlineCharacters.value = onlineIds
        }
    }

    private suspend fun checkOpenEveClients() = withContext(Dispatchers.IO) {
        val state = eveWindowsRepository.state.value
        if (state.status != EveWindowsRepository.Status.Available) return@withContext
        updateOpenEveClients(state.windows.map(EveWindow::characterName).toSet())
    }

    private suspend fun updateOpenEveClients(characterNames: Set<String>) {
        var characterIds: Set<Int> = emptySet()
        if (characterNames.isNotEmpty()) {
            val now = Instant.now()
            characterIds = localCharactersRepository.characters.value
                .filter { it.info?.name in characterNames }
                .map { it.characterId }
                .toSet()
            characterIds.forEach { characterId ->
                lastSeenMutex.withLock {
                    lastSeen[characterId] = now
                }
            }
        }
        val wentOffline = onlineGameClients - characterIds
        wentOffline.forEach {
            lastSeenMutex.withLock {
                lastSeen.remove(it)
            }
        }
        onlineGameClients = characterIds
        updateOnlineCharacters()
    }

    /**
     * @param checkOnline Whether to check only online, or only offline characters
     */
    private suspend fun checkEsiOnlineCharacters(checkOnline: Boolean) {
        coroutineScope {
            val now = Instant.now()
            localCharactersRepository.characters.value
                .filter { it.characterId in _onlineCharacters.value == checkOnline }
                .filter {
                    // No need to check online if we see the client
                    !checkOnline || it.characterId !in onlineGameClients
                }
                .filter { ScopeGroups.readOnlineStatus in it.scopes } // No point checking unauthenticated characters
                .map {
                    async {
                        it.characterId to esiApi.getCharacterIdOnline(Originator.LocalCharacters, it.characterId)
                    }
                }.awaitAll().forEach { (characterId, result) ->
                    if (result.success?.isOnline == true) {
                        lastSeenMutex.withLock {
                            lastSeen[characterId] = now
                        }
                    }
                }
        }
        updateOnlineCharacters()
    }
}
