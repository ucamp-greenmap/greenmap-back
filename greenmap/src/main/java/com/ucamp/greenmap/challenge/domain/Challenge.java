package com.ucamp.greenmap.challenge.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
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
@Table(name = "challenge")
public class Challenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long challengeId;

    @Column(name = "challenge_name", nullable = false)
    private String challengeName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "member_count", nullable = false)
    private Long memberCount;

    @Column(name = "deadline", nullable = false)
    private Long deadline;

    @Column(name = "success", nullable = false)
    private Long success;

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount;
}