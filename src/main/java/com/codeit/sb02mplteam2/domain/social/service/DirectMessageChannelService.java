package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageChannelDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
import java.time.LocalDateTime;

public interface DirectMessageChannelService {

  DirectMessageChannelResponse create(Long senderId, Long receiverId);

  CursorPageResponseDirectMessageChannelDto findAll(Long userId, LocalDateTime cursor, int size);

}
