package org.market.bingebuddies.controllers;

import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.services.MovieClubService;
import org.market.bingebuddies.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String clubDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails currentUser) {
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

            Boolean isMember = false;

            if(currentUser != null) {
                User user = userService.findByUsername(currentUser.getUsername());

                if(user != null) {
                    Long currentUserId = user.getId();
                    if(movieClub.getMembers() != null) {
                        isMember = movieClub.getMembers().stream().anyMatch(m -> currentUserId.equals(m.getId()));
                    }
                }
            }

            model.addAttribute("isMember", isMember);
            return "clubDetails";
        }
        else {
            return "redirect:/";
        }
    }

    @PostMapping("clubs/{id}/join")
    public String joinClub(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails currentUser, RedirectAttributes redirectAttributes) {

        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to join the club.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());

        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your user account.");
            return "redirect:/clubs/" + id;
        }

        Long userId = user.getId();
        Boolean success = movieClubService.joinMovieClub(id, userId);

        if(success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully joined the club!");
        }
        else {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not join the club. It might be full or you are already a member.");
        }

        return "redirect:/clubs/" + id;
    }
}
