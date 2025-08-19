package com.codeit.sb02mplteam2.domain.social.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserFollowDto;
import java.util.List;

public record CursorPageResponseFollowDto(
    List<UserFollowDto> userList,
    Long nextCursor,
    boolean hasNext
) {

}
