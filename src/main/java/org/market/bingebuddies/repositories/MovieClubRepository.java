package org.market.bingebuddies.repositories;

import org.market.bingebuddies.domain.MovieClub;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieClubRepository extends PagingAndSortingRepository<MovieClub, Long> {
    List<MovieClub> findAllBySettingsIsPublic(boolean value);
}
