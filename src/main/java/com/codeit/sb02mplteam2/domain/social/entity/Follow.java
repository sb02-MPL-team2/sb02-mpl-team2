package com.codeit.sb02mplteam2.domain.social.entity;

@Entity
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

  private LocalDateTime createdAt; // 팔로우한 시간 등 추가 정보 저장 가능

}