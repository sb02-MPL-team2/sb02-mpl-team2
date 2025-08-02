package com.codeit.sb02mplteam2.domain.playlist.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Subscribe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(name = "subscribed_at")
  private LocalDateTime subscribedAt;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "playlist_id")
  private Playlist playlist;

  public Subscribe(User user, Playlist playlist) {
    this.user = user;
    this.playlist = playlist;
    this.subscribedAt = LocalDateTime.now();
  }
}
