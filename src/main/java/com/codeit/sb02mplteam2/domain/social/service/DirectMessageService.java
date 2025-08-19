package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;


public interface DirectMessageService {

  DirectMessageResponse create(DirectMessageCreateRequest request);

  CursorPageResponseDirectMessageDto findAll(Long channelId, Long after, Long before, int limit);

  Long findReceiverId(Long channelId, Long senderId);

}
