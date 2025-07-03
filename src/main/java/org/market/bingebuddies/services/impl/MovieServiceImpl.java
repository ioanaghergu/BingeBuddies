package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.exceptions.MovieAlreadyExistsException;
import org.market.bingebuddies.mappers.MovieMapper;
import org.market.bingebuddies.repositories.MovieRepository;
import org.market.bingebuddies.services.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map(movieMapper::toMovieDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieDTO addMovie(MovieDTO movieDTO) {
        Optional<Movie> movie = movieRepository.findByTitleAndAndReleaseYear(movieDTO.getTitle(), movieDTO.getReleaseYear());

        if (movie.isPresent()) {
            throw new MovieAlreadyExistsException("Movie already exists");
        }

        Movie newMovie = movieMapper.toMovie(movieDTO);
        movieRepository.save(newMovie);
        return movieMapper.toMovieDTO(newMovie);

    }
}
