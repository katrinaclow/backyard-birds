package ca.backyardbirds.data.specieslist

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

class SpeciesListRepositoryImplTest {

    private fun repositoryWith(handler: MockRequestHandler): SpeciesListRepositoryImpl {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json() }
        }
        return SpeciesListRepositoryImpl(
            apiKey = "test-api-key",
            client = client,
            baseUrl = "https://api.ebird.org/v2"
        )
    }

    private val sampleSpeciesListJson = """
        ["hoocro1", "bkcchi", "eursta"]
    """.trimIndent()

    @Test
    fun getSpeciesInRegion_returnsSuccess_onHTTP200() = runTest {
        val repo = repositoryWith { _ ->
            respond(
                content = sampleSpeciesListJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val result = repo.getSpeciesInRegion("US-NY")

        assertTrue(result is DomainResult.Success)
        assertEquals(3, (result as DomainResult.Success).data.size)
        assertEquals(listOf("hoocro1", "bkcchi", "eursta"), result.data)
    }

    @Test
    fun getSpeciesInRegion_buildsCorrectUrl() = runTest {
        var capturedUrl: String? = null
        val repo = repositoryWith { request ->
            capturedUrl = request.url.toString()
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getSpeciesInRegion("US-NY")

        assertTrue(capturedUrl!!.contains("/product/spplist/US-NY"))
    }

    @Test
    fun getSpeciesInRegion_includesApiKeyInHeader() = runTest {
        var capturedToken: String? = null
        val repo = repositoryWith { request ->
            capturedToken = request.headers["X-eBirdApiToken"]
            respond(content = "[]", status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        repo.getSpeciesInRegion("US-NY")

        assertEquals("test-api-key", capturedToken)
    }

    @Test
    fun getSpeciesInRegion_returnsFailure_onHTTP401() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val result = repo.getSpeciesInRegion("US-NY")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Authentication failed"))
    }

    @Test
    fun getSpeciesInRegion_returnsFailure_onHTTP404() = runTest {
        val repo = repositoryWith { _ ->
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }

        val result = repo.getSpeciesInRegion("INVALID")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Region not found"))
    }

    @Test
    fun getSpeciesInRegion_returnsFailure_onNetworkException() = runTest {
        val repo = repositoryWith { _ ->
            throw RuntimeException("Connection refused")
        }

        val result = repo.getSpeciesInRegion("US-NY")

        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).message.contains("Network error"))
    }
}
