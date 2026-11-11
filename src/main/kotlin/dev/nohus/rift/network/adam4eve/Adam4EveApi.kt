package dev.nohus.rift.network.adam4eve

import dev.nohus.rift.network.Result
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.requests.RequestExecutor
import okhttp3.OkHttpClient
import org.apache.commons.csv.CSVFormat
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@Single
class Adam4EveApi(
    @Named("api") client: OkHttpClient,
    requestExecutor: RequestExecutor,
) : RequestExecutor by requestExecutor {

    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://static.adam4eve.eu/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private val service = retrofit.create(Adam4EveService::class.java)
    private val csvFormat = CSVFormat.DEFAULT.builder()
        .setDelimiter(';')
        .setHeader()
        .setSkipHeaderRecord(true)
        .get()

    data class Structure(
        val structureId: Long,
        val name: String,
        val solarSystemId: Int,
        val typeId: Int?,
        val ownerId: Int?,
    )

    suspend fun getStructures(originator: Originator): Result<Map<Long, Structure>> {
        return execute {
            val csv = service.getStructures(originator).removePrefix("\uFEFF")
            csvFormat.parse(csv.reader()).use { parser ->
                require(parser.headerNames.containsAll(listOf("structureID", "name", "solarSystemID", "typeID", "ownerID"))) {
                    "Adam4Eve structures CSV is missing required columns"
                }
                buildMap {
                    for (record in parser) {
                        val structure = Structure(
                            structureId = record["structureID"].toLong(),
                            name = record["name"],
                            solarSystemId = record["solarSystemID"].toInt(),
                            typeId = record["typeID"].toIntOrNull()?.takeIf { it > 0 },
                            ownerId = record["ownerID"].toIntOrNull()?.takeIf { it > 0 },
                        )
                        put(structure.structureId, structure)
                    }
                    require(isNotEmpty()) { "Adam4Eve structures CSV is empty" }
                }
            }
        }
    }
}
