package com.codeit.sb02mplteam2.domain.content.dto.tmdb;

import java.util.List;

public record TmdbTvApiResponseDto(
    List<TmdbTvDto> results
) {
}
