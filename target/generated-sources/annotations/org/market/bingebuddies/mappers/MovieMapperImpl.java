package org.market.bingebuddies.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-04T21:19:14+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class MovieMapperImpl implements MovieMapper {

    @Override
    public Movie toMovie(MovieDTO movieDTO) {
        if ( movieDTO == null ) {
            return null;
        }

        Movie.MovieBuilder movie = Movie.builder();

        movie.id( movieDTO.getId() );
        movie.title( movieDTO.getTitle() );
        movie.genre( movieDTO.getGenre() );
        movie.releaseYear( movieDTO.getReleaseYear() );
        movie.avgRating( movieDTO.getAvgRating() );
        movie.reviews( reviewDTOListToReviewList( movieDTO.getReviews() ) );

        return movie.build();
    }

    @Override
    public MovieDTO toMovieDTO(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieDTO.MovieDTOBuilder movieDTO = MovieDTO.builder();

        movieDTO.id( movie.getId() );
        movieDTO.title( movie.getTitle() );
        movieDTO.genre( movie.getGenre() );
        movieDTO.releaseYear( movie.getReleaseYear() );
        movieDTO.avgRating( movie.getAvgRating() );
        movieDTO.reviews( reviewListToReviewDTOList( movie.getReviews() ) );

        return movieDTO.build();
    }

    protected Review reviewDTOToReview(ReviewDTO reviewDTO) {
        if ( reviewDTO == null ) {
            return null;
        }

        Review review = new Review();

        review.setId( reviewDTO.getId() );
        review.setRating( reviewDTO.getRating() );
        review.setComment( reviewDTO.getComment() );

        return review;
    }

    protected List<Review> reviewDTOListToReviewList(List<ReviewDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Review> list1 = new ArrayList<Review>( list.size() );
        for ( ReviewDTO reviewDTO : list ) {
            list1.add( reviewDTOToReview( reviewDTO ) );
        }

        return list1;
    }

    protected ReviewDTO reviewToReviewDTO(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDTO.ReviewDTOBuilder reviewDTO = ReviewDTO.builder();

        reviewDTO.id( review.getId() );
        reviewDTO.rating( review.getRating() );
        reviewDTO.comment( review.getComment() );

        return reviewDTO.build();
    }

    protected List<ReviewDTO> reviewListToReviewDTOList(List<Review> list) {
        if ( list == null ) {
            return null;
        }

        List<ReviewDTO> list1 = new ArrayList<ReviewDTO>( list.size() );
        for ( Review review : list ) {
            list1.add( reviewToReviewDTO( review ) );
        }

        return list1;
    }
}
