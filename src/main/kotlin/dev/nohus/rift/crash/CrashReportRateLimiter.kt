package dev.nohus.rift.crash

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import java.time.Duration
import java.time.Instant
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readLines

class CrashReportRateLimiter(
    private val getDirectory: () -> Path,
) {
    private val maxReportsPerDay = 5

    @Synchronized
    fun tryAcquireReport(): Boolean {
        return try {
            val directory = getDirectory()
            val ledger = directory.resolve("sentry-report-timestamps")
            FileChannel.open(directory.resolve("sentry-report-timestamps.lock"), CREATE, WRITE).use { channel ->
                channel.lock().use {
                    val timestamps = try {
                        ledger.readLines().map { it.toLong() }.also {
                            require(it.isNotEmpty() && it.size <= maxReportsPerDay && it.all { timestamp -> timestamp > 0 })
                        }
                    } catch (_: NoSuchFileException) {
                        emptyList()
                    }
                    val now = Instant.now()
                    val dayAgo = (now - Duration.ofDays(1)).toEpochMilli()
                    val recentTimestamps = timestamps.filter { it > dayAgo }
                    if (recentTimestamps.size >= maxReportsPerDay) return false

                    val temporaryFile = Files.createTempFile(directory, ".sentry-report-timestamps-", ".pending")
                    try {
                        val bytes = (recentTimestamps + now.toEpochMilli()).joinToString("\n").toByteArray(Charsets.UTF_8)
                        FileChannel.open(temporaryFile, WRITE).use { output ->
                            val buffer = ByteBuffer.wrap(bytes)
                            while (buffer.hasRemaining()) output.write(buffer)
                            output.force(true)
                        }
                        Files.move(temporaryFile, ledger, ATOMIC_MOVE, REPLACE_EXISTING)
                    } finally {
                        temporaryFile.deleteIfExists()
                    }
                    true
                }
            }
        } catch (e: Exception) {
            println("Couldn't reserve crash report rate limit; withholding report: $e")
            false
        }
    }
}
