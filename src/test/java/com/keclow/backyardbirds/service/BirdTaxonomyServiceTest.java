package com.keclow.backyardbirds.service;

import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.repository.BirdTaxonomyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BirdTaxonomyServiceTest {

    @Mock
    private BirdTaxonomyRepository birdTaxonomyRepository;

    @InjectMocks
    private BirdTaxonomyService birdTaxonomyService;

    private BirdTaxonomy testTaxonomy;

    @BeforeEach
    void setUp() {
        testTaxonomy = new BirdTaxonomy();
        testTaxonomy.setSpeciesCode("NORCAD");
        testTaxonomy.setCommonName("Northern Cardinal");
        testTaxonomy.setScientificName("Cardinalis cardinalis");
        testTaxonomy.setCategory("species");
        testTaxonomy.setFamily("Cardinalidae");
    }

    @Nested
    @DisplayName("Save Taxonomy Tests")
    class SaveTaxonomyTests {

        @Test
        @DisplayName("Should successfully save valid taxonomy")
        void saveTaxonomy_WithValidData_ShouldSucceed() {
            when(birdTaxonomyRepository.save(any(BirdTaxonomy.class))).thenReturn(testTaxonomy);

            BirdTaxonomy saved = birdTaxonomyService.saveTaxonomy(testTaxonomy);

            assertThat(saved).isNotNull();
            assertThat(saved.getSpeciesCode()).isEqualTo(testTaxonomy.getSpeciesCode());
            verify(birdTaxonomyRepository).save(any(BirdTaxonomy.class));
        }

        @Test
        @DisplayName("Should handle save with null taxonomy")
        void saveTaxonomy_WithNullTaxonomy_ShouldThrowException() {
            assertThatThrownBy(() -> birdTaxonomyService.saveTaxonomy(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Bird taxonomy cannot be null");

            verify(birdTaxonomyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Taxonomy Tests")
    class GetTaxonomyTests {

        @Test
        @DisplayName("Should return taxonomy when exists")
        void getTaxonomy_WhenExists_ShouldReturnTaxonomy() {
            when(birdTaxonomyRepository.findById("NORCAD")).thenReturn(Optional.of(testTaxonomy));

            BirdTaxonomy found = birdTaxonomyService.getTaxonomy("NORCAD");

            assertThat(found).isNotNull();
            assertThat(found.getSpeciesCode()).isEqualTo("NORCAD");
        }

        @Test
        @DisplayName("Should throw exception when taxonomy not found")
        void getTaxonomy_WhenNotExists_ShouldThrowException() {
            when(birdTaxonomyRepository.findById("NORCAD")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> birdTaxonomyService.getTaxonomy("NORCAD"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Bird taxonomy not found");
        }
    }

    @Nested
    @DisplayName("Get All Taxonomies Tests")
    class GetAllTaxonomiesTests {

        @Test
        @DisplayName("Should return all taxonomies")
        void getAllTaxonomies_ShouldReturnList() {
            List<BirdTaxonomy> taxonomies = List.of(testTaxonomy);
            when(birdTaxonomyRepository.findAll()).thenReturn(taxonomies);

            List<BirdTaxonomy> found = birdTaxonomyService.getAllTaxonomies();

            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getSpeciesCode()).isEqualTo(testTaxonomy.getSpeciesCode());
        }

        @Test
        @DisplayName("Should return empty list when no taxonomies exist")
        void getAllTaxonomies_WhenEmpty_ShouldReturnEmptyList() {
            when(birdTaxonomyRepository.findAll()).thenReturn(List.of());

            List<BirdTaxonomy> found = birdTaxonomyService.getAllTaxonomies();

            assertThat(found).isEmpty();
        }
    }
}