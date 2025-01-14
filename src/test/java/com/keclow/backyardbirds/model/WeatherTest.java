package com.keclow.backyardbirds.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class WeatherTest {
    private Weather weather;

    @BeforeEach
    void setUp() {
        weather = new Weather();
    }

    @Test
    void createWeather_ShouldSetAllProperties() {
        // Arrange
        LocalDateTime recordedTime = LocalDateTime.now();
        weather.setTemperature(72.5);
        weather.setPrecipitation(0.0);
        weather.setWindSpeed(5);
        weather.setWindDirection("NW");
        weather.setConditions("Partly Cloudy");
        weather.setHumidity(65);
        weather.setRecordedAt(recordedTime);

        // Assert
        assertThat(weather.getTemperature()).isEqualTo(72.5);
        assertThat(weather.getPrecipitation()).isEqualTo(0.0);
        assertThat(weather.getWindSpeed()).isEqualTo(5);
        assertThat(weather.getWindDirection()).isEqualTo("NW");
        assertThat(weather.getConditions()).isEqualTo("Partly Cloudy");
        assertThat(weather.getHumidity()).isEqualTo(65);
        assertThat(weather.getRecordedAt()).isEqualTo(recordedTime);
    }

    @Test
    void onCreate_ShouldSetCreatedAt() {
        // Act
        weather.onCreate();

        // Assert
        assertThat(weather.getCreatedAt())
                .isNotNull()
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }

    @Test
    void createWeather_ShouldHandleNullValues() {
        // Act
        weather.setTemperature(null);
        weather.setPrecipitation(null);
        weather.setWindSpeed(null);
        weather.setWindDirection(null);
        weather.setConditions(null);
        weather.setHumidity(null);
        weather.setRecordedAt(null);

        // Assert
        assertThat(weather.getTemperature()).isNull();
        assertThat(weather.getPrecipitation()).isNull();
        assertThat(weather.getWindSpeed()).isNull();
        assertThat(weather.getWindDirection()).isNull();
        assertThat(weather.getConditions()).isNull();
        assertThat(weather.getHumidity()).isNull();
        assertThat(weather.getRecordedAt()).isNull();
    }
}