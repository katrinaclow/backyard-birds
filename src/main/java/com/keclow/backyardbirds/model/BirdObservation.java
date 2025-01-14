package com.keclow.backyardbirds.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a bird observation record.
 * This entity stores individual bird sightings, including species information,
 * location details, weather conditions, and observation specifics.
 *
 * @author Katrina Clow
 * @version 1.0
 * @since 2024-01-14
 */
@Entity
@Table(name = "bird_observation")
@Data
@NoArgsConstructor
public class BirdObservation {
    /** Unique identifier for the observation */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to the observed bird species taxonomy */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_code", nullable = false)
    private BirdTaxonomy speciesCode;

    /** Location where the observation was made */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /** Weather conditions during the observation */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_id")
    private Weather weather;

    /** Date and time when the observation was made */
    @Column(name = "observation_datetime", nullable = false)
    private LocalDateTime observationDateTime;

    /** Number of individual birds observed */
    @Column(nullable = false)
    private Integer count;

    /** Duration of the observation period in minutes */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /** Sex of the observed bird(s) (male, female, unknown) */
    @Column(length = 20)
    private String sex;

    /** Age category of the observed bird(s) (adult, juvenile, unknown) */
    @Column(length = 20)
    private String age;

    /** Observed behavior description */
    private String behavior;

    /** Indicates if all species seen during observation period were recorded */
    @Column(name = "is_complete_checklist")
    private Boolean isCompleteChecklist = false;

    /** Additional observations or comments */
    private String notes;

    /** Timestamp when the record was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the record was last updated */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Sets the creation and update timestamps before persisting the entity.
     * Automatically called by JPA during entity creation.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the last modified timestamp before updating the entity.
     * Automatically called by JPA during entity updates.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}