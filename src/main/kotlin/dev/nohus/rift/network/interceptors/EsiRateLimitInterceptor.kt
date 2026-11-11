package dev.nohus.rift.network.interceptors

import dev.nohus.rift.network.requests.Character
import dev.nohus.rift.network.requests.RateLimit
import dev.nohus.rift.network.requests.RateLimitGroup
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.core.annotation.Single
import retrofit2.Invocation
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.math.ln

private val logger = KotlinLogging.logger {}

@Single
class EsiRateLimitInterceptor : Interceptor {

    private val mutex = Mutex()
    private val buckets = mutableMapOf<BucketKey, Bucket>()
    private val spentTokens = mutableMapOf<BucketKey, List<SpentTokens>>()
    private val inflightRequests = mutableMapOf<BucketKey, List<UUID>>()

    data class BucketKey(
        val group: RateLimitGroup,
        val character: Character?,
    )

    data class SpentTokens(
        val tokens: Int,
        val returnTimestamp: Instant,
    )

    data class Bucket(
        val limit: Limit,
        val remaining: Int,
        val retryAfter: Instant?,
    )

    data class Limit(
        val tokens: Int,
        val windowSeconds: Int,
    )

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val request = chain.request()
        val group = getRateLimitGroup(request)
        if (group != null) {
            val character = request.tag(Character::class.java)
            val bucketKey = BucketKey(group, character)

            val requestId = UUID.randomUUID()
            val response = try {
                handleRateLimit(group, bucketKey, requestId)
                val response = chain.proceed(request)
                handleResponse(response, group, bucketKey)
                response
            } finally {
                mutex.withLock {
                    inflightRequests[bucketKey] = (inflightRequests[bucketKey] ?: emptyList()) - requestId
                }
            }

            response
        } else {
            // This endpoint does not have rate limits set
            chain.proceed(request)
        }
    }

    suspend fun getBuckets(): Pair<Map<BucketKey, Bucket>, Map<BucketKey, List<SpentTokens>>> {
        return mutex.withLock {
            buckets.toMap() to spentTokens.toMap()
        }
    }

    private fun getRateLimitGroup(request: Request): RateLimitGroup? {
        val invocation = request.tag(Invocation::class.java)
        val rateLimitGroupAnnotation = invocation?.method()?.getAnnotation(RateLimit::class.java)
        return rateLimitGroupAnnotation?.value?.objectInstance
    }

    sealed interface RateLimitAction {
        data object Proceed : RateLimitAction
        data class ProceedAfterDelay(val delay: Duration) : RateLimitAction
        data class RecheckAfterDelay(val delay: Duration) : RateLimitAction
    }

    private suspend fun handleRateLimit(
        group: RateLimitGroup,
        bucketKey: BucketKey,
        requestId: UUID,
    ) {
        withContext(Dispatchers.Default) {
            var isThrottleable = true
            while (true) {
                when (val action = getRateLimitAction(group, bucketKey, requestId, isThrottleable)) {
                    RateLimitAction.Proceed -> return@withContext
                    is RateLimitAction.ProceedAfterDelay -> {
                        delay(action.delay)
                        isThrottleable = false
                    }
                    is RateLimitAction.RecheckAfterDelay -> {
                        delay(action.delay)
                        isThrottleable = true
                    }
                }
            }
        }
    }

    private suspend fun getRateLimitAction(
        group: RateLimitGroup,
        bucketKey: BucketKey,
        requestId: UUID,
        isThrottleable: Boolean,
    ): RateLimitAction {
        return mutex.withLock {
            val bucket = buckets[bucketKey]
            if (bucket == null) {
                // No bucket, there were no requests yet for this rate limit group
                inflightRequests[bucketKey] = (inflightRequests[bucketKey] ?: emptyList()) + requestId
                return@withLock RateLimitAction.Proceed
            }

            val now = Instant.now()
            if (bucket.retryAfter != null && bucket.retryAfter.isAfter(now)) {
                logger.info { "Request in group \"${group.name}\" is being rate limited, waiting ${bucket.retryAfter} before rechecking" }
                return@withLock RateLimitAction.RecheckAfterDelay(Duration.between(now, bucket.retryAfter))
            }

            val recordedSpentTokens = spentTokens[bucketKey] ?: emptyList()
            val replenishedTokens = recordedSpentTokens
                .filter { !it.returnTimestamp.isAfter(now) }
                .sumOf { it.tokens }
            spentTokens[bucketKey] = recordedSpentTokens.filter { it.returnTimestamp.isAfter(now) }

            val updatedBucket = bucket.copy(remaining = bucket.remaining + replenishedTokens)
            buckets[bucketKey] = updatedBucket

            val inflightCount = inflightRequests[bucketKey]?.size ?: 0
            val potentiallyUsedTokens = inflightCount * 5
            val tokensRemaining = updatedBucket.remaining - potentiallyUsedTokens
            val slowDownBreakpoint = (updatedBucket.limit.tokens * 0.3f).toInt()
                .coerceIn(10..200)

            if (tokensRemaining <= 0) {
                logger.info { "Request in group \"${group.name}\", tokens remaining: $tokensRemaining (inflight $inflightCount), waiting for 10 seconds before rechecking" }
                return@withLock RateLimitAction.RecheckAfterDelay(Duration.ofSeconds(10))
            }

            if (isThrottleable && tokensRemaining < slowDownBreakpoint) {
                val tokensPerSecond = updatedBucket.limit.tokens / updatedBucket.limit.windowSeconds.toFloat()
                val tokensToRegenerate = slowDownBreakpoint - tokensRemaining
                val secondsToRegenerate = tokensToRegenerate / tokensPerSecond
                val waitFactor = getWaitFactor(tokensRemaining, slowDownBreakpoint)
                val delayMillis = (secondsToRegenerate * waitFactor * 1000).toLong()
                return@withLock RateLimitAction.ProceedAfterDelay(Duration.ofMillis(delayMillis))
            }

            inflightRequests[bucketKey] = (inflightRequests[bucketKey] ?: emptyList()) + requestId
            RateLimitAction.Proceed
        }
    }

    /**
     * Determines how much we want to slow down requests based on remaining tokens.
     * The function returns:
     * - For 100% tokens or more -> 0 (no wait needed)
     * - For 50% tokens -> 0.22 (22% wait needed)
     * - For 0% tokens -> 1 (100% wait needed)
     * with a gradual non-linear ramp up inbetween.
     */
    private fun getWaitFactor(tokensRemaining: Int, slowDownBreakpoint: Int): Double {
        if (tokensRemaining <= 0 || slowDownBreakpoint <= 0) return 1.0
        val scaledTokens = tokensRemaining.toDouble() / slowDownBreakpoint * 100.0
        return (1.5513 - (ln(scaledTokens + 5) / 3.0)).coerceIn(0.0..1.0)
    }

    private suspend fun handleResponse(
        response: Response,
        group: RateLimitGroup,
        bucketKey: BucketKey,
    ) {
        val groupResponse = response.header("X-Ratelimit-Group")
        val limit = parseLimit(response.header("X-Ratelimit-Limit"))
        val remaining = response.header("X-Ratelimit-Remaining")?.toIntOrNull()
        val used = response.header("X-Ratelimit-Used")?.toIntOrNull()
        val retryAfter = parseRetryAfter(response.header("Retry-After"))

        if (limit == null) {
            return
        } else if (groupResponse != group.name) {
            logger.error { "Invalid group response for request in group \"${group.name}\": $groupResponse" }
        } else if (remaining == null) {
            logger.error { "Invalid remaining response for request in group \"${group.name}\": (${response.header("X-Ratelimit-Remaining")})" }
        } else if (used == null) {
            logger.error { "Invalid used response for request in group \"${group.name}\": (${response.header("X-Ratelimit-Used")})" }
        } else {
            val now = Instant.now()
            val bucket = Bucket(limit, remaining, retryAfter)
            val newSpentTokens = SpentTokens(used, now + Duration.ofSeconds(bucket.limit.windowSeconds.toLong()))
            mutex.withLock {
                buckets[bucketKey] = bucket
                spentTokens[bucketKey] = spentTokens.getOrDefault(bucketKey, emptyList()) + newSpentTokens
            }
        }
    }

    private fun parseLimit(limit: String?): Limit? {
        if (limit == null) return null
        val parts = limit.split("/")
        val tokens = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val durationWithUnit = parts.getOrNull(1) ?: return null
        val duration = durationWithUnit.dropLast(1).toIntOrNull() ?: return null
        val unit = durationWithUnit.lastOrNull() ?: return null
        val windowSeconds = when (unit) {
            'm' -> duration * 60
            'h' -> duration * 60 * 60
            else -> return null
        }
        return Limit(tokens, windowSeconds)
    }

    private fun parseRetryAfter(retryAfter: String?): Instant? {
        if (retryAfter == null) return null
        return Instant.now().plusSeconds(retryAfter.toLong())
    }
}
