package ca.backyardbirds.database.repository.impl

import ca.backyardbirds.database.repository.CacheMetadataRepository
import ca.backyardbirds.database.tables.CacheMetadataTable
import ca.backyardbirds.domain.model.DomainResult
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class CacheMetadataRepositoryImpl(
    private val database: Database
) : CacheMetadataRepository {

    override suspend fun isCacheValid(key: String): Boolean = dbQuery {
        val now = Instant.now()
        CacheMetadataTable.selectAll()
            .where { (CacheMetadataTable.cacheKey eq key) and (CacheMetadataTable.expiresAt greater now) }
            .singleOrNull() != null
    }

    override suspend fun updateCacheMetadata(
        key: String,
        entityType: String,
        regionCode: String?,
        ttl: Duration
    ): DomainResult<Unit> = try {
        dbQuery {
            val now = Instant.now()
            val expiresAt = now.plus(ttl.toJavaDuration())

            CacheMetadataTable.upsert {
                it[cacheKey] = key
                it[CacheMetadataTable.entityType] = entityType
                it[CacheMetadataTable.regionCode] = regionCode
                it[lastUpdated] = now
                it[CacheMetadataTable.expiresAt] = expiresAt
                it[createdAt] = now
            }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to update cache metadata: ${e.message}", e)
    }

    override suspend fun invalidateCache(key: String): DomainResult<Unit> = try {
        dbQuery {
            CacheMetadataTable.deleteWhere { cacheKey eq key }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to invalidate cache: ${e.message}", e)
    }

    override suspend fun invalidateCacheByType(entityType: String): DomainResult<Unit> = try {
        dbQuery {
            CacheMetadataTable.deleteWhere { CacheMetadataTable.entityType eq entityType }
        }
        DomainResult.Success(Unit)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to invalidate cache by type: ${e.message}", e)
    }

    override suspend fun cleanExpiredCache(): DomainResult<Int> = try {
        val count = dbQuery {
            CacheMetadataTable.deleteWhere { expiresAt less Instant.now() }
        }
        DomainResult.Success(count)
    } catch (e: Exception) {
        DomainResult.Failure("Failed to clean expired cache: ${e.message}", e)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
