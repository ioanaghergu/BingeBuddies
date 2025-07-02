package org.market.bingebuddies.domain.security;

import jakarta.persistence.*;
import lombok.*;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.Review;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = { @JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @ManyToMany(mappedBy = "members")
    private Set<MovieClub> clubs = new HashSet<>();

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;
}
