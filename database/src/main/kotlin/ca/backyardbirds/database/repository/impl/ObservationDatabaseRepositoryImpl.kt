package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.mapper.toObservation
import ca.backyardbirds.database.repository.ObservationDatabaseRepository
import ca.backyardbirds.database.tables.ObservationsTable
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class ObservationDatabaseRepositoryImpl(
    private val database: Database
) : ObservationDatabaseRepository {

    override suspend fun saveObservations(
        observations: List<Observation>,
        regionCode: String,
        isNotable: Boolean
    ): DomainResult<Unit> = try {
        dbQuery {
            observations.forEach { obs ->
                ObservationsTable.upsert(
                    keys = arrayOf(ObservationsTable.submissionId, ObservationsTable.speciesCode)
                ) {
                    it[submissionId] = obs.submissionId
                    it[speciesCode] = obs.speciesCode
                    it[commonName] = obs.commonName
                    it[scientificName] = obs.scientificName
                    it[locationId] = obs.locationId
                    it[locationName] = obs.locationName
                    it[observationDate] = obs.observationDate.toInstant(ZoneOffset.UTC)
                    it[howMany] = obs.howMany
                    it[latitude] = obs.latitude
                    it[longitude] = obs.longitude
                    it[isValid] = obs.isValid
                    it[isReviewed] = obs.isReviewed
                    it[isLocationPrivate] = obs.isLocationPrivate
                    it[ObservationsTable.regionCode] = regionCode
                    it[ObservationsTable.isNotable] = isNotable
                    it[createdAt] = Instant.now()
                }
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to save observations: ${e.message}", e)
    }

    override suspend fun getRecentObservations(
        regionCode: String,
        limit: Int
    ): DomainResult<List<Observation>> = try {
        val observations = dbQuery {
            ObservationsTable.selectAll()
                .where { ObservationsTable.regionCode eq regionCode }
                .orderBy(ObservationsTable.observationDate, SortOrder.DESC)
                .limit(limit)
                .map { it.toObservation() }
        }
        DomainResult.Success(observations)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get recent observations: ${e.message}", e)
    }

    override suspend fun getRecentNotableObservations(
        regionCode: String,
        limit: Int
    ): DomainResult<List<Observation>> = try {
        val observations = dbQuery {
            ObservationsTable.selectAll()
                .where {
                    (ObservationsTable.regionCode eq regionCode) and
                    (ObservationsTable.isNotable eq true)
                }
                .orderBy(ObservationsTable.observationDate, SortOrder.DESC)
                .limit(limit)
                .map { it.toObservation() }
        }
        DomainResult.Success(observations)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get recent notable observations: ${e.message}", e)
    }

    override suspend fun getObservationsOfSpecies(
        regionCode: String,
        speciesCode: String,
        limit: Int
    ): DomainResult<List<Observation>> = try {
        val observations = dbQuery {
            ObservationsTable.selectAll()
                .where {
                    (ObservationsTable.regionCode eq regionCode) and
                    (ObservationsTable.speciesCode eq speciesCode)
                }
                .orderBy(ObservationsTable.observationDate, SortOrder.DESC)
                .limit(limit)
                .map { it.toObservation() }
        }
        DomainResult.Success(observations)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get observations of species: ${e.message}", e)
    }

    override suspend fun getNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int,
        limit: Int
    ): DomainResult<List<Observation>> = try {
        // Simple bounding box approximation (1 degree ~ 111km)
        val latDelta = distKm / 111.0
        val lngDelta = distKm / (111.0 * kotlin.math.cos(Math.toRadians(lat)))

        val observations = dbQuery {
            ObservationsTable.selectAll()
                .where {
                    (ObservationsTable.latitude greaterEq (lat - latDelta)) and
                    (ObservationsTable.latitude lessEq (lat + latDelta)) and
                    (ObservationsTable.longitude greaterEq (lng - lngDelta)) and
                    (ObservationsTable.longitude lessEq (lng + lngDelta))
                }
                .orderBy(ObservationsTable.observationDate, SortOrder.DESC)
                .limit(limit)
                .map { it.toObservation() }
        }
        DomainResult.Success(observations)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to get nearby observations: ${e.message}", e)
    }

    override suspend fun deleteOldObservations(olderThan: LocalDateTime): DomainResult<Int> = try {
        val count = dbQuery {
            ObservationsTable.deleteWhere {
                observationDate less olderThan.toInstant(ZoneOffset.UTC)
            }
        }
        DomainResult.Success(count)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to delete old observations: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
