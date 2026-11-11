package dev.nohus.rift.repositories.character

import dev.nohus.rift.database.ShowDatabaseFailureUseCase
import dev.nohus.rift.database.local.Characters2
import dev.nohus.rift.database.local.LocalDatabase
import dev.nohus.rift.logs.parse.CharacterNameValidator
import dev.nohus.rift.network.Result.Failure
import dev.nohus.rift.network.Result.Success
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.requests.Originator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.koin.core.annotation.Single
import java.sql.SQLException
import java.time.Instant
import kotlin.time.Duration.Companion.days
import dev.nohus.rift.database.local.CharacterStatus as DbCharacterStatus

private val logger = KotlinLogging.logger {}

@Single
class CharactersRepository(
    private val esiApi: EsiApi,
    private val characterActivityRepository: CharacterActivityRepository,
    private val localDatabase: LocalDatabase,
    private val characterNameValidator: CharacterNameValidator,
    private val showDatabaseFailureUseCase: ShowDatabaseFailureUseCase,
) {

    private data class Character(
        val name: String,
        val characterId: Int?,
        val status: CharacterStatus,
        val checkTimestamp: Long,
    )

    suspend fun getCharacterNamesStatus(originator: Originator, names: List<String>): Map<String, CharacterStatus> {
        val databaseCharacters = getCharactersFromDatabase(names)

        val missing = names.filter { it !in databaseCharacters.keys }
        val esiCharacters: Map<String, CharacterStatus> = if (missing.isNotEmpty()) {
            val characters = getCharactersFromEsi(originator, missing)
            try {
                saveCharactersToDatabase(characters)
            } catch (e: SQLException) {
                showDatabaseFailureUseCase(e, ShowDatabaseFailureUseCase.Failure.CharacterCacheWrite)
            }
            characters
                .filter { it.name in missing }
                .associate { it.name to it.status }
        } else {
            emptyMap()
        }

        if (logger.isDebugEnabled() && esiCharacters.isNotEmpty()) {
            logger.debug { "Checked ${esiCharacters.size} characters from ESI" }
        }

        return databaseCharacters + esiCharacters
    }

    suspend fun getCharacterId(originator: Originator, name: String): Int? {
        if (!characterNameValidator.isValid(name)) {
            return null
        }
        return when (val status = getCharacterNamesStatus(originator, listOf(name)).entries.singleOrNull()?.value) {
            is CharacterStatus.Exists -> status.characterId
            CharacterStatus.DoesNotExist, null -> null
        }
    }

    private suspend fun getCharactersFromEsi(originator: Originator, names: List<String>): List<Character> = withContext(Dispatchers.IO) {
        val now = Instant.now().toEpochMilli()
        val failedCharacters = mutableListOf<String>()
        val esiCharacters = names
            .chunked(100)
            .flatMap { chunk ->
                when (val result = esiApi.postUniverseIds(originator, chunk)) {
                    is Success -> result.data.characters.orEmpty()
                    is Failure -> {
                        failedCharacters += chunk
                        emptyList()
                    }
                }
            }
            .map { async { it to characterActivityRepository.getActivityStatus(originator, it.id) } }
            .awaitAll()
            .map { (character, status) ->
                Character(character.name, character.id, status, now)
            }
        val existing = esiCharacters.map { it.name }
        val notExisting = names.filter { it !in existing && it !in failedCharacters }
        val notExistingCharacters = notExisting
            .map { Character(it, null, CharacterStatus.DoesNotExist, now) }
        esiCharacters + notExistingCharacters
    }

    private suspend fun getCharactersFromDatabase(names: List<String>): Map<String, CharacterStatus> = withContext(Dispatchers.IO) {
        val currentTime = Instant.now().toEpochMilli()
        localDatabase.transaction {
            Characters2.selectAll().where {
                Characters2.name inList names and (
                    ((Characters2.status eq DbCharacterStatus.Active) and (Characters2.checkTimestamp greater currentTime - activeRecheckDuration)) or
                        ((Characters2.status eq DbCharacterStatus.Inactive) and (Characters2.checkTimestamp greater currentTime - inactiveRecheckDuration)) or
                        ((Characters2.status eq DbCharacterStatus.Dormant) and (Characters2.checkTimestamp greater currentTime - dormantRecheckDuration)) or
                        ((Characters2.status eq DbCharacterStatus.DoesNotExists) and (Characters2.checkTimestamp greater currentTime - doesNotExistRecheckDuration))
                    )
            }.associate { row ->
                row[Characters2.name] to when (row[Characters2.status]) {
                    DbCharacterStatus.Active -> CharacterStatus.Active(row[Characters2.characterId]!!)
                    DbCharacterStatus.Inactive -> CharacterStatus.Inactive(row[Characters2.characterId]!!)
                    DbCharacterStatus.Dormant -> CharacterStatus.Dormant(row[Characters2.characterId]!!)
                    DbCharacterStatus.DoesNotExists -> CharacterStatus.DoesNotExist
                }
            }
        }
    }

    private suspend fun saveCharactersToDatabase(characters: List<Character>) = withContext(Dispatchers.IO) {
        localDatabase.transaction {
            Characters2.batchUpsert(characters) {
                this[Characters2.name] = it.name
                this[Characters2.characterId] = it.characterId
                this[Characters2.status] = when (it.status) {
                    CharacterStatus.DoesNotExist -> DbCharacterStatus.DoesNotExists
                    is CharacterStatus.Active -> DbCharacterStatus.Active
                    is CharacterStatus.Inactive -> DbCharacterStatus.Inactive
                    is CharacterStatus.Dormant -> DbCharacterStatus.Dormant
                }
                this[Characters2.checkTimestamp] = it.checkTimestamp
            }
        }
    }

    companion object {
        private val activeRecheckDuration = 30.days.inWholeMilliseconds
        private val inactiveRecheckDuration = 1.days.inWholeMilliseconds
        private val dormantRecheckDuration = 7.days.inWholeMilliseconds
        private val doesNotExistRecheckDuration = 7.days.inWholeMilliseconds
        val expiryDuration = 30.days.inWholeMilliseconds
    }
}
