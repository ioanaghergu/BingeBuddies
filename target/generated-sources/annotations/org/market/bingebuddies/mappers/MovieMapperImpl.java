package org.market.bingebuddies.mappers;

import javax.annotation.processing.Generated;
import org.market.bingebuddies.domain.Movie;
import org.market.bingebuddies.dtos.MovieDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-03T16:50:40+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 23.0.2 (Azul Systems, Inc.)"
)
@Component
public class MovieMapperImpl implements MovieMapper {

    @Override
    public Movie toMovie(MovieDTO movieDTO) {
        if ( movieDTO == null ) {
            return null;
        }

        Movie movie = new Movie();

        movie.setId( movieDTO.getId() );
        movie.setTitle( movieDTO.getTitle() );
        movie.setGenre( movieDTO.getGenre() );
        movie.setReleaseYear( movieDTO.getReleaseYear() );
        movie.setAvgRating( movieDTO.getAvgRating() );

        return movie;
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

        return movieDTO.build();
    }
}
