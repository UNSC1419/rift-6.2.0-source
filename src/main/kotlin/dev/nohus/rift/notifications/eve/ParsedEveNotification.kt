package dev.nohus.rift.notifications.eve

import dev.nohus.rift.compose.text.FormattedText

data class ParsedEveNotification(
    val subject: FormattedText?,
    val body: FormattedText?,
    val data: Map<String, Any?>,
)
