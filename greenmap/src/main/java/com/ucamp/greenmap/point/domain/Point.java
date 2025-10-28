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
    private Integer point;

    @Column(name = "month_point", nullable = false)
    private Integer monthPoint;

    @Column(name = "used_point", nullable = false)
    private Integer usedPoint;

    @Column(name = "whole_point", nullable = false)
    private Integer wholePoint;

    @Column(name = "carbon_save_total", nullable = false)
    private Integer carbonSaveTotal;

    @Column(name = "point_times", nullable = false)
    private Integer pointTimes;

    @Column(name = "whole_point_times", nullable = false)
    private Integer wholePointTimes;
}