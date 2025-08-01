package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MplUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  // Spring Security가 인증을 시도할 때 호출하는 메서드
  // 로그인 폼에서 'username'의 파라미터로 넘어온 값을 인자로 받습니다.
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 전달받은 username으로 데이터베이스에서 사용자를 찾습니다.
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    // UsernameNotFoundException -> Bad Credential

//    TODO Custom Longin page 만들어지면 email로 인증, 지금은 default form login 이라 username 사용
//    User user = userRepository.findByEmail(email)
//        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + email));

    // Spring Security가 이해할 수 있는 userDetails 객체로 변환하여 반환
    // return 한 MplUserDetail의 메서드를 차례로 호출해서 lock 상태인지 등을 검사한다.
    return new MplUserDetail(user);
  }
}
