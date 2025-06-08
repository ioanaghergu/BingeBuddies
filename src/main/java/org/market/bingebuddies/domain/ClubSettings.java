package org.market.bingebuddies.domain;

import jakarta.persistence.*;

@Entity
public class ClubSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isPublic;

    private Integer maxMembers;

    @OneToOne(mappedBy = "settings")
    private MovieClub movieClub;
}
