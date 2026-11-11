package dev.nohus.rift.utils.entitylinks

import dev.nohus.rift.assets.AssetsExternalControl
import dev.nohus.rift.assets.IsDockableTypeUseCase
import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.MulticolorIconType
import dev.nohus.rift.compose.RiftMulticolorIcon
import dev.nohus.rift.contacts.ContactsExternalControl
import dev.nohus.rift.contacts.ContactsRepository
import dev.nohus.rift.contacts.ContactsRepository.EntityType
import dev.nohus.rift.game.AutopilotController
import dev.nohus.rift.game.GameUiController
import dev.nohus.rift.game.GameUiController.LinkCategory
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.entity_interaction_add_contact
import dev.nohus.rift.generated.resources.entity_interaction_add_marker
import dev.nohus.rift.generated.resources.entity_interaction_add_waypoint
import dev.nohus.rift.generated.resources.entity_interaction_autopilot_all_characters
import dev.nohus.rift.generated.resources.entity_interaction_clear_autopilot
import dev.nohus.rift.generated.resources.entity_interaction_copy_name
import dev.nohus.rift.generated.resources.entity_interaction_edit_contact
import dev.nohus.rift.generated.resources.entity_interaction_set_destination
import dev.nohus.rift.generated.resources.entity_interaction_show_assets
import dev.nohus.rift.generated.resources.entity_interaction_show_info
import dev.nohus.rift.generated.resources.entity_interaction_show_on_cluster_map
import dev.nohus.rift.generated.resources.entity_interaction_show_on_map
import dev.nohus.rift.generated.resources.entity_interaction_show_on_region_map
import dev.nohus.rift.generated.resources.indicator_assets
import dev.nohus.rift.generated.resources.map_marker_place_bookmark
import dev.nohus.rift.generated.resources.menu_add
import dev.nohus.rift.generated.resources.menu_set_destination
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.map.MapExternalControl
import dev.nohus.rift.map.MapViewModel.MapType
import dev.nohus.rift.map.markers.MapMarkersInputModel
import dev.nohus.rift.repositories.ExternalServiceRepository
import dev.nohus.rift.repositories.IdRanges
import dev.nohus.rift.repositories.MapStatusRepository
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.windowing.WindowManager
import org.koin.core.annotation.Single

