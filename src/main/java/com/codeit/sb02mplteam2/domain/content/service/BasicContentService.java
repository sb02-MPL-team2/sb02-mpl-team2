package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.mapper.ContentMapper;
import com.codeit.sb02mplteam2.domain.content.mapper.TmdbContentMapper;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicContentService implements ContentService{

  private final ContentRepository contentRepository;
  private final ContentMapper contentMapper;
  private final TmdbService tmdbService;
  private final TmdbContentMapper tmdbContentMapper;

  @Override
  @Transactional(readOnly = true)
  public ContentResponseDto findById(Long id) {
    Content content = contentRepository.findById(id)
        .orElseThrow(() -> new MplException(ErrorCode.CONTENT_NOT_FOUND));
    return contentMapper.toDto(content);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentResponseDto> findAll() {
    return contentRepository.findAll().stream()
        .map(contentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentResponseDto> findByCategory(ContentCategory category) {
    return contentRepository.findByCategory(category).stream()
        .map(contentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!contentRepository.existsById(id)) {
      throw new MplException(ErrorCode.CONTENT_NOT_FOUND);
    }
    contentRepository.deleteById(id);
  }

  @Override
  @Transactional
  public void saveTmdbMovies(ContentCategory category) {
    List<TmdbMovieDto> movies = tmdbService.getTmdbMovies(category);

    LocalDateTime now = LocalDateTime.now();

    List<Content> contents = movies.stream()
        .map(dto -> tmdbContentMapper.toEntity(dto, category))
        .toList();

    contentRepository.saveAll(contents);
  }

  @Override
  @Transactional
  public void saveTmdbTvs(ContentCategory category) {
    List<TmdbTvDto> tvs = tmdbService.getTmdbTvs(category);

    LocalDateTime now = LocalDateTime.now();

    List<Content> contents = tvs.stream()
        .map(dto -> tmdbContentMapper.toEntity(dto, category))
        .toList();

    contentRepository.saveAll(contents);
  }
}
