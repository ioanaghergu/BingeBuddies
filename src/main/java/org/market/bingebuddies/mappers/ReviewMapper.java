package org.market.bingebuddies.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.dtos.ReviewDTO;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    Review toReview(ReviewDTO reviewDTO);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "movie.id", target = "movieId")
    ReviewDTO toReviewDTO(Review review);
}
