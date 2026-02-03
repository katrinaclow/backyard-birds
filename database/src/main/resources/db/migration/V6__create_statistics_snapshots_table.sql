-- V6: Region statistics and top observers snapshot tables

CREATE TABLE region_stats_snapshots (
    id SERIAL PRIMARY KEY,
    region_code VARCHAR(50) NOT NULL,
    stats_date DATE NOT NULL,
    num_checklists INTEGER NOT NULL,
    num_contributors INTEGER NOT NULL,
    num_species INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(region_code, stats_date)
);

CREATE INDEX idx_region_stats_region_code ON region_stats_snapshots(region_code);
CREATE INDEX idx_region_stats_date ON region_stats_snapshots(stats_date);

CREATE TABLE top_observers_snapshots (
    id SERIAL PRIMARY KEY,
    region_code VARCHAR(50) NOT NULL,
    snapshot_date DATE NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    user_display_name VARCHAR(255) NOT NULL,
    num_species INTEGER NOT NULL,
    num_checklists INTEGER NOT NULL,
    row_num INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(region_code, snapshot_date, user_id)
);

CREATE INDEX idx_top_observers_region_code ON top_observers_snapshots(region_code);
CREATE INDEX idx_top_observers_snapshot_date ON top_observers_snapshots(snapshot_date);
CREATE INDEX idx_top_observers_user_id ON top_observers_snapshots(user_id);
