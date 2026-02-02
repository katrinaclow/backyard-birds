package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult

interface ChecklistRepository {
    suspend fun getRecentChecklists(
        regionCode: String,
        maxResults: Int? = null
    ): DomainResult<List<ChecklistSummary>>

    suspend fun getChecklistsOnDate(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        maxResults: Int? = null
    ): DomainResult<List<ChecklistSummary>>

    suspend fun getChecklist(subId: String): DomainResult<Checklist>
}
