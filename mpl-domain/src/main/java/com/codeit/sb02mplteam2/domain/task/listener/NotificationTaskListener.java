package com.codeit.sb02mplteam2.domain.task.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationTaskListener {

  @RabbitListener(queues = "system.notification.log.queue")
  public void receiverSystemLog() {
    log.info("알람 전송 로그를 받습니다.");
    //로그를 비동기적으로 작성할 예정임
  }
}
