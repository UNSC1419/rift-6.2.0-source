package dev.nohus.rift.utils.entitylinks

import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.types.getDisplayName
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.ALLIANCE_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.CHARACTER_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.CORPORATION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.FACTION_TYPE_ID
import dev.nohus.rift.utils.entitylinks.ShowInfoCategoryIds.SOLAR_SYSTEM_TYPE_ID
import org.koin.core.annotation.Single

@Single
class ShowInfoLinkifier {

    fun getLink(typeDetail: TypeDetail): String = with(typeDetail) {
        val href = when (this) {
            is TypeDetail.Structure -> structure.typeId?.let { "showinfo:$it//${structure.structureId}" }
            is TypeDetail.Station -> "showinfo:${station.typeId}//${station.stationId}"
            is TypeDetail.Character -> "showinfo:$CHARACTER_TYPE_ID//${character.characterId}"
            is TypeDetail.Corporation -> "showinfo:$CORPORATION_TYPE_ID//${corporation.corporationId}"
            is TypeDetail.Alliance -> "showinfo:$ALLIANCE_TYPE_ID//${alliance.allianceId}"
            is TypeDetail.Faction -> "showinfo:$FACTION_TYPE_ID//$id"
            is TypeDetail.SolarSystem -> "showinfo:$SOLAR_SYSTEM_TYPE_ID//${system.id}"
            is TypeDetail.Type -> "showinfo:${type.id}"
            is TypeDetail.Celestial -> "showinfo:${celestial.type.id}//${celestial.id}"
            is TypeDetail.FreelanceProject -> "opportunity:freelance_projects:$id"
            is TypeDetail.CorporationProject -> "opportunity:corporation_goals:$id"
            is TypeDetail.DailyGoal,
            is TypeDetail.Unknown,
            -> null
        }
        return if (href != null) {
            "<a href=\"$href\">${getDisplayName()}</a>"
        } else {
            getDisplayName()
        }
    }

    fun getLink(staticLocation: StaticLocation): String = with(staticLocation) {
        return "<a href=\"showinfo:$typeId//$itemId\">$name</a>"
    }
}
