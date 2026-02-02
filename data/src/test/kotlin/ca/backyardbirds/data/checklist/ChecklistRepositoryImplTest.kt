package ca.backyardbirds.data.checklist

import ca.backyardbirds.domain.model.DomainResult
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChecklistRepositoryImplTest {

    private fun repositoryWith(handler: MockRequestHandler): ChecklistRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return ChecklistRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    private val sampleChecklistSummaryJson = """
        [
            {
                "subId": "S123456",
                "locId": "L7884500",
                "userDisplayName": "John Birder",
                "numSpecies": 25,
                "obsDt": "2024-01-15 10:30"
            }
        ]
    """.trimIndent()

    private val sampleChecklistJson = """
        {
            "subId": "S123456",
            "locId": "L7884500",
            "userDisplayName": "John Birder",
            "numSpecies": 2,
            "obsDt": "2024-01-15 10:30",
            "obs": [
                {"speciesCode": "hoocro1", "howManyStr": "5"},
                {"speciesCode": "bkcchi", "howManyStr": "X"}
            ]
        }
    """.trimIndent()

    // --- getRecentChecklists ---

    @Test
    fun getRecentChecklists_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleChecklistSummaryJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getRecentChecklists("US-NY")

        assertTrue(result is DomainResult.Success)
        assertEquals(1, (result as DomainResult.Success).data.size)
        assertEquals("S123456", result.data.first().subId)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), result.data.first().obsDt)
    }

    @Test
    fun getRecentChecklists_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentChecklists("US-NY")

        assertTrue(capturedUrl!!.contains("/product/lists/US-NY"))
    }

    @Test
    fun getRecentChecklists_includesMaxResultsParam_whenProvided() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentChecklists("US-NY", maxResults = 10)

        assertTrue(capturedUrl!!.contains("maxResults=10"))
    }

    @Test
    fun getRecentChecklists_includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getRecentChecklists("US-NY")

        assertEquals("test-api-key", capturedToken)
    }

    @Test
    fun getRecentChecklists_returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getRecentChecklists("US-NY")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    @Test
    fun getRecentChecklists_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getRecentChecklists("INVALID")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Region not found"))
    }

    // --- getChecklistsOnDate ---

    @Test
    fun getChecklistsOnDate_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleChecklistSummaryJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getChecklistsOnDate("US-NY", 2024, 1, 15)

        assertTrue(result is DomainResult.Success)
        assertEquals(1, (result as DomainResult.Success).data.size)
        assertEquals("S123456", result.data.first().subId)
    }

    @Test
    fun getChecklistsOnDate_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getChecklistsOnDate("US-NY", 2024, 1, 15)

        assertTrue(capturedUrl!!.contains("/product/lists/US-NY/2024/1/15"))
    }

    @Test
    fun getChecklistsOnDate_includesMaxResultsParam_whenProvided() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getChecklistsOnDate("US-NY", 2024, 1, 15, maxResults = 10)

        assertTrue(capturedUrl!!.contains("maxResults=10"))
    }

    // --- getChecklist ---

    @Test
    fun getChecklist_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleChecklistJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getChecklist("S123456")

        assertTrue(result is DomainResult.Success)
        assertEquals("S123456", (result as DomainResult.Success).data.subId)
        assertEquals(2, result.data.obs.size)
        assertEquals("hoocro1", result.data.obs[0].speciesCode)
        assertEquals(5, result.data.obs[0].howMany)
    }

    @Test
    fun getChecklist_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(
                content = sampleChecklistJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        repo.getChecklist("S123456")

        assertTrue(capturedUrl!!.contains("/product/checklist/view/S123456"))
    }

    @Test
    fun getChecklist_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getChecklist("INVALID")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Checklist not found"))
    }

    // --- Error handling ---

    @Test
    fun returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getRecentChecklists("US-NY")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
    }
}
