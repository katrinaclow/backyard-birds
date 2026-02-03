package ca.backyardbirds.data.checklist

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.ChecklistDatabaseRepository
import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.ChecklistRepository
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

class CachingChecklistRepository(
    private val apiRepository: ChecklistRepository,
    private val dbRepository: ChecklistDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : ChecklistRepository {

    companion object {
        private val CHECKLISTS_TTL = 15.minutes
        private const val ENTITY_TYPE = "checklists"
        private const val ENTITY_TYPE_DETAIL = "checklist_detail"
    }

    override suspend fun getRecentChecklists(
        regionCode: String,
        maxResults: Int?
    ): DomainResult<List<ChecklistSummary>> {
        val cacheKey = "checklists:recent:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getRecentChecklists(regionCode, maxResults ?: 200)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getRecentChecklists(regionCode, maxResults)) {
            is DomainResult.Success -> {
                dbRepository.saveChecklistSummaries(apiResult.data, regionCode)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, regionCode, CHECKLISTS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getChecklistsOnDate(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        maxResults: Int?
    ): DomainResult<List<ChecklistSummary>> {
        val date = LocalDate.of(year, month, day)
        val cacheKey = "checklists:date:$regionCode:$date"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getChecklistsOnDate(regionCode, date, maxResults ?: 200)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getChecklistsOnDate(regionCode, year, month, day, maxResults)) {
            is DomainResult.Success -> {
                dbRepository.saveChecklistSummaries(apiResult.data, regionCode)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, regionCode, CHECKLISTS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getChecklist(subId: String): DomainResult<Checklist> {
        val cacheKey = "checklist:detail:$subId"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            when (val cached = dbRepository.getChecklist(subId)) {
                is DomainResult.Success -> {
                    cached.data?.let { return DomainResult.Success(it) }
                }
                is DomainResult.Failure -> { /* Cache miss, continue to API */ }
            }
        }

        return when (val apiResult = apiRepository.getChecklist(subId)) {
            is DomainResult.Success -> {
                dbRepository.saveChecklist(apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE_DETAIL, null, CHECKLISTS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }
}
