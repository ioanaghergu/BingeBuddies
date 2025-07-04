package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Movie title required.")
    @Size(max = 255, message = "Title must not exceed 255 characters.")
    private String title;

    @NotBlank(message = "Movie genre required.")
    @Size(max = 255, message = "Genre must not exceed 255 characters.")
    private String genre;

    @NotNull(message = "Release year required.")
    @Min(value = 2000, message = "Release year must be after 2000.")
    @Max(value = 2030, message = "Release year can't be in the distant future")
    private Integer releaseYear;

    @Min(value = 0, message = "Movie rating can't be negative.")
    @Max(value = 5, message = "Movie rating can't exceed 5 stars.")
    private Double avgRating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Review> reviews;

    @ManyToMany(mappedBy = "movies")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Watchlist> watchlists = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ScreeningEvent> screeningEvents;

}
