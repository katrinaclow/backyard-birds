package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionInfo
import ca.backyardbirds.domain.query.RegionInfoQueryParams

interface RegionRepository {
    suspend fun getSubRegions(
        regionType: String,
        parentRegionCode: String
    ): DomainResult<List<Region>>

    suspend fun getRegionInfo(
        regionCode: String,
        params: RegionInfoQueryParams = RegionInfoQueryParams.DEFAULT
    ): DomainResult<RegionInfo>

    suspend fun getAdjacentRegions(regionCode: String): DomainResult<List<Region>>
}
