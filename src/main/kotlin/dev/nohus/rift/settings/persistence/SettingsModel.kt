package dev.nohus.rift.settings.persistence

import androidx.compose.ui.graphics.Color
import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.assets.AssetFilterDefinition
import dev.nohus.rift.repositories.RoutePreference
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Assets
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Clones
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Colonies
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Incursions
import dev.nohus.rift.settings.persistence.MapSystemInfoType.IntelHostiles
import dev.nohus.rift.settings.persistence.MapSystemInfoType.JoveObservatories
import dev.nohus.rift.settings.persistence.MapSystemInfoType.MetaliminalStorms
import dev.nohus.rift.settings.persistence.MapSystemInfoType.RaidableSkyhooks
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Security
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Sovereignty
import dev.nohus.rift.settings.persistence.MapSystemInfoType.SovereigntyCapitalSystem
import dev.nohus.rift.settings.persistence.MapSystemInfoType.SovereigntyUpgrades
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Standings
import dev.nohus.rift.settings.persistence.MapSystemInfoType.Wormholes
import dev.nohus.rift.standings.StandingsRepository.Standings
import dev.nohus.rift.utils.Pos
import dev.nohus.rift.utils.Size
import dev.nohus.rift.windowing.WindowManager.RiftWindow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SettingsModel(
    val languageTag: String? = null,
    val eveLogsDirectory: String? = null,
    val launcherLogsDirectory: String? = null,
    val eveSettingsDirectory: String? = null,
    val eveSharedCacheDirectory: String? = null,
    val intelMap: IntelMap = IntelMap(),
    val authenticatedCharacters: Map<Int, SsoAuthentication> = emptyMap(),
    val intelChannels: List<IntelChannel> = emptyList(),
    val isRememberOpenWindows: Boolean = false,
    val isRememberWindowPlacement: Boolean = true,
    val openWindows: Set<RiftWindow> = emptySet(),
    val windowSettings: Map<RiftWindow, List<WindowSettings>> = emptyMap(),
    val notificationEditPosition: Pos? = null,
    val notificationPosition: Pos? = null,
    val notificationsTab: NotificationsTab = NotificationsTab.Rift,
    val isUsingCompactNotificationsHistoryMode: Boolean = false,
    val excludedEveNotificationTypes: Set<String> = emptySet(),
    val excludedEveNotificationAlertTypes: Set<String> = emptySet(),
    val eveNotificationLastKnownTimestamps: Map<Int, Long> = emptyMap(),
    val alerts: List<Alert> = emptyList(),
    val isSetupWizardFinished: Boolean = false,
    val isShowSetupWizardOnNextStart: Boolean = false,
    val isDisplayEveTime: Boolean = false,
    val isShowIskCents: Boolean = false,
    val jabberJidLocalPart: String? = null,
    val jabberPassword: String? = null,
    val jabberCollapsedGroups: List<String> = emptyList(),
    val jabberIsUsingBiggerFontSize: Boolean = false,
    val jabberConferences: Set<String> = emptySet(),
    val isDemoMode: Boolean = false,
    val isSettingsReadFailure: Boolean = false,
    val intelReports: IntelReports = IntelReports(),
    val intelScanner: IntelScanner = IntelScanner(),
    val intelFeed: IntelFeed = IntelFeed(),
    val soundsVolume: Int = 100,
    val uiSoundsVolume: Int = 100,
    val alertGroups: Set<String> = emptySet(),
    val configurationPack: ConfigurationPack? = null,
    val isConfigurationPackReminderDismissed: Boolean = false,
    val hiddenCharacterIds: List<Int> = emptyList(),
    val jumpBridgeNetwork: Map<String, String>? = null,
    val isUsingRiftAutopilotRoute: Boolean = true,
    val routePreference: RoutePreference = RoutePreference.Shortest,
    val isSettingAutopilotToAll: Boolean = false,
    val whatsNewVersion: String? = null,
    val jumpRange: JumpRange? = null,
    val overlayInfoTypes: Set<OverlayInfoType> = OverlayInfoType.entries.toSet() - setOf(
        OverlayInfoType.AnsiblexZone,
        OverlayInfoType.JumpRange,
        OverlayInfoType.Planets,
    ),
    val isOverlaySpaceTypeIconOnly: Boolean = false,
    val isOverlaySovereigntyIconOnly: Boolean = true,
    val overlayEnabledCharacterIds: List<Int> = emptyList(),
    val overlayPosition: Pos? = null,
    val hiddenOverlaySections: Set<InfoPanelSection> = emptySet(),
    val isOverlayDistanceIntelCountAlwaysShown: Boolean = false,
    val overlayDistanceIntelMaxJumps: Int = 5,
    val ansiblexCapitalSystemId: Int? = null,
    val selectedPlanetTypes: List<Int> = emptyList(),
    val selectedSovereigntyUpgradeTypes: List<Int> = emptyList(),
    val installationId: String? = null,
    val isShowingSystemDistance: Boolean = true,
    val isUsingJumpBridgesForDistance: Boolean = false,
    val intelExpireSeconds: Int = 300,
    val standings: Standings = Standings(),
    val planetaryIndustry: PlanetaryIndustry = PlanetaryIndustry(),
    val isShowingCharactersClones: Boolean = true,
    val planetaryIndustryTriggeredAlerts: Map<String, Map<String, Long>> = emptyMap(),
    val pushover: Pushover = Pushover(),
    val ntfy: Ntfy = Ntfy(),
    val skipSplashScreen: Boolean = false,
    val dismissedWarnings: List<String> = emptyList(),
    val uiScale: Float = 1f,
    val accountAssociations: Map<Int, Int> = emptyMap(),
    val isWindowTransparencyEnabled: Boolean = false,
    val windowTransparencyModifier: Float = 1f,
    val isSmartAlwaysAbove: Boolean = false,
    val mapMarkers: List<MapMarker> = emptyList(),
    val mapMarkerGroups: Set<String> = emptySet(),
    val assetLocationPins: Map<Long, LocationPinStatus> = emptyMap(),
    val assetLocationCustomNames: Map<Long, String> = emptyMap(),
    val isJukeboxRevealed: Boolean = false,
    val sovereigntyUpgrades: Map<String, List<Int>> = emptyMap(),
    val isSovereigntyUpgradesHackImportingEnabled: Boolean = true,
    val isSovereigntyUpgradesHackImportingOfflineEnabled: Boolean = false,
    val killmailPosting: KillmailPosting = KillmailPosting.Dialog,
    val killmailPostingDelay: Int = 0,
    val preferredExternalServices2: List<ExternalService> = emptyList(),
    val corpWalletDivisionNames: Map<Int, Map<Int, String>> = emptyMap(),
    val walletTimeSpanSeconds: Long? = null,
    val newVersionSeenTimestamp: Long? = null,
    val characterPortraits: CharacterPortraits = CharacterPortraits(),
    val isZkillboardMonitoringEnabled: Boolean = true,
    val chatWindows: Map<
        @Serializable(with = UuidSerializer::class)
        UUID,
        ChatWindowState,
        > = emptyMap(),
    val isEquinoxMockingEnabled: Boolean = false,
    val sentryTimestamps: List<Long> = emptyList(),
    val forbiddenStructures: Map<Int, Set<Long>> = emptyMap(),
    val isShowingChatPortraits: Boolean = true,
    val isShowingChatStandings: Boolean = false,
    val isShowingPaps: Boolean? = null,
    val assetFilters: List<AssetFilterDefinition> = emptyList(),
)

