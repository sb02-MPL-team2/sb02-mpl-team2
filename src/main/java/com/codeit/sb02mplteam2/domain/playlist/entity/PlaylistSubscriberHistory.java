package com.codeit.sb02mplteam2.domain.playlist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlist_subscriber_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaylistSubscriberHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //단방향 다대일
  @ManyToOne
  @JoinColumn(name = "playlist_id")
  private Playlist playlist;

  @Column(name = "subscriber_Count")
  private int count;

  public PlaylistSubscriberHistory(Playlist playlist, int count) {
    this.playlist = playlist;
    this.count = count;
  }
}
