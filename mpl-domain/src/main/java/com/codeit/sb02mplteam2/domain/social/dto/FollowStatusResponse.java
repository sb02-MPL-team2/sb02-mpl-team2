package com.codeit.sb02mplteam2.domain.social.dto;

public record FollowStatusResponse(
    boolean isFollowing,
    Long followeeId,
    Long followerId
) {

}
