package com.codeit.sb02mplteam2.domain.content.repository;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    cast(0 as long),
    c.runtime,
    lwr.id,
    c.releaseDate
  )
  from Content c
  left join LiveWatchRoom lwr on lwr.content.id = c.id
  left join Review r on r.content.id = c.id
  where c.id = :id
  group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime
  """)
  Optional<ContentResponseDto> findByIdWithRoom(@Param("id") Long id);

  // TODO: watchCount는 현재 0으로 설정됨. 실제 참가자 수는 RedisLiveWatchParticipantService에서 조회 필요

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
      cast(0 as long),
      c.runtime,
      lwr.id,
      c.releaseDate
    )
    from Content c
    left join LiveWatchRoom lwr on lwr.content.id = c.id
    left join Review r on r.content.id = c.id
    group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime
    """,
      countQuery = """
    select count(c)
    from Content c
    """
  )
  Page<ContentResponseDto> findAllWithRoom(Pageable pageable);

  // TODO: watchCount는 현재 0으로 설정됨. 실제 참가자 수는 RedisLiveWatchParticipantService에서 조회 필요

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
      cast(0 as long),
      c.runtime,
      lwr.id,
      c.releaseDate
    )
    from Content c
    left join LiveWatchRoom lwr on lwr.content.id = c.id
    left join Review r on r.content.id = c.id
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

  // TODO: watchCount는 현재 0으로 설정됨. 실제 참가자 수는 RedisLiveWatchParticipantService에서 조회 필요

  @Query("""
select new com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto(
  c.id,
  c.title,
  c.description,
  cast(c.category as string),
  c.imageUrl,
  coalesce(cast(avg(r.rating) as double), cast(0 as double)),
  count(r),
  cast(0 as long),
  c.runtime,
  lwr.id,
  c.releaseDate
)
from Content c
left join LiveWatchRoom lwr on lwr.content.id = c.id
left join Review r on r.content.id = c.id
where (:category is null or c.category = :category)
  and (
    :cursorDate is null 
    or c.releaseDate < CAST(:cursorDate AS date)
    or (c.releaseDate = CAST(:cursorDate AS date) and c.id < :cursorId)
  )
group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime, c.releaseDate
order by c.releaseDate desc, c.id desc
""")
  List<ContentResponseDto> scrollContents(
      @Param("category") ContentCategory category,
      @Param("cursorDate") LocalDate cursorDate,
      @Param("cursorId") Long cursorId,
      Pageable pageable
  );

  @Query("""
select new com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto(
  c.id,
  c.title,
  c.description,
  cast(c.category as string),
  c.imageUrl,
  coalesce(cast(avg(r.rating) as double), cast(0 as double)),
  count(r),
  cast(0 as long),
  c.runtime,
  lwr.id,
  c.releaseDate
)
from Content c
left join LiveWatchRoom lwr on lwr.content.id = c.id
left join Review r on r.content.id = c.id
where (:category is null or c.category = :category)
group by c.id, c.title, c.description, c.category, c.imageUrl, lwr.id, c.runtime, c.releaseDate
order by c.releaseDate desc, c.id desc
""")
  List<ContentResponseDto> scrollContentsWithoutCursor(
      @Param("category") ContentCategory category,
      Pageable pageable
  );
}
