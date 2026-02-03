package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object RegionStatsSnapshotsTable : Table("region_stats_snapshots") {
    val id = integer("id").autoIncrement()
    val regionCode = varchar("region_code", 50)
    val statsDate = date("stats_date")
    val numChecklists = integer("num_checklists")
    val numContributors = integer("num_contributors")
    val numSpecies = integer("num_species")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object TopObserversSnapshotsTable : Table("top_observers_snapshots") {
    val id = integer("id").autoIncrement()
    val regionCode = varchar("region_code", 50)
    val snapshotDate = date("snapshot_date")
    val userId = varchar("user_id", 50)
    val userDisplayName = varchar("user_display_name", 255)
    val numSpecies = integer("num_species")
    val numChecklists = integer("num_checklists")
    val rowNum = integer("row_num")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
