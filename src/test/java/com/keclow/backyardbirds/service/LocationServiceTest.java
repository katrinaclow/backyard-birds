package com.keclow.backyardbirds.service;

import com.keclow.backyardbirds.model.Location;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setName("Test Location");
        testLocation.setLatitude(45.523064);
        testLocation.setLongitude(-122.676483);
        testLocation.setDescription("Test Description");
        testLocation.setIsActive(true);
    }

    @Nested
    @DisplayName("Save Location Tests")
    class SaveLocationTests {

        @Test
        @DisplayName("Should successfully save valid location")
        void saveLocation_WithValidData_ShouldSucceed() {
            when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

            Location saved = locationService.saveLocation(testLocation);

            assertThat(saved).isNotNull();
            assertThat(saved.getName()).isEqualTo(testLocation.getName());
            verify(locationRepository).save(any(Location.class));
        }

        @Test
        @DisplayName("Should handle save with null location")
        void saveLocation_WithNullLocation_ShouldThrowException() {
            assertThatThrownBy(() -> locationService.saveLocation(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Location cannot be null");

            verify(locationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Location Tests")
    class GetLocationTests {

        @Test
        @DisplayName("Should return location when exists")
        void getLocation_WhenExists_ShouldReturnLocation() {
            when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));

            Location found = locationService.getLocation(1L);

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when location not found")
        void getLocation_WhenNotExists_ShouldThrowException() {
            when(locationRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> locationService.getLocation(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Location not found");
        }
    }

    @Nested
    @DisplayName("Get All Locations Tests")
    class GetAllLocationsTests {

        @Test
        @DisplayName("Should return all locations")
        void getAllLocations_ShouldReturnList() {
            List<Location> locations = List.of(testLocation);
            when(locationRepository.findAll()).thenReturn(locations);

            List<Location> found = locationService.getAllLocations();

            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getName()).isEqualTo(testLocation.getName());
        }

        @Test
        @DisplayName("Should return empty list when no locations exist")
        void getAllLocations_WhenEmpty_ShouldReturnEmptyList() {
            when(locationRepository.findAll()).thenReturn(List.of());

            List<Location> found = locationService.getAllLocations();

            assertThat(found).isEmpty();
        }
    }
}