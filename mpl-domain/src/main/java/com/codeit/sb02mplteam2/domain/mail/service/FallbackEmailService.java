package com.codeit.sb02mplteam2.domain.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class FallbackEmailService implements EmailService {

  private final SmtpEmailService smtpEmailService;
  private final EmailService fallbackEmailService;
  private final Environment environment;

  public FallbackEmailService(
      SmtpEmailService smtpEmailService,
      @Qualifier("fallbackService") EmailService fallbackEmailService,
      Environment environment) {
    this.smtpEmailService = smtpEmailService;
    this.fallbackEmailService = fallbackEmailService;
    this.environment = environment;
  }

  @Override
  public void sendEmail(String to, String subject, String text) {
    try {
      smtpEmailService.sendEmail(to, subject, text);
      log.info("SMTP 이메일 발송 성공: To={}", to);
    } catch (Exception e) {
      String activeProfile = getActiveProfile();
      log.warn("SMTP 이메일 발송 실패 ({}), 폴백 서비스로 전환: To={}, Error={}", 
          activeProfile, to, e.getMessage());
      
      try {
        fallbackEmailService.sendEmail(to, subject, text);
        log.info("폴백 이메일 서비스 사용 완료 ({}): To={}", activeProfile, to);
      } catch (Exception fallbackException) {
        log.error("폴백 이메일 서비스도 실패 ({}): To={}, Error={}", 
            activeProfile, to, fallbackException.getMessage());
        throw fallbackException;
      }
    }
  }

  private String getActiveProfile() {
    String[] activeProfiles = environment.getActiveProfiles();
    return activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";
  }
}