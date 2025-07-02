package org.market.bingebuddies.controllers;

import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.dtos.UserDTO;
import org.market.bingebuddies.services.MovieClubService;
import org.market.bingebuddies.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping({"/", "/clubs"})
public class MovieClubController {

    private final MovieClubService movieClubService;
    private final UserService userService;

    public MovieClubController(MovieClubService movieClubService, UserService userService) {
        this.movieClubService = movieClubService;
        this.userService = userService;
    }

    @RequestMapping("")
    public String homePage(Model model, Authentication authentication) {
        List<MovieClubDTO> movieClubs = movieClubService.getPublicMovieClubs();
        Map<MovieClubDTO, String> clubs = new HashMap<>();

        for(MovieClubDTO movieClub : movieClubs) {
            String adminUsername = "N/A";

            if(movieClub.getAdminId() != null) {
                User admin = userService.findById(movieClub.getAdminId());
                adminUsername = admin.getUsername();
            }
            clubs.put(movieClub, adminUsername);
        }
        model.addAttribute("clubs", clubs);
        return "home";
    }

    @GetMapping("/clubs/{id}")
    public String clubDetails(@PathVariable Long id, Model model) {
        Optional<MovieClubDTO> club = movieClubService.getMovieClubById(id);
        if(club.isPresent()) {
            MovieClubDTO movieClub = club.get();
            model.addAttribute("movieClub", movieClub);

            String adminUsername = "N/A";

            if(movieClub.getAdminId() != null) {
                User admin = userService.findById(movieClub.getAdminId());
                adminUsername = admin.getUsername();
            }

            model.addAttribute("adminUsername", adminUsername);
            return "clubDetails";
        }
        else {
            return "redirect:/";
        }
    }
}
