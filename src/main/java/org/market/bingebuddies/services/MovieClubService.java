package org.market.bingebuddies.services;

import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MovieClubService {
    Page<MovieClubDTO> getPublicMovieClubs(Pageable pageable);
    Optional<MovieClubDTO> getMovieClubById(Long id);
    Boolean joinMovieClub(Long movieClubId, Long userId);
    MovieClubDTO createMovieClub(MovieClubDTO movieClubDTO, Long adminId);
    Boolean removeMemberFromClub(Long clubId, Long memberId, Long adminId);
    MovieClubDTO updateMovieClub(Long clubId, MovieClubDTO movieClubDTO);
    void deleteMovieClub(Long clubId, Long adminId);
}
