package com.codeit.sb02mplteam2.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${mpl.storage.local.root-path}")
  private String rootPath;

  @Value("${mpl.storage.local.base-url}")
  private String baseUrl;

  @Bean
  public MDCLoggingInterceptor mdcLoggingInterceptor() {
    return new MDCLoggingInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(mdcLoggingInterceptor())
        .addPathPatterns("/**"); // 모든 경로에 적용
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // baseUrl에서 마지막 슬래시를 제거하고 '/**' 를 붙여 URL 패턴을 만든다. (예: /files/**)
    String resourceUri = baseUrl.endsWith("/") ? baseUrl + "**" : baseUrl + "/**";
    // 물리적 경로를 나타내는 'file:' 접두사를 붙인다. (예: file:./storage/)
    String resourceLocation = "file:" + rootPath +(rootPath.endsWith("/") ? "" : "/");

    log.info("정적 리소스 핸들러 등록: {} -> {}", resourceUri, resourceLocation);

    registry.addResourceHandler(resourceUri)
        .addResourceLocations(resourceLocation);
  }
}
