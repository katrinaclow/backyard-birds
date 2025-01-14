package com.keclow.backyardbirds.controller;

import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.service.BirdTaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxonomy")
public class BirdTaxonomyController {

    private final BirdTaxonomyService birdTaxonomyService;

    @Autowired
    public BirdTaxonomyController(BirdTaxonomyService birdTaxonomyService) {
        this.birdTaxonomyService = birdTaxonomyService;
    }

    @PostMapping
    public ResponseEntity<BirdTaxonomy> createTaxonomy(@RequestBody BirdTaxonomy birdTaxonomy) {
        BirdTaxonomy savedTaxonomy = birdTaxonomyService.saveTaxonomy(birdTaxonomy);
        return ResponseEntity.ok(savedTaxonomy);
    }

    @GetMapping("/{speciesCode}")
    public ResponseEntity<BirdTaxonomy> getTaxonomy(@PathVariable String speciesCode) {
        BirdTaxonomy taxonomy = birdTaxonomyService.getTaxonomy(speciesCode);
        return ResponseEntity.ok(taxonomy);
    }

    @GetMapping
    public ResponseEntity<List<BirdTaxonomy>> getAllTaxonomies() {
        List<BirdTaxonomy> taxonomies = birdTaxonomyService.getAllTaxonomies();
        return ResponseEntity.ok(taxonomies);
    }
}