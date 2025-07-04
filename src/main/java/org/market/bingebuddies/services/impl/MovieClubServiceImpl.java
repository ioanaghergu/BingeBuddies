package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.exceptions.ClubAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieClubNotFoundException;
import org.market.bingebuddies.exceptions.PermissionDeniedException;
import org.market.bingebuddies.exceptions.UserNotFoundException;
import org.market.bingebuddies.mappers.ClubSettingsMapper;
import org.market.bingebuddies.mappers.MovieClubMapper;
import org.market.bingebuddies.repositories.MovieClubRepository;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.market.bingebuddies.services.MovieClubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieClubServiceImpl implements MovieClubService {

    private final MovieClubRepository movieClubRepository;
    private final MovieClubMapper movieClubMapper;
    private final UserRepository userRepository;
    private final ClubSettingsMapper clubSettingsMapper;

    public MovieClubServiceImpl(MovieClubRepository movieClubRepository, MovieClubMapper movieClubMapper, UserRepository userRepository, ClubSettingsMapper clubSettingsMapper) {
        this.movieClubRepository = movieClubRepository;
        this.movieClubMapper = movieClubMapper;
        this.userRepository = userRepository;
        this.clubSettingsMapper = clubSettingsMapper;
    }

    @Override
    public Page<MovieClubDTO> getPublicMovieClubs(Pageable pageable) {
        Page<MovieClub> movieClubsPage = movieClubRepository.findAllBySettingsIsPublic(true, pageable);
        return movieClubsPage.map(movieClub -> {
            MovieClubDTO movieClubDTO = movieClubMapper.toMovieClubDTO(movieClub);
            return movieClubDTO;
        });
    }

    @Override
    public Optional<MovieClubDTO> getMovieClubById(Long id) {
        Optional<MovieClub> movieClub = movieClubRepository.findById(id);

        if(movieClub.isEmpty()) {
            throw new MovieClubNotFoundException("Club with id " + id + " not found");
        }

        return Optional.of(movieClubMapper.toMovieClubDTO(movieClub.get()));
    }


    @Override
    @Transactional
    public Boolean joinMovieClub(Long movieClubId, Long userId) {

        Optional<MovieClub> clubOptional = movieClubRepository.findById(movieClubId);
        Optional<User> userOptional = userRepository.findById(userId);

        if(clubOptional.isPresent() && userOptional.isPresent()) {
            MovieClub club = clubOptional.get();
            User user = userOptional.get();

            if(club.getMembers().contains(user)) {
                return false;
            }

            if(club.getSettings() != null && club.getSettings().getMaxMembers() != null) {
                if(club.getMembers().size() == club.getSettings().getMaxMembers()) {
                    return false;
                }
            }

            club.getMembers().add(user);
            user.getClubs().add(club);
            movieClubRepository.save(club);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public MovieClubDTO createMovieClub(MovieClubDTO movieClubDTO, Long adminId) {
        Optional<MovieClub> existingClub = movieClubRepository.findByNameAndAdminId(movieClubDTO.getName(), adminId);

        if(existingClub.isPresent()) {
            throw new ClubAlreadyExistsException("Club with name " + movieClubDTO.getName() + " already exists");
        }

        MovieClub movieClub = movieClubMapper.toMovieClub(movieClubDTO);
        movieClub.setAdminId(adminId);

        ClubSettings settings = clubSettingsMapper.toClubSettings(movieClubDTO.getSettings());
        movieClub.setSettings(settings);
        settings.setMovieClub(movieClub);

        Optional<User> adminOptional = userRepository.findById(adminId);
        if(adminOptional.isPresent()) {
            User admin = adminOptional.get();
            movieClub.getMembers().add(admin);
            admin.getClubs().add(movieClub);
            userRepository.save(admin);
        }
        else {
            System.out.println("Admin not found");
        }

        MovieClub savedClub = movieClubRepository.save(movieClub);

        return movieClubMapper.toMovieClubDTO(savedClub);

    }

    @Override
    @Transactional
    public Boolean removeMemberFromClub(Long clubId, Long memberId, Long adminId) {
        Optional<MovieClub> clubOptional = movieClubRepository.findById(clubId);
        Optional<User> userOptional = userRepository.findById(memberId);

        if(clubOptional.isEmpty())
        {
            throw new MovieClubNotFoundException("Club with id " + clubId + " not found");
        }

        if(userOptional.isEmpty())
        {
            throw new UserNotFoundException("User with id" + memberId + " not found");
        }

        MovieClub movieClub = clubOptional.get();
        User user = userOptional.get();

        if(!movieClub.getAdminId().equals(adminId)) {
            throw new PermissionDeniedException("Only the admin of the club can remove members");
        }

        Boolean memberRemoved = movieClub.getMembers().remove(user);

        if(!memberRemoved)
        {
            System.out.println("Member not found in this club");
            return false;
        }

        boolean clubRemoved = user.getClubs().remove(movieClub);

        movieClubRepository.save(movieClub);
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public MovieClubDTO updateMovieClub(Long clubId, MovieClubDTO movieClubDTO) {
        Optional<MovieClub> clubOptional = movieClubRepository.findById(clubId);

        if (clubOptional.isEmpty()) {
            throw new MovieClubNotFoundException("Movie club with id " + clubId + " not found");
        }

        MovieClub existingClub = clubOptional.get();

        if (existingClub.getName().equals(movieClubDTO.getName())) {
            Optional<MovieClub> conflictClub = movieClubRepository.findByNameAndAdminId(movieClubDTO.getName(), existingClub.getAdminId());
            if (conflictClub.isPresent() && !conflictClub.get().getId().equals(clubId)) {
                throw new ClubAlreadyExistsException("Another club with the name '" + movieClubDTO.getName() + "' already exists for this admin.");
            }
        }

        existingClub.setName(movieClubDTO.getName());
        existingClub.setDescription(movieClubDTO.getDescription());

        ClubSettings existingSettings = existingClub.getSettings();
        existingSettings.setIsPublic(movieClubDTO.getSettings().getIsPublic());
        existingSettings.setMaxMembers(movieClubDTO.getSettings().getMaxMembers());
        existingClub.setSettings(existingSettings);

        MovieClub updatedClub = movieClubRepository.save(existingClub);

        MovieClubDTO updatedDto = movieClubMapper.toMovieClubDTO(updatedClub);
        return updatedDto;
    }

    @Override
    @Transactional
    public void deleteMovieClub(Long clubId, Long adminId) {
        Optional<MovieClub> clubOptional = movieClubRepository.findById(clubId);

        if (clubOptional.isEmpty()) {
            throw new MovieClubNotFoundException("Movie club with id " + clubId + " not found");
        }

        MovieClub clubToDelete = clubOptional.get();

        if (!clubToDelete.getAdminId().equals(adminId)) {
            throw new PermissionDeniedException("Only the admin of the club can delete the club");
        }

        for (User member : clubToDelete.getMembers()) {
            member.getClubs().remove(clubToDelete);
            userRepository.save(member);
        }

        clubToDelete.getMembers().clear();
        movieClubRepository.save(clubToDelete);

        movieClubRepository.deleteById(clubId);
        System.out.println("Successfully deleted club with ID: " + clubId);
    }
}
