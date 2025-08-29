package com.codeit.sb02mplteam2.domain.mail.config;

import com.codeit.sb02mplteam2.domain.mail.service.EmailService;
import com.codeit.sb02mplteam2.domain.mail.service.LogEmailService;
import com.codeit.sb02mplteam2.domain.mail.service.SesEmailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EmailServiceConfig {

  @Autowired
  private Environment environment;

  @Bean(name = "fallbackService")
  public EmailService fallbackEmailService(
      @Autowired(required = false) LogEmailService logEmailService,
      @Autowired(required = false) SesEmailSendService sesEmailSendService) {
    
    String[] activeProfiles = environment.getActiveProfiles();
    boolean isProdProfile = false;
    
    for (String profile : activeProfiles) {
      if ("prod".equals(profile)) {
        isProdProfile = true;
        break;
      }
    }
    
    if (isProdProfile && logEmailService != null) {
      return logEmailService;
    } else if (!isProdProfile && sesEmailSendService != null) {
      return sesEmailSendService;
    } else if (logEmailService != null) {
      return logEmailService;
    } else if (sesEmailSendService != null) {
      return sesEmailSendService;
    } else {
      throw new IllegalStateException("적절한 폴백 이메일 서비스를 찾을 수 없습니다.");
    }
  }
}