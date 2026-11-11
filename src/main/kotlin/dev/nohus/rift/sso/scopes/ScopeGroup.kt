package dev.nohus.rift.sso.scopes

import dev.nohus.rift.localization.UiText

data class ScopeGroup(
    val name: UiText,
    val reasons: List<UiText>,
    val scopes: List<EsiScope>,
    val isRequired: Boolean = false,
)
