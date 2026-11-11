package dev.nohus.rift.network.requests

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
class CacheStatistics {

    private val mutex = Mutex()
    val requests: StateFlow<Map<BucketKey, Int>>
        field: MutableStateFlow<Map<BucketKey, Int>> = MutableStateFlow(emptyMap())

    data class BucketKey(
        val endpoint: Endpoint,
        val cacheStatus: CacheStatus,
        val responseStatus: Int?,
    )

    enum class CacheStatus {
        LocalCacheHit,
        EsiCacheHitNotModified,
        EsiCacheMissNotModified,
        EsiCacheHit,
        EsiCacheMiss,
        EsiDynamic,
        EsiRevalidated,
        EsiExpired,
        EsiNull,
        Unknown,
    }

    fun addRequest(endpoint: Endpoint, cacheStatus: CacheStatus, responseStatus: Int?) {
        runBlocking {
            mutex.withLock {
                val cacheStatus = when (cacheStatus) {
                    CacheStatus.EsiCacheHit if responseStatus == 304 -> CacheStatus.EsiCacheHitNotModified
                    CacheStatus.EsiCacheMiss if responseStatus == 304 -> CacheStatus.EsiCacheMissNotModified
                    else -> cacheStatus
                }
                val key = BucketKey(endpoint, cacheStatus, responseStatus)
                val count = requests.value.getOrDefault(key, 0) + 1
                requests.value += (key to count)
            }
        }
    }
}
