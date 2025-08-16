package com.codeit.sb02mplteam2.domain.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
//@Profile("!prod")
public class LogEmailService implements EmailService{

  @Override
  public void sendEmail(String to, String subject, String text) {
    // 실제 이메일 발송 대신 로그 출력
    log.info("==== MOCK EMAIL SENDING ====");
    log.info("To: {}", to);
    log.info("Subject: {}", subject);
    log.info("Text: {}", text);
    log.info("============================");
  }
}