@Serializable
enum class NotificationsTab {
    Rift,
    Eve,
}

@Serializable
enum class KillmailPosting {
    DoNothing,
    Dialog,
    Automatic,
}

@Serializable
data class ChatWindowState(
    val openChannels: List<Channel> = emptyList(),
    val selectedChannel: Channel? = null,
)

@Serializable
data class Channel(
    val characterId: Int?,
    val name: String,
)

@Serializable
enum class MapType {
    NewEden,
    Region,
    Distance,
}

@Serializable
sealed interface MapOpenedTab {
    @Serializable
    @SerialName("ClusterSystemsMap")
    data class ClusterSystemsMap(val is2D: Boolean) : MapOpenedTab

    @Serializable
    @SerialName("ClusterRegionsMap")
    data object ClusterRegionsMap : MapOpenedTab

    @Serializable
    @SerialName("RegionMap")
    data class RegionMap(val layoutId: Int) : MapOpenedTab

    @Serializable
    @SerialName("DistanceMap")
    data class DistanceMap(val centerSystemId: Int, val followingCharacterId: Int?, val distance: Int) : MapOpenedTab
}

@Serializable
enum class MapSystemInfoType {
    StarColor,
    Security,
    NullSecurity,
    IntelHostiles,
    Jumps,
    Kills,
    NpcKills,
    Assets,
    AssetSafety,
    Clones,
    Incursions,
    Stations,
    FactionWarfare,
    Sovereignty,
    SovereigntyUpgrades,
    SovereigntyStrategicIndex,
    SovereigntyMilitaryIndex,
    SovereigntyIndustrialIndex,
    SovereigntyActivityDefenseMultiplier,
    SovereigntyCapitalSystem,
    RaidableSkyhooks,
    MetaliminalStorms,
    JumpRange,
    AnsiblexZones,
    Planets,
    JoveObservatories,
    Wormholes,
    Colonies,
    Standings,
    RatsType,
    AsteroidBelts,
    IceFields,
    Region,
    Constellation,
    IndustryIndexCopying,
    IndustryIndexInvention,
    IndustryIndexManufacturing,
    IndustryIndexReaction,
    IndustryIndexMaterialEfficiency,
    IndustryIndexTimeEfficiency,
}

