-- V5: Checklists and checklist observations tables

CREATE TABLE checklists (
    sub_id VARCHAR(50) PRIMARY KEY,
    loc_id VARCHAR(50) NOT NULL,
    user_display_name VARCHAR(255) NOT NULL,
    num_species INTEGER NOT NULL,
    obs_dt TIMESTAMP NOT NULL,
    region_code VARCHAR(50), -- Cached region for faster queries
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_checklists_loc_id ON checklists(loc_id);
CREATE INDEX idx_checklists_obs_dt ON checklists(obs_dt);
CREATE INDEX idx_checklists_region_code ON checklists(region_code);
CREATE INDEX idx_checklists_user_display_name ON checklists(user_display_name);

CREATE TABLE checklist_observations (
    id SERIAL PRIMARY KEY,
    sub_id VARCHAR(50) NOT NULL,
    species_code VARCHAR(20) NOT NULL,
    how_many INTEGER,
    CONSTRAINT fk_checklist FOREIGN KEY (sub_id) REFERENCES checklists(sub_id) ON DELETE CASCADE,
    UNIQUE(sub_id, species_code)
);

CREATE INDEX idx_checklist_observations_sub_id ON checklist_observations(sub_id);
CREATE INDEX idx_checklist_observations_species_code ON checklist_observations(species_code);
