package com.codeit.sb02mplteam2.security.jwt;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtBlacklist {

//  String 타입의 key와 Value를 처리하는 RedisTemplate 주입
  private final RedisTemplate<String, String> redisTemplate;
  private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

  public void put(String accessToken, Instant expirationTime) {
    if(expirationTime.isBefore(Instant.now())){
      return;
    }

//    Redis에 저장할 키 생성
    String key = BLACKLIST_PREFIX + accessToken;
//    토큰이 만료될 때가지 남은 시간 계산
    Duration ttl = Duration.between(Instant.now(), expirationTime);

//    Redis에 "accessToken" : "balckListed" 와 같은 형태로 저장하고 만료시간을 설정
    redisTemplate.opsForValue().set(key, "blacklisted", ttl);
  }

  public boolean contains(String accessToken) {
    String key = BLACKLIST_PREFIX + accessToken;
//    Redis에 해당 키가 존재하는지 확인
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

}
