package com.codeit.sb02mplteam2.domain.binaryContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.binaryContent.entity.UploadStatus;
import com.codeit.sb02mplteam2.domain.binaryContent.repository.BinaryContentRepository;
import com.codeit.sb02mplteam2.domain.binaryContent.service.BinaryContentService;
import com.codeit.sb02mplteam2.exception.MplException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class BasicBinaryContentServiceIntegrationTest {

  @Autowired
  private BinaryContentService binaryContentService;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  // JUnit 5가 제공하는 임시 디렉토리, 테스트 끝나면 자동 삭제
  @TempDir
  static Path tempDir;

  private static final String TEST_BASE_URL_PATH = "/test-files/";

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("mpl.storage.local.root-path", () -> tempDir.toString());
    registry.add("mpl.storage.local.base-url", () -> TEST_BASE_URL_PATH);
  }

  @AfterEach
  void tearDown() {
    binaryContentRepository.deleteAll();
  }

  private String generateKey(BinaryContent content) {
    return String.format("images/%s/%s",
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
        content.getId() + "." + content.getExtension());
  }

  @Test
  @DisplayName("파일 업로드 성공 - 트랜잭션 커밋 후 비동기 처리 완료")
  void upload_Success() throws IOException {
    // given
    MultipartFile file = new MockMultipartFile(
        "image",
        "test-image.png",
        "image/png",
        "test image content".getBytes(StandardCharsets.UTF_8)
    );

    // when
    // upload 메서드 호출 (DB에 PENDING 상태로 저장)
    BinaryContent initialSave = binaryContentService.upload(file);

    // then
    // 초기 상태 검증
    assertThat(initialSave.getId()).isNotNull();
    assertThat(initialSave.getUploadStatus()).isEqualTo(UploadStatus.PENDING);

    // 비동기 작업이 완료될 때까지 최대 5초간 대기
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
      // DB 에서 최종 상태를 다시 조회
      BinaryContent finalContent =
          binaryContentRepository.findById(initialSave.getId()).orElseThrow();

      // 최종 상태 검증 (COMPLETED, URL 업데이트)
      assertThat(finalContent.getUploadStatus()).isEqualTo(UploadStatus.COMPLETED);
      assertThat(finalContent.getUrl()).startsWith(TEST_BASE_URL_PATH);
      assertThat(finalContent.getUrl()).endsWith(initialSave.getId() + ".png");

      // 실제 파일이 임시 디렉토리에 저장되었는지 확인
      String key = finalContent.getUrl().replace(TEST_BASE_URL_PATH, "");
      Path filePath = tempDir.resolve(key);
      assertThat(Files.exists(filePath)).isTrue();
    });
  }

  @Test
  @DisplayName("파일 업로드 실패 - 파일이 비어있을 경우")
  void upload_Fail_WhenFileIsEmpty() {
    // given
    MultipartFile emptyFile = new MockMultipartFile("empty", new byte[0]);

    // when & then
    assertThrows(MplException.class, () -> {
      binaryContentService.upload(emptyFile);
    });
  }

  @Test
  @DisplayName("파일 삭제 성공")
  void delete_Success() throws IOException {
    // given: 먼저 파일을 업로드
    MultipartFile file = new MockMultipartFile(
        "file", "to-delete.txt", "text/plain", "delete me".getBytes());
    BinaryContent savedContent = binaryContentService.upload(file);

    // 비동기 업로드 완료 대기
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
      BinaryContent content = binaryContentRepository.findById(savedContent.getId()).orElseThrow();
      assertThat(content.getUploadStatus()).isEqualTo(UploadStatus.COMPLETED);
    });

    BinaryContent finalContent = binaryContentRepository.findById(savedContent.getId()).get();
    String key = finalContent.getUrl().replace(TEST_BASE_URL_PATH, "");
    Path filePath = tempDir.resolve(key);
    assertThat(Files.exists(filePath)).isTrue(); // 파일이 존재하는지 확인

    // when
    binaryContentService.delete(finalContent.getId());

    // then
    assertThat(binaryContentRepository.findById(finalContent.getId())).isEmpty();
    assertThat(Files.exists(filePath)).isFalse();
  }

  @Test
  @DisplayName("파일 다운로드 실패 - 존재하지 않는 파일")
  void download_Fail_WhenContentNotFound() {
    // given
    Long nonExistentId = 999L;

    // when & then
    assertThrows(MplException.class, () -> {
      binaryContentService.download(nonExistentId);
    });
  }
}
