package com.codeit.sb02mplteam2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // 4. Java 8의 날짜/시간(LocalDateTime 등) 직렬화 모듈 JavaTimeModule 등록
    mapper.registerModule(new JavaTimeModule());

    //날짜/시간 타입을 JSON으로 변환할 때, 타임스탬프(숫자) 형식이 아닌
    //    ISO-8601 표준 문자열 형식("2025-08-23T12:30:00")으로 변환하도록 설정합니다.
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return mapper;
  }

}
