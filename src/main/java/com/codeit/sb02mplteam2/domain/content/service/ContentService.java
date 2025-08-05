package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.util.List;

public interface ContentService {
  ContentResponseDto findById(Long id);
  List<ContentResponseDto> findAll();
  List<ContentResponseDto> findByCategory(ContentCategory category);
  void delete(Long id);
  void saveTmdbMovies(ContentCategory category);
  void saveTmdbTvs(ContentCategory category);
}
