package ca.backyardbirds.data.hotspot

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

class HotspotRepositoryImplTest {

    private fun repositoryWith(handler: MockRequestHandler): HotspotRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return HotspotRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    private val sampleHotspotJson = """
        [
            {
                "locId": "L7884500",
                "locName": "Central Park",
                "countryCode": "US",
                "subnational1Code": "US-NY",
                "subnational2Code": "US-NY-061",
                "lat": 40.7829,
                "lng": -73.9654,
                "latestObsDt": "2024-01-15 10:30",
                "numSpeciesAllTime": 275
            }
        ]
    """.trimIndent()

    private val sampleHotspotInfoJson = """
        {
            "locId": "L7884500",
            "locName": "Central Park",
            "countryCode": "US",
            "subnational1Code": "US-NY",
            "subnational2Code": "US-NY-061",
            "lat": 40.7829,
            "lng": -73.9654,
            "latestObsDt": "2024-01-15 10:30",
            "numSpeciesAllTime": 275
        }
    """.trimIndent()

    // --- getHotspotsInRegion ---

    @Test
    fun getHotspotsInRegion_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleHotspotJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getHotspotsInRegion("US-NY")

        assertTrue(result is DomainResult.Success)
        assertEquals(1, (result as DomainResult.Success).data.size)
        assertEquals("L7884500", result.data.first().locId)
    }

    @Test
    fun getHotspotsInRegion_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getHotspotsInRegion("US-NY")

        assertTrue(capturedUrl!!.contains("/ref/hotspot/US-NY"))
    }

    @Test
    fun getHotspotsInRegion_includesBackParam_whenProvided() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getHotspotsInRegion("US-NY", back = 7)

        assertTrue(capturedUrl!!.contains("back=7"))
    }

    @Test
    fun getHotspotsInRegion_includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getHotspotsInRegion("US-NY")

        assertEquals("test-api-key", capturedToken)
    }

    // --- getNearbyHotspots ---

    @Test
    fun getNearbyHotspots_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleHotspotJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getNearbyHotspots(40.78, -73.97)

        assertTrue(result is DomainResult.Success)
        assertEquals(1, (result as DomainResult.Success).data.size)
    }

    @Test
    fun getNearbyHotspots_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getNearbyHotspots(40.78, -73.97, distKm = 25, back = 14)

        assertTrue(capturedUrl!!.contains("/ref/hotspot/geo"))
        assertTrue(capturedUrl!!.contains("lat=40.78"))
        assertTrue(capturedUrl!!.contains("lng=-73.97"))
        assertTrue(capturedUrl!!.contains("dist=25"))
        assertTrue(capturedUrl!!.contains("back=14"))
    }

    @Test
    fun getNearbyHotspots_omitsOptionalParams_whenNull() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getNearbyHotspots(40.78, -73.97)

        assertTrue(!capturedUrl!!.contains("dist="))
        assertTrue(!capturedUrl!!.contains("back="))
    }

    // --- getHotspotInfo ---

    @Test
    fun getHotspotInfo_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleHotspotInfoJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getHotspotInfo("L7884500")

        assertTrue(result is DomainResult.Success)
        assertEquals("L7884500", (result as DomainResult.Success).data.locId)
        assertEquals("Central Park", result.data.locName)
    }

    @Test
    fun getHotspotInfo_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(
                content = sampleHotspotInfoJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        repo.getHotspotInfo("L7884500")

        assertTrue(capturedUrl!!.contains("/ref/hotspot/info/L7884500"))
    }

    @Test
    fun getHotspotInfo_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getHotspotInfo("INVALID")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Hotspot not found"))
    }

    // --- Error handling ---

    @Test
    fun returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getHotspotsInRegion("US-NY")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    @Test
    fun returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getHotspotsInRegion("US-NY")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
    }
}
