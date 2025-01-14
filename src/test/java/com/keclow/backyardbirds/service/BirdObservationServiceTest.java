package com.keclow.backyardbirds.service;

import com.keclow.backyardbirds.model.BirdObservation;
import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.model.Location;
import com.keclow.backyardbirds.repository.BirdObservationRepository;
import com.keclow.backyardbirds.repository.BirdTaxonomyRepository;
import com.keclow.backyardbirds.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BirdObservationServiceTest {

    @Mock
    private BirdObservationRepository birdObservationRepository;

    @Mock
    private BirdTaxonomyRepository birdTaxonomyRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private BirdObservationService birdObservationService;

    private BirdObservation testObservation;
    private BirdTaxonomy testTaxonomy;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testTaxonomy = new BirdTaxonomy();
        testTaxonomy.setSpeciesCode("NORCAD");
        testTaxonomy.setCommonName("Northern Cardinal");
        testTaxonomy.setScientificName("Cardinalis cardinalis");

        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setName("Backyard Feeder");
        testLocation.setLatitude(45.523064);
        testLocation.setLongitude(-122.676483);

        testObservation = new BirdObservation();
        testObservation.setSpeciesCode(testTaxonomy);
        testObservation.setLocation(testLocation);
        testObservation.setObservationDateTime(LocalDateTime.now());
        testObservation.setCount(1);
    }

    @Nested
    @DisplayName("Save Observation Tests")
    class SaveObservationTests {

        @Test
        @DisplayName("Should save observation with valid data")
        void saveObservation_WithValidData_ShouldSucceed() {
            // Arrange
            when(birdTaxonomyRepository.existsById(testTaxonomy.getSpeciesCode())).thenReturn(true);
            when(locationRepository.existsById(testLocation.getId())).thenReturn(true);
            when(birdObservationRepository.save(any(BirdObservation.class))).thenReturn(testObservation);

            // Act
            BirdObservation saved = birdObservationService.saveObservation(testObservation);

            // Assert
            assertThat(saved).isNotNull();
            assertThat(saved.getSpeciesCode()).isEqualTo(testTaxonomy);
            assertThat(saved.getLocation()).isEqualTo(testLocation);
            verify(birdObservationRepository).save(any(BirdObservation.class));
        }

        @Test
        @DisplayName("Should throw exception when species not found")
        void saveObservation_WithInvalidSpecies_ShouldThrowException() {
            // Arrange
            when(birdTaxonomyRepository.existsById(testTaxonomy.getSpeciesCode())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> birdObservationService.saveObservation(testObservation))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Bird species not found");

            verify(birdObservationRepository, never()).save(any(BirdObservation.class));
        }

        @Test
        @DisplayName("Should throw exception when location not found")
        void saveObservation_WithInvalidLocation_ShouldThrowException() {
            // Arrange
            when(birdTaxonomyRepository.existsById(testTaxonomy.getSpeciesCode())).thenReturn(true);
            when(locationRepository.existsById(testLocation.getId())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> birdObservationService.saveObservation(testObservation))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Location not found");

            verify(birdObservationRepository, never()).save(any(BirdObservation.class));
        }

        @Test
        @DisplayName("Should throw exception when observation is null")
        void saveObservation_WithNullObservation_ShouldThrowException() {
            assertThatThrownBy(() -> birdObservationService.saveObservation(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Bird observation cannot be null");

            verify(birdObservationRepository, never()).save(any(BirdObservation.class));
        }
    }

    @Nested
    @DisplayName("Get Observations Tests")
    class GetObservationsTests {

        @Test
        @DisplayName("Should return all observations")
        void getAllObservations_ShouldReturnList() {
            // Arrange
            List<BirdObservation> observations = List.of(testObservation);
            when(birdObservationRepository.findAll()).thenReturn(observations);

            // Act
            List<BirdObservation> found = birdObservationService.getAllObservations();

            // Assert
            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getSpeciesCode()).isEqualTo(testTaxonomy);
            assertThat(found.getFirst().getLocation()).isEqualTo(testLocation);
        }

        @Test
        @DisplayName("Should return empty list when no observations exist")
        void getAllObservations_WhenEmpty_ShouldReturnEmptyList() {
            // Arrange
            when(birdObservationRepository.findAll()).thenReturn(List.of());

            // Act
            List<BirdObservation> found = birdObservationService.getAllObservations();

            // Assert
            assertThat(found).isEmpty();
        }
    }
}