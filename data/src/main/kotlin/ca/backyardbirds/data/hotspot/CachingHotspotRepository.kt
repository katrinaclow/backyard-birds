package ca.backyardbirds.data.hotspot

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.HotspotDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Hotspot
import ca.backyardbirds.domain.repository.HotspotRepository
import kotlin.time.Duration.Companion.hours

class CachingHotspotRepository(
    private val apiRepository: HotspotRepository,
    private val dbRepository: HotspotDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : HotspotRepository {

    companion object {
        private val HOTSPOTS_TTL = 24.hours
        private const val ENTITY_TYPE = "hotspots"
    }

    override suspend fun getHotspotsInRegion(regionCode: String, back: Int?): DomainResult<List<Hotspot>> {
        val cacheKey = "hotspots:region:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getHotspotsInRegion(regionCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getHotspotsInRegion(regionCode, back)) {
            is DomainResult.Success -> {
                dbRepository.saveHotspots(apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, regionCode, HOTSPOTS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getNearbyHotspots(
        lat: Double,
        lng: Double,
        distKm: Int?,
        back: Int?
    ): DomainResult<List<Hotspot>> {
        // For nearby queries, we pass through to API
        // Could implement spatial caching later
        return apiRepository.getNearbyHotspots(lat, lng, distKm, back)
    }

    override suspend fun getHotspotInfo(locId: String): DomainResult<Hotspot> {
        val cacheKey = "hotspot:info:$locId"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            when (val cached = dbRepository.getHotspotById(locId)) {
                is DomainResult.Success -> {
                    cached.data?.let { return DomainResult.Success(it) }
                }
                is DomainResult.Failure -> { /* Cache miss, continue to API */ }
            }
        }

        return when (val apiResult = apiRepository.getHotspotInfo(locId)) {
            is DomainResult.Success -> {
                dbRepository.saveHotspots(listOf(apiResult.data))
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, null, HOTSPOTS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }
}
