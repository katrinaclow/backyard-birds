-- V7: Region species lists table

CREATE TABLE region_species_lists (
    id SERIAL PRIMARY KEY,
    region_code VARCHAR(50) NOT NULL,
    species_code VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(region_code, species_code)
);

CREATE INDEX idx_region_species_region_code ON region_species_lists(region_code);
CREATE INDEX idx_region_species_species_code ON region_species_lists(species_code);
