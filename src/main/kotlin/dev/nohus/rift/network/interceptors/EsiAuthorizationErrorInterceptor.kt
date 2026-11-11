package dev.nohus.rift.network.interceptors

import dev.nohus.rift.network.esi.EsiErrorResponse
import dev.nohus.rift.network.requests.Character
import dev.nohus.rift.sso.authentication.EveSsoRepository
import dev.nohus.rift.sso.authentication.NoAuthenticationException
import dev.nohus.rift.sso.authentication.SsoException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class EsiAuthorizationErrorInterceptor(
    private val eveSsoRepository: EveSsoRepository,
    @Named("network") private val json: Json,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = try {
            chain.proceed(request)
        } catch (e: NoAuthenticationException) {
            return createSyntheticFailure(request, e)
        } catch (e: SsoException) {
            return createSyntheticFailure(request, e)
        }

        val character = request.tag(Character::class.java)
        if (character != null && !response.isSuccessful) {
            val body = response.peekBody(Long.MAX_VALUE).string()
            try {
                val errorResponse: EsiErrorResponse = json.decodeFromString(body)
                if ("Token is not valid" in errorResponse.error) {
                    logger.error { "Character ${character.id} has an invalid token, removing" }
                    eveSsoRepository.removeAuthentication(character.id)
                } else if ("Character has been deleted" in errorResponse.error) {
                    logger.error { "Character ${character.id} has been deleted from the game, removing" }
                    eveSsoRepository.removeAuthentication(character.id)
                }
            } catch (_: SerializationException) {
                // Authentication errors come as EsiErrorResponse, so this error is not relevant
            }
        }
        return response
    }

    private fun createSyntheticFailure(request: Request, exception: NoAuthenticationException): Response {
        val message = if (exception.scope != null) {
            "Could not execute request because the character ${exception.characterId} does not allow scope \"${exception.scope.id}\""
        } else {
            "Could not execute request because the character ${exception.characterId} is not authenticated"
        }
        return createSyntheticFailure(request, message)
    }

    private fun createSyntheticFailure(request: Request, exception: SsoException): Response {
        val message = exception.errorResponse?.errorDescription ?: exception.message ?: "Unknown error"
        return createSyntheticFailure(request, message)
    }

    private fun createSyntheticFailure(request: Request, text: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message(text)
            .body(text.toResponseBody(null))
            .build()
    }
}
