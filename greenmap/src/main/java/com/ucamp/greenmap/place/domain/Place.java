package com.ucamp.greenmap.place.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
import com.ucamp.greenmap.common.domain.Category;
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
@Table(name = "place")
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "opening_id")
    private OpeningHours openingHours;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "location_x", nullable = false)
    private Double locationX;

    @Column(name = "location_y", nullable = false)
    private Double locationY;

    @Column(name = "address")
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "tel_num")
    private String telNum;

    public void updateLocationX(double newX){
        this.locationX = newX;
    }
    public void updateLocationY(double newY){
        this.locationY = newY;
    }
}