package com.codeit.sb02mplteam2.domain.binaryContent.controller;

import com.codeit.sb02mplteam2.domain.binaryContent.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.domain.binaryContent.dto.DownloadFileDto;
import com.codeit.sb02mplteam2.domain.binaryContent.service.BinaryContentService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BinaryContentController implements BinaryContentApi{

  private final BinaryContentService binaryContentService;

  @GetMapping("/binaryContent/{binaryContentId}")
  @Override
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable Long binaryContentId) {
    log.info("바이너리 컨텐츠 조회 요청: Id = {}", binaryContentId);
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    log.debug("바이너리 컨텐츠 조회 응답: {}", binaryContent);
    return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
  }

  @GetMapping("/binaryContent")
  @Override
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(List<Long> binaryContentIds) {
    log.info("바이너리 컨텐츠 목록 조회 요청: Ids = {}", binaryContentIds);
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    log.debug("바이너리 컨텐츠 목록 조회 응답: {}", binaryContents);
    return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
  }

  @GetMapping("/binaryContent/{binaryContentId}/download")
  @Override
  public ResponseEntity<?> download(Long binaryContentId) {
    log.info("바이너리 컨텐츠 다운로드 요청: Id = {}", binaryContentId);

    // 서비스에서 다운로드에 필요한 데이터 묶음(DTO)을 받음
    DownloadFileDto fileDto = binaryContentService.download(binaryContentId);

    // 컨트롤러에서 HTTP 응답 생성
    String encodedFileName = URLEncoder.encode(fileDto.fileName(), StandardCharsets.UTF_8)
        .replaceAll("\\+", "%20");

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + encodedFileName + "\"")
        .header(HttpHeaders.CONTENT_TYPE, fileDto.contentType())
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileDto.size()))
        .body(fileDto.resource());
  }
}
