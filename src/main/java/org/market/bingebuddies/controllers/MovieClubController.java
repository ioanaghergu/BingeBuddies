package org.market.bingebuddies.controllers;

import jakarta.validation.Valid;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ClubSettingsDTO;
import org.market.bingebuddies.dtos.MovieClubDTO;
import org.market.bingebuddies.exceptions.ClubAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieClubNotFoundException;
import org.market.bingebuddies.exceptions.PermissionDeniedException;
import org.market.bingebuddies.services.MovieClubService;
import org.market.bingebuddies.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public String homePage(Model model) {
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
    public String joinClub(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser, RedirectAttributes redirectAttributes) {

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

    @GetMapping("/clubs/new")
    public String showCreateClubForm(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if(currentUser == null) {
            return "redirect:/login";
        }

        MovieClubDTO movieClub = new MovieClubDTO();
        movieClub.setSettings(new ClubSettingsDTO());
        model.addAttribute("movieClub", movieClub);
        return "newClub";
    }

    @PostMapping("/clubs/new")
    public String createClub(@Valid @ModelAttribute("movieClub") MovieClubDTO movieClubDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails currentUser) {
        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to create a club.");
            return "redirect:/login";
        }

        if(bindingResult.hasErrors()) {
            return "newClub";
        }

        User admin = userService.findByUsername(currentUser.getUsername());
        if(admin == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your user account.");
            return "redirect:/clubs/new";
        }

        try{
            movieClubService.createMovieClub(movieClubDTO, admin.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Successfully created club!");
            return "redirect:/clubs";
        }
        catch (ClubAlreadyExistsException e) {
            bindingResult.rejectValue("name", "club.name.exists", e.getMessage());
            return "newClub";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating club: " + e.getMessage());
            return "redirect:/clubs/new";

        }
    }

    @PostMapping("/clubs/{clubId}/remove/{memberId}")
    public String removeMember(@PathVariable("clubId") Long clubId,
                               @PathVariable("memberId") Long memberId,
                               @AuthenticationPrincipal UserDetails currentUser,
                               RedirectAttributes redirectAttributes) {
        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to create a club.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());

        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your user account.");
            return "redirect:/clubs/" + clubId;
        }

        try{
            boolean succes = movieClubService.removeMemberFromClub(clubId, memberId, user.getId());

            if(succes) {
                redirectAttributes.addFlashAttribute("successMessage", "Successfully removed member!");
            }
            else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove member!");
            }
        } catch (MovieClubNotFoundException | PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove member: " + e.getMessage());
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing member: " + e.getMessage());
        }

        return "redirect:/clubs/" + clubId;
    }

    @GetMapping("/clubs/{id}/edit")
    public String showEditClubForm(@PathVariable("id") Long id, Model model,
                                   @AuthenticationPrincipal UserDetails currentUser,
                                   RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit a club.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your user account.");
            return "redirect:/clubs/" + id;
        }

        try {
            MovieClubDTO movieClubDTO = movieClubService.getMovieClubById(id)
                    .orElseThrow(() -> new MovieClubNotFoundException("Club not found."));


            Boolean isMember = movieClubDTO.getMembers().stream()
                    .anyMatch(m -> user.getId().equals(m.getId()));

            if (!isMember) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not a member of this club and cannot edit it.");
                return "redirect:/clubs/" + id;
            }

            model.addAttribute("movieClub", movieClubDTO);
            return "editClub";
        } catch (MovieClubNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clubs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading club for editing: " + e.getMessage());
            return "redirect:/clubs/" + id;
        }
    }

    @PostMapping("/clubs/{id}/edit")
    public String updateClub(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("movieClub") MovieClubDTO movieClubDTO,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails currentUser,
                             RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit a club.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your user account.");
            return "redirect:/clubs/" + id;
        }

        movieClubDTO.setId(id);

        if (bindingResult.hasErrors()) {
            return "editClub";
        }

        try {
            movieClubService.updateMovieClub(id, movieClubDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Club updated successfully!");
            return "redirect:/clubs/" + id;
        } catch (MovieClubNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clubs/" + id;
        } catch (ClubAlreadyExistsException e) {
            bindingResult.rejectValue("name", "club.name.exists", e.getMessage());
            return "editClub";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating club: " + e.getMessage());
            return "redirect:/clubs/" + id;
        }
    }


    @PostMapping("/clubs/{id}/delete")
    public String deleteClub(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails currentUser,
                             RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to delete a club.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your user account.");
            return "redirect:/clubs";
        }

        try {
            movieClubService.deleteMovieClub(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Club deleted successfully!");
            return "redirect:/clubs";
        } catch (MovieClubNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clubs";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clubs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "redirect:/clubs";
        }
    }
}
