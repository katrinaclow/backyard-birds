package ca.backyardbirds.data.hotspot

import ca.backyardbirds.data.hotspot.dto.HotspotDto
import ca.backyardbirds.data.hotspot.dto.toDomain
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Hotspot
import ca.backyardbirds.domain.repository.HotspotRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class HotspotRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : HotspotRepository {

    override suspend fun getHotspotsInRegion(
        regionCode: String,
        back: Int?
    ): DomainResult<List<Hotspot>> {
        return try {
            val response = client.get("$baseUrl/ref/hotspot/$regionCode") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                parameter("fmt", "json")
                if (back != null) {
                    parameter("back", back.toString())
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<HotspotDto>>().map { it.toDomain() }
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Region not found: $regionCode"
                )
                else -> DomainResult.Failure(
                    "eBird API returned ${response.status.value}: ${response.status.description}"
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure("Network error: ${e.message}", cause = e)
        }
    }

    override suspend fun getNearbyHotspots(
        lat: Double,
        lng: Double,
        distKm: Int?,
        back: Int?
    ): DomainResult<List<Hotspot>> {
        return try {
            val response = client.get("$baseUrl/ref/hotspot/geo") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                parameter("fmt", "json")
                parameter("lat", lat.toString())
                parameter("lng", lng.toString())
                if (distKm != null) {
                    parameter("dist", distKm.toString())
                }
                if (back != null) {
                    parameter("back", back.toString())
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<HotspotDto>>().map { it.toDomain() }
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Resource not found"
                )
                else -> DomainResult.Failure(
                    "eBird API returned ${response.status.value}: ${response.status.description}"
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure("Network error: ${e.message}", cause = e)
        }
    }

    override suspend fun getHotspotInfo(locId: String): DomainResult<Hotspot> {
        return try {
            val response = client.get("$baseUrl/ref/hotspot/info/$locId") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<HotspotDto>().toDomain()
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Hotspot not found: $locId"
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
