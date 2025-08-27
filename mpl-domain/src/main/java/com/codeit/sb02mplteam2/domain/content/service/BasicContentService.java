package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.content.ScrollResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.livewatch.redis.RedisLiveWatchParticipantService;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicContentService implements ContentService{

  private final ContentRepository contentRepository;
  private final RedisLiveWatchParticipantService redisService;

  @Override
  @Transactional(readOnly = true)
  public ContentResponseDto findById(Long id) {
    return contentRepository.findByIdWithRoom(id)
        .map(this::replaceWatchCount)
        .orElseThrow(() -> new MplException(ErrorCode.CONTENT_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentResponseDto> findAll(Pageable pageable) {
    Page<ContentResponseDto> page = contentRepository.findAllWithRoom(pageable);
    return page.map(this::replaceWatchCount).getContent();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentResponseDto> findByCategory(ContentCategory category, Pageable pageable) {
    Page<ContentResponseDto> page =
        contentRepository.findByCategoryWithRoom(category, pageable);
    return page.map(this::replaceWatchCount).getContent();
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
  @Transactional(readOnly = true)
  public ScrollResponseDto<ContentResponseDto> scroll(
      ContentCategory category,
      LocalDate cursorDate,
      int size
  ) {
    Pageable pageable = PageRequest.of(
        0,
        size + 1,
        Sort.by(Sort.Direction.DESC, "releaseDate").and(Sort.by(Sort.Direction.DESC, "id"))
    );

    List<ContentResponseDto> rows;
    if (cursorDate == null) {
      rows = contentRepository.scrollContentsWithoutCursor(category, pageable);
    } else {
      rows = contentRepository.scrollContents(category, cursorDate, Long.MAX_VALUE, pageable);
    }

    var replaced = rows.stream()
        .map(this::replaceWatchCount)
        .toList();

    boolean hasNext = replaced.size() > size;

    List<ContentResponseDto> items;
    if (hasNext) {
      items = replaced.subList(0, size);
    } else {
      items = replaced;
    }

    LocalDate nextCursorDate = null;
    Long nextCursorId = null;

    if (!items.isEmpty()) {
      var last = items.get(items.size() - 1);
      nextCursorDate = last.releaseDate();
      nextCursorId = last.id();
    }

    return new ScrollResponseDto<>(items, hasNext, nextCursorDate, nextCursorId);
  }

  private ContentResponseDto replaceWatchCount(ContentResponseDto dto) {
    Long watchCount = 0L;
    if (dto.roomId() != null) {
      watchCount = redisService.getParticipantCount(dto.roomId()).longValue();
    }
    return new ContentResponseDto(
        dto.id(),
        dto.title(),
        dto.description(),
        dto.category(),
        dto.imageUrl(),
        dto.totalRating(),
        dto.reviewCount(),
        watchCount,
        dto.runtime(),
        dto.roomId(),
        dto.releaseDate()
    );
  }
}
