package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.ApiError
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.query.HistoricQueryParams
import ca.backyardbirds.domain.query.ObservationQueryParams
import ca.backyardbirds.domain.repository.NearbyObservationRepository
import ca.backyardbirds.domain.repository.RegionObservationRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private data class GeoParams(val lat: Double, val lng: Double, val dist: Int?)

private fun RoutingCall.parseGeoParams(): GeoParams? {
    val lat = request.queryParameters["lat"]?.toDoubleOrNull() ?: return null
    val lng = request.queryParameters["lng"]?.toDoubleOrNull() ?: return null
    val dist = request.queryParameters["dist"]?.toIntOrNull()
    return GeoParams(lat, lng, dist)
}

private fun RoutingCall.parseObservationQueryParams(): ObservationQueryParams {
    return ObservationQueryParams(
        back = request.queryParameters["back"]?.toIntOrNull(),
        hotspot = request.queryParameters["hotspot"]?.toBooleanStrictOrNull(),
        includeProvisional = request.queryParameters["includeProvisional"]?.toBooleanStrictOrNull(),
        maxResults = request.queryParameters["maxResults"]?.toIntOrNull(),
        sppLocale = request.queryParameters["sppLocale"],
        cat = request.queryParameters["cat"],
        sort = request.queryParameters["sort"]
    )
}

private fun RoutingCall.parseHistoricQueryParams(): HistoricQueryParams {
    return HistoricQueryParams(
        rank = request.queryParameters["rank"],
        detail = request.queryParameters["detail"],
        hotspot = request.queryParameters["hotspot"]?.toBooleanStrictOrNull(),
        includeProvisional = request.queryParameters["includeProvisional"]?.toBooleanStrictOrNull(),
        maxResults = request.queryParameters["maxResults"]?.toIntOrNull(),
        sppLocale = request.queryParameters["sppLocale"],
        cat = request.queryParameters["cat"]
    )
}

private suspend fun RoutingCall.respondWithObservations(result: DomainResult<List<Observation>>) {
    when (result) {
        is DomainResult.Success -> respond(HttpStatusCode.OK, result.data)
        is DomainResult.Failure -> respond(HttpStatusCode.InternalServerError, ApiError(result.message))
    }
}

fun Route.observationRoutes(
    regionRepo: RegionObservationRepository,
    nearbyRepo: NearbyObservationRepository
) {
    route("/api/observations") {
        route("/region/{regionCode}") {
            get {
                val regionCode = call.parameters["regionCode"]!!
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(regionRepo.getRecentObservations(regionCode, params))
            }

            get("/notable") {
                val regionCode = call.parameters["regionCode"]!!
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(regionRepo.getRecentNotableObservations(regionCode, params))
            }

            get("/species/{speciesCode}") {
                val regionCode = call.parameters["regionCode"]!!
                val speciesCode = call.parameters["speciesCode"]!!
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(regionRepo.getRecentObservationsOfSpecies(regionCode, speciesCode, params))
            }

            get("/historic/{year}/{month}/{day}") {
                val regionCode = call.parameters["regionCode"]!!
                val year = call.parameters["year"]?.toIntOrNull()
                val month = call.parameters["month"]?.toIntOrNull()
                val day = call.parameters["day"]?.toIntOrNull()
                if (year == null || month == null || day == null) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("year, month, and day must be valid integers"))
                    return@get
                }
                val params = call.parseHistoricQueryParams()
                call.respondWithObservations(regionRepo.getHistoricObservations(regionCode, year, month, day, params))
            }
        }

        route("/nearby") {
            get {
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("lat and lng are required and must be valid numbers"))
                    return@get
                }
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(nearbyRepo.getRecentNearbyObservations(geo.lat, geo.lng, geo.dist, params))
            }

            get("/notable") {
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("lat and lng are required and must be valid numbers"))
                    return@get
                }
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(nearbyRepo.getRecentNearbyNotableObservations(geo.lat, geo.lng, geo.dist, params))
            }

            get("/species/{speciesCode}") {
                val speciesCode = call.parameters["speciesCode"]!!
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("lat and lng are required and must be valid numbers"))
                    return@get
                }
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(nearbyRepo.getRecentNearbyObservationsOfSpecies(speciesCode, geo.lat, geo.lng, geo.dist, params))
            }

            get("/nearest/{speciesCode}") {
                val speciesCode = call.parameters["speciesCode"]!!
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ApiError("lat and lng are required and must be valid numbers"))
                    return@get
                }
                val params = call.parseObservationQueryParams()
                call.respondWithObservations(nearbyRepo.getNearestObservationsOfSpecies(speciesCode, geo.lat, geo.lng, geo.dist, params))
            }
        }
    }
}
