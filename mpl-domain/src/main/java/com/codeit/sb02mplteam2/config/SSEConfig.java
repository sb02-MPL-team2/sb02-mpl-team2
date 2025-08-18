package com.codeit.sb02mplteam2.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
public class SSEConfig {

  @Bean(name = "notificationExecutor")
  public Executor notificationExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("notification-");
    executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
    executor.initialize();
    //Security Config 설정 자동 전파
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }
}
