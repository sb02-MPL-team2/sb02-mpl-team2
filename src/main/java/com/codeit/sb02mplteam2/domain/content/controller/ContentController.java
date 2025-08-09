package com.codeit.sb02mplteam2.domain.content.controller;

import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

  private final ContentService contentService;

  @PostMapping("/tmdb/movies/{category}")
  public void saveTmdbMovies(@PathVariable ContentCategory category) {
    contentService.saveTmdbMovies(category);
  }

  @PostMapping("/tmdb/tvs/{category}")
  public void saveTmdbTvs(@PathVariable ContentCategory category) {
    contentService.saveTmdbTvs(category);
  }

  @GetMapping
  public Object getAllContents() {
    return contentService.findAll();
  }

  @GetMapping("/category/{category}")
  public Object getContentsByCategory(@PathVariable ContentCategory category) {
    return contentService.findByCategory(category);
  }
}
