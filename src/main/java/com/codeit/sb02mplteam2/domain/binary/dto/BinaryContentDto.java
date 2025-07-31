package com.codeit.sb02mplteam2.domain.binary.dto;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BinaryContentDto(
    Long id,
    LocalDateTime createdAt,
    String fileName,
    String extension,
    String contentType,
    Long size
) {
  public static BinaryContentDto from(BinaryContent binaryContent) {
    return BinaryContentDto.builder()
        .id(binaryContent.getId())
        .createdAt(binaryContent.getCreatedAt())
        .fileName(binaryContent.getFileName())
        .extension(binaryContent.getExtension())
        .contentType(binaryContent.getContentType())
        .size(binaryContent.getSize())
        .build();
  }
}
