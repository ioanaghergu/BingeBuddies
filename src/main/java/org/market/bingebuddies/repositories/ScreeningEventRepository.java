package org.market.bingebuddies.repositories;

import org.market.bingebuddies.domain.ScreeningEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface ScreeningEventRepository extends JpaRepository<ScreeningEvent, Long> {
    List<ScreeningEvent> findByMovieClubIdOrderByDateAsc(Long movieClubId);
    List<ScreeningEvent> findByMovieClubIdAndDateAfterOrderByDateAsc(Long movieClubId, LocalDateTime date);
}
