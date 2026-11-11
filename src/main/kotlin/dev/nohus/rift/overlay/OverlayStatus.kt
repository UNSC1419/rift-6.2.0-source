package dev.nohus.rift.overlay

import dev.nohus.rift.characters.repositories.LocalCharactersRepository.LocalCharacter
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.settings_overlay_character_hidden
import dev.nohus.rift.generated.resources.settings_overlay_disabled
import dev.nohus.rift.generated.resources.settings_overlay_location_unavailable
import dev.nohus.rift.generated.resources.settings_overlay_native_window_unavailable
import dev.nohus.rift.generated.resources.settings_overlay_no_eve_window
import dev.nohus.rift.generated.resources.settings_overlay_ready
import dev.nohus.rift.generated.resources.settings_overlay_system_unavailable
import dev.nohus.rift.generated.resources.settings_overlay_unknown_character
import dev.nohus.rift.generated.resources.settings_overlay_window_hidden
import dev.nohus.rift.generated.resources.settings_overlay_window_minimized
import dev.nohus.rift.generated.resources.settings_overlay_window_position_unavailable
import dev.nohus.rift.generated.resources.settings_overlay_window_too_large
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.utils.evewindows.EveWindow
import dev.nohus.rift.utils.evewindows.EveWindowState

internal enum class OverlayStatus(val displayText: UiText) {
    NoEveWindow(Res.string.settings_overlay_no_eve_window.toUiText()),
    Disabled(Res.string.settings_overlay_disabled.toUiText()),
    WindowMinimized(Res.string.settings_overlay_window_minimized.toUiText()),
    WindowHidden(Res.string.settings_overlay_window_hidden.toUiText()),
    WindowPositionUnavailable(Res.string.settings_overlay_window_position_unavailable.toUiText()),
    UnknownCharacter(Res.string.settings_overlay_unknown_character.toUiText()),
    CharacterHidden(Res.string.settings_overlay_character_hidden.toUiText()),
    LocationUnavailable(Res.string.settings_overlay_location_unavailable.toUiText()),
    SystemUnavailable(Res.string.settings_overlay_system_unavailable.toUiText()),
    WindowTooLarge(Res.string.settings_overlay_window_too_large.toUiText()),
    NativeWindowUnavailable(Res.string.settings_overlay_native_window_unavailable.toUiText()),
    Ready(Res.string.settings_overlay_ready.toUiText()),
}

internal fun getOverlayStatus(
    window: EveWindow,
    isOverlayEnabled: Boolean,
    character: LocalCharacter?,
    systemId: Int?,
    isSystemAvailable: Boolean,
    runtimeFailure: OverlayWindowFailure? = null,
): OverlayStatus {
    val bounds = window.geometry.client.overlayBounds
    return when {
        !isOverlayEnabled -> OverlayStatus.Disabled
        window.state == EveWindowState.Minimized -> OverlayStatus.WindowMinimized
        window.state == EveWindowState.Hidden -> OverlayStatus.WindowHidden
        bounds == null || bounds.width <= 0 || bounds.height <= 0 -> OverlayStatus.WindowPositionUnavailable
        character == null -> OverlayStatus.UnknownCharacter
        character.isHidden -> OverlayStatus.CharacterHidden
        systemId == null -> OverlayStatus.LocationUnavailable
        !isSystemAvailable -> OverlayStatus.SystemUnavailable
        runtimeFailure?.geometry == window.getOverlayWindowGeometryKey() && runtimeFailure is OverlayWindowFailure.TooLarge ->
            OverlayStatus.WindowTooLarge

        runtimeFailure?.geometry == window.getOverlayWindowGeometryKey() && runtimeFailure is OverlayWindowFailure.RetryLater ->
            OverlayStatus.NativeWindowUnavailable

        else -> OverlayStatus.Ready
    }
}
