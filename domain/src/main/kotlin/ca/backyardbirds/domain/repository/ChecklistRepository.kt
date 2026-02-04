package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.query.ChecklistQueryParams

interface ChecklistRepository {
    suspend fun getRecentChecklists(
        regionCode: String,
        params: ChecklistQueryParams = ChecklistQueryParams.DEFAULT
    ): DomainResult<List<ChecklistSummary>>

    suspend fun getChecklistsOnDate(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        params: ChecklistQueryParams = ChecklistQueryParams.DEFAULT
    ): DomainResult<List<ChecklistSummary>>

    suspend fun getChecklist(subId: String): DomainResult<Checklist>
}
