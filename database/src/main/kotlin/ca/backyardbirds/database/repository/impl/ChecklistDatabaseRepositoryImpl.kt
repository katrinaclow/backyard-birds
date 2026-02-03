package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.mapper.toChecklistObservation
import ca.backyardbirds.database.mapper.toChecklistSummary
import ca.backyardbirds.database.repository.ChecklistDatabaseRepository
import ca.backyardbirds.database.tables.ChecklistObservationsTable
import ca.backyardbirds.database.tables.ChecklistsTable
import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChecklistDatabaseRepositoryImpl(
    private val database: Database
) : ChecklistDatabaseRepository {

    override suspend fun saveChecklistSummaries(
        summaries: List<ChecklistSummary>,
        regionCode: String
    ): DomainResult<Unit> = try {
        dbQuery {
            summaries.forEach { summary ->
                ChecklistsTable.upsert {
                    it[subId] = summary.subId
                    it[locId] = summary.locId
                    it[userDisplayName] = summary.userDisplayName
                    it[numSpecies] = summary.numSpecies
                    it[obsDt] = summary.obsDt.toInstant(ZoneOffset.UTC)
                    it[ChecklistsTable.regionCode] = regionCode
                    it[createdAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save checklist summaries: ${e.message}", e)
    }

    override suspend fun saveChecklist(checklist: Checklist): DomainResult<Unit> = try {
        dbQuery {
            // Save checklist summary
            ChecklistsTable.upsert {
                it[subId] = checklist.subId
                it[locId] = checklist.locId
                it[userDisplayName] = checklist.userDisplayName
                it[numSpecies] = checklist.numSpecies
                it[obsDt] = checklist.obsDt.toInstant(ZoneOffset.UTC)
                it[createdAt] = Instant.now()
            }

            // Delete existing observations for this checklist
            ChecklistObservationsTable.deleteWhere {
                ChecklistObservationsTable.subId eq checklist.subId
            }

            // Save checklist observations
            checklist.obs.forEach { obs ->
                ChecklistObservationsTable.insert {
                    it[subId] = checklist.subId
                    it[speciesCode] = obs.speciesCode
                    it[howMany] = obs.howMany
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save checklist: ${e.message}", e)
    }

    override suspend fun getRecentChecklists(
        regionCode: String,
        maxResults: Int
    ): DomainResult<List<ChecklistSummary>> = try {
        val summaries = dbQuery {
            ChecklistsTable.selectAll()
                .where { ChecklistsTable.regionCode eq regionCode }
                .orderBy(ChecklistsTable.obsDt, SortOrder.DESC)
                .limit(maxResults)
                .map { it.toChecklistSummary() }
        }
        DomainResult.Success(summaries)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get recent checklists: ${e.message}", e)
    }

    override suspend fun getChecklistsOnDate(
        regionCode: String,
        date: LocalDate,
        maxResults: Int
    ): DomainResult<List<ChecklistSummary>> = try {
        val startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC)
        val endOfDay = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)

        val summaries = dbQuery {
            ChecklistsTable.selectAll()
                .where {
                    (ChecklistsTable.regionCode eq regionCode) and
                    (ChecklistsTable.obsDt greaterEq startOfDay) and
                    (ChecklistsTable.obsDt less endOfDay)
                }
                .orderBy(ChecklistsTable.obsDt, SortOrder.DESC)
                .limit(maxResults)
                .map { it.toChecklistSummary() }
        }
        DomainResult.Success(summaries)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get checklists on date: ${e.message}", e)
    }

    override suspend fun getChecklist(subId: String): DomainResult<Checklist?> = try {
        val checklist = dbQuery {
            val summary = ChecklistsTable.selectAll()
                .where { ChecklistsTable.subId eq subId }
                .singleOrNull()
                ?.toChecklistSummary()
                ?: return@dbQuery null

            val observations = ChecklistObservationsTable.selectAll()
                .where { ChecklistObservationsTable.subId eq subId }
                .map { it.toChecklistObservation() }

            Checklist(
                subId = summary.subId,
                locId = summary.locId,
                userDisplayName = summary.userDisplayName,
                numSpecies = summary.numSpecies,
                obsDt = summary.obsDt,
                obs = observations
            )
        }
        DomainResult.Success(checklist)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get checklist: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
