package com.ucamp.greenmap.verification.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.place.domain.Place;
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
@Table(name = "history")
public class History extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "charge_amount")
    private Integer chargeAmount;

    @Column(name = "purchase_amount")
    private Integer purchaseAmount;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "carbon_save")
    private Integer carbonSave;
}