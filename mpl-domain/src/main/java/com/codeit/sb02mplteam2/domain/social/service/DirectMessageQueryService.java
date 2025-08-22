package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.directmessage.DirectMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageQueryService {

  private final DirectMessageRepository directMessageRepository;

  /**
   * 메서드 필요해서 제가 만들어서 사용합니다. 참고하세요
   */
  @Cacheable(value = "directMessages", key = "#directMessageId")
  public DirectMessageDto findByDirectMessageId(Long directMessageId) {
    DirectMessage directMessage = directMessageRepository.findById(directMessageId).orElseThrow(
        () -> new DirectMessageException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND)
    );
    return new DirectMessageDto(
        directMessage.getId(),
        directMessage.getCreatedAt(),
        directMessage.getDirectMessageChannel().getId(),
        directMessage.getContent());
  }

  @CachePut(value = "directMessages", key = "#directMessageId")
  public DirectMessageDto refreshAndFindByDirectMessageId(Long directMessageId) {
    DirectMessage directMessage = directMessageRepository.findById(directMessageId).orElseThrow(
        () -> new DirectMessageException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND)
    );
    return new DirectMessageDto(
        directMessage.getId(),
        directMessage.getCreatedAt(),
        directMessage.getDirectMessageChannel().getId(),
        directMessage.getContent());
  }
}
