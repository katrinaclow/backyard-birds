package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.TaxonomyEntry

interface TaxonomyDatabaseRepository {
    suspend fun saveTaxonomy(entries: List<TaxonomyEntry>): DomainResult<Unit>

    suspend fun getTaxonomy(
        speciesCodes: List<String>? = null,
        category: String? = null
    ): DomainResult<List<TaxonomyEntry>>

    suspend fun getTaxonomyEntry(speciesCode: String): DomainResult<TaxonomyEntry?>

    suspend fun count(): Long
}
