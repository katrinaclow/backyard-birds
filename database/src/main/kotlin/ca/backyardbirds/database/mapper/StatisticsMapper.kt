package ca.backyardbirds.database.mapper

import ca.backyardbirds.database.tables.RegionStatsSnapshotsTable
import ca.backyardbirds.database.tables.TopObserversSnapshotsTable
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toRegionStats(): RegionStats = RegionStats(
    numChecklists = this[RegionStatsSnapshotsTable.numChecklists],
    numContributors = this[RegionStatsSnapshotsTable.numContributors],
    numSpecies = this[RegionStatsSnapshotsTable.numSpecies]
)

fun ResultRow.toTopObserver(): TopObserver = TopObserver(
    userDisplayName = this[TopObserversSnapshotsTable.userDisplayName],
    numSpecies = this[TopObserversSnapshotsTable.numSpecies],
    numChecklists = this[TopObserversSnapshotsTable.numChecklists],
    rowNum = this[TopObserversSnapshotsTable.rowNum],
    userId = this[TopObserversSnapshotsTable.userId]
)
