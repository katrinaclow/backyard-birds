package ca.backyardbirds.database.repository

import ca.backyardbirds.domain.model.DomainResult
import kotlin.time.Duration

interface CacheMetadataRepository {
    suspend fun isCacheValid(key: String): Boolean
    suspend fun updateCacheMetadata(
        key: String,
        entityType: String,
        regionCode: String?,
        ttl: Duration
    ): DomainResult<Unit>
    suspend fun invalidateCache(key: String): DomainResult<Unit>
    suspend fun invalidateCacheByType(entityType: String): DomainResult<Unit>
    suspend fun cleanExpiredCache(): DomainResult<Int>
}
