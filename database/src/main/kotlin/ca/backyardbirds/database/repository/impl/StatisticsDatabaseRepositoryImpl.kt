package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.mapper.toRegionStats
import ca.backyardbirds.database.mapper.toTopObserver
import ca.backyardbirds.database.repository.StatisticsDatabaseRepository
import ca.backyardbirds.database.tables.RegionStatsSnapshotsTable
import ca.backyardbirds.database.tables.TopObserversSnapshotsTable
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.time.LocalDate

class StatisticsDatabaseRepositoryImpl(
    private val database: Database
) : StatisticsDatabaseRepository {

    override suspend fun saveRegionStats(
        regionCode: String,
        date: LocalDate,
        stats: RegionStats
    ): DomainResult<Unit> = try {
        dbQuery {
            // Delete existing stats for this region/date
            RegionStatsSnapshotsTable.deleteWhere {
                (RegionStatsSnapshotsTable.regionCode eq regionCode) and
                (RegionStatsSnapshotsTable.statsDate eq date)
            }

            RegionStatsSnapshotsTable.insert {
                it[RegionStatsSnapshotsTable.regionCode] = regionCode
                it[statsDate] = date
                it[numChecklists] = stats.numChecklists
                it[numContributors] = stats.numContributors
                it[numSpecies] = stats.numSpecies
                it[createdAt] = Instant.now()
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save region stats: ${e.message}", e)
    }

    override suspend fun getRegionStats(
        regionCode: String,
        date: LocalDate
    ): DomainResult<RegionStats?> = try {
        val stats = dbQuery {
            RegionStatsSnapshotsTable.selectAll()
                .where {
                    (RegionStatsSnapshotsTable.regionCode eq regionCode) and
                    (RegionStatsSnapshotsTable.statsDate eq date)
                }
                .singleOrNull()
                ?.toRegionStats()
        }
        DomainResult.Success(stats)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get region stats: ${e.message}", e)
    }

    override suspend fun saveTopObservers(
        regionCode: String,
        date: LocalDate,
        observers: List<TopObserver>
    ): DomainResult<Unit> = try {
        dbQuery {
            // Delete existing top observers for this region/date
            TopObserversSnapshotsTable.deleteWhere {
                (TopObserversSnapshotsTable.regionCode eq regionCode) and
                (TopObserversSnapshotsTable.snapshotDate eq date)
            }

            observers.forEach { observer ->
                TopObserversSnapshotsTable.insert {
                    it[TopObserversSnapshotsTable.regionCode] = regionCode
                    it[snapshotDate] = date
                    it[userId] = observer.userId
                    it[userDisplayName] = observer.userDisplayName
                    it[numSpecies] = observer.numSpecies
                    it[numChecklists] = observer.numChecklists
                    it[rowNum] = observer.rowNum
                    it[createdAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save top observers: ${e.message}", e)
    }

    override suspend fun getTopObservers(
        regionCode: String,
        date: LocalDate
    ): DomainResult<List<TopObserver>> = try {
        val observers = dbQuery {
            TopObserversSnapshotsTable.selectAll()
                .where {
                    (TopObserversSnapshotsTable.regionCode eq regionCode) and
                    (TopObserversSnapshotsTable.snapshotDate eq date)
                }
                .orderBy(TopObserversSnapshotsTable.rowNum, SortOrder.ASC)
                .map { it.toTopObserver() }
        }
        DomainResult.Success(observers)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get top observers: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
