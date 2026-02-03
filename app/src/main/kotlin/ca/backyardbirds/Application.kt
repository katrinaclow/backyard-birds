package ca.backyardbirds

import ca.backyardbirds.core.network.HttpClientFactory
import ca.backyardbirds.data.checklist.CachingChecklistRepository
import ca.backyardbirds.data.checklist.ChecklistRepositoryImpl
import ca.backyardbirds.data.hotspot.CachingHotspotRepository
import ca.backyardbirds.data.hotspot.HotspotRepositoryImpl
import ca.backyardbirds.data.obs.CachingObservationRepository
import ca.backyardbirds.data.obs.ObservationRepositoryImpl
import ca.backyardbirds.data.region.CachingRegionRepository
import ca.backyardbirds.data.region.RegionRepositoryImpl
import ca.backyardbirds.data.specieslist.CachingSpeciesListRepository
import ca.backyardbirds.data.specieslist.SpeciesListRepositoryImpl
import ca.backyardbirds.data.statistics.CachingStatisticsRepository
import ca.backyardbirds.data.statistics.StatisticsRepositoryImpl
import ca.backyardbirds.data.taxonomy.CachingTaxonomyRepository
import ca.backyardbirds.data.taxonomy.TaxonomyRepositoryImpl
import ca.backyardbirds.database.DatabaseFactory
import ca.backyardbirds.database.FlywayMigration
import ca.backyardbirds.database.repository.impl.*
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

    // Database initialization
    val dbUrl = dotenv["DB_URL"] ?: System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/ebird"
    val dbUser = dotenv["DB_USER"] ?: System.getenv("DB_USER") ?: "katrinaclow"
    val dbPassword = dotenv["DB_PASSWORD"] ?: System.getenv("DB_PASSWORD") ?: ""

    val database = DatabaseFactory.create(
        url = dbUrl,
        user = dbUser,
        password = dbPassword
    )

    // Run Flyway migrations
    DatabaseFactory.getDataSource()?.let { dataSource ->
        FlywayMigration.run(dataSource)
    }

    val httpClient = HttpClientFactory().create()

    // Database repository instances
    val cacheMetadataRepo = CacheMetadataRepositoryImpl(database)
    val observationDbRepo = ObservationDatabaseRepositoryImpl(database)
    val hotspotDbRepo = HotspotDatabaseRepositoryImpl(database)
    val regionDbRepo = RegionDatabaseRepositoryImpl(database)
    val taxonomyDbRepo = TaxonomyDatabaseRepositoryImpl(database)
    val checklistDbRepo = ChecklistDatabaseRepositoryImpl(database)
    val statisticsDbRepo = StatisticsDatabaseRepositoryImpl(database)
    val speciesListDbRepo = SpeciesListDatabaseRepositoryImpl(database)

    // API repository instances
    val observationApiRepo = ObservationRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val taxonomyApiRepo = TaxonomyRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val regionApiRepo = RegionRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val hotspotApiRepo = HotspotRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val speciesListApiRepo = SpeciesListRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val statisticsApiRepo = StatisticsRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )
    val checklistApiRepo = ChecklistRepositoryImpl(
        apiKey = ebirdApiKey,
        client = httpClient
    )

    // Caching repository instances (decorators)
    val observationRepository = CachingObservationRepository(
        apiRepository = observationApiRepo,
        nearbyApiRepository = observationApiRepo,
        dbRepository = observationDbRepo,
        cacheMetadata = cacheMetadataRepo
    )
    val taxonomyRepository = CachingTaxonomyRepository(
        apiRepository = taxonomyApiRepo,
        dbRepository = taxonomyDbRepo,
        cacheMetadata = cacheMetadataRepo
    )
    val regionRepository = CachingRegionRepository(
        apiRepository = regionApiRepo,
        dbRepository = regionDbRepo,
        cacheMetadata = cacheMetadataRepo
    )
    val hotspotRepository = CachingHotspotRepository(
        apiRepository = hotspotApiRepo,
        dbRepository = hotspotDbRepo,
        cacheMetadata = cacheMetadataRepo
    )
    val speciesListRepository = CachingSpeciesListRepository(
        apiRepository = speciesListApiRepo,
        dbRepository = speciesListDbRepo,
        cacheMetadata = cacheMetadataRepo
    )
    val statisticsRepository = CachingStatisticsRepository(
        apiRepository = statisticsApiRepo,
        dbRepository = statisticsDbRepo,
        cacheMetadata = cacheMetadataRepo
    )
    val checklistRepository = CachingChecklistRepository(
        apiRepository = checklistApiRepo,
        dbRepository = checklistDbRepo,
        cacheMetadata = cacheMetadataRepo
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