@Serializable
enum class OverlayInfoType {
    System,
    Constellation,
    Region,
    LocalRats,
    SpaceType,
    Sovereignty,
    MapMarkers,
    Jumps,
    Kills,
    NpcKills,
    Assets,
    Stations,
    AsteroidBelts,
    IceFields,
    Colonies,
    JoveObservatories,
    AnsiblexZone,
    JumpRange,
    Clones,
    Planets,
    SovereigntyUpgrades,
    RaidableSkyhooks,
}

@Serializable
data class IntelMap(
    val isUsingCompactMode: Boolean = false,
    val mapTypeSystemColor: Map<MapType, MapSystemInfoType> = mapOf(
        MapType.NewEden to Security,
        MapType.Region to Security,
        MapType.Distance to IntelHostiles,
    ),
    val mapTypeBackgroundColor: Map<MapType, MapSystemInfoType?> = mapOf(
        MapType.NewEden to null,
        MapType.Region to null,
        MapType.Distance to null,
    ),
    val mapTypeIndicatorInfoTypes: Map<MapType, List<MapSystemInfoType>> = mapOf(
        MapType.NewEden to listOf(Assets, Clones, Incursions, SovereigntyUpgrades, MetaliminalStorms, Colonies, SovereigntyCapitalSystem),
        MapType.Region to listOf(Assets, Clones, Incursions, SovereigntyUpgrades, MetaliminalStorms, Colonies, SovereigntyCapitalSystem),
        MapType.Distance to listOf(),
    ),
    val mapTypeInfoBoxInfoTypes: Map<MapType, List<MapSystemInfoType>> = mapOf(
        MapType.NewEden to listOf(Security, Assets, Clones, Incursions, Sovereignty, SovereigntyUpgrades, RaidableSkyhooks, MetaliminalStorms, JoveObservatories, Wormholes, Colonies, Standings, SovereigntyCapitalSystem),
        MapType.Region to listOf(Security, Assets, Clones, Incursions, Sovereignty, SovereigntyUpgrades, RaidableSkyhooks, MetaliminalStorms, JoveObservatories, Wormholes, Colonies, Standings, SovereigntyCapitalSystem),
        MapType.Distance to listOf(Security, Assets, Clones, Incursions, Sovereignty, SovereigntyUpgrades, RaidableSkyhooks, MetaliminalStorms, JoveObservatories, Wormholes, Colonies, Standings, SovereigntyCapitalSystem),
    ),
    val intelPopupTimeoutSeconds: Int = 60,
    val isFollowingCharacterAcrossLayouts: Boolean = true,
    val isFollowingCharacterWithinLayouts: Boolean = true,
    val isInvertZoom: Boolean = false,
    val isJumpBridgeNetworkShown: Boolean = true,
    val jumpBridgeNetworkOpacity: Int = 100,
    val openedTabs2: Map<
        @Serializable(with = UuidSerializer::class)
        UUID,
        MapOpenedTab,
        > = emptyMap(),
    val isAlwaysShowingSystems: Boolean = false,
    val isPreferringRegionMaps: Boolean = true,
    val isUsing2DClusterLayout: Boolean = true,
)

