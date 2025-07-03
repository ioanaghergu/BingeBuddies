package org.market.bingebuddies.repositories;

import org.market.bingebuddies.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends PagingAndSortingRepository<Movie, Long>, JpaRepository<Movie, Long> {
    Optional<Movie> findByTitleAndAndReleaseYear(String title, Integer releaseYear);
}
