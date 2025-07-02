package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import lombok.*;
import org.market.bingebuddies.domain.security.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovieClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Long adminId;

    private String name;

    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SETTINGS_ID", referencedColumnName = "ID")
    @EqualsAndHashCode.Exclude
    private ClubSettings settings;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "club_members",
            joinColumns = { @JoinColumn(name = "CLUB_ID", referencedColumnName = "ID")},
            inverseJoinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    @EqualsAndHashCode.Exclude
    private Set<User> members = new HashSet<User>();

    @OneToMany(mappedBy = "movieClub", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Watchlist> watchlists;

    @OneToMany(mappedBy = "movieClub", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<ScreeningEvent> events;
}
