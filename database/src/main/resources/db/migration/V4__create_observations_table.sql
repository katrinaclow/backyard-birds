-- V4: Observations table - bird sightings

CREATE TABLE observations (
    id SERIAL PRIMARY KEY,
    submission_id VARCHAR(50) NOT NULL,
    species_code VARCHAR(20) NOT NULL,
    common_name VARCHAR(255) NOT NULL,
    scientific_name VARCHAR(255) NOT NULL,
    location_id VARCHAR(50) NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    observation_date TIMESTAMP NOT NULL,
    how_many INTEGER,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT TRUE,
    is_reviewed BOOLEAN NOT NULL DEFAULT FALSE,
    is_location_private BOOLEAN NOT NULL DEFAULT FALSE,
    region_code VARCHAR(50), -- Cached region for faster queries
    is_notable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(submission_id, species_code)
);

CREATE INDEX idx_observations_species_code ON observations(species_code);
CREATE INDEX idx_observations_location_id ON observations(location_id);
CREATE INDEX idx_observations_observation_date ON observations(observation_date);
CREATE INDEX idx_observations_region_code ON observations(region_code);
CREATE INDEX idx_observations_location ON observations(latitude, longitude);
CREATE INDEX idx_observations_submission_id ON observations(submission_id);
CREATE INDEX idx_observations_is_notable ON observations(is_notable) WHERE is_notable = TRUE;
