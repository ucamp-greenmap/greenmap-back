package com.ucamp.greenmap.badge.domain;

import com.ucamp.greenmap.common.domain.BaseEntity;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.image.domain.Image;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "badge")
public class Badge extends BaseEntity {
    @Id
    @Column(name = "badge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "badge_name", nullable = false)
    private String badgeName;

    @Column(name = "requirement", nullable = false)
    private Long requirement;

    @Column(name = "description")
    private String description;
}