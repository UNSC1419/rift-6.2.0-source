package dev.nohus.rift.network.adam4eve

import dev.nohus.rift.network.requests.Endpoint
import dev.nohus.rift.network.requests.EndpointTag
import dev.nohus.rift.network.requests.Originator
import retrofit2.http.GET
import retrofit2.http.Tag

interface Adam4EveService {

    @GET("IDs/playerStructure_IDs.csv")
    @EndpointTag(Endpoint.Adam4EveStructures::class)
    suspend fun getStructures(
        @Tag originator: Originator,
    ): String
}
