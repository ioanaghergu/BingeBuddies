package org.market.bingebuddies.controllers;

import jakarta.validation.Valid;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.ReviewDTO;
import org.market.bingebuddies.exceptions.MovieNotFoundException;
import org.market.bingebuddies.exceptions.PermissionDeniedException;
import org.market.bingebuddies.exceptions.ReviewNotFoundException;
import org.market.bingebuddies.exceptions.UserNotFoundException;
import org.market.bingebuddies.services.ReviewService;
import org.market.bingebuddies.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;


    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/movies/{movieId}/new")
    public String addReview(@PathVariable Long movieId,
                            @Valid @ModelAttribute("newReview") ReviewDTO reviewDTO,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails currentUser,
                            RedirectAttributes redirectAttributes) {
        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to leave a review.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());

        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your account.");
            return "redirect:/movies/" + movieId;
        }

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please correct the review errors.");
            return "redirect:/movies/" + movieId;
        }

        try{
            reviewService.addReview(movieId, user.getId(), reviewDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Review added successfully.");
        } catch (MovieNotFoundException | UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding review: " + e.getMessage());
        }

        return "redirect:/movies/" + movieId;
    }

    @GetMapping("/edit/{reviewId}")
    public String displayReviewForm(@PathVariable Long reviewId, Model model,
                                    @AuthenticationPrincipal UserDetails currentUser,
                                    RedirectAttributes redirectAttributes) {
        if(currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit a review.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());

        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your account.");
            return "redirect:/movies";
        }

        try {
            Optional<ReviewDTO> reviewDTO = reviewService.getReviewById(reviewId);

            if (reviewDTO.isEmpty()){
                redirectAttributes.addFlashAttribute("errorMessage", "Could not find review");
                return "redirect:/movies";
            }

            ReviewDTO reviewToEdit = reviewDTO.get();

            if(!reviewToEdit.getUserId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to edit this review.");
                return "redirect:/movies/" + reviewToEdit.getMovieId();
            }

            model.addAttribute("review", reviewToEdit);
            return "editReview";

        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage","Error loading review for editing " + e.getMessage());
            return "redirect:/movies";
        }
    }

    @PostMapping("/edit/{reviewId}")
    public String updateReview(@PathVariable Long reviewId,
                               @Valid @ModelAttribute("review") ReviewDTO reviewDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit a review.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your account.");
            return "redirect:/movies";
        }

        if(bindingResult.hasErrors()) {
            return "editReview";
        }

        try{
            reviewService.updateReview(reviewId, user.getId(), reviewDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully.");
        }catch (ReviewNotFoundException | PermissionDeniedException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating review: " + e.getMessage());
        }
        return "redirect:/movies/" + reviewDTO.getMovieId();
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId,
                               @AuthenticationPrincipal UserDetails currentUser,
                               RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to edit a review.");
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find your account.");
            return "redirect:/login";
        }

        Long movieId = null;

        try{
            Optional<ReviewDTO> reviewDTO = reviewService.getReviewById(reviewId);
            if(reviewDTO.isPresent()){
                movieId = reviewDTO.get().getMovieId();
            }
            else {
                redirectAttributes.addFlashAttribute("errorMessage", "Could not find review");
                return "redirect:/movies/";
            }

            reviewService.deleteReview(reviewId, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully.");
        }catch (ReviewNotFoundException | PermissionDeniedException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting review: " + e.getMessage());
        }

        return "redirect:/movies/" + (movieId != null ? movieId : "");
    }
}
