package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.content.dto.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // private 생성자로 인스턴스화 방지
public final class PlaylistUtil {

  public static List<ContentResponseDto> toResponseDto(List<PlaylistItem> items) {
    return items.stream()
        .map(PlaylistItem::getContent)
        .map(content -> ContentResponseDto.builder()
            .id(content.getId())
            .title(content.getTitle())
            .description(content.getDescription())
            .category(content.getCategory())
            // TODO: 관련된 실제 데이터를 채워 넣어야 함
            .build())
        .collect(Collectors.toList());
  }
}
