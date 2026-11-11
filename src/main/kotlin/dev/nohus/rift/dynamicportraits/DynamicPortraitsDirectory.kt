package dev.nohus.rift.dynamicportraits

import dev.nohus.rift.utils.directories.AppDirectories
import org.koin.core.annotation.Single
import java.nio.file.Path

@Single
class DynamicPortraitsDirectory(
    appDirectories: AppDirectories,
) {
    val path: Path = appDirectories.getAppCacheDirectory().resolve("portraits")
}
