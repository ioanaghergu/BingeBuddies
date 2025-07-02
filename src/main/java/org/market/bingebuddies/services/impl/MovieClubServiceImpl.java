package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.ClubSettings;
import org.market.bingebuddies.domain.MovieClub;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ClubSettingsDTO;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.exceptions.ClubAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieClubNotFoundException;
import org.market.bingebuddies.mappers.ClubSettingsMapper;
import org.market.bingebuddies.mappers.MovieClubMapper;
import org.market.bingebuddies.repositories.MovieClubRepository;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.market.bingebuddies.services.MovieClubService;
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
    public List<MovieClubDTO> getPublicMovieClubs() {
        List<MovieClub> movieClubs = movieClubRepository.findAllBySettingsIsPublic(true);

        return movieClubs.stream().map(movieClubMapper::toMovieClubDTO).collect(Collectors.toList());
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
    public MovieClub save(MovieClub movieClub) {
        Optional<MovieClub> club = movieClubRepository.findByNameAndAdminId(movieClub.getName(), movieClub.getAdminId());
        if(club.isPresent()) {
            throw new ClubAlreadyExistsException("Club with name " + movieClub.getName() + " already exists");
        }
        return movieClubRepository.save(movieClub);
    }

    @Override
    @Transactional
    public Boolean joinMovieClub(Long movieClubId, Long userId) {

        Optional<MovieClub> clubOptional = movieClubRepository.findById(movieClubId);
        Optional<User> userOptional = userRepository.findById(userId);

        if(clubOptional.isPresent() && userOptional.isPresent()) {
            MovieClub club = clubOptional.get();
            User user = userOptional.get();

            //User is already a member of the club
            if(club.getMembers().contains(user)) {
                return false;
            }

            //Club is full
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

        MovieClubDTO savedDTO = movieClubMapper.toMovieClubDTO(savedClub);

        return savedDTO;

    }
}
