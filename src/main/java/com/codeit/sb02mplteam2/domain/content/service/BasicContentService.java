package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.batch.TmdbBatchMetrics;
import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.mapper.ContentMapper;
import com.codeit.sb02mplteam2.domain.content.mapper.TmdbContentMapper;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicContentService implements ContentService{

  private final ContentRepository contentRepository;
  private final ContentMapper contentMapper;
  private final TmdbService tmdbService;
  private final TmdbContentMapper tmdbContentMapper;
  private final TmdbBatchMetrics tmdbBatchMetrics;
  private final LiveWatchService liveWatchService;

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
  @PreAuthorize("hasRole('MANAGER')")
  public void delete(Long id) {
    if (!contentRepository.existsById(id)) {
      throw new MplException(ErrorCode.CONTENT_NOT_FOUND);
    }
    contentRepository.deleteById(id);
  }

  @Override
  @Transactional
  public int saveTmdbMovies(ContentCategory category) {
    List<TmdbMovieDto> movies = tmdbService.getTmdbMovies(category);
    if (movies == null || movies.isEmpty()) {
      tmdbBatchMetrics.recordItemCount(category, 0);
      return 0;
    }

    LocalDateTime now = LocalDateTime.now();
    List<Content> contents = movies.stream()
        .map(dto -> tmdbContentMapper.toEntity(dto, category, now))
        .toList();

    contentRepository.saveAll(contents);

    contents.forEach(c -> liveWatchService.createRoom(c.getId(), c.getTitle()));

    int saved = contents.size();
    tmdbBatchMetrics.recordItemCount(category, saved);
    return saved;
  }

  @Override
  @Transactional
  public int saveTmdbTvs(ContentCategory category) {
    List<TmdbTvDto> tvs = tmdbService.getTmdbTvs(category);
    if (tvs == null || tvs.isEmpty()) {
      tmdbBatchMetrics.recordItemCount(category, 0);
      return 0;
    }

    LocalDateTime now = LocalDateTime.now();
    List<Content> contents = tvs.stream()
        .map(dto -> tmdbContentMapper.toEntity(dto, category, now))
        .toList();

    contentRepository.saveAll(contents);

    contents.forEach(c -> liveWatchService.createRoom(c.getId(), c.getTitle()));

    int saved = contents.size();
    tmdbBatchMetrics.recordItemCount(category, saved);
    return saved;
  }
}
