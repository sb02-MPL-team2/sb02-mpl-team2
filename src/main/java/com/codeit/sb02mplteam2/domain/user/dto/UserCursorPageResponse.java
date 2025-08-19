package com.codeit.sb02mplteam2.domain.user.dto;

import java.util.List;

public record UserCursorPageResponse<U>(
    List<U> items,
    Long nextCursor,
    boolean hasNext
) {

}
