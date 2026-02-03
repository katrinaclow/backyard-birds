package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.HotspotRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private data class HotspotGeoParams(val lat: Double, val lng: Double, val dist: Int?, val back: Int?)

private fun RoutingCall.parseHotspotGeoParams(): HotspotGeoParams? {
    val lat = request.queryParameters["lat"]?.toDoubleOrNull() ?: return null
    val lng = request.queryParameters["lng"]?.toDoubleOrNull() ?: return null
    val dist = request.queryParameters["dist"]?.toIntOrNull()
    val back = request.queryParameters["back"]?.toIntOrNull()
    return HotspotGeoParams(lat, lng, dist, back)
}

fun Route.hotspotRoutes(hotspotRepo: HotspotRepository) {
    route("/api/hotspots") {
        get("/region/{regionCode}") {
            val regionCode = call.parameters["regionCode"]!!
            val back = call.request.queryParameters["back"]?.toIntOrNull()

            when (val result = hotspotRepo.getHotspotsInRegion(regionCode, back)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/nearby") {
            val geo = call.parseHotspotGeoParams()
            if (geo == null) {
                call.respond(HttpStatusCode.BadRequest, ApiError("lat and lng are required and must be valid numbers"))
                return@get
            }

            when (val result = hotspotRepo.getNearbyHotspots(geo.lat, geo.lng, geo.dist, geo.back)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }

        get("/{locId}") {
            val locId = call.parameters["locId"]!!

            when (val result = hotspotRepo.getHotspotInfo(locId)) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is DomainResult.Failure -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(result.message)
                )
            }
        }
    }
}
