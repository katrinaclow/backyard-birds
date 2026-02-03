package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import java.time.LocalDate

interface StatisticsDatabaseRepository {
    suspend fun saveRegionStats(
        regionCode: String,
        date: LocalDate,
        stats: RegionStats
    ): DomainResult<Unit>

    suspend fun getRegionStats(
        regionCode: String,
        date: LocalDate
    ): DomainResult<RegionStats?>

    suspend fun saveTopObservers(
        regionCode: String,
        date: LocalDate,
        observers: List<TopObserver>
    ): DomainResult<Unit>

    suspend fun getTopObservers(
        regionCode: String,
        date: LocalDate
    ): DomainResult<List<TopObserver>>
}
