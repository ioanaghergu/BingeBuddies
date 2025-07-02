package org.market.bingebuddies.services;

import org.market.bingebuddies.dtos.MovieClubDTO;

import java.util.List;

public interface MovieClubService {
    List<MovieClubDTO> getPublicMovieClubs();
}
