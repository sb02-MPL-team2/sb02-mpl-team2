package com.codeit.sb02mplteam2.domain.binaryContent.repository;

import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;

public interface BinaryContentStorage {
  CompletableFuture<String> upload(String key, byte[] bytes);

  Resource download(String key);

  void delete(String key);
}
