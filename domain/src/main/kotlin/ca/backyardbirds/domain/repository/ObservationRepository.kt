package ca.backyardbirds.domain.repository

import ca.backyardbirds.domain.model.Observation

interface ObservationRepository {
    // 1. Recent observations in a region
    suspend fun getRecentObservations(
        regionCode: String
    ): List<Observation>

    // 2. Recent notable observations in a region
    suspend fun getRecentNotableObservations(
        regionCode: String
    ): List<Observation>

    // 3. Recent observations of a species in a region
    suspend fun getRecentObservationsOfSpecies(
        regionCode: String,
        speciesCode: String
    ): List<Observation>

    // 4. Recent nearby observations
    suspend fun getRecentNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): List<Observation>

    // 5. Recent nearby observations of a species
    suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): List<Observation>

    // 6. Nearest observations of a species
    suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): List<Observation>

    // 7. Recent nearby notable observations
    suspend fun getRecentNearbyNotableObservations(
        lat: Double,
        lng: Double,
        distKm: Int? = null
    ): List<Observation>

    // 8. Historic observations on a date
    suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): List<Observation>
}
