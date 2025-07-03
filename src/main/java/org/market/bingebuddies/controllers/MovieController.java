package org.market.bingebuddies.controllers;

import jakarta.validation.Valid;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.exceptions.MovieAlreadyExistsException;
import org.market.bingebuddies.services.MovieService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("")
    public String displayMovies(Model model) {
        List<MovieDTO> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        return "movies";
    }

    @GetMapping("/new")
    public String displayNewMovieForm(Model model, @AuthenticationPrincipal UserDetails currentUser, RedirectAttributes redirectAttributes) {

        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to add movies.");
            return "redirect:/login";
        }

        if(!currentUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to add movies.");
            return "redirect:/movies";
        }

        model.addAttribute("movie", new MovieDTO());
        return "newMovie";
    }

    @PostMapping("/new")
    public String addMovie(@Valid @ModelAttribute("movie") MovieDTO movieDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null || !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to add movies.");
            return "redirect:/movies";
        }

        if (bindingResult.hasErrors()) {
            return "newMovie";
        }

        try {
            movieService.addMovie(movieDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Movie added successfully!");
            return "redirect:/movies";
        } catch (MovieAlreadyExistsException e) {
            bindingResult.rejectValue("title", "movie.title.exists", e.getMessage());
            return "newMovie";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding movie: " + e.getMessage());
            return "redirect:/movies/new";
        }
    }
}
