package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation

interface NearbyObservationRepository {
    suspend fun getRecentNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): DomainResult<List<Observation>>

    suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): DomainResult<List<Observation>>

    suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): DomainResult<List<Observation>>

    suspend fun getRecentNearbyNotableObservations(
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): DomainResult<List<Observation>>
}
