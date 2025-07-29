package com.codeit.sb02mplteam2.domain.playlist.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "playlists")
public class Playlist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Playlist가 User와의 관계에서 연관관계의 주인(FK를 가짐)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "subscription_count")
  private int subscribeCount;

  @Column(name = "description")
  private String description;

  @Column(name = "title")
  private String title;

  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlaylistItem> items = new ArrayList<>();

  public void setUser(User user) {
    // 기존 User와의 연관관계를 제거 (있을 경우)
    if (this.user != null) {
      this.user.getPlaylists().remove(this);
    }
    this.user = user;
    // 새로운 User의 playlists에 현재 Playlist를 추가
    user.getPlaylists().add(this);
  }
}
