package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.query.ChecklistQueryParams
import ca.backyardbirds.domain.repository.ChecklistRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private fun RoutingCall.parseChecklistQueryParams(): ChecklistQueryParams {
    return ChecklistQueryParams(
        sortKey = request.queryParameters["sortKey"],
        maxResults = request.queryParameters["maxResults"]?.toIntOrNull()
    )
}

fun Route.checklistRoutes(checklistRepo: ChecklistRepository) {
    route("/api/checklists") {
        get("/region/{regionCode}") {
            val regionCode = call.parameters["regionCode"]!!
            val params = call.parseChecklistQueryParams()

            when (val result = checklistRepo.getRecentChecklists(regionCode, params)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/region/{regionCode}/{year}/{month}/{day}") {
            val regionCode = call.parameters["regionCode"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val month = call.parameters["month"]?.toIntOrNull()
            val day = call.parameters["day"]?.toIntOrNull()

            if (year == null || month == null || day == null) {
                call.respond(HttpStatusCode.BadRequest, ApiError("year, month, and day must be valid integers"))
                return@get
            }

            val params = call.parseChecklistQueryParams()
            when (val result = checklistRepo.getChecklistsOnDate(regionCode, year, month, day, params)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/{subId}") {
            val subId = call.parameters["subId"]!!

            when (val result = checklistRepo.getChecklist(subId)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }
    }
}
