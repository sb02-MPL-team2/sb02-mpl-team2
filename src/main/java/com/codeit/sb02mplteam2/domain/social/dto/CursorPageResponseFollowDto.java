package com.codeit.sb02mplteam2.domain.social.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseFollowDto(
    List<UserSlimDto> userList,
    LocalDateTime nextCursor,
    boolean hasNext
) {

}
