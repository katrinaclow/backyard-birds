-- V1: Taxonomy table - species reference data

CREATE TABLE taxonomy (
    species_code VARCHAR(20) PRIMARY KEY,
    common_name VARCHAR(255) NOT NULL,
    scientific_name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    taxon_order DOUBLE PRECISION NOT NULL,
    banding_codes TEXT[], -- Array of banding codes
    com_name_codes TEXT[], -- Array of common name codes
    sci_name_codes TEXT[], -- Array of scientific name codes
    taxon_order_name VARCHAR(100),
    family_code VARCHAR(20),
    family_common_name VARCHAR(255),
    family_scientific_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_taxonomy_common_name ON taxonomy(common_name);
CREATE INDEX idx_taxonomy_scientific_name ON taxonomy(scientific_name);
CREATE INDEX idx_taxonomy_category ON taxonomy(category);
CREATE INDEX idx_taxonomy_family_code ON taxonomy(family_code);
