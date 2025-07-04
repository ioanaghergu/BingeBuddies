package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.market.bingebuddies.exceptions.MovieAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieNotFoundException;
import org.market.bingebuddies.mappers.MovieMapper;
import org.market.bingebuddies.mappers.ReviewMapper;
import org.market.bingebuddies.repositories.MovieRepository;
import org.market.bingebuddies.services.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final ReviewMapper reviewMapper;

    public MovieServiceImpl(MovieRepository movieRepository, MovieMapper movieMapper, ReviewMapper reviewMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map(movieMapper::toMovieDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieDTO addMovie(MovieDTO movieDTO) {
        Optional<Movie> movie = movieRepository.findByTitleAndAndReleaseYear(movieDTO.getTitle(), movieDTO.getReleaseYear());

        if (movie.isPresent()) {
            throw new MovieAlreadyExistsException("Movie already exists");
        }

        Movie newMovie = movieMapper.toMovie(movieDTO);
        movieRepository.save(newMovie);
        return movieMapper.toMovieDTO(newMovie);

    }

    @Override
    public Optional<MovieDTO> findMovieById(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);

        if(movie.isEmpty()) {
            throw new MovieNotFoundException("Movie with id " + id + " not found");
        }

        List<Review> reviews = movie.get().getReviews();
        List<ReviewDTO> reviewDTOS = reviews.stream().map(reviewMapper::toReviewDTO).collect(Collectors.toList());

        MovieDTO movieDTO = movieMapper.toMovieDTO(movie.get());
        movieDTO.setReviews(reviewDTOS);

        return Optional.of(movieDTO);
    }

    @Override
    public MovieDTO updateMovie(Long id, MovieDTO movieDTO) {
        Optional<Movie> movieOptional = movieRepository.findById(id);

        if(movieOptional.isEmpty()) {
            throw new MovieNotFoundException("Movie with id " + id + " not found");
        }

        Movie movie = movieOptional.get();

        if(movie.getTitle().equals(movieDTO.getTitle())) {
            Optional<Movie> conflictMovie = movieRepository.findByTitleAndAndReleaseYearAndGenre(movieDTO.getTitle(), movieDTO.getReleaseYear(), movieDTO.getGenre());

            if(conflictMovie.isPresent() && !conflictMovie.get().getId().equals(id)) {
                throw new MovieAlreadyExistsException("Movie already exists");
            }
        }

        movie.setTitle(movieDTO.getTitle());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setGenre(movieDTO.getGenre());

        List<Review> reviews = movie.getReviews();
        List<ReviewDTO> reviewDTOS = reviews.stream().map(reviewMapper::toReviewDTO).collect(Collectors.toList());

        Movie updatedMovie = movieRepository.save(movie);

        MovieDTO updatedMovieDTO = movieMapper.toMovieDTO(updatedMovie);
        movieDTO.setReviews(reviewDTOS);

        System.out.println(updatedMovieDTO);
        return movieMapper.toMovieDTO(updatedMovie);
    }

    @Override
    public void deleteMovie(Long id) {

        Optional<Movie> movieOptional = movieRepository.findById(id);

        if(movieOptional.isEmpty()) {
            throw new MovieNotFoundException("Movie with id " + id + " not found");
        }

        movieOptional.get().getReviews().clear();
        movieRepository.save(movieOptional.get());
        movieRepository.deleteById(id);
    }
}
