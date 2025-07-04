package org.market.bingebuddies.controllers;

import jakarta.validation.Valid;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.market.bingebuddies.exceptions.MovieAlreadyExistsException;
import org.market.bingebuddies.exceptions.MovieNotFoundException;
import org.market.bingebuddies.services.MovieService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public String movieDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MovieDTO> movieDTO = movieService.findMovieById(id);

        if(movieDTO.isPresent()) {
            MovieDTO movie = movieDTO.get();
            model.addAttribute("movie", movie);
            model.addAttribute("newReview", new ReviewDTO());
            return "movieDetails";
        }
        else {
            redirectAttributes.addFlashAttribute("errorMessage", "Movie not found");
            return "redirect:/movies";
        }
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
            return "redirect:/movies/";
        } catch (MovieAlreadyExistsException e) {
            bindingResult.rejectValue("title", "movie.title.exists", e.getMessage());
            return "newMovie";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding movie: " + e.getMessage());
            return "redirect:/movies/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String displayMovieEditForm(@PathVariable Long id, Model model,
                                       RedirectAttributes redirectAttributes,
                                       @AuthenticationPrincipal UserDetails currentUser) {
        if(currentUser == null)
        {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit movies.");
            return "redirect:/login";
        }

        try {
            Optional<MovieDTO> movieDTO = movieService.findMovieById(id);
            if(movieDTO.isPresent()) {

                model.addAttribute("movie", movieDTO.get());
                System.out.println("in display movie form");
                return "editMovie";
            }
            else {
                redirectAttributes.addFlashAttribute("errorMessage", "Movie not found");
                return "redirect:/movies";
            }
        }
        catch (MovieNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Movie not found" + e.getMessage());
            return "redirect:/movies";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error editing movie: " + e.getMessage());
            return "redirect:/movies/edit/" + id;
        }
    }

    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable Long id,
                              @Valid @ModelAttribute("movie") MovieDTO movieDTO,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes redirectAttributes, BindingResult bindingResult){
        if(currentUser == null){
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit movies.");
            return "redirect:/login";
        }

        if(bindingResult.hasErrors()){
            System.out.println("in update movie");
            return "editMovie";
        }

        try{
            movieService.updateMovie(id, movieDTO);
            System.out.println("in update movie dupa service update");
            redirectAttributes.addFlashAttribute("successMessage", "Movie updated successfully!");
            return "redirect:/movies/" + id;
        }
        catch (MovieNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Movie not found" + e.getMessage());
            return "redirect:/movies";
        }
        catch (MovieAlreadyExistsException e) {
            bindingResult.rejectValue("title", "movie.title.exists", e.getMessage());
            return "editMovie";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating movie: " + e.getMessage());
            return "redirect:/movies/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser, RedirectAttributes redirectAttributes) {
        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to delete movies.");
            return "redirect:/login";
        }

        try{
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("successMessage", "Movie deleted successfully!");
            return "redirect:/movies";
        }catch (MovieNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Movie not found" + e.getMessage());
            return "redirect:/movies";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting movie: " + e.getMessage());
            return "redirect:/movies/" + id;
        }
    }
}
