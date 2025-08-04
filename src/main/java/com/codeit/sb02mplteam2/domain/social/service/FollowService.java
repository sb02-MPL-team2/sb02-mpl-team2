package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseFollowDto;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;
import java.time.LocalDateTime;

public interface FollowService {

  FollowResponse create(Long followeeId, Long followerId);

  FollowResponse delete(Long followeeId, Long followerId);

  FollowStatusResponse isFollowing(Long followeeId, Long followerId);

  CursorPageResponseFollowDto getFollowers(Long userId, LocalDateTime cursor, int size);

  CursorPageResponseFollowDto getFollowings(Long userId, LocalDateTime cursor, int size);

}
