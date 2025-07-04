package org.market.bingebuddies.services;

import org.market.bingebuddies.dtos.MovieDTO;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    Optional<MovieDTO> getMovieById(Long id);
    List<MovieDTO> getAllMovies();
    Optional<MovieDTO> findMovieById(Long id);
    MovieDTO addMovie(MovieDTO movieDTO);
    MovieDTO updateMovie(Long id, MovieDTO movieDTO);
    void deleteMovie(Long id);
}
