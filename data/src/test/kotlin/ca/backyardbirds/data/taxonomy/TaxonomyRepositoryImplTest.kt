package ca.backyardbirds.data.taxonomy

import ca.backyardbirds.domain.model.DomainResult
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaxonomyRepositoryImplTest {

    private fun repositoryWith(handler: MockRequestHandler): TaxonomyRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return TaxonomyRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    private val sampleTaxonomyJson = """
        [
            {
                "speciesCode": "hoocro1",
                "comName": "Hooded Crow",
                "sciName": "Corvus cornix",
                "category": "species",
                "taxonOrder": 24143.0,
                "bandingCodes": ["HOCR"],
                "comNameCodes": ["HOCR"],
                "sciNameCodes": ["COCO"],
                "order": "Passeriformes",
                "familyCode": "corvid1",
                "familyComName": "Crows, Jays, and Magpies",
                "familySciName": "Corvidae"
            }
        ]
    """.trimIndent()

    private val sampleVersionsJson = """
        [
            {"authorityVer": 2024.0, "latest": true},
            {"authorityVer": 2023.0, "latest": false}
        ]
    """.trimIndent()

    private val sampleLocalesJson = """
        [
            {"code": "en", "name": "English", "lastUpdate": "2024-01-15"},
            {"code": "de", "name": "German", "lastUpdate": "2024-01-10"}
        ]
    """.trimIndent()

    private val sampleGroupsJson = """
        [
            {"groupName": "Waterfowl", "groupOrder": 1, "taxonOrderBounds": [[1.0, 100.0]]},
            {"groupName": "Raptors", "groupOrder": 2, "taxonOrderBounds": [[101.0, 200.0]]}
        ]
    """.trimIndent()

    // --- getTaxonomy ---

    @Test
    fun getTaxonomy_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleTaxonomyJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getTaxonomy()

        assertTrue(result is DomainResult.Success)
        assertEquals(1, (result as DomainResult.Success).data.size)
        assertEquals("hoocro1", result.data.first().speciesCode)
    }

    @Test
    fun getTaxonomy_returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getTaxonomy()

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    @Test
    fun getTaxonomy_includesSpeciesParam_whenProvided() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTaxonomy(speciesCodes = listOf("hoocro1", "bkcchi"))

        assertTrue(capturedUrl!!.contains("species=hoocro1%2Cbkcchi") || capturedUrl!!.contains("species=hoocro1,bkcchi"))
    }

    @Test
    fun getTaxonomy_includesCategoryParam_whenProvided() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTaxonomy(category = "species")

        assertTrue(capturedUrl!!.contains("cat=species"))
    }

    @Test
    fun getTaxonomy_includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTaxonomy()

        assertEquals("test-api-key", capturedToken)
    }

    // --- getSubspecies ---

    @Test
    fun getSubspecies_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = """["hoocro1", "hoocro2"]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getSubspecies("hoocro1")

        assertTrue(result is DomainResult.Success)
        assertEquals(listOf("hoocro1", "hoocro2"), (result as DomainResult.Success).data)
    }

    @Test
    fun getSubspecies_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getSubspecies("hoocro1")

        assertTrue(capturedUrl!!.contains("/ref/taxon/forms/hoocro1"))
    }

    @Test
    fun getSubspecies_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getSubspecies("invalid")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Species not found"))
    }

    // --- getTaxonomyVersions ---

    @Test
    fun getTaxonomyVersions_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleVersionsJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getTaxonomyVersions()

        assertTrue(result is DomainResult.Success)
        assertEquals(2, (result as DomainResult.Success).data.size)
        assertEquals(2024.0, result.data.first().authorityVer)
        assertEquals(true, result.data.first().latest)
    }

    @Test
    fun getTaxonomyVersions_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTaxonomyVersions()

        assertTrue(capturedUrl!!.contains("/ref/taxonomy/versions"))
    }

    // --- getTaxaLocaleCodes ---

    @Test
    fun getTaxaLocaleCodes_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleLocalesJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getTaxaLocaleCodes()

        assertTrue(result is DomainResult.Success)
        assertEquals(2, (result as DomainResult.Success).data.size)
        assertEquals("en", result.data.first().code)
        assertEquals("English", result.data.first().name)
    }

    @Test
    fun getTaxaLocaleCodes_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTaxaLocaleCodes()

        assertTrue(capturedUrl!!.contains("/ref/taxonomy/locales"))
    }

    // --- getTaxonomicGroups ---

    @Test
    fun getTaxonomicGroups_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleGroupsJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getTaxonomicGroups("merlin")

        assertTrue(result is DomainResult.Success)
        assertEquals(2, (result as DomainResult.Success).data.size)
        assertEquals("Waterfowl", result.data.first().groupName)
        assertEquals(1, result.data.first().groupOrder)
    }

    @Test
    fun getTaxonomicGroups_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTaxonomicGroups("merlin")

        assertTrue(capturedUrl!!.contains("/ref/sppgroup/merlin"))
    }

    @Test
    fun getTaxonomicGroups_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getTaxonomicGroups("invalid")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Species grouping not found"))
    }

    // --- Error handling ---

    @Test
    fun returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getTaxonomy()

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
    }
}