@Serializable
data class SsoAuthentication(
    val accessToken: String,
    val refreshToken: String,
    val expiration: Long,
    /**
     * The default list are the scopes requested before optional scopes were added, meaning all previous access tokens
     * have these scopes.
     */
    val scopes: List<String> = [
        "esi-location.read_online.v1",
        "esi-location.read_location.v1",
        "esi-universe.read_structures.v1",
        "esi-ui.write_waypoint.v1",
        "esi-wallet.read_character_wallet.v1",
        "esi-search.search_structures.v1",
        "esi-assets.read_assets.v1",
        "esi-alliances.read_contacts.v1",
        "esi-corporations.read_contacts.v1",
        "esi-characters.read_contacts.v1",
        "esi-characters.write_contacts.v1",
        "esi-clones.read_clones.v1",
        "esi-clones.read_implants.v1",
        "esi-planets.manage_planets.v1",
    ],
)

@Serializable
data class IntelChannel(
    val name: String,
    val region: String?,
)

@Serializable
data class WindowSettings(
    @Serializable(with = UuidSerializer::class)
    val uuid: UUID,
    val position: Pos? = null,
    val size: Size? = null,
    val isAlwaysOnTop: Boolean = false,
    val isLocked: Boolean = false,
    val isTransparent: Boolean = false,
)

@Serializable
data class IntelReports(
    val isUsingCompactMode: Boolean = false,
    val isUsingReverseOrder: Boolean = false,
    val isShowingReporter: Boolean = true,
    val isShowingChannel: Boolean = true,
    val isShowingRegion: Boolean = false,
)

