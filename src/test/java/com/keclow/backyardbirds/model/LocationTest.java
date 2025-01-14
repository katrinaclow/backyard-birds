package com.keclow.backyardbirds.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class LocationTest {
    private Location location;

    @BeforeEach
    void setUp() {
        location = new Location();
    }

    @Test
    void createLocation_ShouldSetAllProperties() {
        // Arrange
        location.setName("Backyard Feeder");
        location.setLatitude(45.523064);
        location.setLongitude(-122.676483);
        location.setDescription("Main feeder in backyard");
        location.setIsActive(true);

        // Assert
        assertThat(location.getName()).isEqualTo("Backyard Feeder");
        assertThat(location.getLatitude()).isEqualTo(45.523064);
        assertThat(location.getLongitude()).isEqualTo(-122.676483);
        assertThat(location.getDescription()).isEqualTo("Main feeder in backyard");
        assertThat(location.getIsActive()).isTrue();
    }

    @Test
    void onCreate_ShouldSetCreatedAt() {
        // Act
        location.onCreate();

        // Assert
        assertThat(location.getCreatedAt())
                .isNotNull()
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }

    @Test
    void createLocation_ShouldHandleNullValues() {
        // Act
        location.setName(null);
        location.setLatitude(null);
        location.setLongitude(null);
        location.setDescription(null);
        location.setIsActive(null);

        // Assert
        assertThat(location.getName()).isNull();
        assertThat(location.getLatitude()).isNull();
        assertThat(location.getLongitude()).isNull();
        assertThat(location.getDescription()).isNull();
        assertThat(location.getIsActive()).isNull();
    }
}