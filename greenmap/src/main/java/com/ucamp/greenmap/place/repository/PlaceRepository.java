package com.ucamp.greenmap.place.repository;

import com.ucamp.greenmap.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlaceNameAndAddress(String placeName, String address);
}
