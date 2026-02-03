package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.mapper.toHotspot
import ca.backyardbirds.database.repository.HotspotDatabaseRepository
import ca.backyardbirds.database.tables.HotspotsTable
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Hotspot
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class HotspotDatabaseRepositoryImpl(
    private val database: Database
) : HotspotDatabaseRepository {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    override suspend fun saveHotspots(hotspots: List<Hotspot>): DomainResult<Unit> = try {
        dbQuery {
            hotspots.forEach { hotspot ->
                HotspotsTable.upsert {
                    it[locId] = hotspot.locId
                    it[locName] = hotspot.locName
                    it[countryCode] = hotspot.countryCode
                    it[subnational1Code] = hotspot.subnational1Code
                    it[subnational2Code] = hotspot.subnational2Code
                    it[latitude] = hotspot.lat
                    it[longitude] = hotspot.lng
                    it[latestObsDt] = hotspot.latestObsDt?.let { dt ->
                        LocalDateTime.parse(dt, dateTimeFormatter).toInstant(ZoneOffset.UTC)
                    }
                    it[numSpeciesAllTime] = hotspot.numSpeciesAllTime
                    it[createdAt] = Instant.now()
                    it[updatedAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save hotspots: ${e.message}", e)
    }

    override suspend fun getHotspotsInRegion(regionCode: String): DomainResult<List<Hotspot>> = try {
        val hotspots = dbQuery {
            // Region code could be country, subnational1, or subnational2
            HotspotsTable.selectAll()
                .where {
                    (HotspotsTable.countryCode eq regionCode) or
                    (HotspotsTable.subnational1Code eq regionCode) or
                    (HotspotsTable.subnational2Code eq regionCode)
                }
                .map { it.toHotspot() }
        }
        DomainResult.Success(hotspots)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get hotspots in region: ${e.message}", e)
    }

    override suspend fun getNearbyHotspots(
        lat: Double,
        lng: Double,
        distKm: Int
    ): DomainResult<List<Hotspot>> = try {
        // Simple bounding box approximation (1 degree ~ 111km)
        val latDelta = distKm / 111.0
        val lngDelta = distKm / (111.0 * kotlin.math.cos(Math.toRadians(lat)))

        val hotspots = dbQuery {
            HotspotsTable.selectAll()
                .where {
                    (HotspotsTable.latitude greaterEq (lat - latDelta)) and
                    (HotspotsTable.latitude lessEq (lat + latDelta)) and
                    (HotspotsTable.longitude greaterEq (lng - lngDelta)) and
                    (HotspotsTable.longitude lessEq (lng + lngDelta))
                }
                .map { it.toHotspot() }
        }
        DomainResult.Success(hotspots)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get nearby hotspots: ${e.message}", e)
    }

    override suspend fun getHotspotById(locId: String): DomainResult<Hotspot?> = try {
        val hotspot = dbQuery {
            HotspotsTable.selectAll()
                .where { HotspotsTable.locId eq locId }
                .singleOrNull()
                ?.toHotspot()
        }
        DomainResult.Success(hotspot)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get hotspot by ID: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
