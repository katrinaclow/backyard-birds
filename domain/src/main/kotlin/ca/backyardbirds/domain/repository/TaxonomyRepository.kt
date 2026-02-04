package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.TaxaLocale
import ca.backyardbirds.domain.model.TaxonomicGroup
import ca.backyardbirds.domain.model.TaxonomyEntry
import ca.backyardbirds.domain.model.TaxonomyVersion
import ca.backyardbirds.domain.query.TaxonomyQueryParams

interface TaxonomyRepository {
    suspend fun getTaxonomy(
        speciesCodes: List<String>? = null,
        category: String? = null,
        params: TaxonomyQueryParams = TaxonomyQueryParams.DEFAULT
    ): DomainResult<List<TaxonomyEntry>>

    suspend fun getSubspecies(speciesCode: String): DomainResult<List<String>>

    suspend fun getTaxonomyVersions(): DomainResult<List<TaxonomyVersion>>

    suspend fun getTaxaLocaleCodes(): DomainResult<List<TaxaLocale>>

    suspend fun getTaxonomicGroups(speciesGrouping: String): DomainResult<List<TaxonomicGroup>>
}
