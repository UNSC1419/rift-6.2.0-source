package dev.nohus.rift.compose.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.platform.Font
import dev.nohus.rift.charactersettings.EveResourceResolver
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.directories.AppDirectories
import dev.nohus.rift.utils.directories.DetectEveSharedCacheDirectoryUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isRegularFile
import kotlin.io.path.readBytes
import androidx.compose.ui.text.font.Font as ComposeFont

private val logger = KotlinLogging.logger {}

@Single
class EveFontsRepository(
    private val settings: Settings,
    private val appDirectories: AppDirectories,
    private val detectEveSharedCacheDirectoryUseCase: DetectEveSharedCacheDirectoryUseCase,
    private val eveResourceResolver: EveResourceResolver,
) {

    data class Fonts(
        val eveSansNeue: FontFamily = FontFamily.SansSerif,
        val triglavian: FontFamily = FontFamily.SansSerif,
        val regularFontBytes: ByteArray? = null,
    )

    private enum class FontGroup {
        EveSansNeue,
        Triglavian,
    }

    private enum class FontResource(
        val fileName: String,
        val group: FontGroup,
        val weight: FontWeight,
        val style: FontStyle,
    ) {
        EveRegular("evesansneue-regular.otf", FontGroup.EveSansNeue, FontWeight.Normal, FontStyle.Normal),
        EveItalic("evesansneue-italic.otf", FontGroup.EveSansNeue, FontWeight.Normal, FontStyle.Italic),
        EveBold("evesansneue-bold.otf", FontGroup.EveSansNeue, FontWeight.Bold, FontStyle.Normal),
        EveBoldItalic("evesansneue-bolditalic.otf", FontGroup.EveSansNeue, FontWeight.Bold, FontStyle.Italic),
        Triglavian("triglavian.ttf", FontGroup.Triglavian, FontWeight.Normal, FontStyle.Normal),
    }

    private data class FontFile(val path: Path, val modified: Long, val size: Long)

    private data class LoadedFont(val font: ComposeFont, val bytes: ByteArray)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _state = MutableStateFlow(Fonts())
    val state = _state.asStateFlow()

    private val copiedFiles = mutableMapOf<FontResource, FontFile>()
    private val fontsDirectory by lazy { appDirectories.getAppDataDirectory().resolve("fonts") }
    private val loadedFonts = mutableMapOf<FontResource, LoadedFont>()

    fun initialize() {
        loadStoredFonts()
        try {
            detectEveSharedCacheDirectoryUseCase()
        } catch (e: Exception) {
            logger.warn(e) { "Could not discover EVE fonts" }
        }
        refreshFonts()
        scope.launch {
            settings.updateFlow.map { it.eveSharedCacheDirectory }.distinctUntilChanged().collect {
                refreshFonts()
            }
        }
    }

    private fun loadStoredFonts() {
        for (resource in FontResource.entries) {
            try {
                val path = fontsDirectory.resolve(resource.fileName)
                if (!path.isRegularFile()) continue
                val font = loadFont(resource, readFontFile(path)) ?: continue
                loadedFonts[resource] = font
            } catch (e: Exception) {
                logger.warn(e) { "Could not load stored EVE font ${resource.fileName}" }
            }
        }
        publishFonts()
    }

    private fun refreshFonts() {
        for (resource in FontResource.entries) {
            try {
                val path = eveResourceResolver("res:/ui/fonts/${resource.fileName}")?.takeIf { it.isRegularFile() } ?: continue
                val file = readFontFile(path)
                if (copiedFiles[resource] == file) continue
                val font = copyFont(resource, file) ?: continue
                loadedFonts[resource] = font
                copiedFiles[resource] = file
            } catch (e: Exception) {
                logger.warn(e) { "Could not copy EVE font ${resource.fileName}" }
            }
        }
        publishFonts()
    }

    private fun copyFont(resource: FontResource, source: FontFile): LoadedFont? {
        val directory = fontsDirectory.createDirectories()
        val temporaryFile = Files.createTempFile(directory, ".${resource.fileName}-", ".tmp")
        try {
            Files.copy(source.path, temporaryFile, REPLACE_EXISTING)
            val font = loadFont(resource, readFontFile(temporaryFile)) ?: return null
            Files.move(temporaryFile, directory.resolve(resource.fileName), ATOMIC_MOVE, REPLACE_EXISTING)
            return font
        } finally {
            temporaryFile.deleteIfExists()
        }
    }

    private fun readFontFile(path: Path): FontFile {
        return FontFile(path, path.getLastModifiedTime().toMillis(), Files.size(path))
    }

    private fun publishFonts() {
        val eveFamily = loadFontFamily(FontGroup.EveSansNeue)
        val triglavianFamily = loadFontFamily(FontGroup.Triglavian)
        val previous = _state.value
        _state.value = Fonts(
            eveSansNeue = eveFamily ?: previous.eveSansNeue,
            triglavian = triglavianFamily ?: previous.triglavian,
            regularFontBytes = loadedFonts[FontResource.EveRegular]?.bytes ?: previous.regularFontBytes,
        )
    }

    private fun loadFont(resource: FontResource, file: FontFile): LoadedFont? {
        return try {
            val bytes = file.path.readBytes()
            Data.makeFromBytes(bytes).use { data ->
                requireNotNull(FontMgr.default.makeFromData(data)) { "Invalid font data" }.close()
            }
            val font = Font(
                identity = "$file",
                data = bytes,
                weight = resource.weight,
                style = resource.style,
            )
            createFontFamilyResolver().resolve(
                fontFamily = FontFamily(font),
                fontWeight = resource.weight,
                fontStyle = resource.style,
            ).value
            LoadedFont(font, bytes)
        } catch (e: Exception) {
            logger.warn(e) { "Could not load EVE font ${file.path}" }
            null
        }
    }

    private fun loadFontFamily(group: FontGroup): FontFamily? {
        val resources = FontResource.entries.filter { it.group == group }
        val regularFont = resources.single { it.weight == FontWeight.Normal && it.style == FontStyle.Normal }
        if (loadedFonts[regularFont] == null) return null
        return FontFamily(resources.mapNotNull { loadedFonts[it]?.font })
    }
}
