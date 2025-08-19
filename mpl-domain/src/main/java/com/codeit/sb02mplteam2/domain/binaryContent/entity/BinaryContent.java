package com.codeit.sb02mplteam2.domain.binaryContent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class BinaryContent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name="created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
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

  @Column(name = "url", length = 2048) // URL 저장할 칼럼 추가
  private String url;

  @Enumerated(EnumType.STRING)
  @Column(name = "upload_status")
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
    String fileName = FilenameUtils.getName(originalFilename);
    String fileExtension = FilenameUtils.getExtension(originalFilename);
    return new BinaryContent(fileName, file.getSize(), file.getContentType(), fileExtension);
  }

  public void completeUpload(String url) {
    this.url = url;
    this.uploadStatus = UploadStatus.COMPLETED;
  }

  public void failUpload() {
    this.uploadStatus = UploadStatus.FAILED;
  }


}