@Serializable
data class IntelScanner(
    val isUsingCompactMode: Boolean = false,
    val isShareHintShown: Boolean = false,
    val maxLocalScans: Int = 3,
    val maxDScans: Int = 3,
    val isShowingDScanShipMass: Boolean = true,
    val isOpenOnClipboardDScanEnabled: Boolean = false,
    val isShowingShipsInDScanShipClasses: Boolean = true,
    val enabledDScanSections: List<IntelScannerDScanSection> = IntelScannerDScanSection.entries,
    val collapsedDScanSections: List<IntelScannerDScanSection> = emptyList(),
    val isShowingSummary: Boolean = true,
    val isShowingCharactersSummary: Boolean = true,
    val isShowingCorporationsSummary: Boolean = false,
    val maxCorporationsInSummary: Int = 10,
    val isShowingAlliancesSummary: Boolean = true,
    val maxAlliancesInSummary: Int = 10,
    val isShowingFactionWarfareSummary: Boolean = true,
    val isOpenOnClipboardCharacterListEnabled: Boolean = false,
    val ignoredCharacters: List<String> = emptyList(),
    val ignoredFactionIds: List<Int> = emptyList(),
    val isIgnoringCharactersWithPositiveStandings: Boolean = true,
    val isIgnoringOwnCharacters: Boolean = true,
    val isShowingCorpWithCharacter: Boolean = true,
    val isShowingAllianceWithCharacter: Boolean = true,
    val isShowingTickersWithCharacter: Boolean = true,
    val isShowingCorporationName: Boolean = true,
    val isShowingCorporationTicker: Boolean = true,
    val isShowingAllianceName: Boolean = true,
    val isShowingAllianceTicker: Boolean = true,
    val isShowingFactionName: Boolean = false,
    val isShowingAverageGangSize: Boolean = true,
    val isShowingKillDeathTotals: Boolean = true,
    val isShowingFleetCommanderScore: Boolean = true,
    val isShowingAwoxCount: Boolean = true,
    val isShowingBaitCount: Boolean = true,
    val isShowingGankerCount: Boolean = true,
    val topShipsCount: Int = 5,
    val recentShipsCount: Int = 5,
    val affiliatesCount: Int = 5,
    val affiliatesMinimumKills: Int = 3,
    val enabledColumns: List<IntelScannerColumn> = listOf(
        IntelScannerColumn.Character,
        IntelScannerColumn.Age,
        IntelScannerColumn.Faction,
        IntelScannerColumn.Security,
        IntelScannerColumn.Danger,
        IntelScannerColumn.Gang,
        IntelScannerColumn.SoloKills,
        IntelScannerColumn.KillDeathRatio,
        IntelScannerColumn.GroupSize,
        IntelScannerColumn.Cyno,
        IntelScannerColumn.Space,
        IntelScannerColumn.Isk,
        IntelScannerColumn.Tags,
        IntelScannerColumn.FleetCommander,
        IntelScannerColumn.RecentShips,
        IntelScannerColumn.Groups,
        IntelScannerColumn.Partners,
        IntelScannerColumn.AwoxKills,
        IntelScannerColumn.Bait,
        IntelScannerColumn.Ganker,
    ),
    val columnOrder: List<IntelScannerColumn> = IntelScannerColumn.entries,
    val sortingColumn: IntelScannerColumn? = IntelScannerColumn.Danger,
    val sortingDirection: IntelScannerSortDirection = IntelScannerSortDirection.Descending,
)

@Serializable
enum class IntelScannerDScanSection {
    ShipsByClass,
    Ships,
    Structures,
    Stations,
    CelestialBodies,
    Stargates,
    Celestials,
    Wrecks,
    Drones,
    Corpses,
    Other,
}

@Serializable
enum class IntelScannerColumn {
    Character,
    Corporation,
    Alliance,
    Faction,
    Age,
    Danger,
    Security,
    Gang,
    SoloKills,
    KillDeathRatio,
    GroupSize,
    Space,
    Isk,
    Tags,
    RecentShips,
    TopShips,
    Groups,
    Partners,
    Cyno,
    FleetCommander,
    Bait,
    Ganker,
    AwoxKills,
}

@Serializable
enum class IntelScannerSortDirection {
    Ascending,
    Descending,
}

@Serializable
data class IntelFeed(
    val isUsingCompactMode: Boolean = false,
    val locationFilters: List<LocationFilter> = [
        LocationFilter.KnownSpace,
        LocationFilter.WormholeSpace,
        LocationFilter.AbyssalSpace,
    ],
    val distanceFilter: DistanceFilter = DistanceFilter.All,
    val entityFilters: List<EntityFilter> = [
        EntityFilter.Killmails,
        EntityFilter.Characters,
        EntityFilter.Other,
    ],
    val sortingFilter: SortingFilter = SortingFilter.Time,
)

@Serializable
data class PlanetaryIndustry(
    val view: ColonyView = ColonyView.List,
    val sortingFilter: ColonySortingFilter = ColonySortingFilter.Character,
)

