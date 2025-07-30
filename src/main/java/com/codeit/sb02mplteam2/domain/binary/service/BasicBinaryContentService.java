package com.codeit.sb02mplteam2.domain.binary.service;

import com.codeit.sb02mplteam2.domain.binary.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.binary.repository.BinaryContentRepository;
import com.codeit.sb02mplteam2.domain.binary.repository.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContentDto upload(MultipartFile file) throws IOException {
    BinaryContent binaryContent = BinaryContent.from(file);
    binaryContentRepository.save(binaryContent);
    CompletableFuture<String> future = binaryContentStorage.put(binaryContent.getExtension(), binaryContent.getContentType(),
        file.getBytes());

    future.thenAccept(fileName ->
        log.info("비동기 파일 저장 완료. 파일명 : {}", fileName)
    ).exceptionally(error -> {
      log.warn("비동기 파일 저장 실패!", error);
      return null;
    });

    return BinaryContentDto.from(binaryContent);
  }

  @Override
  public List<BinaryContentDto> findAll() {
    return binaryContentRepository.findAll().stream().map(BinaryContentDto::from).toList();
  }
}
