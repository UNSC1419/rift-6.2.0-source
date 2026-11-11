package dev.nohus.rift.sso.authentication

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.net.BindException

private val logger = KotlinLogging.logger {}

@Factory
class CallbackServer(
    private val callbackPageRenderer: CallbackPageRenderer,
) {

    sealed interface CallbackResult {
        data class Success(
            val characterId: Int,
            val characterName: String,
        ) : CallbackResult

        data object Failure : CallbackResult
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    private var callback: (suspend (code: String?, state: String, error: String?) -> CallbackResult)? = null
    private var authenticateAnotherUrlProvider: (suspend () -> String)? = null
    private var finish: (() -> Unit)? = null

    fun start(port: Int, deferred: CompletableDeferred<Unit>) {
        stop()
        callbackPageRenderer.reset()
        scope.launch {
            try {
                createCallbackServer(port).let {
                    server = it
                    logger.info { "Starting server" }
                    it.start(wait = true)
                }
            } catch (e: BindException) {
                logger.error { "Could not start SSO callback server due to a BindException: ${e.message}" }
                stop()
                deferred.completeExceptionally(e)
            }
        }
    }

    fun setCallback(block: suspend (code: String?, state: String, error: String?) -> CallbackResult) {
        callback = block
    }

    fun setAuthenticateAnotherUrlProvider(block: suspend () -> String) {
        authenticateAnotherUrlProvider = block
    }

    fun setFinish(block: () -> Unit) {
        finish = block
    }

    fun stop() = scope.launch {
        if (server != null) logger.info { "Stopping server" }
        server?.stop(0, 0)
        server = null
    }

    private fun createCallbackServer(port: Int): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
        return embeddedServer(Netty, port = port) {
            routing {
                get("/callback") {
                    val state = call.parameters["state"] ?: return@get
                    val result = callback?.invoke(
                        call.parameters["code"],
                        state,
                        call.parameters["error"],
                    ) ?: CallbackResult.Failure
                    call.respondCallbackPage(result)
                }
                get("/authenticate") {
                    val authorizationUrl = authenticateAnotherUrlProvider?.invoke() ?: return@get
                    call.respondRedirect(authorizationUrl)
                }
                get("/finish") {
                    call.respond(HttpStatusCode.NoContent)
                    finish?.invoke()
                }
            }
        }
    }

    private suspend fun ApplicationCall.respondCallbackPage(result: CallbackResult) {
        respondText(
            callbackPageRenderer.render(result),
            ContentType.Text.Html,
            HttpStatusCode.OK,
        )
    }
}
