package org.market.bingebuddies.services.impl;

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
import org.market.bingebuddies.services.ScreeningEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScreeningEventServiceImpl implements ScreeningEventService {

    private final ScreeningEventRepository screeningEventRepository;
    private final ScreeningEventMapper screeningEventMapper;
    private final MovieClubRepository movieClubRepository;
    private final MovieRepository movieRepository;

    public ScreeningEventServiceImpl(ScreeningEventRepository screeningEventRepository, ScreeningEventMapper screeningEventMapper, MovieClubRepository movieClubRepository, MovieRepository movieRepository){
        this.screeningEventRepository = screeningEventRepository;
        this.screeningEventMapper = screeningEventMapper;
        this.movieClubRepository = movieClubRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public ScreeningEventDTO addScreeningEvent(Long clubId, ScreeningEventDTO screeningEventDTO) {
        Optional<MovieClub> club = movieClubRepository.findById(clubId);
        Optional<Movie> movie = movieRepository.findById(screeningEventDTO.getMovieId());

        if(club.isEmpty()){
            throw new MovieClubNotFoundException("Movie with id " + clubId + " not found");
        }

        if(movie.isEmpty()){
            throw new MovieNotFoundException("Movie with id " + screeningEventDTO.getMovieId() + " not found");
        }

        ScreeningEvent event = screeningEventMapper.toScreeningEvent(screeningEventDTO);
        event.setMovieClub(club.get());
        event.setMovie(movie.get());

        ScreeningEvent savedEvent = screeningEventRepository.save(event);

        return screeningEventMapper.toScreeningEventDTO(savedEvent);
    }

    @Override
    public List<ScreeningEventDTO> getUpcomingScreeningEventsForClub(Long clubId) {
        List<ScreeningEvent> events = screeningEventRepository.findByMovieClubIdAndDateAfterOrderByDateAsc(clubId, LocalDateTime.now());

        return events.stream().map(screeningEventMapper::toScreeningEventDTO).collect(Collectors.toList());

    }


}
