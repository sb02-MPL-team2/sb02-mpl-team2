package com.codeit.sb02mplteam2.security.jwt;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.access-token-expiration}")
  private long accessTokenValiditySeconds;
  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenValiditySeconds;

  private final JwtSessionRepository jwtSessionRepository;
  private final JwtBlacklist jwtBlacklist;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ObjectMapper objectMapper;

  private JWSVerifier verifier;

  public JwtSession registerJwtSession(UserDto userDto) {
    JwtObject accessJwtObject = generateJwtObject(userDto, accessTokenValiditySeconds);
    JwtObject refreshJwtObject = generateJwtObject(userDto, refreshTokenValiditySeconds);

    JwtSession jwtSession = new JwtSession(
        userDto.id(),
        accessJwtObject.token(),
        refreshJwtObject.token(),
        refreshJwtObject.expirationTime()
    );
    jwtSessionRepository.save(jwtSession);

    return jwtSession;
  }

  public JwtObject parseTokenToJwtObject(String token) {
    try {
       // JWSOjbect = haeder, payload
      JWSObject jwsObject = JWSObject.parse(token);
      Payload payload = jwsObject.getPayload();
      Map<String, Object> jsonObject = payload.toJSONObject();

      /*
      *  payload JSON 예시
      * {
          "sub": "testuser", // username or email etc...
          "iat": 1698402600, // issueTime
          "exp": 1698406200, // expirationTime
          "userDto": {
            "id": "a1b2c3d4-...",
            "username": "testuser",
            "email": "test@example.com",
            ...
          }
         }
      * */

      return new JwtObject(
          objectMapper.convertValue(jsonObject.get("iat"), Instant.class),
          objectMapper.convertValue(jsonObject.get("exp"), Instant.class),
          objectMapper.convertValue(jsonObject.get("userDto"), UserDto.class),
          token
      );
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new MplException(ErrorCode.INVALID_TOKEN, Map.of("token", token), e);
    }
  }

  /*
  *  서버 SecretKey 로 만든 Token 인지
  *  JwtSessionRepository 에 존재하는 유효한 Token 인지 validate
  * */
  public boolean validate(String token) {
    boolean verified;

    try {
      JWSVerifier verifier = new MACVerifier(secret); // 검증기 - 서버 비밀 키 주입
      JWSObject jwsObject = JWSObject.parse(token);      // token을 JWSObject로 parse (header, payload, signature 로 분해)
      verified = jwsObject.verify(verifier);             // jwsObject가 서버 비밀 키로 만들었는지 확인

      if(verified) { // 서버 비밀 키로 만든 token이 맞다면
        JwtObject jwtObject = parseTokenToJwtObject(token);
        verified = !jwtObject.isExpired(); // 만료되었다면 true 가 나오고 ! 를 만나서 false로 바뀜, 다음 로직 X
      }

      if(verified) {
        verified = (jwtSessionRepository.existsByAccessToken(token) ||
            jwtSessionRepository.existsByRefreshToken(token));
      }

    } catch (JOSEException | ParseException e) {
      log.error(e.getMessage());
      verified = false;
    }

    return verified;
  }

  // JWT 생성
  // TODO: 재사용되는 MACSigner, MACVerifier 리팩토링
  private JwtObject generateJwtObject(UserDto userDto, long validitySeconds) {
    Instant IssueTime = Instant.now();
    Instant expirationTime = IssueTime.plus(Duration.ofSeconds(validitySeconds));

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder() // payload 생성
        .subject(userDto.email())
        .claim("userDto", userDto)
        .issueTime(new Date(IssueTime.toEpochMilli()))
        .expirationTime(new Date(expirationTime.toEpochMilli()))
        .build();

    JWSHeader header = new JWSHeader(JWSAlgorithm.HS256); // HMAC SHA-256 알고리즘 사용 명시
    SignedJWT signedJWT = new SignedJWT(header, claimsSet); // haeder + payload 서명 준비 완료

    try {
      signedJWT.sign(new MACSigner(secret)); // 서명 준비 완료된 객체 + secretKey로 토큰 서명
    } catch (JOSEException e) {
      log.error(e.getMessage());
      throw new MplException(ErrorCode.INVALID_TOKEN_SECRET, e);
    }

    String token = signedJWT.serialize();

    return new JwtObject(IssueTime, expirationTime, userDto, token);
  }

  public JwtSession refreshJwtSession(String refreshToken) {
    if(!validate(refreshToken)) {
      throw new MplException(ErrorCode.INVALID_TOKEN, Map.of("refreshToken", refreshToken));
    }
    JwtSession jwtSession = jwtSessionRepository.findByRefreshToken(refreshToken)
        .orElseThrow(() -> new MplException(ErrorCode.NOT_FOUND_TOKEN,
            Map.of("refreshToken", refreshToken)));

    Long userId = parseTokenToJwtObject(refreshToken).userDto().id();
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    JwtObject accessJwtObject = generateJwtObject(userDto, accessTokenValiditySeconds);
    JwtObject refreshJwtObject = generateJwtObject(userDto, refreshTokenValiditySeconds);

    jwtSession.update(
        accessJwtObject.token(),
        refreshJwtObject.token(),
        refreshJwtObject.expirationTime()
    );

    return jwtSession;
  }

  @Transactional
  public void invalidateJwtSession(String refreshToken) {
    jwtSessionRepository.findByRefreshToken(refreshToken)
        .ifPresent(this::invalidate);
  }

  @Transactional
  public void invalidateJwtSession(Long userId) {
    jwtSessionRepository.findByUserId(userId)
        .ifPresent(this::invalidate);
  }

  private void invalidate(JwtSession session) {
    jwtSessionRepository.delete(session);
    if(!session.isExpired()) {
      jwtBlacklist.put(session.getAccessToken(), session.getExpirationTime());
    }
  }
}
