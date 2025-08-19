package com.codeit.sb02mplteam2.domain.livewatch.dto.websocket;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.time.LocalDateTime;
import java.util.Map;

public record ErrorNotificationDto(
    String errorCode,
    LocalDateTime timestamp,
    String message,
    Map<String, Object> details
) {
    public static ErrorNotificationDto fromMplException(MplException e) {
        return new ErrorNotificationDto(
            e.getErrorCode().name(),
            e.getTimestamp(),
            e.getErrorCode().getMessage(),
            e.getDetails()
        );
    }
    public static ErrorNotificationDto fromErrorCode(ErrorCode errorCode) {
        return new ErrorNotificationDto(
            errorCode.name(),
            LocalDateTime.now(),
            errorCode.getMessage(),
            Map.of()
        );
    }
    
    public static ErrorNotificationDto of(String errorCode, String message) {
        return new ErrorNotificationDto(errorCode, LocalDateTime.now(), message, Map.of());
    }
    
    public static ErrorNotificationDto of(String errorCode, String message, Map<String, Object> details) {
        return new ErrorNotificationDto(errorCode, LocalDateTime.now(), message, details);
    }
}