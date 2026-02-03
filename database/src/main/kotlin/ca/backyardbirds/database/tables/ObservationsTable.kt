package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ObservationsTable : Table("observations") {
    val id = integer("id").autoIncrement()
    val submissionId = varchar("submission_id", 50)
    val speciesCode = varchar("species_code", 20)
    val commonName = varchar("common_name", 255)
    val scientificName = varchar("scientific_name", 255)
    val locationId = varchar("location_id", 50)
    val locationName = varchar("location_name", 255)
    val observationDate = timestamp("observation_date")
    val howMany = integer("how_many").nullable()
    val latitude = double("latitude")
    val longitude = double("longitude")
    val isValid = bool("is_valid")
    val isReviewed = bool("is_reviewed")
    val isLocationPrivate = bool("is_location_private")
    val regionCode = varchar("region_code", 50).nullable()
    val isNotable = bool("is_notable")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
