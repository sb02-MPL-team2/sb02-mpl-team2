package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // private 생성자로 인스턴스화 방지
public final class PlaylistUtil {
  private static final int MAX_LENGTH = 13;
  //Content DTO 변환 기능
  public static List<ContentResponseDto> toResponseDto(List<PlaylistItem> items) {
    return items.stream()
        .map(PlaylistItem::getContent)
        .map(content -> ContentResponseDto.builder()
            .id(content.getId())
            .title(content.getTitle())
            .description(content.getDescription())
            .category(content.getCategory().toString())
            // TODO: 관련된 실제 데이터를 채워 넣어야 함
            .build())
        .collect(Collectors.toList());
  }

  //Summary 기능
  public static String summary(List<PlaylistItem> items) {
    if (items == null || items.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();

    for (PlaylistItem item : items) {
      String title = item.getContent().getTitle();
      if (sb.isEmpty()) {
        sb.append(title);
        continue;
      }
      if (sb.length() + title.length() + 2 > MAX_LENGTH) {
        break;
      }
      sb.append(", ").append(title);
    }

    return sb.toString();
  }
}
