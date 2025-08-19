package com.codeit.sb02mplteam2.domain.user.repository;

import com.codeit.sb02mplteam2.domain.user.dto.UserSearchDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchFilter;
import java.util.List;

public interface UserRepositoryCustom {

  List<UserSearchDto> searchByFilter(
      Long currentUserId,
      String keyword,
      UserSearchFilter filter,
      Long cursorId,
      int pageSize
  );
}
