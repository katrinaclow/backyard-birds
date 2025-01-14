package com.keclow.backyardbirds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.service.BirdTaxonomyService;
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
class BirdTaxonomyControllerTest {

    @Mock
    private BirdTaxonomyService birdTaxonomyService;

    @InjectMocks
    private BirdTaxonomyController birdTaxonomyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BirdTaxonomy testTaxonomy;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(birdTaxonomyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testTaxonomy = new BirdTaxonomy();
        testTaxonomy.setSpeciesCode("NORCAD");
        testTaxonomy.setCommonName("Northern Cardinal");
        testTaxonomy.setScientificName("Cardinalis cardinalis");
        testTaxonomy.setCategory("species");
        testTaxonomy.setFamily("Cardinalidae");
        testTaxonomy.setSpeciesGroup("songbirds");
    }

    @Nested
    @DisplayName("Create Taxonomy Tests")
    class CreateTaxonomyTests {

        @Test
        @DisplayName("Should create taxonomy successfully")
        void createTaxonomy_WithValidData_ShouldSucceed() throws Exception {
            when(birdTaxonomyService.saveTaxonomy(any(BirdTaxonomy.class))).thenReturn(testTaxonomy);

            mockMvc.perform(post("/api/taxonomy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testTaxonomy)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.speciesCode").value(testTaxonomy.getSpeciesCode()))
                    .andExpect(jsonPath("$.commonName").value(testTaxonomy.getCommonName()))
                    .andExpect(jsonPath("$.scientificName").value(testTaxonomy.getScientificName()));
        }

        @Test
        @DisplayName("Should handle invalid taxonomy data")
        void createTaxonomy_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            testTaxonomy.setSpeciesCode(null); // Species code is required

            mockMvc.perform(post("/api/taxonomy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testTaxonomy)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Taxonomy Tests")
    class GetTaxonomyTests {

        @Test
        @DisplayName("Should return taxonomy when exists")
        void getTaxonomy_WhenExists_ShouldReturnTaxonomy() throws Exception {
            when(birdTaxonomyService.getTaxonomy("NORCAD")).thenReturn(testTaxonomy);

            mockMvc.perform(get("/api/taxonomy/NORCAD"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.speciesCode").value(testTaxonomy.getSpeciesCode()))
                    .andExpect(jsonPath("$.commonName").value(testTaxonomy.getCommonName()));
        }

        @Test
        @DisplayName("Should return 404 when taxonomy not found")
        void getTaxonomy_WhenNotExists_ShouldReturn404() throws Exception {
            when(birdTaxonomyService.getTaxonomy("NORCAD"))
                    .thenThrow(new EntityNotFoundException("Bird taxonomy not found"));

            mockMvc.perform(get("/api/taxonomy/NORCAD"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get All Taxonomies Tests")
    class GetAllTaxonomiesTests {

        @Test
        @DisplayName("Should return all taxonomies")
        void getAllTaxonomies_ShouldReturnList() throws Exception {
            List<BirdTaxonomy> taxonomies = List.of(testTaxonomy);
            when(birdTaxonomyService.getAllTaxonomies()).thenReturn(taxonomies);

            mockMvc.perform(get("/api/taxonomy"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].speciesCode").value(testTaxonomy.getSpeciesCode()))
                    .andExpect(jsonPath("$[0].commonName").value(testTaxonomy.getCommonName()));
        }

        @Test
        @DisplayName("Should return empty list when no taxonomies exist")
        void getAllTaxonomies_WhenEmpty_ShouldReturnEmptyList() throws Exception {
            when(birdTaxonomyService.getAllTaxonomies()).thenReturn(List.of());

            mockMvc.perform(get("/api/taxonomy"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }
}