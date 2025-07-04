package org.market.bingebuddies.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.ScreeningEvent;
import org.market.bingebuddies.dtos.ScreeningEventDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-04T16:14:23+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ScreeningEventMapperImpl implements ScreeningEventMapper {

    @Override
    public ScreeningEvent toScreeningEvent(ScreeningEventDTO screeningEventDTO) {
        if ( screeningEventDTO == null ) {
            return null;
        }

        ScreeningEvent screeningEvent = new ScreeningEvent();

        screeningEvent.setId( screeningEventDTO.getId() );
        screeningEvent.setDate( screeningEventDTO.getDate() );

        return screeningEvent;
    }

    @Override
    public ScreeningEventDTO toScreeningEventDTO(ScreeningEvent screeningEvent) {
        if ( screeningEvent == null ) {
            return null;
        }

        ScreeningEventDTO.ScreeningEventDTOBuilder screeningEventDTO = ScreeningEventDTO.builder();

        screeningEventDTO.movieClubId( screeningEventMovieClubId( screeningEvent ) );
        screeningEventDTO.movieId( screeningEventMovieId( screeningEvent ) );
        screeningEventDTO.movieTitle( screeningEventMovieTitle( screeningEvent ) );
        screeningEventDTO.id( screeningEvent.getId() );
        screeningEventDTO.date( screeningEvent.getDate() );

        return screeningEventDTO.build();
    }

    @Override
    public List<ScreeningEventDTO> toScreeningEventDTOList(List<ScreeningEvent> screeningEvents) {
        if ( screeningEvents == null ) {
            return null;
        }

        List<ScreeningEventDTO> list = new ArrayList<ScreeningEventDTO>( screeningEvents.size() );
        for ( ScreeningEvent screeningEvent : screeningEvents ) {
            list.add( toScreeningEventDTO( screeningEvent ) );
        }

        return list;
    }

    private Long screeningEventMovieClubId(ScreeningEvent screeningEvent) {
        MovieClub movieClub = screeningEvent.getMovieClub();
        if ( movieClub == null ) {
            return null;
        }
        return movieClub.getId();
    }

    private Long screeningEventMovieId(ScreeningEvent screeningEvent) {
        Movie movie = screeningEvent.getMovie();
        if ( movie == null ) {
            return null;
        }
        return movie.getId();
    }

    private String screeningEventMovieTitle(ScreeningEvent screeningEvent) {
        Movie movie = screeningEvent.getMovie();
        if ( movie == null ) {
            return null;
        }
        return movie.getTitle();
    }
}
