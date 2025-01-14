// BirdObservationController.java
package com.keclow.backyardbirds.controller;

import com.keclow.backyardbirds.model.BirdObservation;
import com.keclow.backyardbirds.service.BirdObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observations")
public class BirdObservationController {

    @Autowired
    private BirdObservationService birdObservationService;

    @PostMapping
    public ResponseEntity<BirdObservation> createObservation(@RequestBody BirdObservation birdObservation) {
        BirdObservation createdObservation = birdObservationService.saveObservation(birdObservation);
        return ResponseEntity.ok(createdObservation);
    }

    @GetMapping
    public ResponseEntity<List<BirdObservation>> getAllObservations() {
        List<BirdObservation> observations = birdObservationService.getAllObservations();
        return ResponseEntity.ok(observations);
    }
}