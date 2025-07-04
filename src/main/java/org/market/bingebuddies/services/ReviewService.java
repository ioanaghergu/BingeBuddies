package org.market.bingebuddies.services;

import org.market.bingebuddies.dtos.ReviewDTO;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewDTO addReview(Long movieId, Long userId, ReviewDTO reviewDTO);
    Optional<ReviewDTO> getReviewById(Long id);
    ReviewDTO updateReview(Long id,Long userId, ReviewDTO reviewDTO);
    void deleteReview(Long reviewId, Long userId);
    void updateMovieRating(Long movieId);
}
