package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionInfo

interface RegionRepository {
    suspend fun getSubRegions(
        regionType: String,
        parentRegionCode: String
    ): DomainResult<List<Region>>

    suspend fun getRegionInfo(regionCode: String): DomainResult<RegionInfo>

    suspend fun getAdjacentRegions(regionCode: String): DomainResult<List<Region>>
}
