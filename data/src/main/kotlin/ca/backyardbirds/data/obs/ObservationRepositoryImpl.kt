package ca.backyardbirds.data.obs

import ca.backyardbirds.data.obs.dto.ObservationDto
import ca.backyardbirds.data.obs.dto.toDomain
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.query.HistoricQueryParams
import ca.backyardbirds.domain.query.ObservationQueryParams
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

    override suspend fun getRecentObservations(
        regionCode: String,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent", params.toQueryMap())

    override suspend fun getRecentNotableObservations(
        regionCode: String,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent/notable", params.toQueryMap())

    override suspend fun getRecentObservationsOfSpecies(
        regionCode: String,
        speciesCode: String,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/recent/$speciesCode", params.toQueryMap())

    override suspend fun getRecentNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int?,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent",
            buildMap {
                put("lat", "$lat")
                put("lng", "$lng")
                distKm?.let { put("dist", it.toString()) }
                putAll(params.toQueryMap())
            }
        )

    override suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent/$speciesCode",
            buildMap {
                put("lat", "$lat")
                put("lng", "$lng")
                distKm?.let { put("dist", it.toString()) }
                putAll(params.toQueryMap())
            }
        )

    override suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/nearest/geo/recent/$speciesCode",
            buildMap {
                put("lat", "$lat")
                put("lng", "$lng")
                distKm?.let { put("dist", it.toString()) }
                putAll(params.toQueryMap())
            }
        )

    override suspend fun getRecentNearbyNotableObservations(
        lat: Double,
        lng: Double,
        distKm: Int?,
        params: ObservationQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations(
            "$baseUrl/data/obs/geo/recent/notable",
            buildMap {
                put("lat", "$lat")
                put("lng", "$lng")
                distKm?.let { put("dist", it.toString()) }
                putAll(params.toQueryMap())
            }
        )

    override suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        params: HistoricQueryParams
    ): DomainResult<List<Observation>> =
        fetchObservations("$baseUrl/data/obs/$regionCode/historic/$year/$month/$day", params.toQueryMap())

    private suspend fun fetchObservations(
        url: String,
        queryParams: Map<String, String> = emptyMap()
    ): DomainResult<List<Observation>> {
        return try {
            val response = client.get(url) {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                queryParams.forEach { (key, value) ->
                    parameter(key, value)
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

    private fun ObservationQueryParams.toQueryMap(): Map<String, String> = buildMap {
        back?.let { put("back", it.toString()) }
        hotspot?.let { put("hotspot", it.toString()) }
        includeProvisional?.let { put("includeProvisional", it.toString()) }
        maxResults?.let { put("maxResults", it.toString()) }
        sppLocale?.let { put("sppLocale", it) }
        cat?.let { put("cat", it) }
        sort?.let { put("sort", it) }
    }

    private fun HistoricQueryParams.toQueryMap(): Map<String, String> = buildMap {
        rank?.let { put("rank", it) }
        detail?.let { put("detail", it) }
        hotspot?.let { put("hotspot", it.toString()) }
        includeProvisional?.let { put("includeProvisional", it.toString()) }
        maxResults?.let { put("maxResults", it.toString()) }
        sppLocale?.let { put("sppLocale", it) }
        cat?.let { put("cat", it) }
    }
}
