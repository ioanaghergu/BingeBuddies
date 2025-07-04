package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.market.bingebuddies.exceptions.*;
import org.market.bingebuddies.mappers.ReviewMapper;
import org.market.bingebuddies.repositories.MovieRepository;
import org.market.bingebuddies.repositories.ReviewRepository;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.market.bingebuddies.services.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, MovieRepository movieRepository, UserRepository userRepository, ReviewMapper reviewMapper){
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ReviewDTO addReview(Long movieId, Long userId, ReviewDTO reviewDTO) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        Optional<User> user = userRepository.findById(userId);

        if(movie.isEmpty()){
            throw new MovieNotFoundException("Movie with id " + movieId + " not found");
        }

        if(user.isEmpty()){
            throw new UserNotFoundException("User with id " + userId + " not found");
        }

        if(!reviewRepository.findByMovieIdAndUserId(movieId, userId).isEmpty()) {
            throw new ReviewAlreadyExistsException("user with id " + userId + " has already reviewed movie with id " + movieId);
        }

        Review review = reviewMapper.toReview(reviewDTO);
        review.setUser(user.get());
        review.setMovie(movie.get());

        Review savedReview = reviewRepository.save(review);
        updateMovieRating(movieId);

        return reviewMapper.toReviewDTO(savedReview);
    }

    @Override
    public Optional<ReviewDTO> getReviewById(Long id){
        Optional<Review> review = reviewRepository.findById(id);

        if(review.isEmpty()){
            throw new ReviewNotFoundException("Review with id " + id + " not found");
        }

        return review.map(reviewMapper::toReviewDTO);
    }

    @Override
    @Transactional
    public   ReviewDTO updateReview(Long id, Long userId, ReviewDTO reviewDTO){
        Optional<Review> review = reviewRepository.findById(id);

        if(review.isEmpty()){
            throw new ReviewNotFoundException("Review with id " + id + " not found");
        }

        if(!review.get().getUser().getId().equals(userId)){
            throw new PermissionDeniedException("You don't have permission to update this review");
        }

        Review existingReview = review.get();
        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());

        Review updatedReview = reviewRepository.save(existingReview);
        updateMovieRating(updatedReview.getMovie().getId());

        return reviewMapper.toReviewDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Optional<Review> review = reviewRepository.findById(reviewId);

        if(review.isEmpty()){
            throw new ReviewNotFoundException("Review with id " + reviewId + " not found");
        }

        if(!review.get().getUser().getId().equals(userId)){
            throw new PermissionDeniedException("You don't have permission to update this review");
        }

        Long movieId = review.get().getMovie().getId();
        reviewRepository.deleteById(reviewId);
        updateMovieRating(movieId);
    }

    @Override
    @Transactional
    public void updateMovieRating(Long movieId){
        Optional<Movie> movie = movieRepository.findById(movieId);

        if(movie.isEmpty()){
            throw new MovieNotFoundException("Movie with id " + movieId + " not found");
        }

        List<Review> reviews = reviewRepository.findByMovieId(movieId);

        if(reviews.isEmpty()){
            movie.get().setAvgRating(0.0);
        }
        else {
            Integer sumRating = reviews.stream().mapToInt(Review::getRating).sum();
            double avgRating = (double) sumRating / reviews.size();

            movie.get().setAvgRating(Math.round(avgRating * 10.0) / 10.0);
        }

        movieRepository.save(movie.get());
    }
}

