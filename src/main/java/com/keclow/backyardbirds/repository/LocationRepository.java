// LocationRepository.java
package com.keclow.backyardbirds.repository;

import com.keclow.backyardbirds.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}