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
    @Column(name = "history_id")
    private Long historyId;

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
    private Long chargeAmount;

    @Column(name = "purchase_amount")
    private Long purchaseAmount;

    @Column(name = "distance")
    private Long distance;

    @Column(name = "carbon_save")
    private Long carbonSave;
}