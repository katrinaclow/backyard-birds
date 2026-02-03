package ca.backyardbirds.data.obs

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.repository.ObservationDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.model.Observation
import ca.backyardbirds.domain.repository.NearbyObservationRepository
import ca.backyardbirds.domain.repository.RegionObservationRepository
import kotlin.time.Duration.Companion.minutes

class CachingObservationRepository(
    private val apiRepository: RegionObservationRepository,
    private val nearbyApiRepository: NearbyObservationRepository,
    private val dbRepository: ObservationDatabaseRepository,
    private val cacheMetadata: CacheMetadataRepository
) : RegionObservationRepository, NearbyObservationRepository {

    companion object {
        private val OBSERVATIONS_TTL = 15.minutes
        private const val ENTITY_TYPE = "observations"
        private const val ENTITY_TYPE_NOTABLE = "observations_notable"
    }

    override suspend fun getRecentObservations(regionCode: String): DomainResult<List<Observation>> {
        val cacheKey = "observations:region:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getRecentObservations(regionCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getRecentObservations(regionCode)) {
            is DomainResult.Success -> {
                dbRepository.saveObservations(apiResult.data, regionCode)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, regionCode, OBSERVATIONS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getRecentNotableObservations(regionCode: String): DomainResult<List<Observation>> {
        val cacheKey = "observations:notable:$regionCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getRecentNotableObservations(regionCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getRecentNotableObservations(regionCode)) {
            is DomainResult.Success -> {
                dbRepository.saveObservations(apiResult.data, regionCode, isNotable = true)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE_NOTABLE, regionCode, OBSERVATIONS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getRecentObservationsOfSpecies(
        regionCode: String,
        speciesCode: String
    ): DomainResult<List<Observation>> {
        val cacheKey = "observations:species:$regionCode:$speciesCode"

        if (cacheMetadata.isCacheValid(cacheKey)) {
            val cached = dbRepository.getObservationsOfSpecies(regionCode, speciesCode)
            if (cached is DomainResult.Success && cached.data.isNotEmpty()) {
                return cached
            }
        }

        return when (val apiResult = apiRepository.getRecentObservationsOfSpecies(regionCode, speciesCode)) {
            is DomainResult.Success -> {
                dbRepository.saveObservations(apiResult.data, regionCode)
                cacheMetadata.updateCacheMetadata(cacheKey, ENTITY_TYPE, regionCode, OBSERVATIONS_TTL)
                apiResult
            }
            is DomainResult.Failure -> apiResult
        }
    }

    override suspend fun getHistoricObservations(
        regionCode: String,
        year: Int,
        month: Int,
        day: Int
    ): DomainResult<List<Observation>> {
        // Historic observations don't change, so we could cache longer
        // For now, pass through to API
        return apiRepository.getHistoricObservations(regionCode, year, month, day)
    }

    // NearbyObservationRepository methods - these are harder to cache efficiently
    // due to the coordinate-based nature, so we pass through to API for now

    override suspend fun getRecentNearbyObservations(
        lat: Double,
        lng: Double,
        distKm: Int?
    ): DomainResult<List<Observation>> {
        // Could implement spatial caching later with PostGIS
        return nearbyApiRepository.getRecentNearbyObservations(lat, lng, distKm)
    }

    override suspend fun getRecentNearbyObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?
    ): DomainResult<List<Observation>> {
        return nearbyApiRepository.getRecentNearbyObservationsOfSpecies(speciesCode, lat, lng, distKm)
    }

    override suspend fun getNearestObservationsOfSpecies(
        speciesCode: String,
        lat: Double,
        lng: Double,
        distKm: Int?
    ): DomainResult<List<Observation>> {
        return nearbyApiRepository.getNearestObservationsOfSpecies(speciesCode, lat, lng, distKm)
    }

    override suspend fun getRecentNearbyNotableObservations(
        lat: Double,
        lng: Double,
        distKm: Int?
    ): DomainResult<List<Observation>> {
        return nearbyApiRepository.getRecentNearbyNotableObservations(lat, lng, distKm)
    }
}
