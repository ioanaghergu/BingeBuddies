package org.market.bingebuddies.mappers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.ScreeningEvent;
import org.market.bingebuddies.domain.Watchlist;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ClubSettingsDTO;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.dtos.UserDTO;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-03T16:28:11+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class MovieClubMapperImpl implements MovieClubMapper {

    @Override
    public MovieClub toMovieClub(MovieClubDTO movieClubDTO) {
        if ( movieClubDTO == null ) {
            return null;
        }

        MovieClub movieClub = new MovieClub();

        movieClub.setId( movieClubDTO.getId() );
        movieClub.setAdminId( movieClubDTO.getAdminId() );
        movieClub.setName( movieClubDTO.getName() );
        movieClub.setDescription( movieClubDTO.getDescription() );
        movieClub.setSettings( clubSettingsDTOToClubSettings( movieClubDTO.getSettings() ) );
        movieClub.setMembers( userDTOSetToUserSet( movieClubDTO.getMembers() ) );
        List<Watchlist> list = movieClubDTO.getWatchlists();
        if ( list != null ) {
            movieClub.setWatchlists( new ArrayList<Watchlist>( list ) );
        }
        List<ScreeningEvent> list1 = movieClubDTO.getEvents();
        if ( list1 != null ) {
            movieClub.setEvents( new ArrayList<ScreeningEvent>( list1 ) );
        }

        return movieClub;
    }

    @Override
    public MovieClubDTO toMovieClubDTO(MovieClub movieClub) {
        if ( movieClub == null ) {
            return null;
        }

        MovieClubDTO.MovieClubDTOBuilder movieClubDTO = MovieClubDTO.builder();

        movieClubDTO.id( movieClub.getId() );
        movieClubDTO.adminId( movieClub.getAdminId() );
        movieClubDTO.name( movieClub.getName() );
        movieClubDTO.description( movieClub.getDescription() );
        movieClubDTO.settings( clubSettingsToClubSettingsDTO( movieClub.getSettings() ) );
        movieClubDTO.members( userSetToUserDTOSet( movieClub.getMembers() ) );
        List<Watchlist> list = movieClub.getWatchlists();
        if ( list != null ) {
            movieClubDTO.watchlists( new ArrayList<Watchlist>( list ) );
        }
        List<ScreeningEvent> list1 = movieClub.getEvents();
        if ( list1 != null ) {
            movieClubDTO.events( new ArrayList<ScreeningEvent>( list1 ) );
        }

        return movieClubDTO.build();
    }

    protected ClubSettings clubSettingsDTOToClubSettings(ClubSettingsDTO clubSettingsDTO) {
        if ( clubSettingsDTO == null ) {
            return null;
        }

        ClubSettings clubSettings = new ClubSettings();

        clubSettings.setId( clubSettingsDTO.getId() );
        clubSettings.setIsPublic( clubSettingsDTO.getIsPublic() );
        clubSettings.setMaxMembers( clubSettingsDTO.getMaxMembers() );

        return clubSettings;
    }

    protected User userDTOToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDTO.getId() );
        user.username( userDTO.getUsername() );
        user.password( userDTO.getPassword() );

        return user.build();
    }

    protected Set<User> userDTOSetToUserSet(Set<UserDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<User> set1 = new LinkedHashSet<User>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( UserDTO userDTO : set ) {
            set1.add( userDTOToUser( userDTO ) );
        }

        return set1;
    }

    protected ClubSettingsDTO clubSettingsToClubSettingsDTO(ClubSettings clubSettings) {
        if ( clubSettings == null ) {
            return null;
        }

        ClubSettingsDTO clubSettingsDTO = new ClubSettingsDTO();

        clubSettingsDTO.setId( clubSettings.getId() );
        clubSettingsDTO.setIsPublic( clubSettings.getIsPublic() );
        clubSettingsDTO.setMaxMembers( clubSettings.getMaxMembers() );

        return clubSettingsDTO;
    }

    protected UserDTO userToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.username( user.getUsername() );
        userDTO.password( user.getPassword() );

        return userDTO.build();
    }

    protected Set<UserDTO> userSetToUserDTOSet(Set<User> set) {
        if ( set == null ) {
            return null;
        }

        Set<UserDTO> set1 = new LinkedHashSet<UserDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( User user : set ) {
            set1.add( userToUserDTO( user ) );
        }

        return set1;
    }
}
