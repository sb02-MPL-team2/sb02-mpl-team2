package com.codeit.sb02mplteam2.domain.content;

import static org.mockito.Mockito.*;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.mapper.TmdbContentMapper;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicContentServiceMockTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private TmdbService tmdbService;

    @Mock
    private TmdbContentMapper tmdbContentMapper;

    @InjectMocks
    private BasicContentService basicContentService;

    @Test
    void testSaveTmdbMovies() {
        // given
        TmdbMovieDto movieDto = new TmdbMovieDto("제목", "설명", "2025-01-01", "/backdrop.jpg");
        when(tmdbService.getTmdbMovies(ContentCategory.MOVIE))
            .thenReturn(List.of(movieDto));

        LocalDateTime now = LocalDateTime.now();

        Content fakeContent = mock(Content.class);
        when(tmdbContentMapper.toEntity(movieDto, ContentCategory.MOVIE, now))
            .thenReturn(fakeContent);

        // when
        basicContentService.saveTmdbMovies(ContentCategory.MOVIE);

        // then
        verify(tmdbService, times(1)).getTmdbMovies(ContentCategory.MOVIE);
        verify(tmdbContentMapper, times(1))
            .toEntity(movieDto, ContentCategory.MOVIE, now);
        verify(contentRepository, times(1))
            .saveAll(List.of(fakeContent));
    }

    @Test
    void testSaveTmdbTvs() {
        // given
        TmdbTvDto tvDto = new TmdbTvDto("이름", "설명", "2024-12-01", "/backdrop.jpg");
        when(tmdbService.getTmdbTvs(ContentCategory.TV))
            .thenReturn(List.of(tvDto));

        LocalDateTime now = LocalDateTime.now();

        Content fakeContent = mock(Content.class);
        when(tmdbContentMapper.toEntity(tvDto, ContentCategory.TV, now))
            .thenReturn(fakeContent);

        // when
        basicContentService.saveTmdbTvs(ContentCategory.TV);

        // then
        verify(tmdbService, times(1)).getTmdbTvs(ContentCategory.TV);
        verify(tmdbContentMapper, times(1))
            .toEntity(tvDto, ContentCategory.TV, now);
        verify(contentRepository, times(1))
            .saveAll(List.of(fakeContent));
    }
}