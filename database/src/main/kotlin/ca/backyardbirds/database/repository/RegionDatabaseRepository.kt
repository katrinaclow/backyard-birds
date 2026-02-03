package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionInfo

interface RegionDatabaseRepository {
    suspend fun saveRegions(regions: List<Region>, regionType: String, parentCode: String?): DomainResult<Unit>

    suspend fun saveRegionInfo(regionInfo: RegionInfo): DomainResult<Unit>

    suspend fun getSubRegions(regionType: String, parentRegionCode: String): DomainResult<List<Region>>

    suspend fun getRegionInfo(regionCode: String): DomainResult<RegionInfo?>

    suspend fun saveAdjacentRegions(regionCode: String, adjacentRegions: List<Region>): DomainResult<Unit>

    suspend fun getAdjacentRegions(regionCode: String): DomainResult<List<Region>>
}
