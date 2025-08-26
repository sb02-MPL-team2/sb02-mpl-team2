package com.codeit.sb02mplteam2.domain.content.entity;

import com.codeit.sb02mplteam2.domain.review.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(
    name = "contents",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_contents_provider_external_id",
            columnNames = {"provider", "external_id"}
        )
    },
    indexes = {
        @Index(name = "idx_contents_provider_external_id", columnList = "provider, external_id")
    }
)
public class Content {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  private String title;

  @Enumerated(EnumType.STRING)
  private ContentCategory category;

  @Column(columnDefinition = "TEXT")
  private String description;

//  @OneToOne
//  @JoinColumn(name = "binary_content_id")
//  private BinaryContent binaryContent;

  @Column(name = "image_url", columnDefinition = "TEXT")
  private String imageUrl;

  @Column(name = "runtime")
  private Integer runtime;

  @Column(name = "provider")
  private String provider;

  @Column(name = "external_id")
  private String externalId;

  @Column(name = "release_date")
  private LocalDate releaseDate;

  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();

  public Content(String title, ContentCategory category) {
    this.title = title;
    this.category = category;
  }

  public Content(String title, String description, ContentCategory category, String imageUrl,
      LocalDateTime createdAt) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
  }

}
