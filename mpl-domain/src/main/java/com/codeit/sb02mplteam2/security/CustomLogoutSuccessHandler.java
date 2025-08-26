package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.security.jwt.JwtLogoutHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

  private final JwtLogoutHandler jwtLogoutHandler;
  private final ClientRegistrationRepository clientRegistrationRepository;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${mpl.oauth.common-logout-redirect-uri}")
  private String commonLogoutRedirectUri;

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    // 서비스 jwt 토큰 무효화 (로컬 유저는 logout method 로 로그아웃 끝)
    jwtLogoutHandler.logout(request, response, authentication);

    if(authentication instanceof OAuth2AuthenticationToken token) {
      // Principal을 MplUserDetails로 가져옴
      if(token.getPrincipal() instanceof MplUserDetails userDetails) {
        log.info("소셜 로그인 사용자({})의 로그아웃을 시작합니다.", userDetails.getUsername());
      }

      // 어떤 소셜 제공자인지 확인 ("kakao", "google")
      String registrationId = token.getAuthorizedClientRegistrationId();

      switch(registrationId.toLowerCase()) {
        case "kakao":
          handleKakaoLogout(response);
          return;
        case "google":
          handleGoogleLogout(response, authentication);
          return;
        default:
          log.warn("지원하지 않는 OAuth2 제공자({})의 로그아웃 요청입니다.", registrationId);
      }
    }

    log.info("로컬 사용자의 로그아웃이 완료되었습니다.");
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private void handleGoogleLogout(HttpServletResponse response, Authentication authentication)
      throws IOException {
    OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);

    if(authorizedClient != null) {
//      인증 정보에서 Access Token 추출
      String accessToken = authorizedClient.getAccessToken().getTokenValue();

//      토큰 해지 API 호출
      revokeGoogleToken(accessToken, authentication.getName());

//      Session에 저장된 Access Token 삭제
      authorizedClientService.removeAuthorizedClient(
          authorizedClient.getClientRegistration().getRegistrationId(),
          authentication.getName()
      );
    } else {
      log.warn("Google authorized client 를 찾을 수 없어 토큰 해지를 스킵합니다. 사용자: {}", authentication.getName());
    }

    log.info("Google 사용자를 로그아웃하고 다음 주소로 리디렉션합니다: {}", commonLogoutRedirectUri);
    response.sendRedirect(commonLogoutRedirectUri);
  }

  private OAuth2AuthorizedClient getAuthorizedClient(Authentication authentication) {
    if(authentication instanceof OAuth2AuthenticationToken token) {
      return authorizedClientService.loadAuthorizedClient(
          token.getAuthorizedClientRegistrationId(),
          authentication.getName()
      );
    }
    return null;
  }

  /**
   * Google의 토큰 해지 API를 호출
  */
  private void revokeGoogleToken(String token, String principalName) {
    String revokeUrl = "https://oauth2.googleapis.com/revoke";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("token", token);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, request, String.class);
      if(response.getStatusCode().is2xxSuccessful()) {
        log.info("Successfully revoked Google token for user: {}", principalName);
      } else {
        log.warn("Failed to revoke Google token for user '{}'. Status: {}, Body: {}",
            principalName, response.getStatusCode(), response.getBody());
      }
    } catch (RestClientException e) {
      log.error("Error while revoking Google token for user '{}'", principalName, e);
    }
  }

  private void handleKakaoLogout(HttpServletResponse response) throws IOException {
    ClientRegistration kakaoRegistration = clientRegistrationRepository.findByRegistrationId("kakao");
    if(kakaoRegistration != null) {
      String clientId = kakaoRegistration.getClientId();
      String kakaoLogoutUrl = UriComponentsBuilder
          .fromUriString("https://kauth.kakao.com/oauth/logout")
          .queryParam("client_id", clientId)
          .queryParam("logout_redirect_uri", commonLogoutRedirectUri)
          .toUriString();

      log.info("카카오 로그아웃을 위해 다음 주소로 리디렉션합니다.: {}", kakaoLogoutUrl);
      response.sendRedirect(kakaoLogoutUrl);
    } else {
      log.warn("카카오 ClientRegistration 정보를 찾을 수 없어 기본 로그아웃으로 처리합니다.");
      response.sendRedirect(commonLogoutRedirectUri);
    }
  }
}
