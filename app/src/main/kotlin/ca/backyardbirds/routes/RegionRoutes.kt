package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.RegionRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.regionRoutes(regionRepo: RegionRepository) {
    route("/api/regions") {
        get("/{type}/{parentCode}") {
            val type = call.parameters["type"]!!
            val parentCode = call.parameters["parentCode"]!!

            when (val result = regionRepo.getSubRegions(type, parentCode)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/{regionCode}/info") {
            val regionCode = call.parameters["regionCode"]!!

            when (val result = regionRepo.getRegionInfo(regionCode)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/{regionCode}/adjacent") {
            val regionCode = call.parameters["regionCode"]!!

            when (val result = regionRepo.getAdjacentRegions(regionCode)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }
    }
}
