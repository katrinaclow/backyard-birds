package com.keclow.backyardbirds.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class BirdTaxonomyTest {
    private BirdTaxonomy taxonomy;

    @BeforeEach
    void setUp() {
        taxonomy = new BirdTaxonomy();
    }

    @Test
    void createBirdTaxonomy_ShouldSetAllProperties() {
        // Arrange
        taxonomy.setSpeciesCode("NORCAD");
        taxonomy.setCommonName("Northern Cardinal");
        taxonomy.setScientificName("Cardinalis cardinalis");
        taxonomy.setCategory("species");
        taxonomy.setFamily("Cardinalidae");
        taxonomy.setSpeciesGroup("songbirds");

        // Assert
        assertThat(taxonomy.getSpeciesCode()).isEqualTo("NORCAD");
        assertThat(taxonomy.getCommonName()).isEqualTo("Northern Cardinal");
        assertThat(taxonomy.getScientificName()).isEqualTo("Cardinalis cardinalis");
        assertThat(taxonomy.getCategory()).isEqualTo("species");
        assertThat(taxonomy.getFamily()).isEqualTo("Cardinalidae");
        assertThat(taxonomy.getSpeciesGroup()).isEqualTo("songbirds");
    }

    @Test
    void onCreate_ShouldSetCreatedAt() {
        // Act
        taxonomy.onCreate();

        // Assert
        assertThat(taxonomy.getCreatedAt())
                .isNotNull()
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }

    @Test
    void createBirdTaxonomy_ShouldHandleNullValues() {
        // Act
        taxonomy.setSpeciesCode(null);
        taxonomy.setCommonName(null);
        taxonomy.setScientificName(null);
        taxonomy.setCategory(null);
        taxonomy.setFamily(null);
        taxonomy.setSpeciesGroup(null);

        // Assert
        assertThat(taxonomy.getSpeciesCode()).isNull();
        assertThat(taxonomy.getCommonName()).isNull();
        assertThat(taxonomy.getScientificName()).isNull();
        assertThat(taxonomy.getCategory()).isNull();
        assertThat(taxonomy.getFamily()).isNull();
        assertThat(taxonomy.getSpeciesGroup()).isNull();
    }
}