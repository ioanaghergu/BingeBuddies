package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import org.market.bingebuddies.domain.security.User;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;
}
