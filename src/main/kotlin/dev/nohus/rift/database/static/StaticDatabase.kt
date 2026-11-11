package dev.nohus.rift.database.static

import dev.nohus.rift.database.ShowDatabaseFailureUseCase
import dev.nohus.rift.database.SqliteInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.core.annotation.Single
import java.sql.SQLException

@Single
class StaticDatabase(
    sqliteInitializer: SqliteInitializer,
    private val showDatabaseFailureUseCase: ShowDatabaseFailureUseCase,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val targetDatabase = Database.connect("jdbc:sqlite::resource:static.db", "org.sqlite.JDBC")

    fun <T> transaction(block: Transaction.() -> T): T {
        return try {
            transaction(targetDatabase) {
                block()
            }
        } catch (e: SQLException) {
            scope.launch {
                showDatabaseFailureUseCase(e, ShowDatabaseFailureUseCase.Failure.StaticDatabaseLoad)
            }
            throw e
        }
    }
}
