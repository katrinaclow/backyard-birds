package ca.backyardbirds.features.api_explorer.service

import ca.backyardbirds.database.tables.RegionsTable
import ca.backyardbirds.database.tables.TaxonomyTable
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.features.api_explorer.model.RegionSearchResult
import ca.backyardbirds.features.api_explorer.model.SpeciesSearchResult
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Service for searching species and regions with fuzzy matching.
 * Used to power autocomplete in the API Explorer frontend.
 */
class SearchService(private val database: Database) {

    /**
     * Search species by common name, scientific name, or species code.
     * Case-insensitive partial matching.
     *
     * @param query Search term
     * @param limit Maximum results to return (default 20)
     * @return List of matching species
     */
    suspend fun searchSpecies(
        query: String,
        limit: Int = 20
    ): DomainResult<List<SpeciesSearchResult>> = try {
        val searchPattern = "%${query.trim().lowercase()}%"

        val results = dbQuery {
            TaxonomyTable.selectAll()
                .where {
                    (TaxonomyTable.commonName.lowerCase() like searchPattern) or
                    (TaxonomyTable.scientificName.lowerCase() like searchPattern) or
                    (TaxonomyTable.speciesCode.lowerCase() like searchPattern)
                }
                .orderBy(TaxonomyTable.commonName, SortOrder.ASC)
                .limit(limit)
                .map { row ->
                    SpeciesSearchResult(
                        speciesCode = row[TaxonomyTable.speciesCode],
                        commonName = row[TaxonomyTable.commonName],
                        scientificName = row[TaxonomyTable.scientificName],
                        category = row[TaxonomyTable.category]
                    )
                }
        }
        DomainResult.Success(results)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to search species: ${e.message}", e)
    }

    /**
     * Search regions by name or code.
     * Case-insensitive partial matching.
     *
     * @param query Search term
     * @param limit Maximum results to return (default 20)
     * @return List of matching regions
     */
    suspend fun searchRegions(
        query: String,
        limit: Int = 20
    ): DomainResult<List<RegionSearchResult>> = try {
        val searchPattern = "%${query.trim().lowercase()}%"

        val results = dbQuery {
            RegionsTable.selectAll()
                .where {
                    (RegionsTable.name.lowerCase() like searchPattern) or
                    (RegionsTable.code.lowerCase() like searchPattern)
                }
                .orderBy(RegionsTable.name, SortOrder.ASC)
                .limit(limit)
                .map { row ->
                    RegionSearchResult(
                        code = row[RegionsTable.code],
                        name = row[RegionsTable.name],
                        regionType = row[RegionsTable.regionType]
                    )
                }
        }
        DomainResult.Success(results)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to search regions: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
