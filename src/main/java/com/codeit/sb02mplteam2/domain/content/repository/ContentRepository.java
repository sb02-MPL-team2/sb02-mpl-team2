package com.codeit.sb02mplteam2.domain.content.repository;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, UUID> {

}
