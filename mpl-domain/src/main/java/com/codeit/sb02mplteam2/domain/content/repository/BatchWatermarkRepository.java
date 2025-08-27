package com.codeit.sb02mplteam2.domain.content.repository;

import com.codeit.sb02mplteam2.domain.content.entity.BatchWatermark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchWatermarkRepository extends JpaRepository<BatchWatermark, String> {
}