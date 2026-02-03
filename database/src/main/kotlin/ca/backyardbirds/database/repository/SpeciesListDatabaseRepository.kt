package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult

interface SpeciesListDatabaseRepository {
    suspend fun saveSpeciesInRegion(
        regionCode: String,
        speciesCodes: List<String>
    ): DomainResult<Unit>

    suspend fun getSpeciesInRegion(regionCode: String): DomainResult<List<String>>
}
