package dev.nohus.rift.about

import dev.nohus.rift.BuildConfig
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.about_version_caption
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import org.koin.core.annotation.Single

@Single
class GetVersionUseCase {

    operator fun invoke(): UiText {
        @Suppress("KotlinConstantConditions")
        val suffix = BuildConfig.environment.let { if (it != "prod") "-$it" else "" }
        return Res.string.about_version_caption.toUiText("${BuildConfig.version}$suffix".toUiTextArgument())
    }
}
