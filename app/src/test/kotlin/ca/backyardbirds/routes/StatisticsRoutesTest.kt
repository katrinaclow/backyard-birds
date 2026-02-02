package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.RegionStats
import ca.backyardbirds.domain.model.TopObserver
import ca.backyardbirds.domain.repository.StatisticsRepository
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

class StatisticsRoutesTest {
    private val statisticsRepo = mockk<StatisticsRepository>()

    private fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            install(ContentNegotiation) { json() }
            routing {
                statisticsRoutes(statisticsRepo)
            }
            block()
        }
    }

    private val sampleTopObserver = TopObserver(
        userDisplayName = "John Birder",
        numSpecies = 150,
        numChecklists = 45,
        rowNum = 1,
        userId = "USR123"
    )

    private val sampleRegionStats = RegionStats(
        numChecklists = 5000,
        numContributors = 250,
        numSpecies = 400
    )

    // --- GET /api/statistics/top100/{regionCode}/{year}/{month}/{day} ---

    @Test
    fun getTop100_returns200_onSuccess() {
        coEvery { statisticsRepo.getTop100("US", 2024, 1, 15) } returns DomainResult.Success(listOf(sampleTopObserver))

        withTestApp {
            val response = client.get("/api/statistics/top100/US/2024/1/15")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("John Birder"))
            assertTrue(response.bodyAsText().contains("150"))
        }
    }

    @Test
    fun getTop100_returns500_onFailure() {
        coEvery { statisticsRepo.getTop100("US", 2024, 1, 15) } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/statistics/top100/US/2024/1/15")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    @Test
    fun getTop100_returns400_onInvalidDateParams() {
        withTestApp {
            val response = client.get("/api/statistics/top100/US/abc/def/ghi")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("valid integers"))
        }
    }

    // --- GET /api/statistics/{regionCode}/{year}/{month}/{day} ---

    @Test
    fun getRegionStats_returns200_onSuccess() {
        coEvery { statisticsRepo.getRegionStats("US", 2024, 1, 15) } returns DomainResult.Success(sampleRegionStats)

        withTestApp {
            val response = client.get("/api/statistics/US/2024/1/15")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("5000"))
            assertTrue(response.bodyAsText().contains("250"))
            assertTrue(response.bodyAsText().contains("400"))
        }
    }

    @Test
    fun getRegionStats_returns500_onFailure() {
        coEvery { statisticsRepo.getRegionStats("US", 2024, 1, 15) } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/statistics/US/2024/1/15")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    @Test
    fun getRegionStats_returns400_onInvalidDateParams() {
        withTestApp {
            val response = client.get("/api/statistics/US/abc/def/ghi")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("valid integers"))
        }
    }
}
