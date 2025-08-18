package com.codeit.sb02mplteam2;


import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistItemService;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
//TODO application 설정을 걸어서 application Initalizer 설정 여부 선택할 수 있게 만들어야함
public class Initializer implements ApplicationRunner {

  private final ContentRepository contentRepository;
  private final PlaylistRepository playlistRepository;
  private final PlaylistItemRepository playlistItemRepository;

  private final PlaylistService playlistService;
  private final PlaylistItemService playlistItemService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (!contentRepository.existsById(1L)) {
      Content content = new Content("인터스텔라", ContentCategory.MOVIE);
      contentRepository.save(content);
      log.info("테스트용 인터스텔라 영화 자동 생성");
    }

    if (!playlistRepository.existsById(1L)) {
      playlistService.create(1L, new PlaylistCreateRequest( "테스트 플리", "테스트 제목"));
      log.info("테스트용 플리 자동 생성");
    }

    if (!playlistItemRepository.existsById(1L)) {
      playlistItemService.addContent(1L, 1L, 1L);
      log.info("테스트용 플리에 콘텐츠 삽입");
    }
  }
}
