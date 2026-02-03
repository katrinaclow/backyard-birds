package ca.backyardbirds.data.specieslist

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.SpeciesListDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.SpeciesListRepository
import kotlin.time.Duration.Companion.days

class CachingSpeciesListRepository(
    private val apiRepository: SpeciesListRepository,
    private val dbRepository: SpeciesListDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : SpeciesListRepository {

    companion object {
        private val SPECIES_LIST_TTL = 7.days
        private const val ENTITY_TYPE = "species_list"
    }

    override suspend fun getSpeciesInRegion(regionCode: String): DomainResult<List<String>> {
        val cacheKey = "species_list:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getSpeciesInRegion(regionCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getSpeciesInRegion(regionCode)) {
            is DomainResult.Success -> {
                dbRepository.saveSpeciesInRegion(regionCode, apiResult.data)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, regionCode, SPECIES_LIST_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }
}
