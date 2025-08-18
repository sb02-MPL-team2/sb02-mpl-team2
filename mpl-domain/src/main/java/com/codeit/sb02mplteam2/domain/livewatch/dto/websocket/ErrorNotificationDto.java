package com.codeit.sb02mplteam2.domain.livewatch.dto.websocket;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorNotificationDto(
    String errorCode,           // "ROOM_NOT_FOUND", "PERMISSION_DENIED" 등
    LocalDateTime timestamp,    // 에러 발생 시간
    String message,            // 사용자 친화적 메시지
    Map<String, Object> details // 추가 정보 (선택적)
) {
    
    public static ErrorNotificationDto of(String errorCode, String message) {
        return new ErrorNotificationDto(errorCode, LocalDateTime.now(), message, null);
    }
    
    public static ErrorNotificationDto of(String errorCode, String message, Map<String, Object> details) {
        return new ErrorNotificationDto(errorCode, LocalDateTime.now(), message, details);
    }
}