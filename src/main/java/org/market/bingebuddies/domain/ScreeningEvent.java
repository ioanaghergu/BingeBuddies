package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class ScreeningEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date requiered for a screening event.")
    @FutureOrPresent(message = "Event must take place in the present or the future.")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "CLUB_ID")
    @NotNull(message = "A screening event can only be created inside a movie club.")
    private MovieClub movieClub;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    @NotNull(message = "A movie must be selected for the screening event.")
    private Movie movie;
}
