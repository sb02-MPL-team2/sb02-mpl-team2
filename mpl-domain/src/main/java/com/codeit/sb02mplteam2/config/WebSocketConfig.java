package com.codeit.sb02mplteam2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 및 STOMP 메시징 설정
 * 
 * 실시간 채팅, 라이브 시청 등의 양방향 통신을 위한 WebSocket 설정
 * AWS ECS 배포 시 ALB(Application Load Balancer)를 통한 WebSocket 연결 지원
 * 
 * 주요 기능:
 * - JWT 기반 WebSocket 인증
 * - 환경변수 기반 CORS 설정 (프로덕션 보안 강화)
 * - STOMP 프로토콜을 통한 구조화된 메시징
 * - SockJS fallback 지원 (브라우저 호환성)
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketJwtChannelInterceptor webSocketJwtChannelInterceptor;
    
    /**
     * WebSocket CORS 허용 Origins (환경변수에서 쉼표로 구분하여 설정)
     * CorsConfig와 동일한 설정 사용하여 일관성 유지
     */
    @Value("${mpl.cors.allowed-origins:http://localhost:5173,http://localhost:8080}")
    private String[] allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 활성화 - 클라이언트가 구독할 수 있는 destination prefix
        config.enableSimpleBroker("/topic", "/queue");
        
        // 클라이언트가 메시지를 보낼 때 사용할 destination prefix
        config.setApplicationDestinationPrefixes("/app");
        // 개인 메시지용 destination prefix
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // 환경변수 기반 CORS 설정 (프로덕션 보안 강화)
                .setAllowedOriginPatterns(allowedOrigins)
                // SockJS fallback 지원 (WebSocket을 지원하지 않는 브라우저 대응)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // JWT 토큰 기반 WebSocket 인증 인터셉터 등록
        registration.interceptors(webSocketJwtChannelInterceptor);
    }
}