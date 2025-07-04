package org.market.bingebuddies.repositories;

import org.market.bingebuddies.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(Long movieId);
    List<Review> findByMovieIdAndUserId(Long movieId, Long userId);
}
