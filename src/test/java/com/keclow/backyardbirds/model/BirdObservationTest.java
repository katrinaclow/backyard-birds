package com.keclow.backyardbirds.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class BirdObservationTest {
    private BirdObservation observation;

    @BeforeEach
    void setUp() {
        observation = new BirdObservation();
    }

    @Test
    void createBirdObservation_ShouldSetAllProperties() {
        // Arrange
        BirdTaxonomy species = new BirdTaxonomy();
        Location location = new Location();
        Weather weather = new Weather();
        LocalDateTime observationTime = LocalDateTime.now();

        // Act
        observation.setSpeciesCode(species);
        observation.setLocation(location);
        observation.setWeather(weather);
        observation.setObservationDateTime(observationTime);
        observation.setCount(2);
        observation.setBehavior("feeding");
        observation.setIsCompleteChecklist(true);
        observation.setNotes("Test notes");

        // Assert
        assertThat(observation.getSpeciesCode()).isEqualTo(species);
        assertThat(observation.getLocation()).isEqualTo(location);
        assertThat(observation.getWeather()).isEqualTo(weather);
        assertThat(observation.getObservationDateTime()).isEqualTo(observationTime);
        assertThat(observation.getCount()).isEqualTo(2);
        assertThat(observation.getBehavior()).isEqualTo("feeding");
        assertThat(observation.getIsCompleteChecklist()).isTrue();
        assertThat(observation.getNotes()).isEqualTo("Test notes");
    }

    @Test
    void onCreate_ShouldSetTimestamps() {
        // Act
        observation.onCreate();

        // Assert
        assertThat(observation.getCreatedAt()).isNotNull();
        assertThat(observation.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_ShouldUpdateTimestamp() {
        // Act
        observation.onUpdate();

        // Assert
        assertThat(observation.getUpdatedAt())
                .isNotNull()
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }

    @Test
    void createBirdObservation_ShouldHandleNullValues() {
        // Act
        observation.setSpeciesCode(null);
        observation.setLocation(null);
        observation.setWeather(null);
        observation.setObservationDateTime(null);
        observation.setCount(null);
        observation.setBehavior(null);
        observation.setIsCompleteChecklist(null);
        observation.setNotes(null);

        // Assert
        assertThat(observation.getSpeciesCode()).isNull();
        assertThat(observation.getLocation()).isNull();
        assertThat(observation.getWeather()).isNull();
        assertThat(observation.getObservationDateTime()).isNull();
        assertThat(observation.getCount()).isNull();
        assertThat(observation.getBehavior()).isNull();
        assertThat(observation.getIsCompleteChecklist()).isNull();
        assertThat(observation.getNotes()).isNull();
    }
}