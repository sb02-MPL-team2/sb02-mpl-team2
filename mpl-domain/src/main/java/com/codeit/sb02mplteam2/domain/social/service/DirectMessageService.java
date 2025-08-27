package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;


public interface DirectMessageService {

  DirectMessageResponse create(DirectMessageCreateRequest request, Optional<MultipartFile> file);

  CursorPageResponseDirectMessageDto findAll(Long channelId, Long after, Long before, int limit);

  Long findReceiverId(Long channelId, Long senderId);

}
