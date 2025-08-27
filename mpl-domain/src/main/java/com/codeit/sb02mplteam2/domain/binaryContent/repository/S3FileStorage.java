package com.codeit.sb02mplteam2.domain.binaryContent.repository;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@Slf4j
@ConditionalOnProperty(name = "mpl.storage.type", havingValue = "s3")
public class S3FileStorage implements BinaryContentStorage{

  private final S3Client s3Client;
  private final String bucketName;

  public S3FileStorage(S3Client s3Client, @Value("${mpl.aws.s3.bucket}") String bucketName){
    this.s3Client = s3Client;
    this.bucketName = bucketName;
    log.info("[S3FileStorage] S3 Storage is enabled. Bucket: {}", bucketName);
  }

  @Async("fileUploadExecutor")
  @Override
  public CompletableFuture<String> upload(String key, byte[] bytes) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        log.info("[S3FileStorage] File uploaded successfully to S3: {}", key);
        return key;
      } catch (Exception e) {
        log.error("[S3FileStorage] Failed to upload file to S3: {}", key, e);
        throw new MplException(ErrorCode.FILE_UPLOAD_FAILED, e);
      }
    });
  }

  @Override
  public Resource download(String key) {
    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();

      // S3 객체의 InputStream을 Spring Resource로 래핑하여 반환
      return new InputStreamResource(s3Client.getObject(getObjectRequest));
    } catch (NoSuchKeyException e) {
      log.warn("[S3FileStorage] File not found in S3: {}", key);
      throw new MplException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    } catch (Exception e) {
      log.error("[S3FileStorage] Error while downloading file from S3: {}", key, e);
      throw new MplException(ErrorCode.FILE_DOWNLOAD_FAILED, e);
    }
  }

  @Override
  public void delete(String key) {
    try {
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();

      s3Client.deleteObject(deleteObjectRequest);
      log.info("[S3FileStorage] File deleted successfully from S3: {}", key);
    } catch (Exception e) {
      log.error("[S3FileStorage] Failed to delete file from S3: {}", key, e);
      throw new MplException(ErrorCode.FILE_DELETE_FAILED, e);
    }
  }
}
