package com.keclow.backyardbirds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keclow.backyardbirds.model.Location;
import com.keclow.backyardbirds.service.LocationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setName("Test Location");
        testLocation.setLatitude(45.523064);
        testLocation.setLongitude(-122.676483);
        testLocation.setDescription("Test Description");
        testLocation.setIsActive(true);
    }

    @Nested
    @DisplayName("Create Location Tests")
    class CreateLocationTests {

        @Test
        @DisplayName("Should create location successfully")
        void createLocation_WithValidData_ShouldSucceed() throws Exception {
            when(locationService.saveLocation(any(Location.class))).thenReturn(testLocation);

            mockMvc.perform(post("/api/locations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testLocation)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testLocation.getId()))
                    .andExpect(jsonPath("$.name").value(testLocation.getName()));
        }

        @Test
        @DisplayName("Should handle invalid location data")
        void createLocation_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            testLocation.setName(null); // Name is required

            mockMvc.perform(post("/api/locations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testLocation)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Location Tests")
    class GetLocationTests {

        @Test
        @DisplayName("Should return location when exists")
        void getLocation_WhenExists_ShouldReturnLocation() throws Exception {
            when(locationService.getLocation(1L)).thenReturn(testLocation);

            mockMvc.perform(get("/api/locations/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testLocation.getId()))
                    .andExpect(jsonPath("$.name").value(testLocation.getName()));
        }

        @Test
        @DisplayName("Should return 404 when location not found")
        void getLocation_WhenNotExists_ShouldReturn404() throws Exception {
            when(locationService.getLocation(1L))
                    .thenThrow(new EntityNotFoundException("Location not found"));

            mockMvc.perform(get("/api/locations/1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get All Locations Tests")
    class GetAllLocationsTests {

        @Test
        @DisplayName("Should return all locations")
        void getAllLocations_ShouldReturnList() throws Exception {
            List<Location> locations = List.of(testLocation);
            when(locationService.getAllLocations()).thenReturn(locations);

            mockMvc.perform(get("/api/locations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(testLocation.getId()))
                    .andExpect(jsonPath("$[0].name").value(testLocation.getName()));
        }

        @Test
        @DisplayName("Should return empty list when no locations exist")
        void getAllLocations_WhenEmpty_ShouldReturnEmptyList() throws Exception {
            when(locationService.getAllLocations()).thenReturn(List.of());

            mockMvc.perform(get("/api/locations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }
}