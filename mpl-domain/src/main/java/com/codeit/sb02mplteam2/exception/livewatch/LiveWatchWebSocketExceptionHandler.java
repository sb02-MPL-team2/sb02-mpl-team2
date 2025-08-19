package com.codeit.sb02mplteam2.exception.livewatch;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ErrorNotificationDto;
import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchBroadcastService;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiveWatchWebSocketExceptionHandler {

    private final LiveWatchBroadcastService broadcastService;

    @MessageExceptionHandler(MplException.class)
    @SendToUser("/queue/errors")
    public ErrorNotificationDto handleMplException(MplException e, Principal principal) {
        log.error("[WebSocket] MplException 발생: {}", e.getMessage(), e);
        
        ErrorNotificationDto errorDto = ErrorNotificationDto.fromMplException(e);
        
        if (principal != null) {
            String username = extractUsername(principal);
            broadcastService.sendErrorToUser(username, errorDto);
        }
        
        return errorDto;
    }

    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser("/queue/errors")
    public ErrorNotificationDto handleRuntimeException(RuntimeException e, Principal principal) {
        log.error("[WebSocket] RuntimeException 발생: {}", e.getMessage(), e);
        
        ErrorNotificationDto errorDto = ErrorNotificationDto.fromErrorCode(ErrorCode.INTERNAL_SERVER_ERROR);
        
        if (principal != null) {
            String username = extractUsername(principal);
            broadcastService.sendErrorToUser(username, errorDto);
        }
        
        return errorDto;
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorNotificationDto handleException(Exception e, Principal principal) {
        log.error("[WebSocket] Exception 발생: {}", e.getMessage(), e);
        
        ErrorNotificationDto errorDto = ErrorNotificationDto.fromErrorCode(ErrorCode.INTERNAL_SERVER_ERROR);
        
        if (principal != null) {
            String username = extractUsername(principal);
            broadcastService.sendErrorToUser(username, errorDto);
        }
        
        return errorDto;
    }

    private String extractUsername(Principal principal) {
        try {
            if (principal instanceof UsernamePasswordAuthenticationToken auth) {
                MplUserDetails userDetails = (MplUserDetails) auth.getPrincipal();
                return userDetails.getUsername();
            }
            return principal.getName();
        } catch (Exception e) {
            log.warn("[WebSocket] 사용자명 추출 실패: {}", e.getMessage());
            return "unknown";
        }
    }
}