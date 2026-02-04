package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import ca.backyardbirds.domain.query.Top100QueryParams

interface StatisticsRepository {
    suspend fun getTop100(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        params: Top100QueryParams = Top100QueryParams.DEFAULT
    ): DomainResult<List<TopObserver>>

    suspend fun getRegionStats(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<RegionStats>
}
