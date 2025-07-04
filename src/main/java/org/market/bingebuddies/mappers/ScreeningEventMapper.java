package org.market.bingebuddies.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.market.bingebuddies.domain.ScreeningEvent;
import org.market.bingebuddies.dtos.ScreeningEventDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScreeningEventMapper {

    @Mapping(target = "movieClub", ignore = true)
    @Mapping(target = "movie", ignore = true)
    ScreeningEvent toScreeningEvent(ScreeningEventDTO screeningEventDTO);

    @Mapping(source = "movieClub.id", target = "movieClubId")
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    ScreeningEventDTO toScreeningEventDTO(ScreeningEvent screeningEvent);

    List<ScreeningEventDTO> toScreeningEventDTOList(List<ScreeningEvent> screeningEvents);
}
