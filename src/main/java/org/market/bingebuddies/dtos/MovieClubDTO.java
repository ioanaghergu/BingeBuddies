package org.market.bingebuddies.dtos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.domain.ScreeningEvent;
import org.market.bingebuddies.domain.Watchlist;
import org.market.bingebuddies.domain.security.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieClubDTO {
    private Long id;

    //@NotNull(message = "A movie club must have an admin.")
    private Long adminId;

    @NotBlank(message = "Club name required")
    @Size(min = 3, max = 100, message = "Club name must be between 3 and 100 characters.")
    private String name;

    @Size(max = 100, message = "Club description must not exceed 100 characters.")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SETTINGS_ID", referencedColumnName = "ID")
    @EqualsAndHashCode.Exclude
    @NotNull(message = "Club settings required.")
    @Valid
    private ClubSettingsDTO settings;

    private Set<UserDTO> members = new HashSet<>();
    private List<Watchlist> watchlists;
    private List<ScreeningEvent> events;
}
