package ca.backyardbirds.data.statistics

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

class StatisticsRepositoryImplTest {

    private fun repositoryWith(handler: MockRequestHandler): StatisticsRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return StatisticsRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    private val sampleTop100Json = """
        [
            {
                "userDisplayName": "John Birder",
                "numSpecies": 150,
                "numCompleteChecklists": 45,
                "rowNum": 1,
                "userId": "USR123"
            },
            {
                "userDisplayName": "Jane Observer",
                "numSpecies": 140,
                "numCompleteChecklists": 40,
                "rowNum": 2,
                "userId": "USR456"
            }
        ]
    """.trimIndent()

    private val sampleRegionStatsJson = """
        {
            "numChecklists": 5000,
            "numContributors": 250,
            "numSpecies": 400
        }
    """.trimIndent()

    // --- getTop100 ---

    @Test
    fun getTop100_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleTop100Json,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getTop100("US", 2024, 1, 15)

        assertTrue(result is DomainResult.Success)
        assertEquals(2, (result as DomainResult.Success).data.size)
        assertEquals("John Birder", result.data.first().userDisplayName)
        assertEquals(150, result.data.first().numSpecies)
    }

    @Test
    fun getTop100_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTop100("US", 2024, 1, 15)

        assertTrue(capturedUrl!!.contains("/product/top100/US/2024/1/15"))
    }

    @Test
    fun getTop100_includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getTop100("US", 2024, 1, 15)

        assertEquals("test-api-key", capturedToken)
    }

    @Test
    fun getTop100_returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getTop100("US", 2024, 1, 15)

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    @Test
    fun getTop100_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getTop100("INVALID", 2024, 1, 15)

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Resource not found"))
    }

    // --- getRegionStats ---

    @Test
    fun getRegionStats_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleRegionStatsJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getRegionStats("US", 2024, 1, 15)

        assertTrue(result is DomainResult.Success)
        assertEquals(5000, (result as DomainResult.Success).data.numChecklists)
        assertEquals(250, result.data.numContributors)
        assertEquals(400, result.data.numSpecies)
    }

    @Test
    fun getRegionStats_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(
                content = sampleRegionStatsJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        repo.getRegionStats("US", 2024, 1, 15)

        assertTrue(capturedUrl!!.contains("/product/stats/US/2024/1/15"))
    }

    @Test
    fun getRegionStats_returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getRegionStats("US", 2024, 1, 15)

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    // --- Error handling ---

    @Test
    fun returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getTop100("US", 2024, 1, 15)

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
    }
}
