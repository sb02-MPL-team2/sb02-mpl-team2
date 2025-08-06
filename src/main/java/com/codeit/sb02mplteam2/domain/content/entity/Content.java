package com.codeit.sb02mplteam2.domain.content.entity;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Content {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  private String title;

  @Enumerated(EnumType.STRING)
  private ContentCategory category;

  @Column(columnDefinition = "TEXT")
  private String description;

//  @OneToOne
//  @JoinColumn(name = "binary_content_id")
//  private BinaryContent binaryContent;

  @Column(name = "imageUrl", columnDefinition = "TEXT")
  private String imageUrl;

  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();

  public Content(String title, String description, ContentCategory category, String imageUrl, LocalDateTime createdAt) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
  }

}
