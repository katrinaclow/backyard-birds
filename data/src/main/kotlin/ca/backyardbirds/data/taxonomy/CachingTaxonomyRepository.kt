package ca.backyardbirds.data.taxonomy

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.TaxonomyDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.TaxaLocale
import ca.backyardbirds.domain.model.TaxonomicGroup
import ca.backyardbirds.domain.model.TaxonomyEntry
import ca.backyardbirds.domain.model.TaxonomyVersion
import ca.backyardbirds.domain.query.TaxonomyQueryParams
import ca.backyardbirds.domain.repository.TaxonomyRepository
import kotlin.time.Duration.Companion.days

class CachingTaxonomyRepository(
    private val apiRepository: TaxonomyRepository,
    private val dbRepository: TaxonomyDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : TaxonomyRepository {

    companion object {
        private val TAXONOMY_TTL = 30.days
        private const val ENTITY_TYPE = "taxonomy"
    }

    override suspend fun getTaxonomy(
        speciesCodes: List<String>?,
        category: String?,
        params: TaxonomyQueryParams
    ): DomainResult<List<TaxonomyEntry>> {
        val cacheKey = buildCacheKey(speciesCodes, category, params)

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getTaxonomy(speciesCodes, category)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getTaxonomy(speciesCodes, category, params)) {
            is DomainResult.Success -> {
                dbRepository.saveTaxonomy(apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, null, TAXONOMY_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getSubspecies(speciesCode: String): DomainResult<List<String>> {
        // Pass through - subspecies data is typically small and rarely cached
        return apiRepository.getSubspecies(speciesCode)
    }

    override suspend fun getTaxonomyVersions(): DomainResult<List<TaxonomyVersion>> {
        // Pass through - versions change rarely but should be fresh
        return apiRepository.getTaxonomyVersions()
    }

    override suspend fun getTaxaLocaleCodes(): DomainResult<List<TaxaLocale>> {
        // Pass through - locale codes are static reference data
        return apiRepository.getTaxaLocaleCodes()
    }

    override suspend fun getTaxonomicGroups(speciesGrouping: String): DomainResult<List<TaxonomicGroup>> {
        // Pass through - groupings are static reference data
        return apiRepository.getTaxonomicGroups(speciesGrouping)
    }

    private fun buildCacheKey(
        speciesCodes: List<String>?,
        category: String?,
        params: TaxonomyQueryParams
    ): String {
        val base = when {
            speciesCodes != null && speciesCodes.isNotEmpty() ->
                "taxonomy:species:${speciesCodes.sorted().joinToString(",")}"
            category != null ->
                "taxonomy:category:$category"
            else ->
                "taxonomy:all"
        }
        val suffix = params.toCacheKeySuffix()
        return base + suffix
    }

    private fun TaxonomyQueryParams.toCacheKeySuffix(): String {
        val parts = mutableListOf<String>()
        locale?.let { parts.add("locale=$it") }
        version?.let { parts.add("version=$it") }
        return if (parts.isEmpty()) "" else ":${parts.joinToString(":")}"
    }
}
