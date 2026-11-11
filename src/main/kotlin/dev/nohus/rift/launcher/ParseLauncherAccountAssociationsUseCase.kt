package dev.nohus.rift.launcher

import dev.nohus.rift.utils.get
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.io.IOException
import java.nio.file.Path
import kotlin.io.encoding.Base64
import kotlin.io.path.readText

private val logger = KotlinLogging.logger {}

@Single
class ParseLauncherAccountAssociationsUseCase {

    data class LauncherAssociation(
        val sourceFile: Path,
        val accountId: Int,
        val characterId: Int,
    )

    operator fun invoke(logFile: Path): List<LauncherAssociation> {
        return try {
            readLauncherAssociations(logFile, logFile.readText())
        } catch (e: IOException) {
            logger.error(e) { "Failed reading launcher log: ${logFile.fileName}" }
            emptyList()
        }
    }

    private fun readLauncherAssociations(sourceFile: Path, log: String): List<LauncherAssociation> {
        return launcherClientStartupRegex.findAll(log).mapNotNull { match ->
            val launcherData = try {
                Base64.UrlSafe.decode(match["launcherData"]).toString(Charsets.UTF_8)
            } catch (e: IllegalArgumentException) {
                logger.error(e) { "Failed decoding LauncherData" }
                return@mapNotNull null
            }
            val launcherDataMatch = launcherDataRegex.matchEntire(launcherData) ?: return@mapNotNull null

            LauncherAssociation(
                sourceFile = sourceFile,
                accountId = launcherDataMatch["accountId"].toInt(),
                characterId = launcherDataMatch["characterId"].toInt(),
            )
        }.toList()
    }

    private companion object {
        val launcherClientStartupRegex = (
            """\[client-manager] Starting game client\s+\{""" +
                """[^}]*\bproduct:\s*'eve-online',""" +
                """[^}]*}\s*\[""" +
                """[^]]*'/LauncherData=(?<launcherData>[A-Za-z0-9+/=]+)'""" +
                """[^]]*]"""
            ).toRegex()

        val launcherDataRegex = """
            eve-online:(?:[^:]*:)*(?<accountId>\d+):(?<characterId>\d+)
        """.trimIndent().toRegex()
    }
}
