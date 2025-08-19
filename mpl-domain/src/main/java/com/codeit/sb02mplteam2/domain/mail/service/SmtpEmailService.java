package com.codeit.sb02mplteam2.domain.mail.service;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("dev")
public class SmtpEmailService implements EmailService{

  private final JavaMailSender javaMailSender;

  @Override
  public void sendEmail(String to, String subject, String text) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);
      javaMailSender.send(message);
      log.info("실제 이메일 발송 완료: To={}", to);
    } catch (Exception e) {
      log.error("이메일 발송 중 오류 발생: To={}, Error={}", to, e.getMessage());
      throw new MplException(ErrorCode.EMAIL_SEND_FAILED);
    }
  }
}
