package com.ucamp.greenmap.news.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
import com.ucamp.greenmap.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "news_view_log")
public class NewsViewLog extends BaseEntity {
    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "news_title", nullable = false)
    private String newsTitle;
}