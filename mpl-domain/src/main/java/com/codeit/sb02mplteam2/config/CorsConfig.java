package com.codeit.sb02mplteam2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) 전역 설정
 * 
 * AWS ECS 배포 시 프론트엔드와 백엔드 간의 안전한 통신을 위한 CORS 정책 설정
 * 개발/프로덕션 환경별로 허용된 Origins을 환경변수로 관리
 * 
 * 주요 기능:
 * - WebSocket 연결 지원을 위한 적절한 헤더 설정
 * - SSE(Server-Sent Events) 지원
 * - JWT 토큰 전송을 위한 Credentials 허용
 * - OAuth2 콜백 처리를 위한 설정
 */
@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * 허용할 Origins 목록 (환경변수에서 쉼표로 구분하여 설정)
     * 
     * 개발 환경 예시: http://localhost:5173,http://localhost:8080
     * 프로덕션 예시: https://mpl.yourdomain.com,https://your-alb-dns.amazonaws.com
     */
    @Value("${mpl.cors.allowed-origins:http://localhost:5173,http://localhost:8080}")
    private String[] allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 환경변수 기반 Origins 허용
                .allowedOriginPatterns(allowedOrigins)
                // 모든 HTTP 메서드 허용 (REST API + WebSocket handshake)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 모든 헤더 허용 (JWT Authorization, Content-Type 등)
                .allowedHeaders("*")
                // JWT 토큰 쿠키 전송을 위한 Credentials 허용
                .allowCredentials(true)
                // Preflight 요청 캐시 시간 (1시간)
                .maxAge(3600)
                // WebSocket 및 SSE를 위한 노출 헤더 설정
                .exposedHeaders(
                    "Authorization", 
                    "Content-Type", 
                    "X-Requested-With",
                    "Cache-Control",
                    "Connection",
                    "Upgrade"
                );
    }
}