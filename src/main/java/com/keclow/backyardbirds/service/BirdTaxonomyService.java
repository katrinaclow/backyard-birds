package com.keclow.backyardbirds.service;

import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.repository.BirdTaxonomyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BirdTaxonomyService {

    private final BirdTaxonomyRepository birdTaxonomyRepository;

    @Autowired
    public BirdTaxonomyService(BirdTaxonomyRepository birdTaxonomyRepository) {
        this.birdTaxonomyRepository = birdTaxonomyRepository;
    }

    public BirdTaxonomy saveTaxonomy(BirdTaxonomy birdTaxonomy) {
        if (birdTaxonomy == null) {
            throw new IllegalArgumentException("Bird taxonomy cannot be null");
        }
        return birdTaxonomyRepository.save(birdTaxonomy);
    }

    public BirdTaxonomy getTaxonomy(String speciesCode) {
        return birdTaxonomyRepository.findById(speciesCode)
                .orElseThrow(() -> new EntityNotFoundException("Bird taxonomy not found"));
    }

    public List<BirdTaxonomy> getAllTaxonomies() {
        return birdTaxonomyRepository.findAll();
    }
}