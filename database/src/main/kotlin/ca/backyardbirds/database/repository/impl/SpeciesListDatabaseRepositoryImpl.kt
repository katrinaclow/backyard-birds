package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.repository.SpeciesListDatabaseRepository
import ca.backyardbirds.database.tables.RegionSpeciesListsTable
import ca.backyardbirds.domain.model.DomainResult
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant

class SpeciesListDatabaseRepositoryImpl(
    private val database: Database
) : SpeciesListDatabaseRepository {

    override suspend fun saveSpeciesInRegion(
        regionCode: String,
        speciesCodes: List<String>
    ): DomainResult<Unit> = try {
        dbQuery {
            // Delete existing species list for this region
            RegionSpeciesListsTable.deleteWhere {
                RegionSpeciesListsTable.regionCode eq regionCode
            }

            // Insert new species list
            speciesCodes.forEach { speciesCode ->
                RegionSpeciesListsTable.insert {
                    it[RegionSpeciesListsTable.regionCode] = regionCode
                    it[RegionSpeciesListsTable.speciesCode] = speciesCode
                    it[createdAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save species in region: ${e.message}", e)
    }

    override suspend fun getSpeciesInRegion(regionCode: String): DomainResult<List<String>> = try {
        val speciesCodes = dbQuery {
            RegionSpeciesListsTable.selectAll()
                .where { RegionSpeciesListsTable.regionCode eq regionCode }
                .map { it[RegionSpeciesListsTable.speciesCode] }
        }
        DomainResult.Success(speciesCodes)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get species in region: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
