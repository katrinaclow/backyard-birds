package ca.backyardbirds.routes

import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.repository.NearbyObservationRepository
import ca.backyardbirds.domain.repository.RegionObservationRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
private data class ErrorResponse(val error: String)

private data class GeoParams(val lat: Double, val lng: Double, val dist: Int?)

private fun RoutingCall.parseGeoParams(): GeoParams? {
    val lat = request.queryParameters["lat"]?.toDoubleOrNull() ?: return null
    val lng = request.queryParameters["lng"]?.toDoubleOrNull() ?: return null
    val dist = request.queryParameters["dist"]?.toIntOrNull()
    return GeoParams(lat, lng, dist)
}

private suspend fun RoutingCall.respondWithObservations(result: DomainResult<List<Observation>>) {
    when (result) {
        is DomainResult.Success -> respond(HttpStatusCode.OK, result.data)
        is DomainResult.Failure -> respond(HttpStatusCode.InternalServerError, ErrorResponse(result.message))
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
                call.respondWithObservations(regionRepo.getRecentObservations(regionCode))
            }

            get("/notable") {
                val regionCode = call.parameters["regionCode"]!!
                call.respondWithObservations(regionRepo.getRecentNotableObservations(regionCode))
            }

            get("/species/{speciesCode}") {
                val regionCode = call.parameters["regionCode"]!!
                val speciesCode = call.parameters["speciesCode"]!!
                call.respondWithObservations(regionRepo.getRecentObservationsOfSpecies(regionCode, speciesCode))
            }

            get("/historic/{year}/{month}/{day}") {
                val regionCode = call.parameters["regionCode"]!!
                val year = call.parameters["year"]?.toIntOrNull()
                val month = call.parameters["month"]?.toIntOrNull()
                val day = call.parameters["day"]?.toIntOrNull()
                if (year == null || month == null || day == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("year, month, and day must be valid integers"))
                    return@get
                }
                call.respondWithObservations(regionRepo.getHistoricObservations(regionCode, year, month, day))
            }
        }

        route("/nearby") {
            get {
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("lat and lng are required and must be valid numbers"))
                    return@get
                }
                call.respondWithObservations(nearbyRepo.getRecentNearbyObservations(geo.lat, geo.lng, geo.dist))
            }

            get("/notable") {
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("lat and lng are required and must be valid numbers"))
                    return@get
                }
                call.respondWithObservations(nearbyRepo.getRecentNearbyNotableObservations(geo.lat, geo.lng, geo.dist))
            }

            get("/species/{speciesCode}") {
                val speciesCode = call.parameters["speciesCode"]!!
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("lat and lng are required and must be valid numbers"))
                    return@get
                }
                call.respondWithObservations(nearbyRepo.getRecentNearbyObservationsOfSpecies(speciesCode, geo.lat, geo.lng, geo.dist))
            }

            get("/nearest/{speciesCode}") {
                val speciesCode = call.parameters["speciesCode"]!!
                val geo = call.parseGeoParams()
                if (geo == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("lat and lng are required and must be valid numbers"))
                    return@get
                }
                call.respondWithObservations(nearbyRepo.getNearestObservationsOfSpecies(speciesCode, geo.lat, geo.lng, geo.dist))
            }
        }
    }
}
