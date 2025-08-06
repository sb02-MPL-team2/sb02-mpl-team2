package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageChannelDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;

public interface DirectMessageChannelService {

  DirectMessageChannelResponse create(Long senderId, Long receiverId);

  DirectMessageChannelResponse findByChannelId(Long channelId, Long userId);

  CursorPageResponseDirectMessageChannelDto findAll(Long userId, Long cursor, int size);

}
