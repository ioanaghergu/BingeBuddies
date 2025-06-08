package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import lombok.Singular;
import org.market.bingebuddies.domain.security.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class MovieClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SETTINGS_ID", referencedColumnName = "ID")
    private ClubSettings settings;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "club_members",
            joinColumns = { @JoinColumn(name = "CLUB_ID", referencedColumnName = "ID")},
            inverseJoinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private Set<User> members = new HashSet<User>();

    @OneToMany(mappedBy = "movieClub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Watchlist> watchlists;

    @OneToMany(mappedBy = "movieClub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScreeningEvent> events;
}
