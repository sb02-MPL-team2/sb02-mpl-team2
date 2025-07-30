package com.codeit.sb02mplteam2.domain.social.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name="follows")
public class Follow {

  @Id
  @GeneratedValue
  private Long id;

  // 팔로우 하는 유저 (From)
  @ManyToOne
  @JoinColumn(name = "from_user_id")
  private User fromUser;

  // 팔로우 당하는 유저 (To)
  @ManyToOne
  @JoinColumn(name = "to_user_id")
  private User toUser;

  private LocalDateTime createdAt;

}