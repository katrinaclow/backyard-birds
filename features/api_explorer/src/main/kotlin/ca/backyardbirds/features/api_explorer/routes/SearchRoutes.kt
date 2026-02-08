package ca.backyardbirds.features.api_explorer.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.features.api_explorer.service.SearchService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Routes for the API Explorer search functionality.
 * Provides autocomplete endpoints for species and regions.
 */
fun Route.searchRoutes(searchService: SearchService) {
    route("/api/explorer") {
        route("/search") {
            /**
             * Search species by common name, scientific name, or species code.
             *
             * GET /api/explorer/search/species?q=eagle&limit=20
             */
            get("/species") {
                val query = call.request.queryParameters["q"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                if (query.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("Query parameter 'q' is required"))
                    return@get
                }

                if (query.length < 2) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("Query must be at least 2 characters"))
                    return@get
                }

                when (val result = searchService.searchSpecies(query, limit.coerceIn(1, 100))) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                    is DomainResult.Failure -> call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiError(result.message)
                    )
                }
            }

            /**
             * Search regions by name or region code.
             *
             * GET /api/explorer/search/regions?q=berlin&limit=20
             */
            get("/regions") {
                val query = call.request.queryParameters["q"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                if (query.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("Query parameter 'q' is required"))
                    return@get
                }

                if (query.length < 2) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("Query must be at least 2 characters"))
                    return@get
                }

                when (val result = searchService.searchRegions(query, limit.coerceIn(1, 100))) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                    is DomainResult.Failure -> call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiError(result.message)
                    )
                }
            }
        }
    }
}
