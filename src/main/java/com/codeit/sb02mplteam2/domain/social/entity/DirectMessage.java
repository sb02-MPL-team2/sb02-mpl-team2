package com.codeit.sb02mplteam2.domain.social.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @CreatedDate
  @Column(name="created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="direct_message_channel_id", nullable = false)
  private DirectMessageChannel directMessageChannel;


  private DirectMessage(String content, User user, DirectMessageChannel channel) {
    this.content = content;
    this.user = user;
    this.directMessageChannel = channel;
  }

  public static DirectMessage of(String content, User user, DirectMessageChannel channel) {
    return new DirectMessage(content, user, channel);
  }
}
