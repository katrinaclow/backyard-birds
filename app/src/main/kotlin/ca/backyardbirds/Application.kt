package ca.backyardbirds

import ca.backyardbirds.core.network.HttpClientFactory
import ca.backyardbirds.data.checklist.ChecklistRepositoryImpl
import ca.backyardbirds.data.hotspot.HotspotRepositoryImpl
import ca.backyardbirds.data.obs.ObservationRepositoryImpl
import ca.backyardbirds.data.region.RegionRepositoryImpl
import ca.backyardbirds.data.specieslist.SpeciesListRepositoryImpl
import ca.backyardbirds.data.statistics.StatisticsRepositoryImpl
import ca.backyardbirds.data.taxonomy.TaxonomyRepositoryImpl
import ca.backyardbirds.routes.checklistRoutes
import ca.backyardbirds.routes.hotspotRoutes
import ca.backyardbirds.routes.observationRoutes
import ca.backyardbirds.routes.regionRoutes
import ca.backyardbirds.routes.speciesListRoutes
import ca.backyardbirds.routes.statisticsRoutes
import ca.backyardbirds.routes.taxonomyRoutes
import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val dotenv = dotenv { ignoreIfMissing = true }
val ebirdApiKey = dotenv["EBIRD_API_KEY"] ?: System.getenv("EBIRD_API_KEY")

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val httpClient = HttpClientFactory().create()

    // Repository instances
    val observationRepository = ObservationRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val taxonomyRepository = TaxonomyRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val regionRepository = RegionRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val hotspotRepository = HotspotRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val speciesListRepository = SpeciesListRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val statisticsRepository = StatisticsRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val checklistRepository = ChecklistRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )

    routing {
        get("/") {
            call.respondText("Backyard Birds API is running!")
        }

        get("/health") {
            call.respondText("OK")
        }

        // Register all routes
        observationRoutes(observationRepository, observationRepository)
        taxonomyRoutes(taxonomyRepository)
        regionRoutes(regionRepository)
        hotspotRoutes(hotspotRepository)
        speciesListRoutes(speciesListRepository)
        statisticsRoutes(statisticsRepository)
        checklistRoutes(checklistRepository)
    }
}
