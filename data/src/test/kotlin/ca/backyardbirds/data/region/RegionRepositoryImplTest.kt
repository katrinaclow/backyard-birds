package ca.backyardbirds.data.region

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

class RegionRepositoryImplTest {

    private fun repositoryWith(handler: MockRequestHandler): RegionRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return RegionRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    private val sampleRegionsJson = """
        [
            {"code": "US-CA", "name": "California"},
            {"code": "US-TX", "name": "Texas"}
        ]
    """.trimIndent()

    private val sampleRegionInfoJson = """
        {
            "code": "US",
            "name": "United States",
            "bounds": {
                "minX": -124.733,
                "maxX": -66.954,
                "minY": 24.544,
                "maxY": 49.384
            }
        }
    """.trimIndent()

    // --- getSubRegions ---

    @Test
    fun getSubRegions_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleRegionsJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getSubRegions("subnational1", "US")

        assertTrue(result is DomainResult.Success)
        assertEquals(2, (result as DomainResult.Success).data.size)
        assertEquals("US-CA", result.data.first().code)
    }

    @Test
    fun getSubRegions_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getSubRegions("subnational1", "US")

        assertTrue(capturedUrl!!.contains("/ref/region/list/subnational1/US"))
    }

    @Test
    fun getSubRegions_includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getSubRegions("subnational1", "US")

        assertEquals("test-api-key", capturedToken)
    }

    @Test
    fun getSubRegions_returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getSubRegions("subnational1", "US")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    // --- getRegionInfo ---

    @Test
    fun getRegionInfo_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleRegionInfoJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getRegionInfo("US")

        assertTrue(result is DomainResult.Success)
        assertEquals("US", (result as DomainResult.Success).data.code)
        assertEquals("United States", result.data.name)
        assertEquals(-124.733, result.data.bounds?.minX)
    }

    @Test
    fun getRegionInfo_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(
                content = """{"code": "US", "name": "United States"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        repo.getRegionInfo("US")

        assertTrue(capturedUrl!!.contains("/ref/region/info/US"))
    }

    @Test
    fun getRegionInfo_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getRegionInfo("INVALID")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Region not found"))
    }

    // --- getAdjacentRegions ---

    @Test
    fun getAdjacentRegions_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleRegionsJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getAdjacentRegions("US-CA")

        assertTrue(result is DomainResult.Success)
        assertEquals(2, (result as DomainResult.Success).data.size)
    }

    @Test
    fun getAdjacentRegions_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getAdjacentRegions("US-CA")

        assertTrue(capturedUrl!!.contains("/ref/adjacent/US-CA"))
    }

    // --- Error handling ---

    @Test
    fun returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getSubRegions("subnational1", "US")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
    }
}
