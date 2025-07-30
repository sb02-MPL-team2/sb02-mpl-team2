package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.ContentDto;
import java.util.List;

public interface ContentService {
  ContentDto findById(Long id);
  List<ContentDto> findAll();
  List<ContentDto> findByCategory(String category);
  void delete(Long id);
}
