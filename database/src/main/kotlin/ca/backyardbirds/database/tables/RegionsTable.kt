package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object RegionsTable : Table("regions") {
    val code = varchar("code", 50)
    val name = varchar("name", 255)
    val regionType = varchar("region_type", 20)
    val parentCode = varchar("parent_code", 50).nullable()
    val boundsMinX = double("bounds_min_x").nullable()
    val boundsMaxX = double("bounds_max_x").nullable()
    val boundsMinY = double("bounds_min_y").nullable()
    val boundsMaxY = double("bounds_max_y").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(code)
}

object AdjacentRegionsTable : Table("adjacent_regions") {
    val regionCode = varchar("region_code", 50)
    val adjacentRegionCode = varchar("adjacent_region_code", 50)

    override val primaryKey = PrimaryKey(regionCode, adjacentRegionCode)
}
