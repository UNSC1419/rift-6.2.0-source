package dev.nohus.rift.network.zkillboard

import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.annotation.Single
import java.io.InterruptedIOException
import java.time.Duration
import java.util.ArrayDeque
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Single
class ZkillboardRateLimitInterceptor : Interceptor {

    private val requestLimit = 250
    private val requestLimitPeriod = Duration.ofMinutes(1)
    private val banDuration = Duration.ofMinutes(5)

    private val lock = ReentrantLock()
    private val permitChanged = lock.newCondition()
    private val requestTimestamps = ArrayDeque<Long>().apply {
        // Starting with 150 of the quota already depleted for safety
        val now = System.nanoTime()
        repeat(150) {
            addLast(now)
        }
    }
    private var blockedUntilNanos = 0L

    override fun intercept(chain: Interceptor.Chain): Response {
        awaitPermit()

        return chain.proceed(chain.request()).also { response ->
            if (response.code == 429) {
                blockRequestsFor(banDuration)
            }
        }
    }

    private fun awaitPermit() {
        lock.withLock {
            while (true) {
                val now = System.nanoTime()
                val blockedWaitNanos = blockedUntilNanos - now
                if (blockedWaitNanos > 0) {
                    awaitPermitChange(blockedWaitNanos)
                    continue
                }

                val periodNanos = requestLimitPeriod.toNanos()
                while (requestTimestamps.isNotEmpty() && now - requestTimestamps.first() >= periodNanos) {
                    requestTimestamps.removeFirst()
                }

                if (requestTimestamps.size < requestLimit) {
                    requestTimestamps.addLast(now)
                    return
                }

                val nextPermitAtNanos = requestTimestamps.first() + periodNanos
                awaitPermitChange(nextPermitAtNanos - now)
            }
        }
    }

    private fun awaitPermitChange(waitNanos: Long) {
        try {
            permitChanged.awaitNanos(waitNanos)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedIOException("Interrupted while waiting for the zKillboard rate limit")
        }
    }

    private fun blockRequestsFor(duration: Duration) {
        lock.withLock {
            val blockedUntil = System.nanoTime() + duration.toNanos()
            blockedUntilNanos = maxOf(blockedUntilNanos, blockedUntil)
            permitChanged.signalAll()
        }
    }
}
