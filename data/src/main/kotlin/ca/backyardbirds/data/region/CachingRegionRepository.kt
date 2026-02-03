package ca.backyardbirds.data.region

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.RegionDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionInfo
import ca.backyardbirds.domain.repository.RegionRepository
import kotlin.time.Duration.Companion.days

class CachingRegionRepository(
    private val apiRepository: RegionRepository,
    private val dbRepository: RegionDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : RegionRepository {

    companion object {
        private val REGIONS_TTL = 30.days
        private const val ENTITY_TYPE = "regions"
        private const val ENTITY_TYPE_INFO = "region_info"
        private const val ENTITY_TYPE_ADJACENT = "region_adjacent"
    }

    override suspend fun getSubRegions(
        regionType: String,
        parentRegionCode: String
    ): DomainResult<List<Region>> {
        val cacheKey = "regions:$regionType:$parentRegionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getSubRegions(regionType, parentRegionCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getSubRegions(regionType, parentRegionCode)) {
            is DomainResult.Success -> {
                dbRepository.saveRegions(apiResult.data, regionType, parentRegionCode)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, parentRegionCode, REGIONS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getRegionInfo(regionCode: String): DomainResult<RegionInfo> {
        val cacheKey = "region:info:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            when (val cached = dbRepository.getRegionInfo(regionCode)) {
                is DomainResult.Success -> {
                    cached.data?.let { return DomainResult.Success(it) }
                }
                is DomainResult.Failure -> { /* Cache miss, continue to API */ }
            }
        }

        return when (val apiResult = apiRepository.getRegionInfo(regionCode)) {
            is DomainResult.Success -> {
                dbRepository.saveRegionInfo(apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE_INFO, regionCode, REGIONS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getAdjacentRegions(regionCode: String): DomainResult<List<Region>> {
        val cacheKey = "region:adjacent:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getAdjacentRegions(regionCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getAdjacentRegions(regionCode)) {
            is DomainResult.Success -> {
                dbRepository.saveAdjacentRegions(regionCode, apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE_ADJACENT, regionCode, REGIONS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }
}
