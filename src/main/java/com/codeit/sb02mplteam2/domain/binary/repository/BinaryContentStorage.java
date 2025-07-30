package com.codeit.sb02mplteam2.domain.binary.repository;

import com.codeit.sb02mplteam2.domain.binary.dto.BinaryContentDto;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {
  CompletableFuture<String> put(String extension, String contentType,byte[] bytes);

  ResponseEntity<Resource> download(BinaryContentDto binaryContentDto);
}
