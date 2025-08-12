package com.codeit.sb02mplteam2.domain.user.service;

import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.binaryContent.repository.BinaryContentRepository;
import com.codeit.sb02mplteam2.domain.binaryContent.service.BinaryContentService;
import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import com.codeit.sb02mplteam2.security.jwt.JwtBlacklist;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService{

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final BinaryContentService binaryContentService;
  private final AlarmSettingRepository alarmSettingRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final JwtService jwtService;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest request,
      Optional<MultipartFile> optionalMultipartFile) {
    log.debug("사용자 생성 시작: email={}", request.email());

    String username = request.username();
    String email = request.email();

    if(userRepository.existsByUsername(username)) {
      throw new UserException(ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", username));
    }

    if(userRepository.existsByEmail(email)) {
      throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", email));
    }

    BinaryContent profile = optionalMultipartFile
        .filter(file -> !file.isEmpty())
        .map(file ->{
          try {
            return binaryContentService.upload(file);
          } catch (IOException e){
            log.error("File upload failed during user creation for file name: {}", file.getOriginalFilename());
            throw new MplException(ErrorCode.FILE_UPLOAD_FAILED, e);
          }
        })
        .orElse(null);

    String encodedPassword = passwordEncoder.encode(request.password());

    User user = new User(
        request.username(),
        request.email(),
        encodedPassword,
        profile
    );

    User savedUser = userRepository.save(user);
    alarmSettingRepository.save(new AlarmSetting(savedUser));
    return userMapper.toDto(savedUser);
  }

  @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
  @Transactional
  @Override
  public UserDto update(Long userId, UserUpdateRequest request,
      Optional<MultipartFile> optionalMultipartFile) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    String newUsername = request.newUsername();
    String newEmail = request.newEmail();

    if(newUsername != null && !newUsername.equals(user.getUsername())) {
      if (userRepository.existsByUsername(newUsername)) {
        throw new UserException(ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", newUsername));
      }
    }

    if(newEmail != null && !newEmail.equals(user.getEmail())) {
      if (userRepository.existsByEmail(newEmail)) {
        throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", newEmail));
      }
    }

    BinaryContent newProfile = optionalMultipartFile
        .filter(file -> !file.isEmpty())
        .map(file ->{
          try {
            return binaryContentService.upload(file);
          } catch (IOException e){
            log.error("File upload failed during user profile update for file name: {}", file.getOriginalFilename());
            throw new MplException(ErrorCode.FILE_UPLOAD_FAILED, e);
          }
        })
        .orElse(null);

    String newPassword = request.newPassword() != null
        ? passwordEncoder.encode(request.newPassword())
        : null;

    user.update(newUsername, newEmail, newPassword, newProfile);
    log.info("사용자 수정 완료: {}", userId);

    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto findById(Long userId){
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    if(user.isDeleted() == true || user.isLocked()){
      log.warn("삭제되었거나 잠긴 사용자에 대한 조회 시도: userId={}", userId);
      throw UserNotFoundException.withId(userId);
    }

    log.info("유저 조회 완료: {}", user);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll(){
    List<User> users = userRepository.findAll();
    log.info("유저 목록 조회 완료: {}", users);
    return users.stream()
        .map(userMapper::toDto)
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
  @Transactional
  @Override
  public void softDelete(Long userId){
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.warn("사용자 soft 삭제 시도: {}", userId);
    jwtService.invalidateJwtSession(userId);

    user.softDelete();
  }
}
