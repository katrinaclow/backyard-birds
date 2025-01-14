package com.keclow.backyardbirds.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents weather conditions during bird observations.
 * This entity stores weather-related data that may affect bird activity
 * and observation conditions.
 *
 * @author Katrina Clow
 * @version 1.0
 * @since 2024-01-14
 */
@Entity
@Table(name = "weather")
@Data
@NoArgsConstructor
public class Weather {
    /** Unique identifier for the weather record */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Temperature in degrees (unit to be specified in service layer) */
    @Column(nullable = false)
    private Double temperature;

    /** Precipitation amount (unit to be specified in service layer) */
    private Double precipitation;

    /** Wind speed (unit to be specified in service layer) */
    @Column(name = "wind_speed")
    private Integer windSpeed;

    /** Wind direction (e.g., N, NE, E, etc.) */
    @Column(name = "wind_direction")
    private String windDirection;

    /** General weather conditions description */
    private String conditions;

    /** Relative humidity percentage */
    private Integer humidity;

    /** Time when weather conditions were recorded */
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    /** Timestamp when the record was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Sets the creation timestamp before persisting the entity.
     * Automatically called by JPA during entity creation.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}