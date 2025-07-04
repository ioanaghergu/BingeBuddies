package org.market.bingebuddies.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.market.bingebuddies.exceptions.MovieAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieNotFoundException;
import org.market.bingebuddies.mappers.MovieMapper;
import org.market.bingebuddies.mappers.ReviewMapper;
import org.market.bingebuddies.repositories.MovieRepository;
import org.market.bingebuddies.services.impl.MovieServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("h2")
@ExtendWith(MockitoExtension.class)
public class MovieServiceTests {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;
    private MovieDTO movieDTO;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .releaseYear(2010)
                .avgRating(4.8)
                .reviews(new ArrayList<>())
                .build();

        movieDTO = MovieDTO.builder()
                .id(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .releaseYear(2010)
                .avgRating(4.8)
                .reviews(new ArrayList<>())
                .build();
    }

    @Test
    void testGetAllMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(movieMapper.toMovieDTO(movie)).thenReturn(movieDTO);

        List<MovieDTO> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void testGetMovieById_Found() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.toMovieDTO(movie)).thenReturn(movieDTO);

        Optional<MovieDTO> result = movieService.getMovieById(1L);

        assertTrue(result.isPresent());
        assertEquals("Inception", result.get().getTitle());
    }

    @Test
    void testGetMovieById_NotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<MovieDTO> result = movieService.getMovieById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testAddMovie_Success() {
        when(movieRepository.findByTitleAndAndReleaseYear("Inception", 2010)).thenReturn(Optional.empty());
        when(movieMapper.toMovie(movieDTO)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieMapper.toMovieDTO(movie)).thenReturn(movieDTO);

        MovieDTO result = movieService.addMovie(movieDTO);

        assertEquals("Inception", result.getTitle());
    }

    @Test
    void testAddMovie_AlreadyExists() {
        when(movieRepository.findByTitleAndAndReleaseYear("Inception", 2010)).thenReturn(Optional.of(movie));

        assertThrows(MovieAlreadyExistsException.class, () -> movieService.addMovie(movieDTO));
    }

    @Test
    void testFindMovieById_Found() {
        Review review = new Review();
        List<Review> reviews = List.of(review);
        movie.setReviews(reviews);

        ReviewDTO reviewDTO = new ReviewDTO();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(reviewMapper.toReviewDTO(review)).thenReturn(reviewDTO);
        when(movieMapper.toMovieDTO(movie)).thenReturn(movieDTO);

        Optional<MovieDTO> result = movieService.findMovieById(1L);

        assertTrue(result.isPresent());
        assertEquals("Inception", result.get().getTitle());
        assertEquals(1, result.get().getReviews().size());
    }

    @Test
    void testFindMovieById_NotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.findMovieById(1L));
    }

    @Test
    void testUpdateMovie_Success() {
        MovieDTO updatedDTO = movieDTO.builder().title("New Title").build();
        Movie updatedMovie = movie.builder().title("New Title").build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
        when(movieMapper.toMovieDTO(any(Movie.class))).thenReturn(updatedDTO);

        MovieDTO result = movieService.updateMovie(1L, updatedDTO);

        assertEquals("New Title", result.getTitle());
    }


    @Test
    void testUpdateMovie_NotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(1L, movieDTO));
    }

    @Test
    void testDeleteMovie_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        verify(movieRepository).save(movie);
        verify(movieRepository).deleteById(1L);
    }

    @Test
    void testDeleteMovie_NotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(1L));
    }

}
