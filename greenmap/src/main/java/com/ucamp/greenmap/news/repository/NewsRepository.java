package com.ucamp.greenmap.news.repository;

import com.ucamp.greenmap.news.domain.NewsViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsViewLog, Long> {
    boolean existsByNewsTitleAndMember_MemberId(String title, Long memberId);
}
