package dev.nohus.rift.network.everef

import dev.nohus.rift.network.esi.models.WarsId
import dev.nohus.rift.network.requests.Endpoint
import dev.nohus.rift.network.requests.EndpointTag
import dev.nohus.rift.network.requests.Originator
import retrofit2.http.GET
import retrofit2.http.Tag

interface EveRefService {

    @GET("wars/wars-current.json")
    @EndpointTag(Endpoint.EveRefCurrentWars::class)
    suspend fun getCurrentWars(
        @Tag originator: Originator,
    ): Map<Long, WarsId>
}
