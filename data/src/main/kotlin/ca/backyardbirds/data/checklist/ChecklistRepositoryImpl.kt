package ca.backyardbirds.data.checklist

import ca.backyardbirds.data.checklist.dto.ChecklistDto
import ca.backyardbirds.data.checklist.dto.ChecklistSummaryDto
import ca.backyardbirds.data.checklist.dto.toDomain
import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.ChecklistRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ChecklistRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : ChecklistRepository {

    override suspend fun getRecentChecklists(
        regionCode: String,
        maxResults: Int?
    ): DomainResult<List<ChecklistSummary>> {
        return try {
            val response = client.get("$baseUrl/product/lists/$regionCode") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                if (maxResults != null) {
                    parameter("maxResults", maxResults.toString())
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<ChecklistSummaryDto>>().map { it.toDomain() }
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

    override suspend fun getChecklistsOnDate(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int,
        maxResults: Int?
    ): DomainResult<List<ChecklistSummary>> {
        return try {
            val response = client.get("$baseUrl/product/lists/$regionCode/$year/$month/$day") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                if (maxResults != null) {
                    parameter("maxResults", maxResults.toString())
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<ChecklistSummaryDto>>().map { it.toDomain() }
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

    override suspend fun getChecklist(subId: String): DomainResult<Checklist> {
        return try {
            val response = client.get("$baseUrl/product/checklist/view/$subId") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<ChecklistDto>().toDomain()
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Checklist not found: $subId"
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
