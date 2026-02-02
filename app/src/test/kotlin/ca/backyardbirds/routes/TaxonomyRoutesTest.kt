package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.TaxaLocale
import ca.backyardbirds.domain.model.TaxonomicGroup
import ca.backyardbirds.domain.model.TaxonomyEntry
import ca.backyardbirds.domain.model.TaxonomyVersion
import ca.backyardbirds.domain.repository.TaxonomyRepository
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

class TaxonomyRoutesTest {
    private val taxonomyRepo = mockk<TaxonomyRepository>()

    private fun withTestApp(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            install(ContentNegotiation) { json() }
            routing {
                taxonomyRoutes(taxonomyRepo)
            }
            block()
        }
    }

    private val sampleTaxonomyEntry = TaxonomyEntry(
        speciesCode = "hoocro1",
        commonName = "Hooded Crow",
        scientificName = "Corvus cornix",
        category = "species",
        taxonOrder = 24143.0,
        bandingCodes = listOf("HOCR"),
        comNameCodes = listOf("HOCR"),
        sciNameCodes = listOf("COCO"),
        order = "Passeriformes",
        familyCode = "corvid1",
        familyComName = "Crows, Jays, and Magpies",
        familySciName = "Corvidae"
    )

    private val sampleTaxonomyVersion = TaxonomyVersion(
        authorityVer = 2024.0,
        latest = true
    )

    private val sampleTaxaLocale = TaxaLocale(
        code = "en",
        name = "English",
        lastUpdate = "2024-01-15"
    )

    private val sampleTaxonomicGroup = TaxonomicGroup(
        groupName = "Waterfowl",
        groupOrder = 1,
        taxonOrderBounds = listOf(listOf(1.0, 100.0))
    )

    // --- GET /api/taxonomy ---

    @Test
    fun getTaxonomy_returns200_onSuccess() {
        coEvery { taxonomyRepo.getTaxonomy(null, null) } returns DomainResult.Success(listOf(sampleTaxonomyEntry))

        withTestApp {
            val response = client.get("/api/taxonomy")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("hoocro1"))
        }
    }

    @Test
    fun getTaxonomy_returns500_onFailure() {
        coEvery { taxonomyRepo.getTaxonomy(null, null) } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/taxonomy")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    @Test
    fun getTaxonomy_passesSpeciesParam() {
        coEvery { taxonomyRepo.getTaxonomy(listOf("hoocro1", "bkcchi"), null) } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/taxonomy?species=hoocro1,bkcchi")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun getTaxonomy_passesCategoryParam() {
        coEvery { taxonomyRepo.getTaxonomy(null, "species") } returns DomainResult.Success(emptyList())

        withTestApp {
            val response = client.get("/api/taxonomy?cat=species")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    // --- GET /api/taxonomy/{speciesCode}/forms ---

    @Test
    fun getSubspecies_returns200_onSuccess() {
        coEvery { taxonomyRepo.getSubspecies("hoocro1") } returns DomainResult.Success(listOf("hoocro1", "hoocro2"))

        withTestApp {
            val response = client.get("/api/taxonomy/hoocro1/forms")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("hoocro1"))
            assertTrue(response.bodyAsText().contains("hoocro2"))
        }
    }

    @Test
    fun getSubspecies_returns500_onFailure() {
        coEvery { taxonomyRepo.getSubspecies("invalid") } returns DomainResult.Failure("Species not found")

        withTestApp {
            val response = client.get("/api/taxonomy/invalid/forms")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Species not found"))
        }
    }

    // --- GET /api/taxonomy/versions ---

    @Test
    fun getVersions_returns200_onSuccess() {
        coEvery { taxonomyRepo.getTaxonomyVersions() } returns DomainResult.Success(listOf(sampleTaxonomyVersion))

        withTestApp {
            val response = client.get("/api/taxonomy/versions")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("2024"))
            assertTrue(response.bodyAsText().contains("true"))
        }
    }

    @Test
    fun getVersions_returns500_onFailure() {
        coEvery { taxonomyRepo.getTaxonomyVersions() } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/taxonomy/versions")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    // --- GET /api/taxonomy/locales ---

    @Test
    fun getLocales_returns200_onSuccess() {
        coEvery { taxonomyRepo.getTaxaLocaleCodes() } returns DomainResult.Success(listOf(sampleTaxaLocale))

        withTestApp {
            val response = client.get("/api/taxonomy/locales")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("en"))
            assertTrue(response.bodyAsText().contains("English"))
        }
    }

    @Test
    fun getLocales_returns500_onFailure() {
        coEvery { taxonomyRepo.getTaxaLocaleCodes() } returns DomainResult.Failure("eBird API error")

        withTestApp {
            val response = client.get("/api/taxonomy/locales")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("eBird API error"))
        }
    }

    // --- GET /api/taxonomy/groups/{speciesGrouping} ---

    @Test
    fun getGroups_returns200_onSuccess() {
        coEvery { taxonomyRepo.getTaxonomicGroups("merlin") } returns DomainResult.Success(listOf(sampleTaxonomicGroup))

        withTestApp {
            val response = client.get("/api/taxonomy/groups/merlin")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Waterfowl"))
        }
    }

    @Test
    fun getGroups_returns500_onFailure() {
        coEvery { taxonomyRepo.getTaxonomicGroups("invalid") } returns DomainResult.Failure("Species grouping not found")

        withTestApp {
            val response = client.get("/api/taxonomy/groups/invalid")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue(response.bodyAsText().contains("Species grouping not found"))
        }
    }
}
