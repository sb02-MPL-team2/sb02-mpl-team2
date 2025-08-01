package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;

public interface FollowService {

  FollowResponse create(Long followeeId, Long followerId);

  FollowResponse delete(Long followeeId, Long followerId);

  FollowStatusResponse isFollowing(Long followeeId, Long followerId);

}
