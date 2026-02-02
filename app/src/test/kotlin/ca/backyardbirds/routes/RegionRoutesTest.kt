package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Region
import ca.backyardbirds.domain.model.RegionBounds
import ca.backyardbirds.domain.model.RegionInfo
import ca.backyardbirds.domain.repository.RegionRepository
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

class RegionRoutesTest {
    private val regionRepo = mockk<RegionRepository>()

    private fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            install(ContentNegotiation) { json() }
            routing {
                regionRoutes(regionRepo)
            }
            block()
        }
    }

    private val sampleRegion = Region(
        code = "US-CA",
        name = "California"
    )

    private val sampleRegionInfo = RegionInfo(
        code = "US",
        name = "United States",
        bounds = RegionBounds(
            minX = -124.733,
            maxX = -66.954,
            minY = 24.544,
            maxY = 49.384
        )
    )

    // --- GET /api/regions/{type}/{parentCode} ---

    @Test
    fun getSubRegions_returns200_onSuccess() {
        coEvery { regionRepo.getSubRegions("subnational1", "US") } returns DomainResult.Success(listOf(sampleRegion))

        withTestApp {
            val response = client.get("/api/regions/subnational1/US")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("US-CA"))
        }
    }

    @Test
    fun getSubRegions_returns500_onFailure() {
        coEvery { regionRepo.getSubRegions("subnational1", "US") } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/regions/subnational1/US")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    // --- GET /api/regions/{regionCode}/info ---

    @Test
    fun getRegionInfo_returns200_onSuccess() {
        coEvery { regionRepo.getRegionInfo("US") } returns DomainResult.Success(sampleRegionInfo)

        withTestApp {
            val response = client.get("/api/regions/US/info")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("United States"))
            assertTrue(response.bodyAsText().contains("-124.733"))
        }
    }

    @Test
    fun getRegionInfo_returns500_onFailure() {
        coEvery { regionRepo.getRegionInfo("INVALID") } returns DomainResult.Failure("Region not found")

        withTestApp {
            val response = client.get("/api/regions/INVALID/info")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Region not found"))
        }
    }

    // --- GET /api/regions/{regionCode}/adjacent ---

    @Test
    fun getAdjacentRegions_returns200_onSuccess() {
        coEvery { regionRepo.getAdjacentRegions("US-CA") } returns DomainResult.Success(listOf(sampleRegion))

        withTestApp {
            val response = client.get("/api/regions/US-CA/adjacent")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("US-CA"))
        }
    }

    @Test
    fun getAdjacentRegions_returns500_onFailure() {
        coEvery { regionRepo.getAdjacentRegions("US-CA") } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/regions/US-CA/adjacent")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }
}
