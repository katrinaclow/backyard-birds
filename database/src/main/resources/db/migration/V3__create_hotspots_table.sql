-- V3: Hotspots table - birding locations

CREATE TABLE hotspots (
    loc_id VARCHAR(50) PRIMARY KEY,
    loc_name VARCHAR(255) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    subnational1_code VARCHAR(20) NOT NULL,
    subnational2_code VARCHAR(20),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    latest_obs_dt TIMESTAMP,
    num_species_all_time INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hotspots_country_code ON hotspots(country_code);
CREATE INDEX idx_hotspots_subnational1_code ON hotspots(subnational1_code);
CREATE INDEX idx_hotspots_subnational2_code ON hotspots(subnational2_code);
CREATE INDEX idx_hotspots_location ON hotspots(latitude, longitude);
