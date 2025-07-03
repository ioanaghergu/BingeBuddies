package org.market.bingebuddies.mappers;

import org.mapstruct.Mapper;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.dtos.MovieDTO;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    Movie toMovie(MovieDTO movieDTO);
    MovieDTO toMovieDTO(Movie movie);
}
