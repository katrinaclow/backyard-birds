package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.query.ObservationQueryParams

interface NearbyObservationRepository {
    suspend fun getRecentNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int? = null,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>

    suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int? = null,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>

    suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int? = null,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>

    suspend fun getRecentNearbyNotableObservations(
        lat: Double,
        lng: Double,
        distKm: Int? = null,
        params: ObservationQueryParams = ObservationQueryParams.DEFAULT
    ): DomainResult<List<Observation>>
}
