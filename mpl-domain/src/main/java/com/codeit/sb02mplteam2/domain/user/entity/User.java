package com.codeit.sb02mplteam2.domain.user.entity;

import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.security.Provider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 사용하려면 작성해야함
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String username;

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @Column(nullable = false, length = 100)
  private String password;

  @Column(nullable = false)
  private boolean isLocked = false;

  @Column(nullable = false)
  private boolean isDeleted = false;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int followerCount = 0;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int followingCount = 0;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Playlist> playlists = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", columnDefinition = "BIGINT")
  private BinaryContent profile;

  @Column(name = "picture_url")
  private String pictureUrl;

  @Column(name = "provider_id")
  private String providerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Provider provider;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @CreatedDate
  @Column(columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(columnDefinition = "timestamp with time zone")
  private LocalDateTime updatedAt;

 public User(String username, String email, String password, BinaryContent profile) {
   this.username = username;
   this.email = email;
   this.password = password;
   this.profile = profile;
   this.provider = Provider.LOCAL;
   this.role = Role.USER;
 }

 public User(String username, String email, String password, String pictureUrl, String providerId,
     Provider provider) {
   this.username = username;
   this.email = email;
   this.password = password;
   this.pictureUrl = pictureUrl;
   this.providerId = providerId;
   this.provider = provider;
   this.role = Role.USER;
 }

 public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
   if(newUsername != null && !newUsername.equals(this.username)){
     this.username = newUsername;
   }
   if(newEmail != null && !newEmail.equals(this.email)){
     this.email = newEmail;
   }
   if(newPassword != null && !newPassword.equals(this.password)){
     this.password = newPassword;
   }
   if(newProfile != null) {
     this.profile = newProfile;
   }
 }

  public void updateRole(Role role) {
    if(this.role != role) {
      this.role = role;
    }
  }

  public void updatePassword(String newPassword) {
   if (newPassword != null) {
     this.password = newPassword;
   }
  }

  public void updateUsername(String newUsername) {
   if (newUsername != null) {
     this.username = newUsername;
   }
  }

  public void updatePictureUrl(String newPictureUrl) {
   if(newPictureUrl != null) {
     this.pictureUrl = newPictureUrl;
   }
  }

  public void linkSocialAccount(Provider provider, String providerId) {
   // 이미 다른 소셜 계정과 연동된 경우를 방지하기 위해 LOCAL인 경우만 변경
    if(this.provider == Provider.LOCAL) {
      this.provider = provider;
      this.providerId = providerId;
    }
  }

  public void lock() {
    this.isLocked = true;
  }

  public void unlock() {
    this.isLocked = false;
  }

  public void softDelete() {
   this.isDeleted = true;
  }

  public void restore() {
   this.isDeleted = false;
  }

  public void increaseFollowerCount() {
   this.followerCount++;
  }

  public void decreaseFollowerCount() {
   this.followerCount--;
  }

  public void increaseFollowingCount() {
   this.followingCount++;
  }

  public void decreaseFollowingCount() {
   this.followingCount--;
  }

  public boolean isLocked() {
   return this.isLocked;
  }

  public boolean isDeleted() {
   return this.isDeleted;
  }

}
