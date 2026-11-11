package dev.nohus.rift.sso

import dev.nohus.rift.ViewModel
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.sso_response_listener_failed
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.sso.authentication.SsoAuthenticator
import dev.nohus.rift.sso.authentication.SsoAuthenticator.AuthenticationResult
import dev.nohus.rift.sso.scopes.ScopeGroup
import dev.nohus.rift.sso.scopes.ScopeGroups
import dev.nohus.rift.utils.toggle
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import java.net.BindException

private val logger = KotlinLogging.logger {}

@Factory
class SsoViewModel(
    @InjectedParam private val inputModel: SsoAuthority,
    private val ssoAuthenticator: SsoAuthenticator,
    private val localCharactersRepository: LocalCharactersRepository,
) : ViewModel() {

    data class UiState(
        val status: SsoStatus = SsoStatus.Waiting,
        val scopeGroups: List<ScopeGroup>,
        val selectedScopeGroups: List<ScopeGroup>,
        val authenticatedCharacterIds: Set<Int> = emptySet(),
        val isFinished: Boolean = false,
    )

    sealed interface SsoStatus {
        data object Scopes : SsoStatus
        data object Waiting : SsoStatus
        data object Complete : SsoStatus
        data class Failed(val message: UiText?) : SsoStatus
    }

    private val _state = MutableStateFlow(
        UiState(
            scopeGroups = getScopeGroups(),
            selectedScopeGroups = getScopeGroups(),
        ),
    )
    val state = _state.asStateFlow()
    private var authenticationJob: Job? = null

    fun onWindowOpened() {
        _state.update { it.copy(status = SsoStatus.Waiting) }
        authenticate()
    }

    private fun authenticate() {
        val scopes = _state.value.selectedScopeGroups.flatMap { it.scopes }.map { it.id }
        authenticationJob = viewModelScope.launch {
            try {
                ssoAuthenticator.authenticate(
                    authority = inputModel,
                    scopes = scopes,
                    onAuthenticationStarted = {
                        _state.update { it.copy(status = SsoStatus.Waiting) }
                    },
                    onAuthenticationResult = { result ->
                        when (result) {
                            is AuthenticationResult.Success -> {
                                _state.update {
                                    it.copy(
                                        status = SsoStatus.Complete,
                                        authenticatedCharacterIds = it.authenticatedCharacterIds + result.characterId,
                                    )
                                }
                                viewModelScope.launch {
                                    try {
                                        localCharactersRepository.load()
                                    } catch (e: Exception) {
                                        logger.error(e) { "Could not reload characters after SSO authentication" }
                                    }
                                }
                            }
                            AuthenticationResult.Failure -> {
                                _state.update {
                                    it.copy(status = SsoStatus.Failed(null))
                                }
                            }
                        }
                    },
                )
                _state.update { it.copy(isFinished = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: BindException) {
                _state.update { it.copy(status = SsoStatus.Failed(Res.string.sso_response_listener_failed.toUiText())) }
            } catch (e: Exception) {
                logger.error(e) { "SSO flow failed" }
                _state.update { it.copy(status = SsoStatus.Failed(null)) }
            }
        }
    }

    fun onCheckedChange(group: ScopeGroup) {
        _state.update { it.copy(selectedScopeGroups = it.selectedScopeGroups.toggle(group)) }
    }

    fun onScopesButtonClick() {
        authenticationJob?.cancel()
        ssoAuthenticator.cancel()
        _state.update { it.copy(status = SsoStatus.Scopes) }
    }

    fun onContinueClick() {
        _state.update { it.copy(status = SsoStatus.Waiting) }
        authenticate()
    }

    fun onCloseRequest() {
        authenticationJob?.cancel()
        ssoAuthenticator.cancel()
        _state.update { it.copy(status = SsoStatus.Waiting) }
    }

    fun onFinishRequest() {
        ssoAuthenticator.finish()
    }

    private fun getScopeGroups(): List<ScopeGroup> {
        return ScopeGroups.all
    }
}
