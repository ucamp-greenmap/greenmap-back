package com.ucamp.greenmap.point.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
import com.ucamp.greenmap.image.domain.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gifticon")
public class Gifticon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_id")
    private Long giftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "name", nullable = false)
    private String name;
}