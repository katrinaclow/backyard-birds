package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.SpeciesListRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
private data class SpeciesListErrorResponse(val error: String)

fun Route.speciesListRoutes(speciesListRepo: SpeciesListRepository) {
    route("/api/species") {
        get("/region/{regionCode}") {
            val regionCode = call.parameters["regionCode"]!!

            when (val result = speciesListRepo.getSpeciesInRegion(regionCode)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    SpeciesListErrorResponse(result.message)
                )
            }
        }
    }
}
