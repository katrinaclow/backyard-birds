package ca.backyardbirds.data.region

import ca.backyardbirds.data.region.dto.RegionDto
import ca.backyardbirds.data.region.dto.RegionInfoDto
import ca.backyardbirds.data.region.dto.toDomain
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionInfo
import ca.backyardbirds.domain.query.RegionInfoQueryParams
import ca.backyardbirds.domain.repository.RegionRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class RegionRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : RegionRepository {

    override suspend fun getSubRegions(
        regionType: String,
        parentRegionCode: String
    ): DomainResult<List<Region>> {
        return try {
            val response = client.get("$baseUrl/ref/region/list/$regionType/$parentRegionCode") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<RegionDto>>().map { it.toDomain() }
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Region not found: $parentRegionCode"
                )
                else -> DomainResult.Failure(
                    "eBird API returned ${response.status.value}: ${response.status.description}"
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure("Network error: ${e.message}", cause = e)
        }
    }

    override suspend fun getRegionInfo(
        regionCode: String,
        params: RegionInfoQueryParams
    ): DomainResult<RegionInfo> {
        return try {
            val response = client.get("$baseUrl/ref/region/info/$regionCode") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                params.regionNameFormat?.let { parameter("regionNameFormat", it) }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<RegionInfoDto>().toDomain()
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

    override suspend fun getAdjacentRegions(regionCode: String): DomainResult<List<Region>> {
        return try {
            val response = client.get("$baseUrl/ref/adjacent/$regionCode") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<RegionDto>>().map { it.toDomain() }
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
}
