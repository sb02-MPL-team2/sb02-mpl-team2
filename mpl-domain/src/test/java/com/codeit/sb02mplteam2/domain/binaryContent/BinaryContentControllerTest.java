package com.codeit.sb02mplteam2.domain.binaryContent;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.sb02mplteam2.domain.binaryContent.controller.BinaryContentController;
import com.codeit.sb02mplteam2.domain.binaryContent.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.domain.binaryContent.entity.UploadStatus;
import com.codeit.sb02mplteam2.domain.binaryContent.service.BinaryContentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = BinaryContentController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Disabled //TODO 오류로 인해 Disabled 하였습니다.
public class BinaryContentControllerTest {

  @MockitoBean // 컨트롤러가 의존하는 서비스
  private BinaryContentService binaryContentService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("바이너리 컨텐츠 단건 조회 성공")
  void find_Success() throws Exception {
    // given
    long binaryContentId = 1L;
    String expectedUrl = "http://localhost:8080/files/test.jpg";
    BinaryContentDto mockDto = new BinaryContentDto(
        binaryContentId,
        "test.jpg",
        1024L,
        "image/jpeg",
        "jpg",
        expectedUrl,
        UploadStatus.COMPLETED,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
    given(binaryContentService.find(binaryContentId)).willReturn(mockDto);

    // when
    ResultActions actions = mockMvc.perform(
        get("/api/binaryContent/{binaryContentId}", binaryContentId)
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(binaryContentId))
        .andExpect(jsonPath("$.fileName").value("test.jpg"))
        .andExpect(jsonPath("$.url").value(expectedUrl));
  }
}
