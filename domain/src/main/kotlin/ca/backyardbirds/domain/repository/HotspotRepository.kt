package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Hotspot

interface HotspotRepository {
    suspend fun getHotspotsInRegion(
        regionCode: String,
        back: Int? = null
    ): DomainResult<List<Hotspot>>

    suspend fun getNearbyHotspots(
        lat: Double,
        lng: Double,
        distKm: Int? = null,
        back: Int? = null
    ): DomainResult<List<Hotspot>>

    suspend fun getHotspotInfo(locId: String): DomainResult<Hotspot>
}
