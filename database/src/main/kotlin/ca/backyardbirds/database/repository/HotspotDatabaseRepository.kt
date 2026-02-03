package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Hotspot

interface HotspotDatabaseRepository {
    suspend fun saveHotspots(hotspots: List<Hotspot>): DomainResult<Unit>

    suspend fun getHotspotsInRegion(regionCode: String): DomainResult<List<Hotspot>>

    suspend fun getNearbyHotspots(
        lat: Double,
        lng: Double,
        distKm: Int
    ): DomainResult<List<Hotspot>>

    suspend fun getHotspotById(locId: String): DomainResult<Hotspot?>
}
