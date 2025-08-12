package com.codeit.sb02mplteam2.domain.binaryContent.dto;

import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.binaryContent.entity.UploadStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BinaryContentDto(
    Long id,
    String fileName,
    Long size,
    String contentType,
    String extension,
    String url,
    UploadStatus uploadStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static BinaryContentDto from(BinaryContent binaryContent) {
    return BinaryContentDto.builder()
        .id(binaryContent.getId())
        .fileName(binaryContent.getFileName())
        .size(binaryContent.getSize())
        .contentType(binaryContent.getContentType())
        .extension(binaryContent.getExtension())
        .url(binaryContent.getUrl())
        .uploadStatus(binaryContent.getUploadStatus())
        .createdAt(binaryContent.getCreatedAt())
        .updatedAt(binaryContent.getUpdatedAt())
        .build();
  }
}
