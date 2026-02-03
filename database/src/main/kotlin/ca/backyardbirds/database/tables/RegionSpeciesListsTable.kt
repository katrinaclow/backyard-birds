package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object RegionSpeciesListsTable : Table("region_species_lists") {
    val id = integer("id").autoIncrement()
    val regionCode = varchar("region_code", 50)
    val speciesCode = varchar("species_code", 20)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
