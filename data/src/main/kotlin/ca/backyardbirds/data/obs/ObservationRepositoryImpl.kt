package ca.backyardbirds.data.obs

import ca.backyardbirds.core.network.NetworkResponse
import ca.backyardbirds.data.obs.dto.ObservationDto
import ca.backyardbirds.data.obs.dto.toDomain
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.repository.ObservationRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.*

class ObservationRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : ObservationRepository {

    override suspend fun getRecentObservations(regionCode: String): List<Observation> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent")

    override suspend fun getRecentNotableObservations(regionCode: String): List<Observation> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent/notable")

    override suspend fun getRecentObservationsOfSpecies(regionCode: String, speciesCode: String): List<Observation> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent/$speciesCode")

    override suspend fun getRecentNearbyObservations(lat: Double, lng: Double, distKm: Int?): List<Observation> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?
    ): List<Observation> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent/$speciesCode",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?
    ): List<Observation> =
        fetchObservations(
            "$baseUrl/data/nearest/geo/recent/$speciesCode",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getRecentNearbyNotableObservations(lat: Double, lng: Double, distKm: Int?): List<Observation> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent/notable",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): List<Observation> =
        fetchObservations("$baseUrl/data/obs/$regionCode/historic/$year/$month/$day")

    private suspend fun fetchObservations(
        url: String,
        queryParams: Map<String, String?> = emptyMap()
    ): List<Observation> {
        return try {
            val response: List<ObservationDto> = client.get(url) {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                queryParams.forEach { (key, value) ->
                    if (!value.isNullOrBlank()) parameter(key, value)
                }
            }.body()
            response.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList() // You may want to handle errors differently
        }
    }
}
