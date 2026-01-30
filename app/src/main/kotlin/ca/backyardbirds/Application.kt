package ca.backyardbirds

import ca.backyardbirds.core.network.createHttpClient
import ca.backyardbirds.data.obs.ObservationRepositoryImpl
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
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
    val httpClient = createHttpClient()
    val observationRepository = ObservationRepositoryImpl(
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
    }
}
