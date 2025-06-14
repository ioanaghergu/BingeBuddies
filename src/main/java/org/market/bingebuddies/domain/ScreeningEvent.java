package org.market.bingebuddies.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ScreeningEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "CLUB_ID")
    private MovieClub movieClub;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;
}
