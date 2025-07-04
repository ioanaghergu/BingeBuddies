package org.market.bingebuddies.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.market.bingebuddies.exceptions.*;
import org.market.bingebuddies.mappers.ReviewMapper;
import org.market.bingebuddies.repositories.MovieRepository;
import org.market.bingebuddies.repositories.ReviewRepository;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.market.bingebuddies.services.impl.ReviewServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("h2")
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Movie movie;
    private User user;
    private Review review;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        movie = new Movie();
        movie.setId(1L);
        movie.setAvgRating(0.0);

        review = new Review(1L, 4, "Great movie", user, movie);

        reviewDTO = new ReviewDTO(1L, 4, "Great movie", 1L, 1L, "testUser");
    }

    @Test
    void testAddReview_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByMovieIdAndUserId(1L, 1L)).thenReturn(List.of());
        when(reviewMapper.toReview(any())).thenReturn(review);
        when(reviewRepository.save(any())).thenReturn(review);
        when(reviewMapper.toReviewDTO(any())).thenReturn(reviewDTO);

        ReviewDTO result = reviewService.addReview(1L, 1L, reviewDTO);

        assertEquals("Great movie", result.getComment());
        verify(reviewRepository).save(any());
        verify(reviewMapper).toReviewDTO(any());
    }

    @Test
    void testGetReviewById_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.toReviewDTO(review)).thenReturn(reviewDTO);

        Optional<ReviewDTO> result = reviewService.getReviewById(1L);

        assertTrue(result.isPresent());
        assertEquals(4, result.get().getRating());
    }

    @Test
    void testUpdateReview_Success() {
        Review updatedReview = new Review(1L, 5, "Updated", user, movie);
        ReviewDTO updatedDTO = new ReviewDTO(1L, 5, "Updated", 1L, 1L, "testUser");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(updatedReview);
        when(reviewMapper.toReviewDTO(updatedReview)).thenReturn(updatedDTO);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));


        ReviewDTO result = reviewService.updateReview(1L, 1L, updatedDTO);

        assertEquals("Updated", result.getComment());
        assertEquals(5, result.getRating());
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        reviewService.deleteReview(1L, 1L);

        verify(reviewRepository).deleteById(1L);
        verify(movieRepository).save(any());
    }

    @Test
    void testUpdateMovieRating_WithMultipleReviews() {
        Review review2 = new Review(2L, 5, "Excellent", user, movie);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(reviewRepository.findByMovieId(1L)).thenReturn(List.of(review, review2));

        reviewService.updateMovieRating(1L);

        assertEquals(4.5, movie.getAvgRating());
        verify(movieRepository).save(movie);
    }

    @Test
    void testUpdateMovieRating_WithNoReviews() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(reviewRepository.findByMovieId(1L)).thenReturn(List.of());

        reviewService.updateMovieRating(1L);

        assertEquals(0.0, movie.getAvgRating());
        verify(movieRepository).save(movie);
    }


    @Test
    void testAddReview_MovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        MovieNotFoundException ex = assertThrows(MovieNotFoundException.class,
                () -> reviewService.addReview(1L, 1L, reviewDTO));

        assertEquals("Movie with id 1 not found", ex.getMessage());
    }

    @Test
    void testAddReview_UserNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> reviewService.addReview(1L, 1L, reviewDTO));

        assertEquals("User with id 1 not found", ex.getMessage());
    }

    @Test
    void testAddReview_AlreadyExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByMovieIdAndUserId(1L, 1L)).thenReturn(List.of(review));

        ReviewAlreadyExistsException ex = assertThrows(ReviewAlreadyExistsException.class,
                () -> reviewService.addReview(1L, 1L, reviewDTO));

        assertEquals("user with id 1 has already reviewed movie with id 1", ex.getMessage());
    }

    @Test
    void testGetReviewById_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        ReviewNotFoundException ex = assertThrows(ReviewNotFoundException.class,
                () -> reviewService.getReviewById(1L));

        assertEquals("Review with id 1 not found", ex.getMessage());
    }

    @Test
    void testUpdateReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        ReviewNotFoundException ex = assertThrows(ReviewNotFoundException.class,
                () -> reviewService.updateReview(1L, 1L, reviewDTO));

        assertEquals("Review with id 1 not found", ex.getMessage());
    }

    @Test
    void testUpdateReview_PermissionDenied() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        review.setUser(anotherUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> reviewService.updateReview(1L, 1L, reviewDTO));

        assertEquals("You don't have permission to update this review", ex.getMessage());
    }

    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        ReviewNotFoundException ex = assertThrows(ReviewNotFoundException.class,
                () -> reviewService.deleteReview(1L, 1L));

        assertEquals("Review with id 1 not found", ex.getMessage());
    }

    @Test
    void testDeleteReview_PermissionDenied() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        review.setUser(anotherUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> reviewService.deleteReview(1L, 1L));

        assertEquals("You don't have permission to update this review", ex.getMessage());
    }

    @Test
    void testUpdateMovieRating_MovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        MovieNotFoundException ex = assertThrows(MovieNotFoundException.class,
                () -> reviewService.updateMovieRating(1L));

        assertEquals("Movie with id 1 not found", ex.getMessage());
    }
}