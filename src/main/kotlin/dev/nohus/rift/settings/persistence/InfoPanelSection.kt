package dev.nohus.rift.settings.persistence

import kotlinx.serialization.Serializable

@Serializable
enum class InfoPanelSection {
    SystemInfo,
    DistanceIntel,
    RouteIntel,
}
