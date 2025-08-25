//package com.codeit.sb02mplteam2.domain.content.mapper;
//
//import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
//import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieResponseDto;
//import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
//import com.codeit.sb02mplteam2.domain.content.entity.Content;
//import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
//import java.time.LocalDateTime;
//import org.mapstruct.Mapper;
//
//@Mapper(componentModel = "spring")
//public interface TmdbContentMapper {
//
//  String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
//
//  default TmdbMovieResponseDto toDto(TmdbMovieDto dto) {
//    return new TmdbMovieResponseDto(
//        dto.title(),
//        dto.overview(),
//        dto.release_date(),
//        fullImageUrl(dto.backdrop_path(), "w1280")
//    );
//  }
//
//  default Content toEntity(TmdbMovieDto dto, ContentCategory category, LocalDateTime now) {
//    String imageUrl = fullImageUrl(dto.backdrop_path(), "w1280");
//    return new Content(
//        dto.title(),
//        dto.overview(),
//        category,
//        imageUrl,
//        now
//    );
//  }
//
//  default Content toEntity(TmdbTvDto dto, ContentCategory category, LocalDateTime now) {
//    String imageUrl = fullImageUrl(dto.backdrop_path(), "w1280");
//    return new Content(
//        dto.name(),
//        dto.overview(),
//        category,
//        imageUrl,
//        now
//    );
//  }
//
//  default String fullImageUrl(String path, String size) {
//    if (path == null || path.isBlank()) {
//      return null;
//    }
//    return IMAGE_BASE_URL + size + path;
//  }
//}
