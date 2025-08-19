package com.codeit.sb02mplteam2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

// 스프링 6의 @EnableWebSocketSecurity에서는 CSRF 비활성화가 안되서 deprecated 클래스 사용
@Configuration
@SuppressWarnings("deprecation")
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages
        .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
        .simpTypeMatchers(SimpMessageType.DISCONNECT).permitAll()
        .simpDestMatchers("/app/livewatch/**").authenticated()
        .simpSubscribeDestMatchers("/topic/livewatch/**").authenticated()
        .simpSubscribeDestMatchers("/queue/livewatch/**").hasRole("USER")

//        .simpDestMatchers("/app/dm/**").authenticated()
//        .simpSubscribeDestMatchers("/user/queue/dm/**").authenticated()

        .anyMessage().authenticated();
  }

  @Override
  protected boolean sameOriginDisabled() {
    // JWT 환경에서는 CSRF 보호 비활성화 (Same Origin 체크 비활성화)
    return true;
  }
}