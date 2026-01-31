package ca.backyardbirds.data.obs

import ca.backyardbirds.data.obs.dto.ObservationDto
import ca.backyardbirds.data.obs.dto.toDomain
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.repository.NearbyObservationRepository
import ca.backyardbirds.domain.repository.RegionObservationRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ObservationRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : RegionObservationRepository, NearbyObservationRepository {

    override suspend fun getRecentObservations(regionCode: String): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent")

    override suspend fun getRecentNotableObservations(regionCode: String): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent/notable")

    override suspend fun getRecentObservationsOfSpecies(regionCode: String, speciesCode: String): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent/$speciesCode")

    override suspend fun getRecentNearbyObservations(lat: Double, lng: Double, distKm: Int?): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?
    ): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent/$speciesCode",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?
    ): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/nearest/geo/recent/$speciesCode",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getRecentNearbyNotableObservations(lat: Double, lng: Double, distKm: Int?): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent/notable",
            mapOf("lat" to "$lat", "lng" to "$lng", "dist" to distKm?.toString())
        )

    override suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/historic/$year/$month/$day")

    private suspend fun fetchObservations(
        url: String,
        queryParams: Map<String, String?> = emptyMap()
    ): DomainResult<List<Observation>> {
        return try {
            val response = client.get(url) {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                queryParams.forEach { (key, value) ->
                    if (!value.isNullOrBlank()) parameter(key, value)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<ObservationDto>>().map { it.toDomain() }
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Resource not found: $url"
                )
                else -> DomainResult.Failure(
                    "eBird API returned ${response.status.value}: ${response.status.description}"
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure("Network error: ${e.message}", cause = e)
        }
    }
}
