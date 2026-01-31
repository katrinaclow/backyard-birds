package ca.backyardbirds.data.obs

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

class ObservationRepositoryImplTest {
    // Creates a repository backed by a mock HTTP client.
    // The handler receives each outgoing request and returns a canned response.
    private fun repositoryWith(handler: MockRequestHandler): ObservationRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return ObservationRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    // Minimal valid eBird observation JSON.
    // TODO: Swap in real response payloads from https://ebird-api-ui.com/ for richer fixtures.
    private val sampleObservationJson = """
        [
            {
                "speciesCode": "hoocro1",
                "comName": "Hooded Crow",
                "sciName": "Corvus cornix",
                "locId": "L7884500",
                "locName": "Test Location",
                "obsDt": "2020-01-21 16:35",
                "howMany": 1,
                "lat": 43.530936,
                "lng": 79.455132,
                "obsValid": true,
                "obsReviewed": false,
                "locationPrivate": true,
                "subId": "S63619695"
            }
        ]
    """.trimIndent()

    // --- Error handling (all endpoints share fetchObservations, so testing one covers the logic) ---

    @Test
    fun returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleObservationJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getRecentObservations("US")

        assertTrue(result is DomainResult.Success)
        assertEquals(1, (result as DomainResult.Success).data.size)
        assertEquals("hoocro1", result.data.first().speciesCode)
    }

    @Test
    fun returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getRecentObservations("US")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    @Test
    fun returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getRecentObservations("US")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("not found"))
    }

    @Test
    fun returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getRecentObservations("US")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
        assertTrue((result as DomainResult.Failure).cause != null)
    }

    // --- API key header ---

    @Test
    fun includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentObservations("US")

        assertEquals("test-api-key", capturedToken)
    }

    // --- URL construction per endpoint ---
    // Verify each method builds the correct eBird API path.
    // Cross-reference paths against https://ebird-api-ui.com/ to confirm correctness.

    @Test
    fun getRecentObservations_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentObservations("US")

        assertTrue(capturedUrl!!.contains("/data/obs/US/recent"))
    }

    @Test
    fun getRecentNotableObservations_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentNotableObservations("CA")

        assertTrue(capturedUrl!!.contains("/data/obs/CA/recent/notable"))
    }

    @Test
    fun getRecentObservationsOfSpecies_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentObservationsOfSpecies("US", "hoocro1")

        assertTrue(capturedUrl!!.contains("/data/obs/US/recent/hoocro1"))
    }

    @Test
    fun getRecentNearbyObservations_includesGeoQueryParams() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentNearbyObservations(43.5, 79.4, distKm = 10)

        assertTrue(capturedUrl!!.contains("lat=43.5"))
        assertTrue(capturedUrl!!.contains("lng=79.4"))
        assertTrue(capturedUrl!!.contains("dist=10"))
    }

    @Test
    fun getRecentNearbyObservations_omitsDistWhenNull() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentNearbyObservations(43.5, 79.4, distKm = null)

        assertTrue(!capturedUrl!!.contains("dist="))
    }

    @Test
    fun getHistoricObservations_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getHistoricObservations("US", 2020, 6, 15)

        assertTrue(capturedUrl!!.contains("/data/obs/US/historic/2020/6/15"))
    }
}
