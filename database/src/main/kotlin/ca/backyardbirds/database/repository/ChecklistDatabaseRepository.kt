package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult
import java.time.LocalDate

interface ChecklistDatabaseRepository {
    suspend fun saveChecklistSummaries(
        summaries: List<ChecklistSummary>,
        regionCode: String
    ): DomainResult<Unit>

    suspend fun saveChecklist(checklist: Checklist): DomainResult<Unit>

    suspend fun getRecentChecklists(
        regionCode: String,
        maxResults: Int = 200
    ): DomainResult<List<ChecklistSummary>>

    suspend fun getChecklistsOnDate(
        regionCode: String,
        date: LocalDate,
        maxResults: Int = 200
    ): DomainResult<List<ChecklistSummary>>

    suspend fun getChecklist(subId: String): DomainResult<Checklist?>
}
