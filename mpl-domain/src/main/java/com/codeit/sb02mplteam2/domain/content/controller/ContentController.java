package com.codeit.sb02mplteam2.domain.content.controller;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.content.SaveResultDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.ContentService;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

  private final ContentService contentService;

  @GetMapping("/{id}")
  public ResponseEntity<ContentResponseDto> findById(@PathVariable @Min(1) Long id) {
    return ResponseEntity.ok(contentService.findById(id));
  }

  @GetMapping
  public ResponseEntity<List<ContentResponseDto>> findAll(
      @RequestParam(value = "category", required = false) ContentCategory category,
      @PageableDefault(size = 20) Pageable pageable
  ) {
    List<ContentResponseDto> list;
    if (category == null) {
      list = contentService.findAll(pageable);
    } else {
      list = contentService.findByCategory(category, pageable);
    }
    return ResponseEntity.ok(list);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
    contentService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/tmdb/movies")
  public ResponseEntity<SaveResultDto> saveTmdbMovies() {
    int saved = contentService.saveTmdbMovies(ContentCategory.MOVIE);
    return ResponseEntity.ok(new SaveResultDto(ContentCategory.MOVIE, saved));
  }

  @PostMapping("/tmdb/tvs")
  public ResponseEntity<SaveResultDto> saveTmdbTvs() {
    int saved = contentService.saveTmdbTvs(ContentCategory.TV);
    return ResponseEntity.ok(new SaveResultDto(ContentCategory.TV, saved));
  }
}