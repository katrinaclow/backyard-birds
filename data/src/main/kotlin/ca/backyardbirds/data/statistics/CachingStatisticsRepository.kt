package ca.backyardbirds.data.statistics

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.StatisticsDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import ca.backyardbirds.domain.query.Top100QueryParams
import ca.backyardbirds.domain.repository.StatisticsRepository
import java.time.LocalDate
import kotlin.time.Duration.Companion.hours

class CachingStatisticsRepository(
    private val apiRepository: StatisticsRepository,
    private val dbRepository: StatisticsDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : StatisticsRepository {

    companion object {
        private val STATISTICS_TTL = 1.hours
        private const val ENTITY_TYPE_TOP100 = "top100"
        private const val ENTITY_TYPE_STATS = "region_stats"
    }

    override suspend fun getTop100(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        params: Top100QueryParams
    ): DomainResult<List<TopObserver>> {
        val date = LocalDate.of(year, month, day)
        val cacheKey = "top100:$regionCode:$date${params.toCacheKeySuffix()}"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getTopObservers(regionCode, date)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getTop100(regionCode, year, month, day, params)) {
            is DomainResult.Success -> {
                dbRepository.saveTopObservers(regionCode, date, apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE_TOP100, regionCode, STATISTICS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    private fun Top100QueryParams.toCacheKeySuffix(): String {
        val parts = mutableListOf<String>()
        rankedBy?.let { parts.add("rankedBy=$it") }
        return if (parts.isEmpty()) "" else ":${parts.joinToString(":")}"
    }

    override suspend fun getRegionStats(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<RegionStats> {
        val date = LocalDate.of(year, month, day)
        val cacheKey = "region_stats:$regionCode:$date"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            when (val cached = dbRepository.getRegionStats(regionCode, date)) {
                is DomainResult.Success -> {
                    cached.data?.let { return DomainResult.Success(it) }
                }
                is DomainResult.Failure -> { /* Cache miss, continue to API */ }
            }
        }

        return when (val apiResult = apiRepository.getRegionStats(regionCode, year, month, day)) {
            is DomainResult.Success -> {
                dbRepository.saveRegionStats(regionCode, date, apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE_STATS, regionCode, STATISTICS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }
}
