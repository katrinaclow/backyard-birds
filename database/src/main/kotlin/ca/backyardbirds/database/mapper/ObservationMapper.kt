package ca.backyardbirds.database.mapper

import ca.backyardbirds.database.tables.ObservationsTable
import ca.backyardbirds.domain.model.Observation
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toObservation(): Observation = Observation(
    speciesCode = this[ObservationsTable.speciesCode],
    commonName = this[ObservationsTable.commonName],
    scientificName = this[ObservationsTable.scientificName],
    locationId = this[ObservationsTable.locationId],
    locationName = this[ObservationsTable.locationName],
    observationDate = this[ObservationsTable.observationDate].toLocalDateTime(),
    howMany = this[ObservationsTable.howMany],
    latitude = this[ObservationsTable.latitude],
    longitude = this[ObservationsTable.longitude],
    isValid = this[ObservationsTable.isValid],
    isReviewed = this[ObservationsTable.isReviewed],
    isLocationPrivate = this[ObservationsTable.isLocationPrivate],
    submissionId = this[ObservationsTable.submissionId]
)

private fun java.time.Instant.toLocalDateTime(): java.time.LocalDateTime =
    java.time.LocalDateTime.ofInstant(this, java.time.ZoneOffset.UTC)
