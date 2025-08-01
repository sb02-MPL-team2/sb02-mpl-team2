package com.codeit.sb02mplteam2.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 보호 비활성화 (stateless한 API 서버의 경우 보통 비활성화 한다.)
        .csrf(AbstractHttpConfigurer::disable)

        // H2 콘솔을 위한 설정
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin)
        )

        // URL 별 접근 권한 설정
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(toH2Console()).permitAll() // H2 콘솔은 누구나 접근 가능
            .requestMatchers("/api/auth/**", "/login").permitAll() // 로그인, 회원가입 관련 API 누구나 접근 가능
            .anyRequest().authenticated() // 나머지 모든 요청은 인증된 사용자만 접근 가능
        )

//        로그인 페이지를 못찾음 /login 페이지를 못찾음
//        .formLogin(form -> form
//            .loginPage("/login")
//            .loginProcessingUrl("/api/auth/login")
//            .usernameParameter("email")
//            .passwordParameter("password")
//            .defaultSuccessUrl("/", true)
//            .failureUrl("/login?error=true")
//        );
        .formLogin(Customizer.withDefaults())
        .sessionManagement(session -> session
            .maximumSessions(1) // 한 사용자 당 최대 1개의 세션만 허용 (동시 로그인 방지)
            .sessionRegistry(sessionRegistry()) // 세션 정보를 관리할 레지스트리 등록
        );

    return http.build();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
