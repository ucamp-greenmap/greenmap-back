package com.ucamp.greenmap.point.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
import com.ucamp.greenmap.member.domain.Member;
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
@Table(name = "point")
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "point", nullable = false)
    private Long point;

    @Column(name = "month_point", nullable = false)
    private Long monthPoint;

    @Column(name = "used_point", nullable = false)
    private Long usedPoint;

    @Column(name = "whole_point", nullable = false)
    private Long wholePoint;

    @Column(name = "carbon_save_total", nullable = false)
    private Long carbonSaveTotal;

    @Column(name = "point_times", nullable = false)
    private Long pointTimes;

    @Column(name = "whole_point_times", nullable = false)
    private Long wholePointTimes;

    public void addPoint(Long getPoint) {
        this.point += getPoint;
        this.monthPoint += getPoint;
        this.wholePoint += getPoint;
        this.pointTimes += 1;
        this.wholePointTimes += 1;
    }

    public void addCarbonSaveTotal(Long carbonSave) {
        this.carbonSaveTotal += carbonSave;
    }

    public void usePoint(Long usedPoint) {
        this.usedPoint += usedPoint;
        this.point -= usedPoint;
    }
    public void addPointChallenge(Long getPoint) {
        this.point += getPoint;
        this.monthPoint += getPoint;
        this.wholePoint += getPoint;
    }
}