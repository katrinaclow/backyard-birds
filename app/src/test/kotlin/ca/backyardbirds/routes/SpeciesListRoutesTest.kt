package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.SpeciesListRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpeciesListRoutesTest {
    private val speciesListRepo = mockk<SpeciesListRepository>()

    private fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            install(ContentNegotiation) { json() }
            routing {
                speciesListRoutes(speciesListRepo)
            }
            block()
        }
    }

    // --- GET /api/species/region/{regionCode} ---

    @Test
    fun getSpeciesInRegion_returns200_onSuccess() {
        coEvery { speciesListRepo.getSpeciesInRegion("US-NY") } returns DomainResult.Success(listOf("hoocro1", "bkcchi", "eursta"))

        withTestApp {
            val response = client.get("/api/species/region/US-NY")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("hoocro1"))
            assertTrue(response.bodyAsText().contains("bkcchi"))
            assertTrue(response.bodyAsText().contains("eursta"))
        }
    }

    @Test
    fun getSpeciesInRegion_returnsEmptyList_whenNoSpecies() {
        coEvery { speciesListRepo.getSpeciesInRegion("US-NY") } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/species/region/US-NY")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("[]", response.bodyAsText())
        }
    }

    @Test
    fun getSpeciesInRegion_returns500_onFailure() {
        coEvery { speciesListRepo.getSpeciesInRegion("INVALID") } returns DomainResult.Failure("Region not found")

        withTestApp {
            val response = client.get("/api/species/region/INVALID")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Region not found"))
        }
    }
}
