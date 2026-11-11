package dev.nohus.rift.network.evekill

import dev.nohus.rift.network.Result
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.requests.RequestExecutor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Single
class EveKillApi(
    @Named("network") json: Json,
    @Named("api") client: OkHttpClient,
    requestExecutor: RequestExecutor,
) : RequestExecutor by requestExecutor {

    private val contentType = "application/json".toMediaType()
    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://api.eve-kill.com/")
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
    private val service = retrofit.create(EveKillService::class.java)

    suspend fun getCharacterIntel(originator: Originator, characterId: Int, days: Int): Result<EveKillIntel> {
        return execute { service.getCharacterIntel(originator, characterId, days) }
    }
}
