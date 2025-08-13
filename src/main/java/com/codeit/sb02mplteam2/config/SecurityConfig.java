package com.codeit.sb02mplteam2.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.security.CustomLoginFailureHandler;
import com.codeit.sb02mplteam2.security.JsonUsernamePasswordAuthenticationFilter;
import com.codeit.sb02mplteam2.security.jwt.JwtAccessDeniedHandler;
import com.codeit.sb02mplteam2.security.jwt.JwtAuthenticationEntryPoint;
import com.codeit.sb02mplteam2.security.jwt.JwtAuthenticationFilter;
import com.codeit.sb02mplteam2.security.jwt.JwtLoginSuccessHandler;
import com.codeit.sb02mplteam2.security.jwt.JwtLogoutHandler;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final JwtService jwtService;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  public static final String[] PERMIT_ALL_PATTERNS = {
      // --- Static Resources ---
      "/", "/assets/**", "/favicon.ico", "/index.html", "/placeholder.svg", "/error",
      // --- Swagger / API Docs ---
      "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
      // --- Auth APIs ---
      "/api/auth/**", "/login",
      // --- files/images ---
      "/files/**",
  };

  @Bean
  public RequestMatcher publicPathMatcher() {
    List<RequestMatcher> publicMatchers = Arrays.stream(PERMIT_ALL_PATTERNS)
        .map(PathPatternRequestMatcher.withDefaults()::matcher)
        .collect(Collectors.toList());

    publicMatchers.add(toH2Console());

    return new OrRequestMatcher(publicMatchers);
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      AuthenticationManager authenticationManager,
      JwtLogoutHandler jwtLogoutHandler
  ) throws Exception {

    final String LOGIN_URL = "/api/auth/login";
    final String LOGOUT_URL = "/api/auth/logout";

    http
        // CSRF 보호 비활성화 (stateless한 API 서버의 경우 보통 비활성화 한다.)
        .csrf(AbstractHttpConfigurer::disable)

        // formLogin 비활성화 (JSON으로 로그인 요청을 처리할 것이므로)
        .formLogin(AbstractHttpConfigurer::disable)

        // httpBasic 인증 비활성화
        .httpBasic(AbstractHttpConfigurer::disable)

        // 익명 사용자 인증 비활성화
        .anonymous(AbstractHttpConfigurer::disable)

        // 세션 관리 정책을 STATELESS(무상태)로 설정
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // H2 콘솔을 위한 설정
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin)
        )

        // URL 별 접근 권한 설정
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(toH2Console()).permitAll() // H2 콘솔은 누구나 접근 가능
            .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
            .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
            .requestMatchers("/api/**").authenticated() // /api/** 요청은 모두 인증 필요
            .anyRequest().permitAll() // 나머지 요청은 모두 인증 없이 접근 가능
        )

        .logout(logout -> logout
            .logoutUrl(LOGOUT_URL)
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler((request, response, authentication) -> response.setStatus(
                HttpServletResponse.SC_OK)) // 로그아웃 성공 시 200 OK 응답
        )

        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // Custom filter 들을 filterChain 에 등록
        // 로그인 요청을 처리파는 필터를 UsernamePasswordAuthenticationFilter 위치에 대체
    http.addFilterBefore(
        new JwtAuthenticationFilter(jwtService, objectMapper, publicPathMatcher()),
        UsernamePasswordAuthenticationFilter.class
    );

    http.addFilterAt(
            jsonUsernamePasswordAuthenticationFilter(authenticationManager, LOGIN_URL),
            UsernamePasswordAuthenticationFilter.class
        );


    return http.build();
  }

  public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter(
      AuthenticationManager authenticationManager,
      String loginUrl) {
    JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
    filter.setAuthenticationManager(authenticationManager);
    filter.setAuthenticationSuccessHandler(new JwtLoginSuccessHandler(objectMapper, jwtService));
    filter.setAuthenticationFailureHandler(new CustomLoginFailureHandler(objectMapper));
    filter.setRequiresAuthenticationRequestMatcher(PathPatternRequestMatcher.withDefaults()
        .matcher(HttpMethod.POST, loginUrl));
    return filter;
  }

  @Bean
  public JwtLogoutHandler jwtLogoutHandler() {
    return new JwtLogoutHandler(jwtService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder,
      RoleHierarchy roleHierarchy
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    provider.setAuthoritiesMapper(new RoleHierarchyAuthoritiesMapper(roleHierarchy));
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      DaoAuthenticationProvider daoAuthenticationProvider) {
    return new ProviderManager(daoAuthenticationProvider);
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role(Role.ADMIN.name())
        .implies(Role.USER.name(), Role.MANAGER.name())

        .role(Role.MANAGER.name())
        .implies(Role.USER.name())

        .build();
  }

  @Profile("!prod")
  @Bean
  public String debugFilterChain(SecurityFilterChain filterChain) {
    log.debug("Debug Filter Chain...");
    int filterSize = filterChain.getFilters().size();
    IntStream.range(0, filterSize)
        .forEach(idx -> {
          log.debug("[{}/{}] {}", idx + 1, filterSize, filterChain.getFilters().get(idx));
        });
    return "debugFilterChain";
  }
}
