package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ContentService {
  ContentResponseDto findById(Long id);
  List<ContentResponseDto> findAll(Pageable pageable);
  List<ContentResponseDto> findByCategory(ContentCategory category, Pageable pageable);
  void delete(Long id);
  int saveTmdbMovies(ContentCategory category);
  int saveTmdbTvs(ContentCategory category);
}
