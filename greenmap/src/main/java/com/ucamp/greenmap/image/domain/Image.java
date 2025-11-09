package com.ucamp.greenmap.image.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되도록 설정
    @Column(name = "image_id")
    private Long imageId;

    @Column(length = 1000)
    private String imageUrl;

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Image() {}
}
