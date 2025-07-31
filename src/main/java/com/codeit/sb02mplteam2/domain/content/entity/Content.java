package com.codeit.sb02mplteam2.domain.content.entity;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
public class Content {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  private String title;

  private String category;

  @Column(columnDefinition = "TEXT")
  private String description;

  @OneToOne
  @JoinColumn(name = "binary_content_id")
  private BinaryContent binaryContent;

  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();
}
