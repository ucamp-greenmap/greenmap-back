package com.ucamp.greenmap.badge.domain;

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
@Table(name = "member_badge")
public class MemberBadge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_badge_id")
    private Long memberBadgeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    public void updateBadge(Badge badge) {
        this.badge = badge;
    }
}