package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver

interface StatisticsRepository {
    suspend fun getTop100(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<List<TopObserver>>

    suspend fun getRegionStats(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<RegionStats>
}
