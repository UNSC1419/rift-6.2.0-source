package dev.nohus.rift.network.requests

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.annotation.Single
import retrofit2.Invocation
import java.time.Instant
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration.Companion.milliseconds

@Single
class RequestStatisticsInterceptor : Interceptor {

    data class Bucket(
        val epochSeconds: Long,
        val requests: List<RequestCounts>,
    )

    data class RequestCounts(
        val endpoint: Endpoint,
        val originator: Originator,
        val successCount: Int,
        val failureCount: Int,
        val pendingCount: Int,
    ) {
        val totalCount: Int get() = successCount + failureCount + pendingCount
    }

    private class MutableRequestCounts(
        var successCount: Int = 0,
        var failureCount: Int = 0,
        var pendingCount: Int = 0,
    )

    private data class MutableBucket(
        val epochSeconds: Long,
        val requests: MutableMap<Originator, MutableMap<Endpoint, MutableRequestCounts>> = mutableMapOf(),
    )

    private val lock = ReentrantLock()
    private val mutableBuckets = arrayOfNulls<MutableBucket>(MAX_BUCKETS)
    val buckets: Flow<List<Bucket>> = flow {
        while (currentCoroutineContext().isActive) {
            emit(getBucketsSnapshot())
            delay(PUBLISH_INTERVAL)
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originator = checkNotNull(request.tag(Originator::class.java)) { "Missing originator tag" }
        val invocation = request.tag(Invocation::class.java)
        val endpointAnnotation = invocation?.method()?.getAnnotation(EndpointTag::class.java)
        val endpoint = endpointAnnotation?.value?.objectInstance ?: run {
            checkNotNull(request.tag(Endpoint::class.java)) { "Missing endpoint tag" }
        }

        val requestHandle = addRequest(endpoint, originator)

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            addResponse(requestHandle, isSuccess = false)
            throw e
        }

        addResponse(requestHandle, isSuccess = response.code in 200..399)

        return response
    }

    /**
     * Adds a request to the statistics without intercepting it
     */
    fun addExternalRequest(originator: Originator, endpoint: Endpoint, isSuccess: Boolean) {
        if (!endpoint.isCounted) return

        lock.withLock {
            val bucket = getCurrentBucket()
            val counts = bucket.getRequestCounts(endpoint, originator)
            if (isSuccess) {
                counts.successCount++
            } else {
                counts.failureCount++
            }
        }
    }

    private fun addRequest(endpoint: Endpoint, originator: Originator): MutableRequestCounts? {
        if (!endpoint.isCounted) return null

        return lock.withLock {
            val bucket = getCurrentBucket()
            bucket.getRequestCounts(endpoint, originator).also {
                it.pendingCount++
            }
        }
    }

    private fun addResponse(counts: MutableRequestCounts?, isSuccess: Boolean) {
        if (counts == null) return

        lock.withLock {
            counts.pendingCount--
            if (isSuccess) {
                counts.successCount++
            } else {
                counts.failureCount++
            }
        }
    }

    private fun MutableBucket.getRequestCounts(endpoint: Endpoint, originator: Originator): MutableRequestCounts {
        return requests
            .getOrPut(originator) { mutableMapOf() }
            .getOrPut(endpoint) { MutableRequestCounts() }
    }

    private fun getCurrentBucket(): MutableBucket {
        val epochSeconds = Instant.now().epochSecond
        val bucketIndex = getBucketIndex(epochSeconds)
        val bucket = mutableBuckets[bucketIndex]
        return if (bucket?.epochSeconds == epochSeconds) {
            bucket
        } else {
            MutableBucket(epochSeconds).also {
                mutableBuckets[bucketIndex] = it
            }
        }
    }

    private fun getBucketIndex(epochSeconds: Long): Int {
        return Math.floorMod(epochSeconds, MAX_BUCKETS.toLong()).toInt()
    }

    private fun getBucketsSnapshot(): List<Bucket> {
        return lock.withLock {
            mutableBuckets
                .filterNotNull()
                .sortedBy { it.epochSeconds }
                .map { bucket ->
                    Bucket(
                        epochSeconds = bucket.epochSeconds,
                        requests = bucket.requests.flatMap { originatorEntry ->
                            originatorEntry.value.map { endpointEntry ->
                                RequestCounts(
                                    endpoint = endpointEntry.key,
                                    originator = originatorEntry.key,
                                    successCount = endpointEntry.value.successCount,
                                    failureCount = endpointEntry.value.failureCount,
                                    pendingCount = endpointEntry.value.pendingCount,
                                )
                            }
                        },
                    )
                }
        }
    }

    companion object {
        const val MAX_BUCKETS = 120
        private val PUBLISH_INTERVAL = 100.milliseconds
    }
}
