package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;

public interface DirectMessageService {

  DirectMessageResponse create(DirectMessageCreateRequest request);

}
