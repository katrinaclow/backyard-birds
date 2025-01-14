package com.keclow.backyardbirds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keclow.backyardbirds.model.BirdObservation;
import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.model.Location;
import com.keclow.backyardbirds.service.BirdObservationService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BirdObservationControllerTest {

    @Mock
    private BirdObservationService birdObservationService;

    @InjectMocks
    private BirdObservationController birdObservationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BirdObservation testObservation;
    private BirdTaxonomy testTaxonomy;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(birdObservationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
        testObservation.setId(1L);
        testObservation.setSpeciesCode(testTaxonomy);
        testObservation.setLocation(testLocation);
        testObservation.setObservationDateTime(LocalDateTime.now());
        testObservation.setCount(1);
    }

    @Nested
    @DisplayName("Create Observation Tests")
    class CreateObservationTests {

        @Test
        @DisplayName("Should create observation successfully")
        void createObservation_WithValidData_ShouldSucceed() throws Exception {
            when(birdObservationService.saveObservation(any(BirdObservation.class)))
                    .thenReturn(testObservation);

            mockMvc.perform(post("/api/observations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testObservation)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testObservation.getId()))
                    .andExpect(jsonPath("$.count").value(testObservation.getCount()));
        }

        @Test
        @DisplayName("Should handle invalid species reference")
        void createObservation_WithInvalidSpecies_ShouldReturn404() throws Exception {
            when(birdObservationService.saveObservation(any(BirdObservation.class)))
                    .thenThrow(new EntityNotFoundException("Bird species not found"));

            mockMvc.perform(post("/api/observations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testObservation)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle missing required fields")
        void createObservation_WithMissingFields_ShouldReturnBadRequest() throws Exception {
            testObservation.setSpeciesCode(null);
            testObservation.setLocation(null);

            mockMvc.perform(post("/api/observations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testObservation)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Observation Tests")
    class GetObservationTests {

        @Test
        @DisplayName("Should return all observations")
        void getAllObservations_ShouldReturnList() throws Exception {
            List<BirdObservation> observations = List.of(testObservation);
            when(birdObservationService.getAllObservations()).thenReturn(observations);

            mockMvc.perform(get("/api/observations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(testObservation.getId()))
                    .andExpect(jsonPath("$[0].count").value(testObservation.getCount()));
        }

        @Test
        @DisplayName("Should return empty list when no observations exist")
        void getAllObservations_WhenEmpty_ShouldReturnEmptyList() throws Exception {
            when(birdObservationService.getAllObservations()).thenReturn(List.of());

            mockMvc.perform(get("/api/observations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Date Range Query Tests")
    class DateRangeTests {

//        @Test
//        @DisplayName("Should return observations within date range")
//        void getObservationsByDateRange_ShouldReturnFilteredList() throws Exception {
//            List<BirdObservation> observations = List.of(testObservation);
//            LocalDateTime start = LocalDateTime.now().minusDays(7);
//            LocalDateTime end = LocalDateTime.now();
//
//            when(birdObservationService.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
//                    .thenReturn(observations);
//
//            mockMvc.perform(get("/api/observations/range")
//                            .param("start", start.toString())
//                            .param("end", end.toString()))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$[0].id").value(testObservation.getId()));
//        }

        @Test
        @DisplayName("Should handle invalid date range parameters")
        void getObservationsByDateRange_WithInvalidDates_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/observations/range")
                            .param("start", "invalid-date")
                            .param("end", LocalDateTime.now().toString()))
                    .andExpect(status().isBadRequest());
        }
    }
}