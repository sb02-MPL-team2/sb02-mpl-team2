package com.codeit.sb02mplteam2.security.service;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.security.Provider;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final UserMapper userMapper;


  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // 기본 OAuth2UserService로 사용자 정보 로드
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    /**
     * 1. userRequest 객체 안에 들어있는 AccessToken을 꺼낸다.
     * 2. AccessToken 과 함께 구글의 UserInfo Endpoint로 실제 HTTP 요청을 보낸다.
     * DefaultOAuth2UserService 는 JSON 응답을 파싱해 OAuth2User 객체로 만들어 반환
    */
    OAuth2User oauth2User = delegate.loadUser(userRequest);

    // registrationId = google / kakao
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    Provider provider = Provider.fromRegistrationId(registrationId);

    User user = processOAuth2User(oauth2User, provider);

    UserDto userDto = userMapper.toDto(user);
    return MplUserDetails.forSocialUser(userDto, oauth2User.getAttributes());
  }

  /**
   * OAuth 2.0 사용자 정보를 처리하고 데이터베이스에 저장한다.
  */
  private User processOAuth2User(OAuth2User oauth2User, Provider provider) {
    // 사용자 정보 추출
    UserInfo userInfo = extractUserInfo(oauth2User, provider);
    log.info("OAuth 2.0 사용자 정보: email={}, name={}, provider={}, pictureUrl={}", userInfo.email(), userInfo.name(),
        provider, userInfo.pictureUrl);

    // 이메일로 사용자를 먼저 찾아본다.
    return userRepository.findByProviderIdAndProvider(userInfo.providerId, provider)
        .map(existingUser -> {
          log.info("기존 사용자 발견: {}", userInfo.email());
          // 기존 계정이 로컬 계정이라면 소셜 정보 연동
          if (existingUser.getProvider() == Provider.LOCAL) {
            log.info("로컬 계정을 소셜 계정과 연동합니다.");
            existingUser.linkSocialAccount(provider, userInfo.providerId());
          }

          return updateUserInfoIfNecessary(existingUser, userInfo);
        })
        // DB에 이메일이 없으면 userInfo 를 이용해 새로운 사용자를 생성
        .orElseGet(() -> {
          log.info("새로운 사용자를 생성합니다.: {}", userInfo.email);
          return createNewUser(userInfo, provider);
        });
  }

  private UserInfo extractUserInfo(OAuth2User oauth2User, Provider provider) {
    Map<String, Object> attributes = oauth2User.getAttributes();

    return switch (provider) {
      case GOOGLE -> extractGoogleUserInfo(attributes);
      case KAKAO -> extractKakaoUserInfo(attributes);
      default -> throw new MplException(ErrorCode.UNKNOWN_PROVIDER);
    };
  }

  private UserInfo extractGoogleUserInfo(Map<String, Object> attributes) {
    String providerId = (String) attributes.get("sub");
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String pictureUrl = (String) attributes.get("picture");

    if(providerId == null || email == null || name == null) {
      throw new MplException(ErrorCode.MISSING_REQUIRED_OAUTH_INFO);
    }

    return new UserInfo(providerId, email, name, pictureUrl);
  }

  private UserInfo extractKakaoUserInfo(Map<String, Object> attributes) {
    /**
     * 카카오 응답은 중첩된 JSON 구조
     * id는 최상위, 그 외 정보는 kakao_account와 properties 객체 안에 있음
    */
    String providerId = String.valueOf(attributes.get("id"));
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

    if(kakaoAccount == null || properties == null) {
      log.error("Kakao OAuth2.0 응답에 kakao_account 또는 properties 필드가 없습니다. attributes: {}", attributes);
      throw new MplException(ErrorCode.MISSING_REQUIRED_OAUTH_INFO);
    }

//    이메일이 없는 경우 임의의 이메일 주소 설정
    String email = (String) kakaoAccount.get("email");
    if(email == null || email.isBlank()) {
      log.warn("카카오 사용자로부터 이메일을 받지 못했습니다ㅣ. providerId를 기반으로 가상 이메일을 생성합니다."
          + "providerId: {}", providerId);
      email = "kakao_" + providerId + "@kakao.social";
    }

//    닉네임이 없는 경우 예외 대신 기본값을 설정
    String name = (String) properties.get("nickname");
    if(!StringUtils.hasText(name)) {
      log.warn("카카오 사용자로부터 닉네임을 받지 못했습니다. providerId를 기반으로 가상 닉네임을 생성합니다."
          + "providerId: {}", providerId);
      name = "Kakao_User_" + providerId;
    }

    String pictureUrl = (String) properties.get("profile_image");

//    필수 정보 검증을 providerId에 대해서만 수행
    if(!StringUtils.hasText(providerId)) {
      log.error("Kakao OAuth2.0 응답에서 필수 정보(providerId, email, name)가 누락되었습니다. attributes: {}", attributes);
      throw new MplException(ErrorCode.MISSING_REQUIRED_OAUTH_INFO);
    }

    return new UserInfo(providerId, email, name, pictureUrl);
  }

  private User createNewUser(UserInfo userInfo, Provider provider) {
    User newUser = new User(
        userInfo.name,
        userInfo.email,
        UUID.randomUUID().toString(),
        userInfo.pictureUrl,
        userInfo.providerId,
        provider
    );

    User savedUser = userRepository.save(newUser);
    log.info("새로운 사용자 생성: email={}, provider={}", userInfo.email(), provider);

    return savedUser;
  }

  private record UserInfo(
     String providerId,
     String email,
     String name,
     String pictureUrl
  ) {}

  private User updateUserInfoIfNecessary(User existingUser, UserInfo userInfo) {
    if(existingUser.getPictureUrl() == null || existingUser.getPictureUrl().isBlank()) {
      log.info("기존 사용자의 프로필 사진이 없어 소셜 프로필 사진으로 업데이트 합니다.");
      existingUser.updatePictureUrl(userInfo.pictureUrl);
    }
    return existingUser;
  }
}
