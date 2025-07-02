package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isPublic;

    private Integer maxMembers;

    @OneToOne(mappedBy = "settings")
    private MovieClub movieClub;
}
