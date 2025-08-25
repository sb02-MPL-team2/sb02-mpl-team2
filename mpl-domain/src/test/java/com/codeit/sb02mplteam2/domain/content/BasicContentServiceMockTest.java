//package com.codeit.sb02mplteam2.domain.content;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.anyList;
//import static org.mockito.Mockito.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//
//import com.codeit.sb02mplteam2.domain.content.batch.monitoring.TmdbBatchMetrics;
//import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
//import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
//import com.codeit.sb02mplteam2.domain.content.entity.Content;
//import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
//import com.codeit.sb02mplteam2.domain.content.mapper.TmdbContentMapper;
//import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
//import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
//import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
//import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class BasicContentServiceMockTest {
//
//    @Mock
//    private ContentRepository contentRepository;
//
//    @Mock
//    private TmdbService tmdbService;
//
//    @Mock
//    private TmdbContentMapper tmdbContentMapper;
//
//    @Mock
//    private TmdbBatchMetrics tmdbBatchMetrics;
//
//    @Mock
//    private LiveWatchService liveWatchService;
//
//    @InjectMocks
//    private BasicContentService basicContentService;
//
//    @Test
//    void testSaveTmdbMovies() {
//        // given
//        TmdbMovieDto movieDto = new TmdbMovieDto("제목", "설명", "2025-01-01", "/backdrop.jpg");
//        when(tmdbService.getTmdbMovies(ContentCategory.MOVIE))
//            .thenReturn(List.of(movieDto));
//
//        Content fakeContent = mock(Content.class);
//        when(tmdbContentMapper.toEntity(eq(movieDto), eq(ContentCategory.MOVIE), any(LocalDateTime.class)))
//            .thenReturn(fakeContent);
//
//        when(fakeContent.getId()).thenReturn(1L);
//        when(fakeContent.getTitle()).thenReturn("제목");
//
//        when(contentRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
//
//        // when
//        int saved = basicContentService.saveTmdbMovies(ContentCategory.MOVIE);
//
//        // then
//        assertThat(saved).isEqualTo(1);
//        verify(tmdbService).getTmdbMovies(ContentCategory.MOVIE);
//        verify(tmdbContentMapper).toEntity(eq(movieDto), eq(ContentCategory.MOVIE), any(LocalDateTime.class));
//        verify(contentRepository).saveAll(anyList());
//        verify(tmdbBatchMetrics).recordItemCount(ContentCategory.MOVIE, 1);
//        verifyNoMoreInteractions(tmdbBatchMetrics, tmdbService, tmdbContentMapper, contentRepository);
//    }
//
//    @Test
//    void testSaveTmdbTvs() {
//        // given
//        TmdbTvDto tvDto = new TmdbTvDto("이름", "설명", "2024-12-01", "/backdrop.jpg");
//        when(tmdbService.getTmdbTvs(ContentCategory.TV))
//            .thenReturn(List.of(tvDto));
//
//        Content fakeContent = mock(Content.class);
//        when(tmdbContentMapper.toEntity(eq(tvDto), eq(ContentCategory.TV), any(LocalDateTime.class)))
//            .thenReturn(fakeContent);
//
//        when(fakeContent.getId()).thenReturn(10L);
//        when(fakeContent.getTitle()).thenReturn("이름");
//
//        when(contentRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
//
//        // when
//        int saved = basicContentService.saveTmdbTvs(ContentCategory.TV);
//
//        // then
//        assertThat(saved).isEqualTo(1);
//        verify(tmdbService).getTmdbTvs(ContentCategory.TV);
//        verify(tmdbContentMapper).toEntity(eq(tvDto), eq(ContentCategory.TV), any(LocalDateTime.class));
//        verify(contentRepository).saveAll(anyList());
//        verify(tmdbBatchMetrics).recordItemCount(ContentCategory.TV, 1);
//        verifyNoMoreInteractions(tmdbBatchMetrics, tmdbService, tmdbContentMapper, contentRepository);
//    }
//}