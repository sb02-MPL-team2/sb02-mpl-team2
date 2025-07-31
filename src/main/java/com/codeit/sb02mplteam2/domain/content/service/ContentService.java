package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.ContentRequestDto;
import com.codeit.sb02mplteam2.domain.content.dto.ContentResponseDto;
import java.util.List;

public interface ContentService {
  ContentResponseDto findById(Long id);
  List<ContentResponseDto> findAll();
  List<ContentResponseDto> findByCategory(String category);
  void delete(Long id);
  void saveContent(ContentRequestDto dto);
}
