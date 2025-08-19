package com.codeit.sb02mplteam2.domain.livewatch.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RoomJoinResponse(
    Long roomId,
    String title,
    LocalDateTime createdAt,
    Integer participantCount,
    List<ParticipantResponseDto> participants
) {

}