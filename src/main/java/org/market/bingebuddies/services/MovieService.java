package org.market.bingebuddies.services;

import org.market.bingebuddies.dtos.MovieDTO;

import java.util.List;

public interface MovieService {
    List<MovieDTO> getAllMovies();
    MovieDTO addMovie(MovieDTO movieDTO);
}
