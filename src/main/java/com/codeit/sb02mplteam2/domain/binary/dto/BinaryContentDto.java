package com.codeit.sb02mplteam2.domain.binary.dto;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BinaryContentDto(
    Long id,
    String fileName,
    Long size,
    String contentType,
    String extension,
    LocalDateTime createdAt
) {
  public static BinaryContentDto from(BinaryContent binaryContent) {
    return BinaryContentDto.builder()
        .id(binaryContent.getId())
        .fileName(binaryContent.getFileName())
        .size(binaryContent.getSize())
        .contentType(binaryContent.getContentType())
        .extension(binaryContent.getExtension())
        .createdAt(binaryContent.getCreatedAt())
        .build();
  }
}
