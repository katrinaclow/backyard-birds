-- V2: Regions table - geographic hierarchy

CREATE TABLE regions (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    region_type VARCHAR(20) NOT NULL, -- country, subnational1, subnational2
    parent_code VARCHAR(50),
    bounds_min_x DOUBLE PRECISION,
    bounds_max_x DOUBLE PRECISION,
    bounds_min_y DOUBLE PRECISION,
    bounds_max_y DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_region FOREIGN KEY (parent_code) REFERENCES regions(code)
);

CREATE INDEX idx_regions_parent_code ON regions(parent_code);
CREATE INDEX idx_regions_region_type ON regions(region_type);

-- Table for adjacent regions (many-to-many relationship)
CREATE TABLE adjacent_regions (
    region_code VARCHAR(50) NOT NULL,
    adjacent_region_code VARCHAR(50) NOT NULL,
    PRIMARY KEY (region_code, adjacent_region_code),
    CONSTRAINT fk_region FOREIGN KEY (region_code) REFERENCES regions(code),
    CONSTRAINT fk_adjacent_region FOREIGN KEY (adjacent_region_code) REFERENCES regions(code)
);
