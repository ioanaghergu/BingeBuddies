package org.market.bingebuddies.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClubSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @lombok.EqualsAndHashCode.Include
    private Long id;

    private Boolean isPublic;

    private Integer maxMembers;

    @OneToOne(mappedBy = "settings")
    @EqualsAndHashCode.Exclude
    private MovieClub movieClub;
}
