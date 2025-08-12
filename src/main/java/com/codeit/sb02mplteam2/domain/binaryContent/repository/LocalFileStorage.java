package com.codeit.sb02mplteam2.domain.binaryContent.repository;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "mpl.storage.type", havingValue = "local")
public class LocalFileStorage implements BinaryContentStorage{

  private final Path rootPath;

  public LocalFileStorage(@Value("${mpl.storage.local.root-path}") String storagePath) {
    this.rootPath = Paths.get(storagePath);
    try {
      Files.createDirectories(this.rootPath);
      log.info("[LocalFileStorage] Storage directory created or already exists: {}", this.rootPath);
    } catch (IOException e) {
      log.error("[LocalFileStorage] Failed to create storage directory", e);
      throw new MplException(ErrorCode.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Async("fileUploadExecutor")
  @Override
  public CompletableFuture<String> upload(String key, byte[] bytes) {
    Path destination = this.rootPath.resolve(key).normalize();
    try{
      Files.createDirectories(destination.getParent());

      try (OutputStream os = Files.newOutputStream(destination)) {
        os.write(bytes);
      }
      log.info("[LocalFileStorage] File saved successfully: {}", key);
      return CompletableFuture.completedFuture(key);
    } catch (IOException e) {
      log.error("[LocalFileStorage] Failed to save file: {}", key, e);
      return CompletableFuture.failedFuture(new MplException(ErrorCode.FILE_UPLOAD_FAILED, e));
    }
  }

  @Override
  public Resource download(String key) {
    try {
      Path file = this.rootPath.resolve(key).normalize();
      Resource resource = new FileSystemResource(file);

      if (!resource.exists() || !resource.isReadable()) {
        log.warn("[LocalFileStorage] File not found or not readable: {}", key);
        throw new MplException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
      }

      return resource;
    } catch (MplException e) {
      throw e;
    } catch (Exception e) {
      log.error("[LocalFileStorage] Error while downloading file: {}", key, e);
      throw new MplException(ErrorCode.FILE_DOWNLOAD_FAILED, e);
    }
  }

  @Override
  public void delete(String key) {
    try {
      Path file = this.rootPath.resolve(key).normalize();
      Files.deleteIfExists(file);
      log.info("[LocalFileStorage] File deleted successfully: {}", key);
    } catch (IOException e) {
      log.error("[LocalFileStorage] Failed to delete file: {}", key, e);
      throw new MplException(ErrorCode.FILE_DELETE_FAILED, e);
    }
  }
}
