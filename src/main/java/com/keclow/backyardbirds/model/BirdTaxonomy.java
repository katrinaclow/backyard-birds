package com.keclow.backyardbirds.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a bird species taxonomy based on eBird classification.
 * This entity stores standardized bird species information and taxonomic classification.
 *
 * @author Katrina Clow
 * @version 1.0
 * @since 2024-01-14
 */
@Entity
@Table(name = "bird_taxonomy")
@Data
@NoArgsConstructor
public class BirdTaxonomy {
    /** Unique eBird species code identifier */
    @Id
    @Column(name = "species_code", length = 50)
    private String speciesCode;

    /** Common name of the bird species */
    @Column(name = "common_name", length = 100, nullable = false)
    private String commonName;

    /** Scientific (Latin) name of the bird species */
    @Column(name = "scientific_name", length = 100, nullable = false)
    private String scientificName;

    /** Taxonomic category (e.g., species, subspecies) */
    @Column(length = 50, nullable = false)
    private String category;

    /** Taxonomic family classification */
    @Column(length = 50, nullable = false)
    private String family;

    /** General bird group classification (e.g., songbirds, waterfowl) */
    @Column(name = "species_group", length = 50)
    private String speciesGroup;

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