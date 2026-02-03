package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ChecklistsTable : Table("checklists") {
    val subId = varchar("sub_id", 50)
    val locId = varchar("loc_id", 50)
    val userDisplayName = varchar("user_display_name", 255)
    val numSpecies = integer("num_species")
    val obsDt = timestamp("obs_dt")
    val regionCode = varchar("region_code", 50).nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(subId)
}

object ChecklistObservationsTable : Table("checklist_observations") {
    val id = integer("id").autoIncrement()
    val subId = varchar("sub_id", 50)
    val speciesCode = varchar("species_code", 20)
    val howMany = integer("how_many").nullable()

    override val primaryKey = PrimaryKey(id)
}
