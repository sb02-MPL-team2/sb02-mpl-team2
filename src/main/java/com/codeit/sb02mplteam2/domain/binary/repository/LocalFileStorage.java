package com.codeit.sb02mplteam2.domain.binary.repository;

import com.codeit.sb02mplteam2.domain.binary.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "mpl.storage.type", havingValue = "local")
public class LocalFileStorage implements BinaryContentStorage{
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private final Path storagePath;

  public LocalFileStorage(@Value("${mpl.storage.local.root-path}") String storagePath) {
    this.storagePath = Paths.get(storagePath);
    init();
  }

  private void init() {
    if (Files.notExists(storagePath)) {
      try {
        log.info("[LocalBinaryContentStorage] Directories created");
        Files.createDirectories(storagePath);
      } catch (IOException e) {
        log.warn("[LocalBinaryContentStorage] To create Directories is failed");
      }
    }
  }

  @Async
  public CompletableFuture<String> put(String extension, String contentType, byte[] bytes) {
    String filename = LocalDateTime.now().format(formatter);
    Path destination = storagePath.resolve(filename + extension);
    try (OutputStream outputStream = Files.newOutputStream(destination)) {
      outputStream.write(bytes);
      log.info("파일 저장 성공");
      return CompletableFuture.completedFuture(filename);
    } catch (IOException e) {
      log.warn("파일 저장 실패");
      CompletableFuture<String> future = new CompletableFuture<>();
      future.completeExceptionally(e);
      return future;
    }
  }

  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    String filename = binaryContentDto.createdAt().format(formatter);
    Path destination = storagePath.resolve(filename + binaryContentDto.extension());
    try {
      InputStream inputStream = new FileInputStream(destination.toFile());
      InputStreamResource resource = new InputStreamResource(inputStream);

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION,
          "inline; filename=\"" + binaryContentDto.fileName() + "\"");
      headers.setContentType(MediaType.parseMediaType(binaryContentDto.contentType()));
      headers.setContentLength(binaryContentDto.size());

      log.info("[LocalBinaryContentStorage] Image Download successfully");
      return ResponseEntity.ok().headers(headers).body(resource);
    } catch (IOException e) {
      log.warn("파일 없음");
      throw new MplException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

}
