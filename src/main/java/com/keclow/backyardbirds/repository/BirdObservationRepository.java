// BirdObservationRepository.java
package com.keclow.backyardbirds.repository;

import com.keclow.backyardbirds.model.BirdObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdObservationRepository extends JpaRepository<BirdObservation, Long> {
}