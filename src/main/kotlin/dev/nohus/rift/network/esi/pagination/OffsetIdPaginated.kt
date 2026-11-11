package dev.nohus.rift.network.esi.pagination

import dev.nohus.rift.network.Result
import dev.nohus.rift.network.Result.Failure
import dev.nohus.rift.network.Result.Success

interface OffsetId {
    val offsetId: Long
}

suspend fun <T : OffsetId> fetchOffsetIdPaginated(
    onProgressUpdate: suspend (loadedItems: Int) -> Unit = { _ -> },
    request: suspend (fromId: Long?) -> Result<List<T>>,
): Result<List<T>> = fetchOffsetIdPaginated(
    getOffsetId = { it.offsetId },
    onProgressUpdate = onProgressUpdate,
    request = request,
)

suspend fun <T> fetchOffsetIdPaginated(
    getOffsetId: (T) -> Long,
    onProgressUpdate: suspend (loadedItems: Int) -> Unit = { _ -> },
    request: suspend (fromId: Long?) -> Result<List<T>>,
): Result<List<T>> {
    val items = mutableListOf<T>()
    var fromId: Long? = null
    while (true) {
        when (val result = request(fromId)) {
            is Success -> {
                val newItems = result.data.filter { getOffsetId(it) != fromId }
                items += newItems
                onProgressUpdate(items.size)
                if (newItems.isEmpty()) break
                fromId = newItems.minOf(getOffsetId)
            }
            is Failure -> return result
        }
    }
    return Success(items)
}
