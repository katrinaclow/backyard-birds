package ca.backyardbirds.data.specieslist

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.SpeciesListRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class SpeciesListRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : SpeciesListRepository {

    override suspend fun getSpeciesInRegion(regionCode: String): DomainResult<List<String>> {
        return try {
            val response = client.get("$baseUrl/product/spplist/$regionCode") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(response.body<List<String>>())
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
