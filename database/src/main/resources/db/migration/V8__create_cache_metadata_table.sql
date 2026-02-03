-- V8: Cache metadata table - tracks cache freshness

CREATE TABLE cache_metadata (
    cache_key VARCHAR(255) PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL, -- taxonomy, regions, hotspots, observations, etc.
    region_code VARCHAR(50), -- Optional, for region-specific caches
    last_updated TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cache_metadata_entity_type ON cache_metadata(entity_type);
CREATE INDEX idx_cache_metadata_region_code ON cache_metadata(region_code);
CREATE INDEX idx_cache_metadata_expires_at ON cache_metadata(expires_at);
