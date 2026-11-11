package dev.nohus.rift.alerts.list

import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.alerts.AlertTrigger
import dev.nohus.rift.alerts.ChatMessageChannel
import dev.nohus.rift.alerts.GameActionType
import dev.nohus.rift.alerts.IntelChannel
import dev.nohus.rift.alerts.IntelReportLocation
import dev.nohus.rift.alerts.JabberMessageChannel
import dev.nohus.rift.alerts.JabberPingType

@Suppress("DEPRECATION")
val AlertsComparator = compareBy<Alert>(
    {
        !it.isEnabled
    },
    {
        when (it.trigger) {
            is AlertTrigger.IntelReported -> when (it.trigger.reportLocation) {
                is IntelReportLocation.System -> 0
                is IntelReportLocation.AnyOwnedCharacter -> 1
                is IntelReportLocation.OwnedCharacter -> 2
            }
            is AlertTrigger.GameAction -> when {
                it.trigger.actionTypes.filterIsInstance<GameActionType.InCombat>().isNotEmpty() -> 3
                it.trigger.actionTypes.filterIsInstance<GameActionType.UnderAttack>().isNotEmpty() -> 4
                it.trigger.actionTypes.filterIsInstance<GameActionType.Attacking>().isNotEmpty() -> 5
                it.trigger.actionTypes.filterIsInstance<GameActionType.BeingWarpScrambled>().isNotEmpty() -> 6
                it.trigger.actionTypes.filterIsInstance<GameActionType.Decloaked>().isNotEmpty() -> 7
                else -> 8
            }
            is AlertTrigger.PlanetaryIndustry -> 9
            is AlertTrigger.EveNotification -> 10
            is AlertTrigger.ChatMessage -> when (it.trigger.channel) {
                is ChatMessageChannel.Any -> 11
                else -> 12
            }
            is AlertTrigger.JabberPing -> when (it.trigger.pingType) {
                is JabberPingType.Fleet -> 13
                JabberPingType.Message -> 14
                is JabberPingType.Message2 -> 15
            }
            is AlertTrigger.JabberMessage -> when (it.trigger.channel) {
                JabberMessageChannel.Any -> 16
                is JabberMessageChannel.Channel -> 17
                JabberMessageChannel.DirectMessage -> 18
            }
            is AlertTrigger.NoChannelActivity -> when (it.trigger.channel) {
                IntelChannel.All -> 19
                IntelChannel.Any -> 20
                is IntelChannel.Channel -> 21
            }
        }
    },
    {
        it.id
    },
)
