package com.codeit.sb02mplteam2.domain.content.dto.tmdb;

public record TmdbTvDto(
    Long id,
    String name,
    String overview,
    String first_air_date,
    String backdrop_path,
    Double popularity
    ) {
}
