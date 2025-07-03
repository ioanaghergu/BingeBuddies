package org.market.bingebuddies.repositories;

import org.market.bingebuddies.domain.MovieClub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieClubRepository extends PagingAndSortingRepository<MovieClub, Long>, JpaRepository<MovieClub, Long> {
    List<MovieClub> findAllBySettingsIsPublic(boolean value);
    Page<MovieClub> findAllBySettingsIsPublic(boolean value, Pageable pageable);
    Optional<MovieClub> findById(Long id);
    Optional<MovieClub> findByNameAndAdminId(String name, Long adminId);
}
