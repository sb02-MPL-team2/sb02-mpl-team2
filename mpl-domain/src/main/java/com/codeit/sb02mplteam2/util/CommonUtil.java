package com.codeit.sb02mplteam2.util;

import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtil {
  public static <T> T retrieveCache(Cache cache, Long id, Class<T> tClass) {
    Object nativeCache = cache.getNativeCache();

    if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
      @SuppressWarnings("unchecked")
      com.github.benmanes.caffeine.cache.Cache<Long, T> caffeineCache
          = (com.github.benmanes.caffeine.cache.Cache<Long, T>) nativeCache;
      return caffeineCache.getIfPresent(id);
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

}
