package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult

interface SpeciesListRepository {
    suspend fun getSpeciesInRegion(regionCode: String): DomainResult<List<String>>
}
