package com.ucamp.greenmap.image.repository;

import com.ucamp.greenmap.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByImageUrl(String imageUrl);
}
