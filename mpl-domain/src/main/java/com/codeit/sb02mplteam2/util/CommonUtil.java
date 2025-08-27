package com.codeit.sb02mplteam2.util;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtil {
  public static <T> T retrieveCache(Cache cache, Long id, Class<T> tClass) {
    return cache.get(id, tClass);
  }

  public static <T> Map<Long, T> retrieveAllFromCache(Cache cache, Set<Long> ids, Class<T> tClass) {
    return ids.parallelStream().map(id -> {
          T value = cache.get(id, tClass);
          return new SimpleEntry<>(id, value);
        }).filter(entry -> Objects.nonNull(entry.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static boolean isTargetRequired(NotificationType type) {
    return switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST, NEW_MESSAGE ->
          true;
      default -> false;
    };
  }

}
