package org.market.bingebuddies.services;

import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.dtos.MovieClubDTO;

import java.util.List;
import java.util.Optional;

public interface MovieClubService {
    List<MovieClubDTO> getPublicMovieClubs();
    Optional<MovieClubDTO> getMovieClubById(Long id);
    Boolean joinMovieClub(Long movieClubId, Long userId);
    MovieClub save(MovieClub movieClub);
    MovieClubDTO createMovieClub(MovieClubDTO movieClubDTO, Long adminId);
}
