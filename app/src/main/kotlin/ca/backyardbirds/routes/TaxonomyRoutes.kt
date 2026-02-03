package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.TaxonomyRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taxonomyRoutes(taxonomyRepo: TaxonomyRepository) {
    route("/api/taxonomy") {
        get {
            val speciesCodes = call.request.queryParameters["species"]
                ?.split(",")
                ?.filter { it.isNotBlank() }
            val category = call.request.queryParameters["cat"]

            when (val result = taxonomyRepo.getTaxonomy(speciesCodes, category)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/{speciesCode}/forms") {
            val speciesCode = call.parameters["speciesCode"]!!
            when (val result = taxonomyRepo.getSubspecies(speciesCode)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/versions") {
            when (val result = taxonomyRepo.getTaxonomyVersions()) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/locales") {
            when (val result = taxonomyRepo.getTaxaLocaleCodes()) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/groups/{speciesGrouping}") {
            val speciesGrouping = call.parameters["speciesGrouping"]!!
            when (val result = taxonomyRepo.getTaxonomicGroups(speciesGrouping)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }
    }
}