@Single
class EntityInteractionProvider(
    val externalServiceRepository: ExternalServiceRepository,
    val gameUiController: GameUiController,
    val contactsRepository: ContactsRepository,
    val contactsExternalControl: ContactsExternalControl,
    val autopilotController: AutopilotController,
    val mapExternalControl: MapExternalControl,
    val solarSystemsRepository: SolarSystemsRepository,
    val windowManager: WindowManager,
    val settings: Settings,
    val mapStatusRepository: MapStatusRepository,
    val assetsExternalControl: AssetsExternalControl,
    val isDockableTypeUseCase: IsDockableTypeUseCase,
) {

    data class Interaction(
        val onClick: () -> Unit,
        val contextMenuItems: List<ContextMenuItem>,
    )

    fun getFrom(typeDetail: TypeDetail, itemId: Long? = null): Interaction? {
        return when (typeDetail) {
            is TypeDetail.Structure -> getLocation(
                systemId = typeDetail.structure.solarSystemId,
                locationId = typeDetail.structure.structureId,
                locationTypeId = typeDetail.structure.typeId,
                locationName = typeDetail.structure.name,
            )
            is TypeDetail.Station -> getLocation(
                systemId = typeDetail.station.solarSystemId,
                locationId = typeDetail.station.stationId.toLong(),
                locationTypeId = typeDetail.station.typeId,
                locationName = typeDetail.station.name,
            )
            is TypeDetail.Character -> getCharacter(typeDetail.character.characterId)
            is TypeDetail.Corporation -> getCorporation(typeDetail.corporation.corporationId)
            is TypeDetail.Alliance -> getAlliance(typeDetail.alliance.allianceId)
            is TypeDetail.SolarSystem -> getLocation(typeDetail.system.id)
            is TypeDetail.Type -> getType(typeDetail.type, itemId)
            is TypeDetail.Celestial -> getLocation(
                systemId = typeDetail.celestial.solarSystemId,
                locationId = typeDetail.celestial.id.toLong(),
                locationTypeId = typeDetail.celestial.type.id,
                locationName = typeDetail.celestial.name,
            )
            is TypeDetail.Faction,
            is TypeDetail.CorporationProject,
            is TypeDetail.FreelanceProject,
            is TypeDetail.DailyGoal,
            is TypeDetail.Unknown,
            -> null
        }
    }

    fun getCharacter(
        characterId: Int,
    ): Interaction {
        val contextMenuItems = buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = { gameUiController.openInfoWindow(characterId) },
                ),
            )
            add(ContextMenuItem.DividerItem)
            addAll(externalServiceRepository.getCharacterMenuItems(characterId))
            add(ContextMenuItem.DividerItem)
            add(getContactMenuItem(characterId, EntityType.Character))
        }
        return Interaction(
            onClick = {
                externalServiceRepository.openCharacterPreferredService(characterId)
            },
            contextMenuItems = contextMenuItems,
        )
    }

    fun getLocation(
        systemId: Int,
        locationId: Long? = null,
        locationTypeId: Int? = null,
        locationName: String? = null,
        mapType: MapType? = null,
    ): Interaction {
        val isKnownSpace = solarSystemsRepository.isKnownSpace(systemId)
        return Interaction(
            onClick = {
                if (isKnownSpace) {
                    mapExternalControl.showSystemOnMap(systemId)
                }
            },
            contextMenuItems = getSystemContextMenuItems(systemId, locationId, locationTypeId, locationName, mapType),
        )
    }

    fun getCorporation(
        corporationId: Int,
    ): Interaction {
        val contextMenuItems = buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = { gameUiController.openInfoWindow(corporationId) },
                ),
            )
            add(ContextMenuItem.DividerItem)
            addAll(externalServiceRepository.getCorporationMenuItems(corporationId))
            add(ContextMenuItem.DividerItem)
            add(getContactMenuItem(corporationId, EntityType.Corporation))
        }
        return Interaction(
            onClick = {
                externalServiceRepository.openCorporationPreferredService(corporationId)
            },
            contextMenuItems = contextMenuItems,
        )
    }

    fun getAlliance(
        allianceId: Int,
    ): Interaction {
        val contextMenuItems = buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = { gameUiController.openInfoWindow(allianceId) },
                ),
            )
            add(ContextMenuItem.DividerItem)
            addAll(externalServiceRepository.getAllianceMenuItems(allianceId))
            add(ContextMenuItem.DividerItem)
            add(getContactMenuItem(allianceId, EntityType.Alliance))
        }
        return Interaction(
            onClick = {
                externalServiceRepository.openAlliancePreferredService(allianceId)
            },
            contextMenuItems = contextMenuItems,
        )
    }

    fun getShip(
        type: Type,
    ): Interaction {
        val contextMenuItems = buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = { gameUiController.pushType(type, LinkCategory.Ship) },
                ),
            )
            add(ContextMenuItem.DividerItem)
            addAll(externalServiceRepository.getShipMenuItems(type))
        }
        return Interaction(
            onClick = {
                externalServiceRepository.openShipPreferredService(type)
            },
            contextMenuItems = contextMenuItems,
        )
    }

    fun getType(
        type: Type,
        singletonId: Long? = null,
        systemId: Int? = null,
    ): Interaction {
        val contextMenuItems = buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = { gameUiController.pushType(type, LinkCategory.Type, singletonId) },
                ),
            )
            add(ContextMenuItem.DividerItem)
            if (singletonId != null && systemId != null && isDockableTypeUseCase(type)) {
                add(
                    ContextMenuItem.TextItem(
                        text = Res.string.entity_interaction_set_destination.toUiText(),
                        iconResource = Res.drawable.menu_set_destination,
                        onClick = {
                            autopilotController.setDestination(singletonId, systemId)
                        },
                    ),
                )
                add(ContextMenuItem.DividerItem)
            }
            addAll(externalServiceRepository.getTypeMenuItems(type))
        }
        return Interaction(
            onClick = {
                externalServiceRepository.openTypePreferredService(type)
            },
            contextMenuItems = contextMenuItems,
        )
    }

    fun getKillmail(
        killMailId: Long,
        killMailHash: String,
    ): Interaction {
        val contextMenuItems = buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = { gameUiController.pushUrl("killReport:$killMailId:$killMailHash", "Killmail") },
                ),
            )
            add(ContextMenuItem.DividerItem)
            addAll(externalServiceRepository.getKillmailMenuItems(killMailId))
        }
        return Interaction(
            onClick = {
                gameUiController.pushUrl("killReport:$killMailId:$killMailHash", "Killmail")
            },
            contextMenuItems = contextMenuItems,
        )
    }

    private fun getSystemContextMenuItems(
        systemId: Int?,
        locationId: Long?,
        locationTypeId: Int?,
        locationName: String?,
        mapType: MapType?,
    ): List<ContextMenuItem> {
        if (systemId == null) return emptyList()

        val system = solarSystemsRepository.getSystem(systemId) ?: return emptyList()
        val isKnownSpace = solarSystemsRepository.isKnownSpace(systemId)
        val isWormholeSpace = !isKnownSpace && solarSystemsRepository.isWormholeSpace(systemId)
        val routableLocationId = locationId?.takeIf { IdRanges.isStation(it) || IdRanges.isSpawnedItem(it) }
        return buildList {
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_show_info.toUiText(),
                    iconContent = { RiftMulticolorIcon(MulticolorIconType.Info, it) },
                    onClick = {
                        if (locationId != null && locationTypeId != null) {
                            gameUiController.pushLocation(locationId, locationTypeId, locationName ?: "Location")
                        } else {
                            gameUiController.pushSystem(system)
                        }
                    },
                ),
            )
            add(ContextMenuItem.DividerItem)
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_set_destination.toUiText(),
                    iconResource = Res.drawable.menu_set_destination,
                    onClick = {
                        autopilotController.setDestination(routableLocationId ?: systemId.toLong(), systemId)
                    },
                ),
            )
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_add_waypoint.toUiText(),
                    onClick = {
                        autopilotController.addWaypoint(routableLocationId ?: systemId.toLong(), systemId)
                    },
                ),
            )
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_clear_autopilot.toUiText(),
                    onClick = {
                        autopilotController.clearRoute()
                    },
                ),
            )
            add(
                ContextMenuItem.CheckboxItemWithInternalState(
                    text = Res.string.entity_interaction_autopilot_all_characters.toUiText(),
                    isSelected = {
                        settings.isSettingAutopilotToAll
                    },
                    onClick = {
                        settings.isSettingAutopilotToAll = !it
                    },
                ),
            )
            add(ContextMenuItem.DividerItem)
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_copy_name.toUiText(),
                    onClick = {
                        Clipboard.copy(system.name)
                    },
                ),
            )
            add(
                ContextMenuItem.TextItem(
                    text = Res.string.entity_interaction_add_marker.toUiText(),
                    iconResource = Res.drawable.map_marker_place_bookmark,
                    onClick = {
                        val inputModel = MapMarkersInputModel.AddToSystem(systemId)
                        windowManager.onWindowOpen(WindowManager.RiftWindow.MapMarkers, inputModel)
                    },
                ),
            )
            if ((mapStatusRepository.status.value[systemId]?.assetCount ?: 0) > 0) {
                add(
                    ContextMenuItem.TextItem(
                        text = Res.string.entity_interaction_show_assets.toUiText(),
                        iconResource = Res.drawable.indicator_assets,
                        onClick = {
                            assetsExternalControl.showSystem(system.name)
                        },
                    ),
                )
            }
            if (isKnownSpace) {
                if (mapType == null) {
                    add(
                        ContextMenuItem.TextItem(
                            text = Res.string.entity_interaction_show_on_map.toUiText(),
                            onClick = {
                                mapExternalControl.showSystemOnMap(systemId)
                            },
                        ),
                    )
                } else {
                    if (mapType !is MapType.ClusterSystemsMap) {
                        add(
                            ContextMenuItem.TextItem(
                                text = Res.string.entity_interaction_show_on_cluster_map.toUiText(),
                                onClick = {
                                    mapExternalControl.showSystemOnNewEdenMap(systemId)
                                },
                            ),
                        )
                    }
                    if (mapType !is MapType.RegionMap) {
                        add(
                            ContextMenuItem.TextItem(
                                text = Res.string.entity_interaction_show_on_region_map.toUiText(),
                                onClick = {
                                    mapExternalControl.showSystemOnRegionMap(systemId)
                                },
                            ),
                        )
                    }
                }
            }
            add(ContextMenuItem.DividerItem)
            addAll(externalServiceRepository.getSystemMenuItems(system.name, systemId, locationId, isWormholeSpace))
        }
    }

    private fun getContactMenuItem(id: Int, type: EntityType): ContextMenuItem {
        val onEditContact = {
            contactsExternalControl.editContact(id, type)
        }
        return if (contactsRepository.isCharacterContact(id)) {
            ContextMenuItem.TextItem(Res.string.entity_interaction_edit_contact.toUiText(), null, onClick = onEditContact)
        } else {
            ContextMenuItem.TextItem(Res.string.entity_interaction_add_contact.toUiText(), Res.drawable.menu_add, onClick = onEditContact)
        }
    }
}
