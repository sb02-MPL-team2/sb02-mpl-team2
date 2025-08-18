package com.codeit.sb02mplteam2.domain.recommendation.repository;

import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistScoreRepository extends JpaRepository<PlaylistScore, Long> {

  List<PlaylistScore> findTop3ByCreatedAtBetweenOrderByScoreDesc(LocalDateTime start, LocalDateTime end);}
