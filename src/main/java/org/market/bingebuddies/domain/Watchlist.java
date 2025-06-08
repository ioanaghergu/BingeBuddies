package org.market.bingebuddies.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "CLUB_ID", referencedColumnName = "ID")
    private MovieClub movieClub;

    @ManyToMany
    @JoinTable(name = "watchlist_movie",
            joinColumns = { @JoinColumn(name = "WATCHLIST_ID", referencedColumnName = "ID")},
            inverseJoinColumns = { @JoinColumn(name = "MOVIE_ID", referencedColumnName = "ID")})
    private Set<Movie> movies = new HashSet<Movie>();
}
