package com.keclow.backyardbirds.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a bird observation location.
 * This entity stores information about specific locations where bird observations are made,
 * such as feeders, birdbaths, or other observation points.
 *
 * @author Katrina Clow
 * @version 1.0
 * @since 2024-01-14
 */
@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
public class Location {
    /** Unique identifier for the location */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Descriptive name of the location */
    @Column(nullable = false, unique = true)
    private String name;

    /** Latitude coordinate of the location */
    @Column(nullable = false)
    private Double latitude;

    /** Longitude coordinate of the location */
    @Column(nullable = false)
    private Double longitude;

    /** Additional details about the location */
    private String description;

    /** Indicates if the location is currently active for observations */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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