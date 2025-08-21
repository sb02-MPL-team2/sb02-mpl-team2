package com.codeit.sb02mplteam2.domain.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class SesEmailSendService implements EmailService {

  @Value("${spring.mail.username}")
  private String fromEmail;

  private SesClient sesClient;

  @Override
  public void sendEmail(String to, String subject, String text) {
    // Destination: 받는 사람
    Destination destination = Destination.builder()
        .toAddresses(to)
        .build();

    // Message: 제목과 본문
    Content subjectContent = Content.builder().data(subject).build();
    Content textContent = Content.builder().data(text).build();
    Body body = Body.builder().text(textContent).build();

    Message message = Message.builder()
        .subject(subjectContent)
        .body(body)
        .build();

    SendEmailRequest emailRequest = SendEmailRequest.builder()
        .source(fromEmail) // 보내는 사람
        .destination(destination) // 목적지
        .message(message) //보내는 메일
        .build();

    sesClient.sendEmail(emailRequest);
  }
}
