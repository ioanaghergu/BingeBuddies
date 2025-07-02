package org.market.bingebuddies.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubSettingsDTO {
    private Long id;
    private Boolean isPublic;
    private Integer maxMembers;
}
