package com.codeit.sb02mplteam2.domain.playlist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "playlist_subscriber_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PlaylistSubscriberHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  //단방향 다대일
  @ManyToOne
  @JoinColumn(name = "playlist_id")
  private Playlist playlist;

  @Column(name = "subscriber_Count")
  private int count;

  public PlaylistSubscriberHistory(Playlist playlist, int count) {
    this.createdAt = LocalDateTime.now();
    this.playlist = playlist;
    this.count = count;
  }
}
