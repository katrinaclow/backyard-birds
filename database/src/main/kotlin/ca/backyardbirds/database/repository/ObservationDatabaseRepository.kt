package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import java.time.LocalDateTime

interface ObservationDatabaseRepository {
    suspend fun saveObservations(
        observations: List<Observation>,
        regionCode: String,
        isNotable: Boolean = false
    ): DomainResult<Unit>

    suspend fun getRecentObservations(
        regionCode: String,
        limit: Int = 100
    ): DomainResult<List<Observation>>

    suspend fun getRecentNotableObservations(
        regionCode: String,
        limit: Int = 100
    ): DomainResult<List<Observation>>

    suspend fun getObservationsOfSpecies(
        regionCode: String,
        speciesCode: String,
        limit: Int = 100
    ): DomainResult<List<Observation>>

    suspend fun getNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int,
        limit: Int = 100
    ): DomainResult<List<Observation>>

    suspend fun deleteOldObservations(olderThan: LocalDateTime): DomainResult<Int>
}
