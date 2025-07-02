package org.market.bingebuddies.mappers;

import org.mapstruct.Mapper;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.dtos.MovieClubDTO;

@Mapper(componentModel = "spring")
public interface MovieClubMapper {
    MovieClub toMovieClub(MovieClubDTO movieClubDTO);
    MovieClubDTO toMovieClubDTO(MovieClub movieClub);
}
