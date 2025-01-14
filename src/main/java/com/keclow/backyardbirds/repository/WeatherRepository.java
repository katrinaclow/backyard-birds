// WeatherRepository.java
package com.keclow.backyardbirds.repository;

import com.keclow.backyardbirds.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
}