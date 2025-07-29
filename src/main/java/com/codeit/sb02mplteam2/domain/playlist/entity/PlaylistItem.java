package com.codeit.sb02mplteam2.domain.playlist.entity;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;

@Entity
public class PlaylistItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name="created_at")
  private LocalDateTime createdAt;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "playlist_id")
  private Playlist playlist;  // 연관관계 주인

  //@OneToOne(fetch = FetchType.LAZY)
  // PlaylistItem은 어떤 Content를 담고 있는지 알아야 함
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id")
  private Content content;    // 연관관계 주인

  private int orderIndex;     // 콘텐츠 순서 등 추가 정보
}