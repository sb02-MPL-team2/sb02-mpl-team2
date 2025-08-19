package com.codeit.sb02mplteam2.domain.content.mapper;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRequestDto;
import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContentMapper {

  @Mapping(target="totalRating", ignore = true)
  @Mapping(target="reviewCount", ignore = true)
  @Mapping(target="watchCount", ignore = true)
  @Mapping(target="roomId", ignore = true)
  ContentResponseDto toDto(Content content);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "reviews", ignore = true)
  Content toEntity(ContentRequestDto dto);
}
