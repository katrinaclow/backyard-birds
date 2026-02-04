package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.query.HistoricQueryParams
import ca.backyardbirds.domain.query.ObservationQueryParams

interface RegionObservationRepository {
    suspend fun getRecentObservations(
        regionCode: String,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>

    suspend fun getRecentNotableObservations(
        regionCode: String,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>

    suspend fun getRecentObservationsOfSpecies(
        regionCode: String,
        speciesCode: String,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>

    suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        params: HistoricQueryParams = HistoricQueryParams.DEFAULT
    ): DomainResult<List<Observation>>
}
