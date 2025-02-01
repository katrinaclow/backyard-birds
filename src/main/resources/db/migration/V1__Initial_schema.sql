-- V1__Initial_schema.sql
-- This creates all your base tables based on your existing entity classes

CREATE TABLE bird_taxonomy
(
    species_code    VARCHAR(50) PRIMARY KEY,
    common_name     VARCHAR(100) NOT NULL,
    scientific_name VARCHAR(100) NOT NULL,
    category        VARCHAR(50)  NOT NULL,
    family          VARCHAR(50)  NOT NULL,
    species_group   VARCHAR(50),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE location
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)     NOT NULL UNIQUE,
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    description TEXT,
    is_active   BOOLEAN          NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE weather
(
    id             BIGSERIAL PRIMARY KEY,
    temperature    DOUBLE PRECISION NOT NULL,
    precipitation  DOUBLE PRECISION,
    wind_speed     INTEGER,
    wind_direction VARCHAR(255),
    conditions     VARCHAR(255),
    humidity       INTEGER,
    recorded_at    TIMESTAMP        NOT NULL,
    created_at     TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bird_observation
(
    id                    BIGSERIAL PRIMARY KEY,
    species_code          VARCHAR(50) NOT NULL REFERENCES bird_taxonomy (species_code),
    location_id           BIGINT      NOT NULL REFERENCES location (id),
    weather_id            BIGINT REFERENCES weather (id),
    observation_datetime  TIMESTAMP   NOT NULL,
    count                 INTEGER     NOT NULL,
    duration_minutes      INTEGER,
    sex                   VARCHAR(20),
    age                   VARCHAR(20),
    behavior              TEXT,
    is_complete_checklist BOOLEAN              DEFAULT FALSE,
    notes                 TEXT,
    created_at            TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
