package dev.nohus.rift.network.evekill

import dev.nohus.rift.network.requests.Endpoint
import dev.nohus.rift.network.requests.EndpointTag
import dev.nohus.rift.network.requests.Originator
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag

interface EveKillService {

    @GET("characters/{characterId}/intel")
    @EndpointTag(Endpoint.EveKillCharacterIntel::class)
    suspend fun getCharacterIntel(
        @Tag originator: Originator,
        @Path("characterId") characterId: Int,
        @Query("days") days: Int,
    ): EveKillIntel
}
