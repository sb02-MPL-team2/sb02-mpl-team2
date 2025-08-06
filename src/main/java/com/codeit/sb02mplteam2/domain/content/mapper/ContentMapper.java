package com.codeit.sb02mplteam2.domain.content.mapper;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRequestDto;
import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContentMapper {

  ContentResponseDto toDto(Content content);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "reviews", ignore = true)
  Content toEntity(ContentRequestDto dto);
}
