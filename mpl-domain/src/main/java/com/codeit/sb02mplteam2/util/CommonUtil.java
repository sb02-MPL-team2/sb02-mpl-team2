package com.codeit.sb02mplteam2.util;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtil {
  public static <T> T retrieveCache(Cache cache, Long id, Class<T> tClass) {
    Object nativeCache = cache.getNativeCache();

    if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
      @SuppressWarnings("unchecked")
      com.github.benmanes.caffeine.cache.Cache<Long, T> caffeineCache
          = (com.github.benmanes.caffeine.cache.Cache<Long, T>) nativeCache;
      return caffeineCache.getIfPresent(id);
    } else if (cache instanceof RedisCache redisCache) {
      return redisCache.get(id, tClass);
    }
    return null;
  }

  public static <T> Map<Long, T> retrieveAllFromCache(Cache cache, Set<Long> ids, Class<T> tClass) {
    Object nativeCache = cache.getNativeCache();

    if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
      @SuppressWarnings("unchecked")
      com.github.benmanes.caffeine.cache.Cache<Long, T> caffeineCache
          = (com.github.benmanes.caffeine.cache.Cache<Long, T>) nativeCache;

      return caffeineCache.getAllPresent(ids);
    }
    return null;
  }

  public static boolean isTargetRequired(NotificationType type) {
    return switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST, NEW_MESSAGE ->
          true;
      default -> false;
    };
  }

}
