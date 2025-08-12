package com.codeit.sb02mplteam2.domain.playlist.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "playlists")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
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

  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Subscribe> subscribes = new HashSet<>();

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;
  //TODO LinkedHashSet으로 순서 기억하는 Set구조로 바꿀까 생각중
  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<PlaylistItem> items = new HashSet<>();

  public Playlist(User user,String title, String description) {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = createdAt;
    this.user = user;
    this.title = title;
    this.description = description;
  }

  public boolean subscribe(Subscribe subscribe) {
    return subscribes.add(subscribe);
  }

  public boolean unSubscribe(Subscribe subscribe) {
    try {
      if (subscribe.getUser().equals(this.user)) {
        return false;
      }
      subscribes.remove(subscribe);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void addItem(PlaylistItem playlistItem) {
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
