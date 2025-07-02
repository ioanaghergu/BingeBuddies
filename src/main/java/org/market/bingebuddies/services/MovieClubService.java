package org.market.bingebuddies.services;

import org.market.bingebuddies.dtos.MovieClubDTO;

import java.util.List;
import java.util.Optional;

public interface MovieClubService {
    List<MovieClubDTO> getPublicMovieClubs();
    Optional<MovieClubDTO> getMovieClubById(Long id);
}
