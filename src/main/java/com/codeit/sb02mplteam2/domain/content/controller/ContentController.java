package com.codeit.sb02mplteam2.domain.content.controller;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.content.SaveResultDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.ContentService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
      @RequestParam(value = "category", required = false) ContentCategory category
  ) {
    if (category == null) {
      return ResponseEntity.ok(contentService.findAll());
    }
    return ResponseEntity.ok(contentService.findByCategory(category));
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