package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.domain.auth.service.AuthService;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {

  private final AuthService authService;
  private final ContentRepository contentRepository;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    // 서버 실행 후 initAdmin 실행
    authService.initAdmin();
    log.info("✅ 서버 실행 후 initAdmin 실행");

    Content content = new Content("인터스텔라", ContentCategory.MOVIE);
    contentRepository.save(content);
    log.info("테스트용 인터스텔라 영화 자동 생성");
  }
}
