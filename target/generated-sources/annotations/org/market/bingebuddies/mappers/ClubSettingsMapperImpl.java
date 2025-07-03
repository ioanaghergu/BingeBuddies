package org.market.bingebuddies.mappers;

import javax.annotation.processing.Generated;
import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.dtos.ClubSettingsDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-03T15:46:24+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ClubSettingsMapperImpl implements ClubSettingsMapper {

    @Override
    public ClubSettings toClubSettings(ClubSettingsDTO clubSettingsDTO) {
        if ( clubSettingsDTO == null ) {
            return null;
        }

        ClubSettings clubSettings = new ClubSettings();

        clubSettings.setId( clubSettingsDTO.getId() );
        clubSettings.setIsPublic( clubSettingsDTO.getIsPublic() );
        clubSettings.setMaxMembers( clubSettingsDTO.getMaxMembers() );

        return clubSettings;
    }

    @Override
    public ClubSettingsDTO toClubSettingsDTO(ClubSettings clubSettings) {
        if ( clubSettings == null ) {
            return null;
        }

        ClubSettingsDTO clubSettingsDTO = new ClubSettingsDTO();

        clubSettingsDTO.setId( clubSettings.getId() );
        clubSettingsDTO.setIsPublic( clubSettings.getIsPublic() );
        clubSettingsDTO.setMaxMembers( clubSettings.getMaxMembers() );

        return clubSettingsDTO;
    }
}
