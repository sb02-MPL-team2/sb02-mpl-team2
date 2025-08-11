package com.codeit.sb02mplteam2.domain.social.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import java.util.List;

public record CursorPageResponseFollowDto(
    List<UserSlimDto> userList,
    Long nextCursor,
    boolean hasNext
) {

}
