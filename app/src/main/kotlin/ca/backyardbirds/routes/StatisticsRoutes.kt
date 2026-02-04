package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.query.Top100QueryParams
import ca.backyardbirds.domain.repository.StatisticsRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private fun RoutingCall.parseTop100QueryParams(): Top100QueryParams {
    return Top100QueryParams(
        rankedBy = request.queryParameters["rankedBy"],
        maxResults = request.queryParameters["maxResults"]?.toIntOrNull()
    )
}

fun Route.statisticsRoutes(statisticsRepo: StatisticsRepository) {
    route("/api/statistics") {
        get("/top100/{regionCode}/{year}/{month}/{day}") {
            val regionCode = call.parameters["regionCode"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val month = call.parameters["month"]?.toIntOrNull()
            val day = call.parameters["day"]?.toIntOrNull()

            if (year == null || month == null || day == null) {
                call.respond(HttpStatusCode.BadRequest, ApiError("year, month, and day must be valid integers"))
                return@get
            }

            val params = call.parseTop100QueryParams()
            when (val result = statisticsRepo.getTop100(regionCode, year, month, day, params)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/{regionCode}/{year}/{month}/{day}") {
            val regionCode = call.parameters["regionCode"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val month = call.parameters["month"]?.toIntOrNull()
            val day = call.parameters["day"]?.toIntOrNull()

            if (year == null || month == null || day == null) {
                call.respond(HttpStatusCode.BadRequest, ApiError("year, month, and day must be valid integers"))
                return@get
            }

            when (val result = statisticsRepo.getRegionStats(regionCode, year, month, day)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }
    }
}
