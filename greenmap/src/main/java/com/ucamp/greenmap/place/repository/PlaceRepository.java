package com.ucamp.greenmap.place.repository;

import com.ucamp.greenmap.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
