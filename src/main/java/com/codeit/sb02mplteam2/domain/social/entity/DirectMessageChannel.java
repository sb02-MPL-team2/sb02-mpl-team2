package com.codeit.sb02mplteam2.domain.social.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessageChannel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name="created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user_id", nullable = false)
  private User fromUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_user_id", nullable = false)
  private User toUser;

  @OneToMany(mappedBy = "directMessageChannel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DirectMessage> messages = new ArrayList<>();

  private DirectMessageChannel(User fromUser, User toUser){
    this.fromUser = fromUser;
    this.toUser = toUser;
  }

  public static DirectMessageChannel of(User fromUser, User toUser) {
    return new DirectMessageChannel(fromUser, toUser);
  }
}
