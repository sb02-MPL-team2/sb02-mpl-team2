//package com.codeit.sb02mplteam2.domain.content;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.codeit.sb02mplteam2.domain.content.entity.Content;
//import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
//import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
//import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
//import java.util.List;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional // 테스트 후 롤백
//class BasicContentServiceTest {
//
//  @Autowired
//  private BasicContentService basicContentService;
//
//  @Autowired
//  private ContentRepository contentRepository;
//
//  @Test
//  @Disabled
//  void testSaveTmdbMovies_realApi() {
//    // when
//    basicContentService.saveTmdbMovies(ContentCategory.MOVIE);
//
//    // then
//    List<Content> contents = contentRepository.findByCategory(ContentCategory.MOVIE);
//    assertThat(contents).isNotEmpty();
//
//    contents.stream()
//        .limit(5)
//        .forEach(c -> System.out.println(c.getTitle() + " | " + c.getCategory()));
//  }
//
//  @Test
//  @Disabled
//  void testSaveTmdbTvs_realApi() {
//    // when
//    basicContentService.saveTmdbTvs(ContentCategory.TV);
//
//    // then
//    List<Content> contents = contentRepository.findByCategory(ContentCategory.TV);
//    assertThat(contents).isNotEmpty();
//
//    contents.stream()
//        .limit(5)
//        .forEach(c -> System.out.println(c.getTitle() + " | " + c.getCategory()));
//  }
//}