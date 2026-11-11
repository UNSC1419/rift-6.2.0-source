package dev.nohus.rift.sso.authentication

import dev.nohus.rift.sso.SsoAuthority
import dev.nohus.rift.sso.authentication.Authentication.EveAuthentication
import dev.nohus.rift.sso.scopes.EsiScope
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory
import java.time.Duration
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Factory
class SsoAuthenticator(
    private val ssoClient: SsoClient,
    private val eveSsoRepository: EveSsoRepository,
) {
    sealed interface AuthenticationResult {
        data class Success(val characterId: Int) : AuthenticationResult
        data object Failure : AuthenticationResult
    }

    // One mutex per character to prevent concurrent refreshes for the same character
    private val refreshMutexes = mutableMapOf<Int, Mutex>()
    private val minValidityDuration = Duration.ofMinutes(1)

    /**
     * Starts the SSO flow, redirecting the user to the SSO login page.
     * Reports each authentication attempt and returns when the user finishes the session.
     */
    suspend fun authenticate(
        authority: SsoAuthority,
        scopes: List<String>,
        onAuthenticationStarted: suspend () -> Unit,
        onAuthenticationResult: suspend (AuthenticationResult) -> Unit,
    ) {
        ssoClient.authenticate(authority, scopes.sorted(), onAuthenticationStarted) { result ->
            try {
                val authentication = result.getOrThrow()
                when (authority) {
                    SsoAuthority.Eve -> {
                        val eveAuthentication = authentication as EveAuthentication
                        eveSsoRepository.addAuthentication(eveAuthentication)
                        logger.info { "SSO authentication successful ($authority)" }
                        onAuthenticationResult(AuthenticationResult.Success(eveAuthentication.characterId))
                    }
                }
            } catch (e: Exception) {
                onAuthenticationResult(AuthenticationResult.Failure)
            }
        }
    }

    /**
     * Cancels an in progress SSO flow, if any
     */
    fun cancel() {
        ssoClient.cancel()
    }

    fun finish() {
        ssoClient.finish()
    }

    /**
     * Retrieves an access token, refreshing it first if needed, or null if there isn't one
     *
     * @param characterId Character ID
     * @param scope ESI scope the token has to be valid for, or null if no scopes are required. If the present
     * access token isn't valid for that scope, this method will throw.
     * @throws NoAuthenticationException When no valid token is available
     */
    suspend fun getValidEveAccessToken(characterId: Int, scope: EsiScope?): String {
        val authentication = getEveAuthentication(characterId, scope)
        if (authentication.isNotExpired()) {
            return authentication.accessToken
        }

        // Token has expired, refresh it
        val mutex = synchronized(refreshMutexes) {
            refreshMutexes.getOrPut(characterId) { Mutex() }
        }
        return mutex.withLock {
            // Re-check after acquiring the lock in case another coroutine already refreshed
            val current = getEveAuthentication(characterId, scope)
            if (current.isNotExpired()) {
                return@withLock current.accessToken
            }

            val newAuthentication = ssoClient.refreshToken(SsoAuthority.Eve, current) as EveAuthentication
            eveSsoRepository.addAuthentication(newAuthentication)
            logger.debug { "Eve SSO access token refreshed for character $characterId" }
            newAuthentication.accessToken
        }
    }

    private fun getEveAuthentication(characterId: Int, scope: EsiScope?): EveAuthentication {
        val authentication = eveSsoRepository.getAuthentication(characterId)
            ?: throw NoAuthenticationException(characterId, null)
        if (scope != null && scope.id !in authentication.scopes) {
            throw NoAuthenticationException(characterId, scope)
        }
        return authentication
    }

    private fun EveAuthentication.isNotExpired(): Boolean {
        return Duration.between(Instant.now(), expiration) > minValidityDuration
    }
}
