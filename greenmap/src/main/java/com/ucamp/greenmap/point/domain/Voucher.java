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
@Table(name = "voucher")
public class Voucher extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "popular", nullable = false)
    private Boolean popular;
}