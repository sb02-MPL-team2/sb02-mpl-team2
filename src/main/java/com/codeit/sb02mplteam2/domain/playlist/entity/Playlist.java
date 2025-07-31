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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "playlists")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

  public Playlist(User user, String description, String title) {
    this.user = user;
    this.title = title;
    this.description = description;
  }

  public void addPlayList(PlaylistItem playlistItem) {
    items.add(playlistItem);
    playlistItem.setPlaylist(this);
  }

  private<T> T updateField (T target, T replace) {
    if (replace != null && !target.equals(replace)) {
      return replace;
    }
    return target;
  }

  public void update(String newTitle, String newDescription) {
    this.title = updateField(this.title, newTitle);
    this.description = updateField(this.description, newDescription);
  }
}
