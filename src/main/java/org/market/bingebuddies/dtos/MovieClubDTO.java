package org.market.bingebuddies.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.domain.ScreeningEvent;
import org.market.bingebuddies.domain.Watchlist;
import org.market.bingebuddies.domain.security.User;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieClubDTO {
    private Long id;
    private Long adminId;
    private String name;
    private String description;
    private ClubSettingsDTO settings;
    private Set<UserDTO> members;
    private List<Watchlist> watchlists;
    private List<ScreeningEvent> events;
}
