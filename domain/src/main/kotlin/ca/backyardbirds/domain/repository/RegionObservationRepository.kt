package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation

interface RegionObservationRepository {
    suspend fun getRecentObservations(
        regionCode: String
    ): DomainResult<List<Observation>>

    suspend fun getRecentNotableObservations(
        regionCode: String
    ): DomainResult<List<Observation>>

    suspend fun getRecentObservationsOfSpecies(
        regionCode: String,
        speciesCode: String
    ): DomainResult<List<Observation>>

    suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<List<Observation>>
}
