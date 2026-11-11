package dev.nohus.rift.sso.authentication

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.sso_character_id
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.network.interceptors.EsiAppHeadersInterceptor
import dev.nohus.rift.network.interceptors.UserAgentInterceptor
import dev.nohus.rift.sso.SsoAuthority
import dev.nohus.rift.utils.openBrowser
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.hc.core5.net.URIBuilder
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.IOException
import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.encoding.Base64

private val logger = KotlinLogging.logger {}

@Single
class SsoClient(
    private val server: CallbackServer,
    private val eveSsoRepository: EveSsoRepository,
    private val appHeadersInterceptor: EsiAppHeadersInterceptor,
    private val userAgentInterceptor: UserAgentInterceptor,
    private val appLocale: AppLocale,
    @Named("network") private val json: Json,
) {
    private data class AuthenticationDetails(
        val authentication: Authentication.EveAuthentication,
        val characterName: String,
    )

    private var sessionFinished: CompletableDeferred<Unit>? = null
    private val httpsJwksCache = ConcurrentHashMap<String, HttpsJwks>()
    private val secureRandom = SecureRandom()
    private val base64UrlEncoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
    private val httpClient = HttpClient(CIO) {
        defaultRequest {
            header(HttpHeaders.UserAgent, userAgentInterceptor.userAgentValue)
            appHeadersInterceptor.getHeaders("SSO").forEach { header(it.key, it.value) }
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    data class SsoConfiguration(
        val authority: SsoAuthority,
        val callbackPort: Int,
        val callbackUrl: String,
        val authorizationEndpoint: String,
        val tokenEndpoint: String,
        val tokenRedirectUrl: String?,
        val jwksEndpoint: String,
        val jwtExpectedIssuer: String,
        val clientId: String,
        val clientSecret: String?,
    )

    private fun getSsoConfiguration(authority: SsoAuthority) = when (authority) {
        SsoAuthority.Eve -> SsoConfiguration(
            authority = SsoAuthority.Eve,
            callbackPort = 25252,
            callbackUrl = "http://localhost:25252/callback",
            authorizationEndpoint = "https://login.eveonline.com/v2/oauth/authorize",
            tokenEndpoint = "https://login.eveonline.com/v2/oauth/token",
            tokenRedirectUrl = null,
            jwksEndpoint = "https://login.eveonline.com/oauth/jwks",
            jwtExpectedIssuer = "https://login.eveonline.com",
            clientId = "84f160b7c2d54bddb41c0ef587923e65",
            clientSecret = null,
        )
    }

    suspend fun authenticate(
        authority: SsoAuthority,
        scopes: List<String>,
        onAuthenticationStarted: suspend () -> Unit,
        onAuthenticationResult: suspend (Result<Authentication>) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val codeVerifiers = ConcurrentHashMap<String, String>()
        val completableDeferred = CompletableDeferred<Unit>()
        sessionFinished = completableDeferred
        val configuration = getSsoConfiguration(authority)

        suspend fun createAuthorizationUrl(): URI {
            val codeVerifier = generateRandomBytes()
            val state = generateRandomBytes()
            codeVerifiers[state] = codeVerifier
            onAuthenticationStarted()
            return buildSsoUrl(configuration, generateCodeChallenge(codeVerifier), state, scopes)
        }

        server.setCallback { code, responseState, error ->
            val codeVerifier = codeVerifiers.remove(responseState)
                ?: return@setCallback CallbackServer.CallbackResult.Failure
            if (code == null || error != null) {
                val exception = IOException("SSO authorization failed${error?.let { ": $it" } ?: ""}")
                logger.error(exception) { "SSO authorization failed" }
                onAuthenticationResult(Result.failure(exception))
                return@setCallback CallbackServer.CallbackResult.Failure
            }
            try {
                val response = postSsoTokenRequest(configuration, code, codeVerifier)
                val details = response.toAuthenticationDetails(configuration, scopes)
                onAuthenticationResult(Result.success(details.authentication))
                CallbackServer.CallbackResult.Success(
                    characterId = details.authentication.characterId,
                    characterName = details.characterName,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error(e) { "SSO authentication failed after a successful callback" }
                onAuthenticationResult(Result.failure(e))
                CallbackServer.CallbackResult.Failure
            }
        }
        server.setAuthenticateAnotherUrlProvider { createAuthorizationUrl().toString() }
        server.setFinish { completableDeferred.complete(Unit) }
        server.start(configuration.callbackPort, completableDeferred)

        try {
            createAuthorizationUrl().openBrowser()
            completableDeferred.await()
        } finally {
            if (sessionFinished === completableDeferred) sessionFinished = null
            server.stop()
        }
    }

    fun cancel() {
        sessionFinished?.cancel()
        server.stop()
    }

    fun finish() {
        sessionFinished?.complete(Unit)
    }

    suspend fun refreshToken(authority: SsoAuthority, authentication: Authentication): Authentication = withContext(Dispatchers.IO) {
        try {
            val configuration = getSsoConfiguration(authority)
            val response = httpClient.submitForm(
                url = configuration.tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "refresh_token")
                    append("refresh_token", authentication.refreshToken)
                    append("client_id", configuration.clientId)
                    if (configuration.clientSecret != null) {
                        append("client_secret", configuration.clientSecret)
                    }
                },
            )
            if (response.status.isSuccess()) {
                val tokenResponse: TokenResponse = response.body()
                tokenResponse.toAuthenticationDetails(configuration, authentication.scopes).authentication
            } else {
                val errorResponse: SsoErrorResponse = response.body()
                handleSsoError(authentication, errorResponse)
                throw SsoException(errorResponse)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SsoException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "Failed to refresh token" }
            throw SsoException(null)
        }
    }

    private fun validateJwt(configuration: SsoConfiguration, accessToken: String): JwtClaims {
        val httpsJwks = httpsJwksCache.computeIfAbsent(configuration.jwksEndpoint) { HttpsJwks(it) }
        val keyResolver = HttpsJwksVerificationKeyResolver(httpsJwks)
        val jwtConsumer = JwtConsumerBuilder()
            .setVerificationKeyResolver(keyResolver)
            .setExpectedIssuer(configuration.jwtExpectedIssuer)
            .setRequireExpirationTime()
            .setSkipDefaultAudienceValidation()
            .build()
        return jwtConsumer.processToClaims(accessToken)
    }

    private suspend fun TokenResponse.toAuthenticationDetails(configuration: SsoConfiguration, scopes: List<String>): AuthenticationDetails {
        val claims = validateJwt(configuration, accessToken)
        val expires = Instant.now() + Duration.ofSeconds(expiresIn.toLong() - 1)
        val refreshToken = refreshToken ?: throw IOException("No refresh token in token response")
        return when (configuration.authority) {
            SsoAuthority.Eve -> {
                val characterId = claims.subject.substringAfter("CHARACTER:EVE:").toInt()
                val grantedScopes = claims.getStringListClaimValue("scp").toList().sorted()
                if (grantedScopes != scopes) {
                    logger.error { "Scopes granted by SSO don't match requested scopes: $grantedScopes, expected $scopes" }
                }
                val authentication = Authentication.EveAuthentication(
                    characterId = characterId,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiration = expires,
                    scopes = grantedScopes,
                )
                AuthenticationDetails(
                    authentication = authentication,
                    characterName = claims.getStringClaimValue("name") ?: Res.string.sso_character_id.toUiText(characterId.toString().toUiTextArgument()).resolve(appLocale),
                )
            }
        }
    }

    private fun buildSsoUrl(
        configuration: SsoConfiguration,
        codeChallenge: String,
        state: String,
        scopes: List<String>,
    ): URI {
        return URIBuilder(configuration.authorizationEndpoint).apply {
            addParameter("response_type", "code")
            addParameter("redirect_uri", configuration.callbackUrl)
            addParameter("client_id", configuration.clientId)
            addParameter("scope", scopes.joinToString(" "))
            addParameter("code_challenge", codeChallenge)
            addParameter("code_challenge_method", "S256")
            addParameter("state", state)
        }.build()
    }

    private suspend fun postSsoTokenRequest(
        configuration: SsoConfiguration,
        code: String,
        codeVerifier: String,
    ): TokenResponse {
        val response = httpClient.submitForm(
            url = configuration.tokenEndpoint,
            formParameters = parameters {
                append("grant_type", "authorization_code")
                append("code", code)
                append("client_id", configuration.clientId)
                append("code_verifier", codeVerifier)
                if (configuration.clientSecret != null) {
                    append("client_secret", configuration.clientSecret)
                }
                if (configuration.tokenRedirectUrl != null) {
                    append("redirect_uri", configuration.tokenRedirectUrl)
                }
            },
        )
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            val errorResponse: SsoErrorResponse = response.body()
            throw SsoException(errorResponse)
        }
    }

    private fun generateRandomBytes(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return base64UrlEncoder.encode(bytes)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(codeVerifier.toByteArray())
        return base64UrlEncoder.encode(hash)
    }

    private fun handleSsoError(authentication: Authentication, errorResponse: SsoErrorResponse) {
        if (errorResponse.error == "invalid_grant") {
            logger.error { "Invalid grant: $errorResponse" }
            when (authentication) {
                is Authentication.EveAuthentication -> eveSsoRepository.removeAuthentication(authentication)
            }
        } else {
            logger.error { "Unknown SSO error: $errorResponse" }
        }
    }
}
