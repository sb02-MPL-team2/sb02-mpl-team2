package com.codeit.sb02mplteam2.domain.mail;

import com.codeit.sb02mplteam2.domain.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

  private final EmailService emailService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("mailExecutor")
  public void handlePasswordResetEvent(PasswordResetEvent event){
    log.info("비밀번호 재설정 이메일 발송 요청 수신. To: {}", event.email());
    emailService.sendEmail(
        event.email(),
        "[모두의 플리] 비밀번호 재설정 링크",
        "아래 링크를 클릭하여 비밀번호를 재설정하세요: " + event.resetLink());
  }
}
