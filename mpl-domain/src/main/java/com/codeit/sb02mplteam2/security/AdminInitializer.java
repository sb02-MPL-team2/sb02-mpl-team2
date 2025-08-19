package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class AdminInitializer implements ApplicationRunner {

  private final AuthService authService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    // 서버 실행 후 initAdmin 실행
    authService.initAdmin();
    log.info("✅ 서버 실행 후 initAdmin 실행");
  }
}
