package com.codeit.sb02mplteam2.security.jwt;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.ErrorResponse;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    Optional<String> optionalAccessToken = resolveAccessToken(request);

    if (optionalAccessToken.isPresent()) {
      try {
        String accessToken = optionalAccessToken.get();
        // validate 토큰 유효성 검사
        if(jwtService.validate(accessToken)) {
          UserDto userDto = jwtService.parseTokenToJwtObject(accessToken).userDto();
          UserDetails userDetails = new MplUserDetails(userDto, "");
          setAuthentication(userDetails);
        } else {
          throw new MplException(ErrorCode.INVALID_TOKEN, Map.of("accessToken", accessToken));
        }
      } catch (JwtException e) {
        log.warn("Invalid JWT token: {}", e.getMessage());
        sendErrorResponse(response, e);
        return;
      }
    }

    // JwtAuthenticationFilter 로직 처리 후 다음 필터로 요청 전달
    filterChain.doFilter(request, response);
  }

  private Optional<String> resolveAccessToken(HttpServletRequest request) {
    String prefix = "Bearer ";
    return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
        .map(value -> {
          if(value.startsWith(prefix)){
            return value.substring(prefix.length());
          } else {
            return null;
          }
        });
  }

  private void setAuthentication(UserDetails userDetails) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }

  private void sendErrorResponse(HttpServletResponse response, Exception e) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    ErrorResponse errorResponse = ErrorResponse.fromMplException(new MplException(
        ErrorCode.UNAUTHORIZED));
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
