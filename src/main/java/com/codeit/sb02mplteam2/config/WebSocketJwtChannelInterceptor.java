package com.codeit.sb02mplteam2.config;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.security.jwt.JwtObject;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
      return message;
    }

    String token = extractBearerToken(accessor);
    if (token == null) {
      return message;
    }

    try {
      if (!jwtService.validate(token)) {
        return message;
      }

      JwtObject jwtObject = jwtService.parseTokenToJwtObject(token);
      UserDto userDto = jwtObject.userDto();

      MplUserDetails userDetails = new MplUserDetails(userDto, null);
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());

      accessor.setUser(authToken);
      accessor.getSessionAttributes().put("userId", userDto.id());

      log.info("[WebSocket] 인증 완료 - sessionId: {}, userId: {}", accessor.getSessionId(),
          userDto.id());

    } catch (Exception e) {
      log.warn("[WebSocket] 인증 실패 - sessionId: {}, error: {}", accessor.getSessionId(),
          e.getMessage());
    }

    return message;
  }

  private String extractBearerToken(StompHeaderAccessor accessor) {
    String authHeader = accessor.getFirstNativeHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }

    return authHeader.substring(7);
  }
}