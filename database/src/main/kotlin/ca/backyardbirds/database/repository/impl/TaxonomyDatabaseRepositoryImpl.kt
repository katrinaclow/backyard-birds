package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.mapper.toJsonArray
import ca.backyardbirds.database.mapper.toTaxonomyEntry
import ca.backyardbirds.database.repository.TaxonomyDatabaseRepository
import ca.backyardbirds.database.tables.TaxonomyTable
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.TaxonomyEntry
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant

class TaxonomyDatabaseRepositoryImpl(
    private val database: Database
) : TaxonomyDatabaseRepository {

    override suspend fun saveTaxonomy(entries: List<TaxonomyEntry>): DomainResult<Unit> = try {
        dbQuery {
            entries.forEach { entry ->
                TaxonomyTable.upsert {
                    it[speciesCode] = entry.speciesCode
                    it[commonName] = entry.commonName
                    it[scientificName] = entry.scientificName
                    it[category] = entry.category
                    it[taxonOrder] = entry.taxonOrder
                    it[bandingCodes] = entry.bandingCodes.toJsonArray()
                    it[comNameCodes] = entry.comNameCodes.toJsonArray()
                    it[sciNameCodes] = entry.sciNameCodes.toJsonArray()
                    it[taxonOrderName] = entry.order
                    it[familyCode] = entry.familyCode
                    it[familyCommonName] = entry.familyComName
                    it[familyScientificName] = entry.familySciName
                    it[createdAt] = Instant.now()
                    it[updatedAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save taxonomy: ${e.message}", e)
    }

    override suspend fun getTaxonomy(
        speciesCodes: List<String>?,
        category: String?
    ): DomainResult<List<TaxonomyEntry>> = try {
        val entries = dbQuery {
            var query = TaxonomyTable.selectAll()

            if (speciesCodes != null && speciesCodes.isNotEmpty()) {
                query = query.where { TaxonomyTable.speciesCode inList speciesCodes }
            }

            if (category != null) {
                query = if (speciesCodes != null && speciesCodes.isNotEmpty()) {
                    query.andWhere { TaxonomyTable.category eq category }
                } else {
                    query.where { TaxonomyTable.category eq category }
                }
            }

            query.orderBy(TaxonomyTable.taxonOrder, SortOrder.ASC)
                .map { it.toTaxonomyEntry() }
        }
        DomainResult.Success(entries)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get taxonomy: ${e.message}", e)
    }

    override suspend fun getTaxonomyEntry(speciesCode: String): DomainResult<TaxonomyEntry?> = try {
        val entry = dbQuery {
            TaxonomyTable.selectAll()
                .where { TaxonomyTable.speciesCode eq speciesCode }
                .singleOrNull()
                ?.toTaxonomyEntry()
        }
        DomainResult.Success(entry)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get taxonomy entry: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
