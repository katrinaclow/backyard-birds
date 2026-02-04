package ca.backyardbirds.data.statistics

import ca.backyardbirds.data.statistics.dto.RegionStatsDto
import ca.backyardbirds.data.statistics.dto.TopObserverDto
import ca.backyardbirds.data.statistics.dto.toDomain
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import ca.backyardbirds.domain.query.Top100QueryParams
import ca.backyardbirds.domain.repository.StatisticsRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class StatisticsRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : StatisticsRepository {

    override suspend fun getTop100(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        params: Top100QueryParams
    ): DomainResult<List<TopObserver>> {
        return try {
            val response = client.get("$baseUrl/product/top100/$regionCode/$year/$month/$day") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                params.rankedBy?.let { parameter("rankedBy", it) }
                params.maxResults?.let { parameter("maxResults", it) }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<TopObserverDto>>().map { it.toDomain() }
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Resource not found for region: $regionCode"
                )
                else -> DomainResult.Failure(
                    "eBird API returned ${response.status.value}: ${response.status.description}"
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure("Network error: ${e.message}", cause = e)
        }
    }

    override suspend fun getRegionStats(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<RegionStats> {
        return try {
            val response = client.get("$baseUrl/product/stats/$regionCode/$year/$month/$day") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<RegionStatsDto>().toDomain()
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Resource not found for region: $regionCode"
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
