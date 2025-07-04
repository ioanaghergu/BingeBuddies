package org.market.bingebuddies.mappers;

import javax.annotation.processing.Generated;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-04T21:19:14+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review toReview(ReviewDTO reviewDTO) {
        if ( reviewDTO == null ) {
            return null;
        }

        Review review = new Review();

        review.setId( reviewDTO.getId() );
        review.setRating( reviewDTO.getRating() );
        review.setComment( reviewDTO.getComment() );

        return review;
    }

    @Override
    public ReviewDTO toReviewDTO(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDTO.ReviewDTOBuilder reviewDTO = ReviewDTO.builder();

        reviewDTO.userId( reviewUserId( review ) );
        reviewDTO.username( reviewUserUsername( review ) );
        reviewDTO.movieId( reviewMovieId( review ) );
        reviewDTO.id( review.getId() );
        reviewDTO.rating( review.getRating() );
        reviewDTO.comment( review.getComment() );

        return reviewDTO.build();
    }

    private Long reviewUserId(Review review) {
        User user = review.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String reviewUserUsername(Review review) {
        User user = review.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getUsername();
    }

    private Long reviewMovieId(Review review) {
        Movie movie = review.getMovie();
        if ( movie == null ) {
            return null;
        }
        return movie.getId();
    }
}
