package ca.backyardbirds.database.mapper

import ca.backyardbirds.database.tables.HotspotsTable
import ca.backyardbirds.domain.model.Hotspot
import org.jetbrains.exposed.sql.ResultRow
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

fun ResultRow.toHotspot(): Hotspot = Hotspot(
    locId = this[HotspotsTable.locId],
    locName = this[HotspotsTable.locName],
    countryCode = this[HotspotsTable.countryCode],
    subnational1Code = this[HotspotsTable.subnational1Code],
    subnational2Code = this[HotspotsTable.subnational2Code],
    lat = this[HotspotsTable.latitude],
    lng = this[HotspotsTable.longitude],
    latestObsDt = this[HotspotsTable.latestObsDt]?.let {
        java.time.LocalDateTime.ofInstant(it, java.time.ZoneOffset.UTC)
            .format(dateTimeFormatter)
    },
    numSpeciesAllTime = this[HotspotsTable.numSpeciesAllTime]
)
