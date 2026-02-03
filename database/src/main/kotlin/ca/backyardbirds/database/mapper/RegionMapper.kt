package ca.backyardbirds.database.mapper

import ca.backyardbirds.database.tables.RegionsTable
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionBounds
import ca.backyardbirds.domain.model.RegionInfo
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toRegion(): Region = Region(
    code = this[RegionsTable.code],
    name = this[RegionsTable.name]
)

fun ResultRow.toRegionInfo(): RegionInfo {
    val minX = this[RegionsTable.boundsMinX]
    val maxX = this[RegionsTable.boundsMaxX]
    val minY = this[RegionsTable.boundsMinY]
    val maxY = this[RegionsTable.boundsMaxY]

    val bounds = if (minX != null && maxX != null && minY != null && maxY != null) {
        RegionBounds(minX = minX, maxX = maxX, minY = minY, maxY = maxY)
    } else {
        null
    }

    return RegionInfo(
        code = this[RegionsTable.code],
        name = this[RegionsTable.name],
        bounds = bounds
    )
}
