package org.market.bingebuddies.unitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.ScreeningEvent;
import org.market.bingebuddies.dtos.ScreeningEventDTO;
import org.market.bingebuddies.exceptions.MovieClubNotFoundException;
import org.market.bingebuddies.exceptions.MovieNotFoundException;
import org.market.bingebuddies.mappers.ScreeningEventMapper;
import org.market.bingebuddies.repositories.MovieClubRepository;
import org.market.bingebuddies.repositories.MovieRepository;
import org.market.bingebuddies.repositories.ScreeningEventRepository;
import org.market.bingebuddies.services.impl.ScreeningEventServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("h2")
@ExtendWith(MockitoExtension.class)
public class EventServiceTests {
    @Mock
    private ScreeningEventRepository screeningEventRepository;

    @Mock
    private ScreeningEventMapper screeningEventMapper;

    @Mock
    private MovieClubRepository movieClubRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ScreeningEventServiceImpl screeningEventService;

    @Test
    void testAddScreeningEvent_success() {
        Long clubId = 1L;
        Long movieId = 2L;

        MovieClub club = new MovieClub();
        club.setId(clubId);

        Movie movie = new Movie();
        movie.setId(movieId);

        ScreeningEventDTO dto = ScreeningEventDTO.builder()
                .movieClubId(clubId)
                .movieId(movieId)
                .date(LocalDateTime.now().plusDays(1))
                .build();

        ScreeningEvent event = new ScreeningEvent();
        ScreeningEvent savedEvent = new ScreeningEvent();
        ScreeningEventDTO returnedDTO = new ScreeningEventDTO();

        when(movieClubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(screeningEventMapper.toScreeningEvent(dto)).thenReturn(event);
        when(screeningEventRepository.save(event)).thenReturn(savedEvent);
        when(screeningEventMapper.toScreeningEventDTO(savedEvent)).thenReturn(returnedDTO);

        ScreeningEventDTO result = screeningEventService.addScreeningEvent(clubId, dto);

        assertEquals(returnedDTO, result);
        verify(screeningEventRepository).save(event);
    }

    @Test
    void testAddScreeningEvent_movieClubNotFound() {
        Long clubId = 1L;
        ScreeningEventDTO dto = ScreeningEventDTO.builder()
                .movieClubId(clubId)
                .movieId(2L)
                .build();

        when(movieClubRepository.findById(clubId)).thenReturn(Optional.empty());

        assertThrows(MovieClubNotFoundException.class, () ->
                screeningEventService.addScreeningEvent(clubId, dto));
    }

    @Test
    void testAddScreeningEvent_movieNotFound() {
        Long clubId = 1L;
        Long movieId = 2L;

        ScreeningEventDTO dto = ScreeningEventDTO.builder()
                .movieClubId(clubId)
                .movieId(movieId)
                .build();

        when(movieClubRepository.findById(clubId)).thenReturn(Optional.of(new MovieClub()));
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () ->
                screeningEventService.addScreeningEvent(clubId, dto));
    }

    @Test
    void testGetUpcomingScreeningEventsForClub() {
        Long clubId = 1L;

        ScreeningEvent event = new ScreeningEvent();
        ScreeningEventDTO dto = new ScreeningEventDTO();

        when(screeningEventRepository.findByMovieClubIdAndDateAfterOrderByDateAsc(eq(clubId), any(LocalDateTime.class)))
                .thenReturn(List.of(event));
        when(screeningEventMapper.toScreeningEventDTO(event)).thenReturn(dto);

        List<ScreeningEventDTO> result = screeningEventService.getUpcomingScreeningEventsForClub(clubId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }
}
