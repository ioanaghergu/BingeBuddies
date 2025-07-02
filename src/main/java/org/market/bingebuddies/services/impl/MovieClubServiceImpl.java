package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.exceptions.MovieClubNotFoundException;
import org.market.bingebuddies.mappers.MovieClubMapper;
import org.market.bingebuddies.repositories.MovieClubRepository;
import org.market.bingebuddies.services.MovieClubService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieClubServiceImpl implements MovieClubService {

    private final MovieClubRepository movieClubRepository;
    private final MovieClubMapper movieClubMapper;

    public MovieClubServiceImpl(MovieClubRepository movieClubRepository, MovieClubMapper movieClubMapper) {
        this.movieClubRepository = movieClubRepository;
        this.movieClubMapper = movieClubMapper;
    }

    @Override
    public List<MovieClubDTO> getPublicMovieClubs() {
        List<MovieClub> movieClubs = movieClubRepository.findAllBySettingsIsPublic(true);

        return movieClubs.stream().map(movieClubMapper::toMovieClubDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<MovieClubDTO> getMovieClubById(Long id) {
        Optional<MovieClub> movieClub = movieClubRepository.findById(id);

        if(movieClub.isEmpty()) {
            throw new MovieClubNotFoundException("Club with id " + id + " not found");
        }

        return Optional.of(movieClubMapper.toMovieClubDTO(movieClub.get()));
    }
}
