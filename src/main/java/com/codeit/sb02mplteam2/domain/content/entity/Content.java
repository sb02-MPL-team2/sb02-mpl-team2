package com.codeit.sb02mplteam2.domain.content.entity;

import com.codeit.sb02mplteam2.domain.review.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;

@Entity
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

  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();
}
