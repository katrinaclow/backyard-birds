package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistObservation
import ca.backyardbirds.domain.model.ChecklistSummary
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.ChecklistRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChecklistRoutesTest {
    private val checklistRepo = mockk<ChecklistRepository>()

    private fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            install(ContentNegotiation) { json() }
            routing {
                checklistRoutes(checklistRepo)
            }
            block()
        }
    }

    private val sampleChecklistSummary = ChecklistSummary(
        subId = "S123456",
        locId = "L7884500",
        userDisplayName = "John Birder",
        numSpecies = 25,
        obsDt = LocalDateTime.of(2024, 1, 15, 10, 30)
    )

    private val sampleChecklist = Checklist(
        subId = "S123456",
        locId = "L7884500",
        userDisplayName = "John Birder",
        numSpecies = 2,
        obsDt = LocalDateTime.of(2024, 1, 15, 10, 30),
        obs = listOf(
            ChecklistObservation("hoocro1", 5),
            ChecklistObservation("bkcchi", null)
        )
    )

    // --- GET /api/checklists/region/{regionCode} ---

    @Test
    fun getRecentChecklists_returns200_onSuccess() {
        coEvery { checklistRepo.getRecentChecklists("US-NY", null) } returns DomainResult.Success(listOf(sampleChecklistSummary))

        withTestApp {
            val response = client.get("/api/checklists/region/US-NY")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("S123456"))
            assertTrue(response.bodyAsText().contains("John Birder"))
        }
    }

    @Test
    fun getRecentChecklists_passesMaxResultsParam() {
        coEvery { checklistRepo.getRecentChecklists("US-NY", 10) } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/checklists/region/US-NY?maxResults=10")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun getRecentChecklists_returns500_onFailure() {
        coEvery { checklistRepo.getRecentChecklists("INVALID", null) } returns DomainResult.Failure("Region not found")

        withTestApp {
            val response = client.get("/api/checklists/region/INVALID")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Region not found"))
        }
    }

    // --- GET /api/checklists/region/{regionCode}/{year}/{month}/{day} ---

    @Test
    fun getChecklistsOnDate_returns200_onSuccess() {
        coEvery { checklistRepo.getChecklistsOnDate("US-NY", 2024, 1, 15, null) } returns DomainResult.Success(listOf(sampleChecklistSummary))

        withTestApp {
            val response = client.get("/api/checklists/region/US-NY/2024/1/15")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("S123456"))
        }
    }

    @Test
    fun getChecklistsOnDate_passesMaxResultsParam() {
        coEvery { checklistRepo.getChecklistsOnDate("US-NY", 2024, 1, 15, 10) } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/checklists/region/US-NY/2024/1/15?maxResults=10")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun getChecklistsOnDate_returns400_onInvalidDateParams() {
        withTestApp {
            val response = client.get("/api/checklists/region/US-NY/abc/def/ghi")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("valid integers"))
        }
    }

    @Test
    fun getChecklistsOnDate_returns500_onFailure() {
        coEvery { checklistRepo.getChecklistsOnDate("INVALID", 2024, 1, 15, null) } returns DomainResult.Failure("Region not found")

        withTestApp {
            val response = client.get("/api/checklists/region/INVALID/2024/1/15")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Region not found"))
        }
    }

    // --- GET /api/checklists/{subId} ---

    @Test
    fun getChecklist_returns200_onSuccess() {
        coEvery { checklistRepo.getChecklist("S123456") } returns DomainResult.Success(sampleChecklist)

        withTestApp {
            val response = client.get("/api/checklists/S123456")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("S123456"))
            assertTrue(response.bodyAsText().contains("hoocro1"))
        }
    }

    @Test
    fun getChecklist_returns500_onFailure() {
        coEvery { checklistRepo.getChecklist("INVALID") } returns DomainResult.Failure("Checklist not found")

        withTestApp {
            val response = client.get("/api/checklists/INVALID")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Checklist not found"))
        }
    }
}
