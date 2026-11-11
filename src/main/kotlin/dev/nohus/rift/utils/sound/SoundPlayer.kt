package dev.nohus.rift.utils.sound

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.settings.persistence.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import java.io.IOException
import java.io.InputStream
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.util.concurrent.Executors
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isReadable

private val logger = KotlinLogging.logger {}

@Single
class SoundPlayer(
    private val settings: Settings,
) {

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val playSubmission = Channel<PlaySubmission>()

    enum class SoundSource {
        Alert,
        Ui,
    }

    private data class PlaySubmission(
        val inputStream: InputStream,
        val volume: Float,
        val source: SoundSource,
    )

    private fun getVolume(source: SoundSource): Int {
        return when (source) {
            SoundSource.Alert -> settings.soundsVolume
            SoundSource.Ui -> settings.uiSoundsVolume
        }
    }

    fun start() = scope.launch {
        while (true) {
            val submission = playSubmission.receive()
            val volume = getVolume(submission.source)
            val gain = submission.volume * volume / 100f
            val player = OpenAlPlayer()

            val playingJobs = mutableListOf<Job>()
            val playingJobsMutex = Mutex()

            val initialJob = launch {
                player.play(submission.inputStream, gain)
            }
            playingJobsMutex.withLock {
                playingJobs += initialJob
            }
            val concurrentJobsListener = launch {
                while (true) {
                    val submission = playSubmission.receive()
                    val volume = getVolume(submission.source)
                    val gain = submission.volume * volume / 100f
                    val job = launch {
                        player.play(submission.inputStream, gain)
                    }
                    playingJobsMutex.withLock {
                        playingJobs += job
                    }
                }
            }
            while (true) {
                val activeJobs = playingJobsMutex.withLock {
                    playingJobs.filter { it.isActive }
                }
                if (activeJobs.isEmpty()) break
                activeJobs.joinAll()
            }
            concurrentJobsListener.cancelAndJoin()
            player.shutdown()
        }
    }

    fun play(soundResource: String, source: SoundSource, volume: Float = 1f) = scope.launch {
        if (getVolume(source) <= 0) return@launch
        val inputStream = Res.readBytes("files/sounds/$soundResource").inputStream()
        play(inputStream, volume, source)
    }

    fun play(sound: UiSound): Job {
        return play(sound.resource, SoundSource.Ui, sound.baseVolume)
    }

    fun playFile(path: String, source: SoundSource) = scope.launch {
        try {
            val file = Path.of(path)
            if (!file.exists() || !file.isReadable()) return@launch
            play(file.inputStream(), 1f, source)
        } catch (e: InvalidPathException) {
            logger.error { "Could not play file with in invalid path: \"$path\"" }
        } catch (e: IOException) {
            logger.error(e) { "Could not play file due to IO exception" }
        }
    }

    private suspend fun play(inputStream: InputStream, volume: Float, source: SoundSource) {
        playSubmission.send(PlaySubmission(inputStream, volume, source))
    }
}
