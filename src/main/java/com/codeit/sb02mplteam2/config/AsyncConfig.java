package com.codeit.sb02mplteam2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean("binaryContentTaskExecutor")
  public TaskExecutor binaryContentTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("binary-content-");
    executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
    executor.initialize();
    //Security Config 설정 자동 전파
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }

}
