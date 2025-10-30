package com.ucamp.greenmap.image.repository;

import com.ucamp.greenmap.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    // 필요한 메소드 정의
}
