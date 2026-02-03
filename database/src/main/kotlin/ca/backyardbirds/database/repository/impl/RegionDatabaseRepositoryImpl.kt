package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.mapper.toRegion
import ca.backyardbirds.database.mapper.toRegionInfo
import ca.backyardbirds.database.repository.RegionDatabaseRepository
import ca.backyardbirds.database.tables.AdjacentRegionsTable
import ca.backyardbirds.database.tables.RegionsTable
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionInfo
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant

class RegionDatabaseRepositoryImpl(
    private val database: Database
) : RegionDatabaseRepository {

    override suspend fun saveRegions(
        regions: List<Region>,
        regionType: String,
        parentCode: String?
    ): DomainResult<Unit> = try {
        dbQuery {
            regions.forEach { region ->
                RegionsTable.upsert {
                    it[code] = region.code
                    it[name] = region.name
                    it[RegionsTable.regionType] = regionType
                    it[RegionsTable.parentCode] = parentCode
                    it[createdAt] = Instant.now()
                    it[updatedAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save regions: ${e.message}", e)
    }

    override suspend fun saveRegionInfo(regionInfo: RegionInfo): DomainResult<Unit> = try {
        dbQuery {
            RegionsTable.upsert {
                it[code] = regionInfo.code
                it[name] = regionInfo.name
                it[boundsMinX] = regionInfo.bounds?.minX
                it[boundsMaxX] = regionInfo.bounds?.maxX
                it[boundsMinY] = regionInfo.bounds?.minY
                it[boundsMaxY] = regionInfo.bounds?.maxY
                it[updatedAt] = Instant.now()
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save region info: ${e.message}", e)
    }

    override suspend fun getSubRegions(
        regionType: String,
        parentRegionCode: String
    ): DomainResult<List<Region>> = try {
        val regions = dbQuery {
            RegionsTable.selectAll()
                .where {
                    (RegionsTable.regionType eq regionType) and
                    (RegionsTable.parentCode eq parentRegionCode)
                }
                .map { it.toRegion() }
        }
        DomainResult.Success(regions)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get sub-regions: ${e.message}", e)
    }

    override suspend fun getRegionInfo(regionCode: String): DomainResult<RegionInfo?> = try {
        val regionInfo = dbQuery {
            RegionsTable.selectAll()
                .where { RegionsTable.code eq regionCode }
                .singleOrNull()
                ?.toRegionInfo()
        }
        DomainResult.Success(regionInfo)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get region info: ${e.message}", e)
    }

    override suspend fun saveAdjacentRegions(
        regionCode: String,
        adjacentRegions: List<Region>
    ): DomainResult<Unit> = try {
        dbQuery {
            // First delete existing adjacent regions
            AdjacentRegionsTable.deleteWhere {
                AdjacentRegionsTable.regionCode eq regionCode
            }

            // Then insert new adjacent regions
            adjacentRegions.forEach { adjacent ->
                AdjacentRegionsTable.insert {
                    it[AdjacentRegionsTable.regionCode] = regionCode
                    it[adjacentRegionCode] = adjacent.code
                }

                // Also ensure the adjacent region exists in regions table
                RegionsTable.upsert {
                    it[code] = adjacent.code
                    it[name] = adjacent.name
                    it[createdAt] = Instant.now()
                    it[updatedAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save adjacent regions: ${e.message}", e)
    }

    override suspend fun getAdjacentRegions(regionCode: String): DomainResult<List<Region>> = try {
        val regions = dbQuery {
            (AdjacentRegionsTable innerJoin RegionsTable)
                .selectAll()
                .where {
                    (AdjacentRegionsTable.regionCode eq regionCode) and
                    (AdjacentRegionsTable.adjacentRegionCode eq RegionsTable.code)
                }
                .map { it.toRegion() }
        }
        DomainResult.Success(regions)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get adjacent regions: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
