package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name required for the watchlist.")
    @Size(min = 3, max = 20, message = "A watchlist name must be between 3 and 20 characters.")
    private String name;

    @ManyToOne
    @JoinColumn(name = "CLUB_ID", referencedColumnName = "ID")
    @NotNull(message = "A watchlist must be tied to a movie club.")
    private MovieClub movieClub;

    @ManyToMany
    @JoinTable(name = "watchlist_movie",
            joinColumns = { @JoinColumn(name = "WATCHLIST_ID", referencedColumnName = "ID")},
            inverseJoinColumns = { @JoinColumn(name = "MOVIE_ID", referencedColumnName = "ID")})
    private Set<Movie> movies = new HashSet<Movie>();
}
