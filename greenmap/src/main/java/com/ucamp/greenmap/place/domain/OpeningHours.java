package com.ucamp.greenmap.place.domain;

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
@Table(name = "opening_hours")
public class OpeningHours extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opening_id")
    private Long openingId;

    @Column(name = "weekday_open")
    private String weekdayOpen;

    @Column(name = "weekday_close")
    private String weekdayClose;

    @Column(name = "weekend_open")
    private String weekendOpen;

    @Column(name = "weekend_close")
    private String weekendClose;

    @Column(name = "opening_days")
    private String openingDays;
}