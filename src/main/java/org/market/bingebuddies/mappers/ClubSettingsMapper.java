package org.market.bingebuddies.mappers;

import org.mapstruct.Mapper;
import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.dtos.ClubSettingsDTO;

@Mapper(componentModel = "spring")
public interface ClubSettingsMapper {
    ClubSettings toClubSettings(ClubSettingsDTO clubSettingsDTO);
    ClubSettingsDTO toClubSettingsDTO(ClubSettings clubSettings);
}
