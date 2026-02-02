package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Hotspot
import ca.backyardbirds.domain.repository.HotspotRepository
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

class HotspotRoutesTest {
    private val hotspotRepo = mockk<HotspotRepository>()

    private fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            install(ContentNegotiation) { json() }
            routing {
                hotspotRoutes(hotspotRepo)
            }
            block()
        }
    }

    private val sampleHotspot = Hotspot(
        locId = "L7884500",
        locName = "Central Park",
        countryCode = "US",
        subnational1Code = "US-NY",
        subnational2Code = "US-NY-061",
        lat = 40.7829,
        lng = -73.9654,
        latestObsDt = "2024-01-15 10:30",
        numSpeciesAllTime = 275
    )

    // --- GET /api/hotspots/region/{regionCode} ---

    @Test
    fun getHotspotsInRegion_returns200_onSuccess() {
        coEvery { hotspotRepo.getHotspotsInRegion("US-NY", null) } returns DomainResult.Success(listOf(sampleHotspot))

        withTestApp {
            val response = client.get("/api/hotspots/region/US-NY")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("L7884500"))
        }
    }

    @Test
    fun getHotspotsInRegion_passesBackParam() {
        coEvery { hotspotRepo.getHotspotsInRegion("US-NY", 7) } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/hotspots/region/US-NY?back=7")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun getHotspotsInRegion_returns500_onFailure() {
        coEvery { hotspotRepo.getHotspotsInRegion("US-NY", null) } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/hotspots/region/US-NY")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    // --- GET /api/hotspots/nearby ---

    @Test
    fun getNearbyHotspots_returns200_onSuccess() {
        coEvery { hotspotRepo.getNearbyHotspots(40.78, -73.97, null, null) } returns DomainResult.Success(listOf(sampleHotspot))

        withTestApp {
            val response = client.get("/api/hotspots/nearby?lat=40.78&lng=-73.97")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("L7884500"))
        }
    }

    @Test
    fun getNearbyHotspots_passesDistAndBackParams() {
        coEvery { hotspotRepo.getNearbyHotspots(40.78, -73.97, 25, 14) } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/hotspots/nearby?lat=40.78&lng=-73.97&dist=25&back=14")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun getNearbyHotspots_returns400_whenLatMissing() {
        withTestApp {
            val response = client.get("/api/hotspots/nearby?lng=-73.97")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("lat and lng"))
        }
    }

    @Test
    fun getNearbyHotspots_returns400_whenLngMissing() {
        withTestApp {
            val response = client.get("/api/hotspots/nearby?lat=40.78")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("lat and lng"))
        }
    }

    @Test
    fun getNearbyHotspots_returns400_whenParamsAreNonNumeric() {
        withTestApp {
            val response = client.get("/api/hotspots/nearby?lat=abc&lng=xyz")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("lat and lng"))
        }
    }

    @Test
    fun getNearbyHotspots_returns500_onFailure() {
        coEvery { hotspotRepo.getNearbyHotspots(40.78, -73.97, null, null) } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/hotspots/nearby?lat=40.78&lng=-73.97")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    // --- GET /api/hotspots/{locId} ---

    @Test
    fun getHotspotInfo_returns200_onSuccess() {
        coEvery { hotspotRepo.getHotspotInfo("L7884500") } returns DomainResult.Success(sampleHotspot)

        withTestApp {
            val response = client.get("/api/hotspots/L7884500")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Central Park"))
        }
    }

    @Test
    fun getHotspotInfo_returns500_onFailure() {
        coEvery { hotspotRepo.getHotspotInfo("INVALID") } returns DomainResult.Failure("Hotspot not found")

        withTestApp {
            val response = client.get("/api/hotspots/INVALID")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Hotspot not found"))
        }
    }
}
