package dev.nohus.rift.network.zkillboard

import dev.nohus.rift.network.requests.Endpoint
import dev.nohus.rift.network.requests.EndpointTag
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.zkillboardr2z2.EsiKillmail
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Tag

interface ZkillboardService {

    @GET("solarSystemID/{id}/pastSeconds/3600/page/{page}/")
    @EndpointTag(Endpoint.ZkillboardSystemKillmails::class)
    suspend fun getSystemKillmails(
        @Tag originator: Originator,
        @Path("id") systemId: Int,
        @Path("page") page: Int,
    ): List<EsiKillmail>

    @GET("recentactivity/")
    @EndpointTag(Endpoint.ZkillboardRecentActivity::class)
    suspend fun getRecentActivity(
        @Tag originator: Originator,
    ): RecentActivity

    @GET("stats/characterID/{id}/kills/")
    @EndpointTag(Endpoint.ZkillboardCharacterStats::class)
    suspend fun getCharacterStats(
        @Tag originator: Originator,
        @Path("id") id: Int,
    ): ZkillCharacterStats

    @POST("killmail/add/{killId}/{hash}/{delay}/")
    @EndpointTag(Endpoint.ZkillboardPostKillmail::class)
    suspend fun postKillmail(
        @Tag originator: Originator,
        @Path("killId") killId: String,
        @Path("hash") hash: String,
        @Path("delay") delay: Int,
    ): PostedKillmail
}
