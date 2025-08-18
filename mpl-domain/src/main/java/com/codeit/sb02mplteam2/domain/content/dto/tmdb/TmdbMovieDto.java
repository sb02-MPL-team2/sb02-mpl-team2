package com.codeit.sb02mplteam2.domain.content.dto.tmdb;

public record TmdbMovieDto (
    String title,
    String overview,
    String release_date,
    String backdrop_path
) {
}
