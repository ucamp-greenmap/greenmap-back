package com.ucamp.greenmap.place.repository;

import com.ucamp.greenmap.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlaceNameAndAddress(String placeName, String address);

    List<Place> findByPlaceNameContainingOrAddressContaining(String placeName, String address);

    Optional<Place> findFirstByCategory_CategoryId(Long categoryId);
    Optional<Place> findByPlaceName(String placeName);


    @Query("""
        select p from Place p
        left join fetch p.openingHours
        left join fetch p.image
        join fetch p.category
    """)
    List<Place> findAllWithJoins();
}
