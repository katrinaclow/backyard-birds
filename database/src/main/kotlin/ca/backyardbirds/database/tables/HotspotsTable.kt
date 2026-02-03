package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object HotspotsTable : Table("hotspots") {
    val locId = varchar("loc_id", 50)
    val locName = varchar("loc_name", 255)
    val countryCode = varchar("country_code", 10)
    val subnational1Code = varchar("subnational1_code", 20)
    val subnational2Code = varchar("subnational2_code", 20).nullable()
    val latitude = double("latitude")
    val longitude = double("longitude")
    val latestObsDt = timestamp("latest_obs_dt").nullable()
    val numSpeciesAllTime = integer("num_species_all_time").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(locId)
}
