package com.keclow.backyardbirds.service;

import com.keclow.backyardbirds.model.BirdObservation;
import com.keclow.backyardbirds.model.BirdTaxonomy;
import com.keclow.backyardbirds.model.Location;
import com.keclow.backyardbirds.repository.BirdObservationRepository;
import com.keclow.backyardbirds.repository.BirdTaxonomyRepository;
import com.keclow.backyardbirds.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BirdObservationService {

    private final BirdObservationRepository birdObservationRepository;
    private final BirdTaxonomyRepository birdTaxonomyRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public BirdObservationService(
            BirdObservationRepository birdObservationRepository,
            BirdTaxonomyRepository birdTaxonomyRepository,
            LocationRepository locationRepository) {
        this.birdObservationRepository = birdObservationRepository;
        this.birdTaxonomyRepository = birdTaxonomyRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public BirdObservation saveObservation(BirdObservation birdObservation) {
        // Validate species exists
        BirdTaxonomy speciesCode = birdObservation.getSpeciesCode();
        if (speciesCode == null || !birdTaxonomyRepository.existsById(speciesCode.getSpeciesCode())) {
            throw new EntityNotFoundException("Bird species not found");
        }

        // Validate location exists
        Location location = birdObservation.getLocation();
        if (location == null || !locationRepository.existsById(location.getId())) {
            throw new EntityNotFoundException("Location not found");
        }

        return birdObservationRepository.save(birdObservation);
    }

    public List<BirdObservation> getAllObservations() {
        return birdObservationRepository.findAll();
    }
}