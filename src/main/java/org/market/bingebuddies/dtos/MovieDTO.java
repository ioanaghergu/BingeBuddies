package org.market.bingebuddies.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.market.bingebuddies.domain.Review;
import org.market.bingebuddies.domain.Watchlist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private Long id;
    private String title;
    private String genre;
    private String ReleaseYear;
    private Double avgRating;
    private List<Review> reviews;
    private Set<Watchlist> watchlists;
}
