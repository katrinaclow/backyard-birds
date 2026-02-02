package ca.backyardbirds.data.taxonomy

import ca.backyardbirds.data.taxonomy.dto.TaxaLocaleDto
import ca.backyardbirds.data.taxonomy.dto.TaxonomicGroupDto
import ca.backyardbirds.data.taxonomy.dto.TaxonomyEntryDto
import ca.backyardbirds.data.taxonomy.dto.TaxonomyVersionDto
import ca.backyardbirds.data.taxonomy.dto.toDomain
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.TaxaLocale
import ca.backyardbirds.domain.model.TaxonomicGroup
import ca.backyardbirds.domain.model.TaxonomyEntry
import ca.backyardbirds.domain.model.TaxonomyVersion
import ca.backyardbirds.domain.repository.TaxonomyRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class TaxonomyRepositoryImpl(
    private val apiKey: String,
    private val client: HttpClient,
    private val baseUrl: String = "https://api.ebird.org/v2"
) : TaxonomyRepository {

    override suspend fun getTaxonomy(
        speciesCodes: List<String>?,
        category: String?
    ): DomainResult<List<TaxonomyEntry>> {
        return try {
            val response = client.get("$baseUrl/ref/taxonomy/ebird") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
                parameter("fmt", "json")
                if (!speciesCodes.isNullOrEmpty()) {
                    parameter("species", speciesCodes.joinToString(","))
                }
                if (!category.isNullOrBlank()) {
                    parameter("cat", category)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<TaxonomyEntryDto>>().map { it.toDomain() }
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

    override suspend fun getSubspecies(speciesCode: String): DomainResult<List<String>> {
        return try {
            val response = client.get("$baseUrl/ref/taxon/forms/$speciesCode") {
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
                    "Species not found: $speciesCode"
                )
                else -> DomainResult.Failure(
                    "eBird API returned ${response.status.value}: ${response.status.description}"
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure("Network error: ${e.message}", cause = e)
        }
    }

    override suspend fun getTaxonomyVersions(): DomainResult<List<TaxonomyVersion>> {
        return try {
            val response = client.get("$baseUrl/ref/taxonomy/versions") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<TaxonomyVersionDto>>().map { it.toDomain() }
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

    override suspend fun getTaxaLocaleCodes(): DomainResult<List<TaxaLocale>> {
        return try {
            val response = client.get("$baseUrl/ref/taxonomy/locales") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<TaxaLocaleDto>>().map { it.toDomain() }
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

    override suspend fun getTaxonomicGroups(speciesGrouping: String): DomainResult<List<TaxonomicGroup>> {
        return try {
            val response = client.get("$baseUrl/ref/sppgroup/$speciesGrouping") {
                headers {
                    append("X-eBirdApiToken", apiKey)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> DomainResult.Success(
                    response.body<List<TaxonomicGroupDto>>().map { it.toDomain() }
                )
                HttpStatusCode.Unauthorized -> DomainResult.Failure(
                    "Authentication failed — verify your eBird API key"
                )
                HttpStatusCode.NotFound -> DomainResult.Failure(
                    "Species grouping not found: $speciesGrouping"
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