@Serializable
sealed interface LocationFilter {
    @Serializable
    @SerialName("KnownSpace")
    data object KnownSpace : LocationFilter

    @Serializable
    @SerialName("WormholeSpace")
    data object WormholeSpace : LocationFilter

    @Serializable
    @SerialName("AbyssalSpace")
    data object AbyssalSpace : LocationFilter

    @Serializable
    @SerialName("CurrentMapRegion")
    data object CurrentMapRegion : LocationFilter
}

@Serializable
sealed interface DistanceFilter {
    @Serializable
    @SerialName("All")
    data object All : DistanceFilter

    @Serializable
    @SerialName("CharacterLocationRegions")
    data object CharacterLocationRegions : DistanceFilter

    @Serializable
    @SerialName("WithinDistance")
    data class WithinDistance(val jumps: Int) : DistanceFilter
}

@Serializable
sealed interface EntityFilter {
    @Serializable
    @SerialName("Killmails")
    data object Killmails : EntityFilter

    @Serializable
    @SerialName("Characters")
    data object Characters : EntityFilter

    @Serializable
    @SerialName("Other")
    data object Other : EntityFilter
}

@Serializable
sealed interface SortingFilter {
    @Serializable
    @SerialName("Time")
    data object Time : SortingFilter

    @Serializable
    @SerialName("Distance")
    data object Distance : SortingFilter
}

@Serializable
sealed interface ColonyView {
    @Serializable
    @SerialName("List")
    data object List : ColonyView

    @Serializable
    @SerialName("Grid")
    data object Grid : ColonyView

    @Serializable
    @SerialName("Rows")
    data object Rows : ColonyView
}

@Serializable
sealed interface ColonySortingFilter {
    @Serializable
    @SerialName("Status")
    data object Status : ColonySortingFilter

    @Serializable
    @SerialName("Character")
    data object Character : ColonySortingFilter

    @Serializable
    @SerialName("CharacterAlphabetical")
    data object CharacterAlphabetical : ColonySortingFilter

    @Serializable
    @SerialName("ExpiryTime")
    data object ExpiryTime : ColonySortingFilter
}

@Serializable
enum class ConfigurationPack {
    Imperium,
    TheInitiative,
    PhoenixCoalition,
}

@Serializable
data class JumpRange(
    val fromId: Int,
    val distanceLy: Double,
)

@Serializable
data class Pushover(
    val apiToken: String? = null,
    val userKey: String? = null,
)

@Serializable
data class Ntfy(
    val topic: String? = null,
)

@Serializable
data class MapMarker(
    @Serializable(with = UuidSerializer::class)
    val id: UUID,
    val systemId: Int,
    val label: String,
    @Serializable(with = ColorSerializer::class)
    val color: Color?,
    val icon: String,
    val isEnabled: Boolean = true,
    val isPinned: Boolean = false,
    val group: String? = null,
)

@Serializable
enum class LocationPinStatus {
    Pinned,
    None,
    Hidden,
}

@Serializable
enum class ExternalService {
    EveWho,
    ZKillboard,
    UniWiki,
    EveRef,
    NewEdenEncyclopedia,
    Dotlan,
    Anoikis,
}

@Serializable
data class CharacterPortraits(
    val standingsEffectStrength: Float = 1f,
    val standingsTargets: CharacterPortraitsStandingsTargets = CharacterPortraitsStandingsTargets.OnlyNonNeutral,
    val parallaxStrength: CharacterPortraitsParallaxStrength = CharacterPortraitsParallaxStrength.Normal,
)

@Serializable
enum class CharacterPortraitsStandingsTargets {
    All,
    OnlyFriendly,
    OnlyHostile,
    OnlyNonNeutral,
    None,
}

@Serializable
enum class CharacterPortraitsParallaxStrength {
    None,
    Reduced,
    Normal,
}
