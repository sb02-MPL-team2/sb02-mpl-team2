package com.codeit.sb02mplteam2.domain.binaryContent.service;

import com.codeit.sb02mplteam2.domain.binaryContent.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.domain.binaryContent.dto.DownloadFileDto;
import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.binaryContent.repository.BinaryContentRepository;
import com.codeit.sb02mplteam2.domain.binaryContent.repository.BinaryContentStorage;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Value("${mpl.storage.local.base-url}")
  private String baseUrl;

  @Value("${mpl.storage.type}")
  private String storageType;

  @Value("${mpl.aws.s3.bucket}")
  private String s3BucketName;

  @Value("${cloud.aws.region.static}")
  private String awsRegion;

  @Override
  @Transactional
  public BinaryContent upload(MultipartFile file) {
    if(file == null || file.isEmpty()) {
      throw new MplException(ErrorCode.FILE_IS_EMPTY);
    }

    final byte[] fileBytes;
    try {
      fileBytes = file.getBytes();
    } catch (IOException e) {
      log.error("Failed to read bytes from multipart file.", e);
      throw new MplException(ErrorCode.FILE_UPLOAD_FAILED, e);
    }

    // DB에 메타데이터를 먼저 저장해서 ID를 확보
    BinaryContent binaryContent = BinaryContent.from(file);
    BinaryContent savedContent = binaryContentRepository.save(binaryContent);

    String key = generateKey(savedContent.getId(), BinaryContentDto.from(savedContent));

    // 현재 트랜잭션이 성공적으로 커밋된 이후에 파일 업로드를 실행하도록 after Commit 예약
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        binaryContentStorage.upload(key, fileBytes)
            .thenAccept(uploadedKey -> {
              // 비동기 작업 성공 시, URL과 상태를 업데이트 이 작업은 별도의 트랜잭션에서 실행
              binaryContentRepository.findById(savedContent.getId()).ifPresent(content -> {

                String finalUrl;
                if("s3".equalsIgnoreCase(storageType)) {
                  finalUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", s3BucketName, awsRegion, uploadedKey);
                } else {
                  finalUrl = Paths.get(baseUrl, uploadedKey).toString().replace("\\", "/");
                }

                content.completeUpload(finalUrl);
                binaryContentRepository.save(content);
                log.info("File upload competed and URL updated Id: {}", content.getId());
              });
            })
            .exceptionally(ex -> {
              // 비동기 작업 실패 시, 상태를 FAILED로 업데이트
              binaryContentRepository.findById(savedContent.getId()).ifPresent(content -> {
                content.failUpload();
                binaryContentRepository.save(content);
                log.error("File upload failed Id: {}, {}", content.getId(), ex.getMessage());
              });
              return null;
            });
      }
    });

    return savedContent;
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(Long binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(BinaryContentDto::from)
        .orElseThrow(() -> new MplException(ErrorCode.BINARY_CONTENT_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<Long> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);
    log.info("바이너리 컨텐츠 목록 조회 서비스 시작");
    return binaryContents.stream()
        .map(BinaryContentDto::from)
        .toList();
  }

  @Override
  public void delete(Long binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new MplException(ErrorCode.BINARY_CONTENT_NOT_FOUND));

    String key = generateKey(binaryContent.getId(), BinaryContentDto.from(binaryContent));

    binaryContentStorage.delete(key);
    binaryContentRepository.delete(binaryContent);
    log.info("바이너리 컨텐츠 삭제 완료: Id = {}", binaryContentId);
  }

  @Override
  @Transactional(readOnly = true)
  public DownloadFileDto download(Long binaryContentId) {
    log.debug("바이너리 컨텐츠 다운로드 서비스 시작: Id = {}", binaryContentId);

    // DB에서 메타데이터 조회
    BinaryContentDto metaData = find(binaryContentId);

    // 스토리지에서 사용하는 파일 키 생성
    String key = generateKey(binaryContentId, metaData);

    // 생성한 키로 스토리지에서 파일 리소스 가져오기
    Resource resource = binaryContentStorage.download(key);

    // 컨트롤러에 전달한 DTO를 생성하여 반환 (ResponseEntity 생성 로직 제거)
    log.info("다운로드 파일 데이터 준비 완료: fileName = {}", metaData.fileName());
    return new DownloadFileDto(
        resource,
        metaData.fileName(),
        metaData.contentType(),
        metaData.size()
    );
  }

  private String generateKey(Long id, BinaryContentDto dto) {
    LocalDate now = LocalDate.now();
    return String.format("images/%s-%s",
        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        id + "." + dto.extension());
    // ex) /files/images/2025-08-12-2.jpg
  }
}
