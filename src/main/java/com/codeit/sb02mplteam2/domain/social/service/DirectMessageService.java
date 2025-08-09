package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import java.time.LocalDateTime;
import java.util.List;

public interface DirectMessageService {

  DirectMessageResponse create(DirectMessageCreateRequest request);

  CursorPageResponseDirectMessageDto findAll(Long channelId, Long fromId, Long toId, LocalDateTime cursor, int limit);

}
