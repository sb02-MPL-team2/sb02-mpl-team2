package com.codeit.sb02mplteam2.domain.content.repository;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContentRepository extends JpaRepository<Content, Long> {

  @Query("""
  select new com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto(
    c.id,
    c.title,
    c.description,
    cast(c.category as string),
    c.imageUrl,
    coalesce(cast(avg(r.rating) as double), cast(0 as double)),
    count(r),
    count(distinct v.id),
    c.runtime,
    lwr.id
  )
  from Content c
  left join LiveWatchRoom lwr on lwr.content.id = c.id
  left join Review r on r.content.id = c.id
  left join LiveWatchParticipant v on v.liveWatchRoom.id = lwr.id
  where c.id = :id
  group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime
  """)
  Optional<ContentResponseDto> findByIdWithRoom(@Param("id") Long id);

  @Query(
      value = """
    select new com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto(
      c.id,
      c.title,
      c.description,
      cast(c.category as string),
      c.imageUrl,
      coalesce(cast(avg(r.rating) as double), cast(0 as double)),
      count(r),
      count(distinct v.id),
      c.runtime,
      lwr.id
    )
    from Content c
    left join LiveWatchRoom lwr on lwr.content.id = c.id
    left join Review r on r.content.id = c.id
    left join LiveWatchParticipant v on v.liveWatchRoom.id = lwr.id
    group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime
    """,
      countQuery = """
    select count(c)
    from Content c
    """
  )
  Page<ContentResponseDto> findAllWithRoom(Pageable pageable);

  @Query(
      value = """
    select new com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto(
      c.id,
      c.title,
      c.description,
      cast(c.category as string),
      c.imageUrl,
      coalesce(cast(avg(r.rating) as double), cast(0 as double)),
      count(r),
      count(distinct v.id),
      c.runtime,
      lwr.id
    )
    from Content c
    left join LiveWatchRoom lwr on lwr.content.id = c.id
    left join Review r on r.content.id = c.id
    left join LiveWatchParticipant v on v.liveWatchRoom.id = lwr.id
    where c.category = :category
    group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime
    """,
      countQuery = """
    select count(c)
    from Content c
    where c.category = :category
    """
  )
  Page<ContentResponseDto> findByCategoryWithRoom(@Param("category") ContentCategory category,
      Pageable pageable);
}
