package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.StatisticsRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
private data class StatisticsErrorResponse(val error: String)

fun Route.statisticsRoutes(statisticsRepo: StatisticsRepository) {
    route("/api/statistics") {
        get("/top100/{regionCode}/{year}/{month}/{day}") {
            val regionCode = call.parameters["regionCode"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val month = call.parameters["month"]?.toIntOrNull()
            val day = call.parameters["day"]?.toIntOrNull()

            if (year == null || month == null || day == null) {
                call.respond(HttpStatusCode.BadRequest, StatisticsErrorResponse("year, month, and day must be valid integers"))
                return@get
            }

            when (val result = statisticsRepo.getTop100(regionCode, year, month, day)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    StatisticsErrorResponse(result.message)
                )
            }
        }

        get("/{regionCode}/{year}/{month}/{day}") {
            val regionCode = call.parameters["regionCode"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val month = call.parameters["month"]?.toIntOrNull()
            val day = call.parameters["day"]?.toIntOrNull()

            if (year == null || month == null || day == null) {
                call.respond(HttpStatusCode.BadRequest, StatisticsErrorResponse("year, month, and day must be valid integers"))
                return@get
            }

            when (val result = statisticsRepo.getRegionStats(regionCode, year, month, day)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    StatisticsErrorResponse(result.message)
                )
            }
        }
    }
}
