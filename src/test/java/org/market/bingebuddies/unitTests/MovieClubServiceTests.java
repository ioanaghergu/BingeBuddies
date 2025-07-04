package org.market.bingebuddies.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ClubSettingsDTO;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.exceptions.ClubAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieClubNotFoundException;
import org.market.bingebuddies.exceptions.PermissionDeniedException;
import org.market.bingebuddies.mappers.ClubSettingsMapper;
import org.market.bingebuddies.mappers.MovieClubMapper;
import org.market.bingebuddies.repositories.MovieClubRepository;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.market.bingebuddies.services.impl.MovieClubServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("h2")
@ExtendWith(MockitoExtension.class)
public class MovieClubServiceTests {
    @Mock
    MovieClubRepository movieClubRepository;

    @Mock
    MovieClubMapper movieClubMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    ClubSettingsMapper clubSettingsMapper;

    @InjectMocks
    MovieClubServiceImpl movieClubService;

    MovieClub movieClub;
    MovieClubDTO movieClubDTO;
    User admin;
    ClubSettings settings;
    ClubSettingsDTO settingsDTO;

    @BeforeEach
    void setup() {
        admin = new User();
        admin.setId(1L);

        settings = new ClubSettings();
        settings.setIsPublic(true);
        settings.setMaxMembers(10);

        movieClub = new MovieClub();
        movieClub.setId(1L);
        movieClub.setName("Club1");
        movieClub.setDescription("Desc");
        movieClub.setAdminId(1L);
        movieClub.setSettings(settings);
        movieClub.getMembers().add(admin);

        settingsDTO = new ClubSettingsDTO();
        settingsDTO.setIsPublic(true);
        settingsDTO.setMaxMembers(10);

        movieClubDTO = MovieClubDTO.builder()
                .id(1L)
                .name("Club1")
                .description("Desc")
                .adminId(1L)
                .settings(settingsDTO)
                .build();
    }

    @Test
    void testGetPublicMovieClubs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieClub> page = new PageImpl<>(List.of(movieClub));
        when(movieClubRepository.findAllBySettingsIsPublic(true, pageable)).thenReturn(page);
        when(movieClubMapper.toMovieClubDTO(movieClub)).thenReturn(movieClubDTO);

        Page<MovieClubDTO> result = movieClubService.getPublicMovieClubs(pageable);

        assertEquals(1, result.getTotalElements());
        verify(movieClubRepository).findAllBySettingsIsPublic(true, pageable);
    }

    @Test
    void testGetMovieClubById_Exists() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        when(movieClubMapper.toMovieClubDTO(movieClub)).thenReturn(movieClubDTO);

        Optional<MovieClubDTO> result = movieClubService.getMovieClubById(1L);
        assertTrue(result.isPresent());
        assertEquals("Club1", result.get().getName());
    }

    @Test
    void testGetMovieClubById_NotFound() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(MovieClubNotFoundException.class, () -> movieClubService.getMovieClubById(1L));
    }

    @Test
    void testJoinMovieClub_Success() {
        User user = new User(); user.setId(2L);
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        boolean result = movieClubService.joinMovieClub(1L, 2L);
        assertTrue(result);
        verify(movieClubRepository).save(movieClub);
        verify(userRepository).save(user);
    }

    @Test
    void testJoinMovieClub_UserAlreadyMember() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = movieClubService.joinMovieClub(1L, 1L);
        assertFalse(result);
    }

    @Test
    void testJoinMovieClub_ClubFull() {
        for (int i = 2; i <= 10; i++) {
            User u = new User(); u.setId((long) i); movieClub.getMembers().add(u);
        }
        User user = new User(); user.setId(11L);
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));

        boolean result = movieClubService.joinMovieClub(1L, 11L);
        assertFalse(result);
    }

    @Test
    void testCreateMovieClub_Success() {
        when(movieClubRepository.findByNameAndAdminId("Club1", 1L)).thenReturn(Optional.empty());
        when(movieClubMapper.toMovieClub(movieClubDTO)).thenReturn(movieClub);
        when(clubSettingsMapper.toClubSettings(settingsDTO)).thenReturn(settings);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(movieClubRepository.save(any())).thenReturn(movieClub);
        when(movieClubMapper.toMovieClubDTO(movieClub)).thenReturn(movieClubDTO);

        MovieClubDTO result = movieClubService.createMovieClub(movieClubDTO, 1L);
        assertEquals("Club1", result.getName());
    }

    @Test
    void testCreateMovieClub_AlreadyExists() {
        when(movieClubRepository.findByNameAndAdminId("Club1", 1L)).thenReturn(Optional.of(movieClub));
        assertThrows(ClubAlreadyExistsException.class, () -> movieClubService.createMovieClub(movieClubDTO, 1L));
    }

    @Test
    void testRemoveMemberFromClub_Success() {
        User member = new User(); member.setId(2L);
        member.getClubs().add(movieClub);
        movieClub.getMembers().add(member);

        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));

        boolean result = movieClubService.removeMemberFromClub(1L, 2L, 1L);
        assertTrue(result);
    }

    @Test
    void testRemoveMemberFromClub_Unauthorized() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        User member = new User(); member.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));

        assertThrows(PermissionDeniedException.class, () -> movieClubService.removeMemberFromClub(1L, 2L, 99L));
    }

    @Test
    void testUpdateMovieClub_Success() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        when(movieClubRepository.save(any())).thenReturn(movieClub);
        when(movieClubMapper.toMovieClubDTO(movieClub)).thenReturn(movieClubDTO);

        MovieClubDTO result = movieClubService.updateMovieClub(1L, movieClubDTO);
        assertEquals("Club1", result.getName());
    }

    @Test
    void testUpdateMovieClub_NotFound() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(MovieClubNotFoundException.class, () -> movieClubService.updateMovieClub(1L, movieClubDTO));
    }

    @Test
    void testDeleteMovieClub_Success() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));

        movieClubService.deleteMovieClub(1L, 1L);

        verify(movieClubRepository).deleteById(1L);
    }

    @Test
    void testDeleteMovieClub_PermissionDenied() {
        when(movieClubRepository.findById(1L)).thenReturn(Optional.of(movieClub));
        assertThrows(PermissionDeniedException.class, () -> movieClubService.deleteMovieClub(1L, 999L));
    }
}
