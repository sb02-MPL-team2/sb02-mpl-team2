package com.codeit.sb02mplteam2.domain.user.entity;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@NoArgsConstructor
@Getter
public class User {

  private Long id;

  private String username;

  private String email;

  private String password;

  private boolean isLocked;

  private List<Playlist> playlists = new ArrayList<>();

  private BinaryContent profile;

  private Role role;
}
