Start new chat

    Projects

Starred

    Backyard Birds

Recents

Troubleshooting Sony A7III Camera Media Display Issue
Connecting GitHub Project to PostgreSQL Database
Setting up PostgreSQL after computer reset
    Setting up Java Development Environment
Identifying a Northern Goshawk in PEI
Installing projectM on Ubuntu
(New chat)

    Visualize Music with ProjectM on Laptop and Raspberry Pi

View all
Professional plan
Help & support
K
All projects
Backyard Birds
Private
I am creating a web app for personal use. It is for recording and storing observations of the birds at my feeder and yard, and then to be able to retrieve and analyze that data to produce information or graphs. It will also have the ability to use the eBird API.


Backyard Birds

    Setting up PostgreSQL after computer reset
    Last message 22 minutes ago 

Factory Reset for MacBook Air Mid-2011
Last message 7 days ago 
Implementing Bird Observation Recording
Last message 13 days ago 
Modern CSS Approaches for Spring Boot and React
Last message 13 days ago 
Building a Bird Observation App
Last message 16 days ago 
Generating a Comprehensive README for Your Project
Last message 18 days ago 
Planning a Personal Web App
Last message 18 days ago 
Displaying Bird Sightings on a Map
Last message 19 days ago 
Project knowledge
7% of knowledge capacity used
Next Step Patterns
13 days ago
Design Patterns
13 days ago
Architecture
13 days ago
V2__Remove_observation_columns
13 days ago
V1__Initial_schema
13 days ago
application.properties
13 days ago
LocationService
13 days ago
BirdTaxonomyService
13 days ago
BirdObservationService
13 days ago
WeatherRepository
13 days ago
LocationRepository
13 days ago
BirdTaxonomyRepository
13 days ago
BirdObservationRepository
13 days ago
Weather
13 days ago
Location
13 days ago
FoodType
13 days ago
BirdTaxonomy
13 days ago
BirdObservation
13 days ago
LocationController
13 days ago
GlobalExceptionHandler
13 days ago
BirdTaxonomyController
13 days ago
BirdObservationController
13 days ago
BackyardBirdsApplication
13 days ago
project-plan
19 days ago
V1__Initial_schema.sql

1.93 KB •55 lines•Formatting may be inconsistent from source
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
