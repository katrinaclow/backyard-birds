package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object CacheMetadataTable : Table("cache_metadata") {
    val cacheKey = varchar("cache_key", 255)
    val entityType = varchar("entity_type", 50)
    val regionCode = varchar("region_code", 50).nullable()
    val lastUpdated = timestamp("last_updated")
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(cacheKey)
}
