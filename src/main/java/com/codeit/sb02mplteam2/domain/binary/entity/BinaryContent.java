package com.codeit.sb02mplteam2.domain.binary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BinaryContent {

  @Id
  @GeneratedValue
  private Long id;

  @CreatedDate
  @Column(name="created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "size")
  private Long size;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "extension")
  private String extension;

  @Enumerated(EnumType.STRING)
  @Column(name = "upload_status")
  @Setter
  private UploadStatus uploadStatus;

  private BinaryContent(String fileName, Long size, String contentType, String extension) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.extension = extension;
    this.uploadStatus = UploadStatus.PENDING;
  }

  public static BinaryContent from(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String fileName = FilenameUtils.getBaseName(originalFilename);
    String fileExtension = FilenameUtils.getExtension(originalFilename);
    return new BinaryContent(fileName, file.getSize(), file.getContentType(), fileExtension);
  }
}