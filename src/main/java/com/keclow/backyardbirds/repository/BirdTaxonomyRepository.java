// BirdTaxonomyRepository.java
package com.keclow.backyardbirds.repository;

import com.keclow.backyardbirds.model.BirdTaxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdTaxonomyRepository extends JpaRepository<BirdTaxonomy, String> {
}