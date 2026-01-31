package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.repository.NearbyObservationRepository
import ca.backyardbirds.domain.repository.RegionObservationRepository
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

class ObservationRoutesTest {
    private val regionRepo = mockk<RegionObservationRepository>()
    private val nearbyRepo = mockk<NearbyObservationRepository>()

    private fun testApp() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            routing {
                observationRoutes(regionRepo, nearbyRepo)
            }
        }
    }

    // Sample domain object used as mock return values.
    // TODO: Add more sample observations with varied data for richer assertions.
    private val sampleObservation = Observation(
        speciesCode = "hoocro1",
        commonName = "Hooded Crow",
        scientificName = "Corvus cornix",
        locationId = "L7884500",
        locationName = "Test Location",
        observationDate = LocalDateTime.of(2020, 1, 21, 16, 35),
        howMany = 1,
        latitude = 43.530936,
        longitude = 79.455132,
        isValid = true,
        isReviewed = false,
        isLocationPrivate = true,
        submissionId = "S63619695"
    )

    // --- Region routes ---

    @Test
    fun regionRecent_returns200_onSuccess() {
        coEvery { regionRepo.getRecentObservations("US") } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: Uncomment and run once you have verified the mock setup works.
        // testApp().test {
        //     val response = client.get("/api/observations/region/US")
        //     assertEquals(HttpStatusCode.OK, response.status)
        //     assertTrue(response.bodyAsText().contains("hoocro1"))
        // }
    }

    @Test
    fun regionRecent_returns500_onFailure() {
        coEvery { regionRepo.getRecentObservations("US") } returns DomainResult.Failure("eBird API error")

        // TODO: Uncomment and run.
        // testApp().test {
        //     val response = client.get("/api/observations/region/US")
        //     assertEquals(HttpStatusCode.InternalServerError, response.status)
        //     assertTrue(response.bodyAsText().contains("eBird API error"))
        // }
    }

    @Test
    fun regionNotable_returns200_onSuccess() {
        coEvery { regionRepo.getRecentNotableObservations("CA") } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/region/CA/notable")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }

    @Test
    fun regionSpecies_returns200_onSuccess() {
        coEvery { regionRepo.getRecentObservationsOfSpecies("US", "hoocro1") } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/region/US/species/hoocro1")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }

    @Test
    fun regionHistoric_returns200_onSuccess() {
        coEvery { regionRepo.getHistoricObservations("US", 2020, 1, 21) } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/region/US/historic/2020/1/21")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }

    @Test
    fun regionHistoric_returns400_onInvalidDateParams() {
        // No mock needed — validation happens before the repo is called.

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/region/US/historic/abc/def/ghi")
        //     assertEquals(HttpStatusCode.BadRequest, response.status)
        //     assertTrue(response.bodyAsText().contains("valid integers"))
        // }
    }

    // --- Nearby routes ---

    @Test
    fun nearbyRecent_returns200_onSuccess() {
        coEvery { nearbyRepo.getRecentNearbyObservations(43.5, 79.4, null) } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby?lat=43.5&lng=79.4")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }

    @Test
    fun nearbyRecent_returns400_whenLatMissing() {
        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby?lng=79.4")
        //     assertEquals(HttpStatusCode.BadRequest, response.status)
        //     assertTrue(response.bodyAsText().contains("lat and lng"))
        // }
    }

    @Test
    fun nearbyRecent_returns400_whenLngMissing() {
        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby?lat=43.5")
        //     assertEquals(HttpStatusCode.BadRequest, response.status)
        // }
    }

    @Test
    fun nearbyRecent_returns400_whenParamsAreNonNumeric() {
        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby?lat=abc&lng=xyz")
        //     assertEquals(HttpStatusCode.BadRequest, response.status)
        // }
    }

    @Test
    fun nearbyNotable_returns200_onSuccess() {
        coEvery { nearbyRepo.getRecentNearbyNotableObservations(43.5, 79.4, 10) } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby/notable?lat=43.5&lng=79.4&dist=10")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }

    @Test
    fun nearbySpecies_returns200_onSuccess() {
        coEvery { nearbyRepo.getRecentNearbyObservationsOfSpecies("hoocro1", 43.5, 79.4, null) } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby/species/hoocro1?lat=43.5&lng=79.4")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }

    @Test
    fun nearbyNearest_returns200_onSuccess() {
        coEvery { nearbyRepo.getNearestObservationsOfSpecies("hoocro1", 43.5, 79.4, 25) } returns DomainResult.Success(listOf(sampleObservation))

        // TODO: testApp().test {
        //     val response = client.get("/api/observations/nearby/nearest/hoocro1?lat=43.5&lng=79.4&dist=25")
        //     assertEquals(HttpStatusCode.OK, response.status)
        // }
    }
}
