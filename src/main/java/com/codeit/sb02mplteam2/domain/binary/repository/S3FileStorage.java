package com.codeit.sb02mplteam2.domain.binary.repository;

import com.codeit.sb02mplteam2.domain.binary.dto.BinaryContentDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@ConditionalOnProperty(name = "mpl.storage.type", havingValue = "s3")
@Component
@RequiredArgsConstructor
public class S3FileStorage implements BinaryContentStorage {

  @Value("${mpl.aws.s3.bucket}")
  private String bucketName;

  @Value("${mpl.aws.s3.base-url}")
  private String baseUrl;

  private final S3Client s3Client;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  @Override
  public CompletableFuture<String> put(String extension, String contentType, byte[] bytes) {
    String s3Key = LocalDateTime.now().format(formatter);
    String s3Url = baseUrl + "/" + s3Key;

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .contentType(contentType)
        .build();

    return null;
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    return null;
  }
}
