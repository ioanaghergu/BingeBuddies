package org.market.bingebuddies.integrationTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.market.bingebuddies.dtos.MovieDTO;
import org.market.bingebuddies.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("h2")
class MovieControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieService movieService;

    private MovieDTO testMovie;

    @BeforeEach
    void setUp() {
        testMovie = MovieDTO.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .releaseYear(2010)
                .avgRating(4.8)
                .build();
    }

    @Test
    void displayMovies_ShouldReturnMoviesView() throws Exception {
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(view().name("movies"))
                .andExpect(model().attributeExists("movies"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void displayNewMovieForm_AsAdmin_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/movies/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("newMovie"))
                .andExpect(model().attributeExists("movie"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addMovie_ValidMovie_ShouldRedirectToMovies() throws Exception {
        mockMvc.perform(post("/movies/new")
                        .param("title", "Matrix")
                        .param("genre", "Action")
                        .param("releaseYear", "2001")
                        .param("avgRating", "4.5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addMovie_InvalidData_ShouldReturnForm() throws Exception {
        mockMvc.perform(post("/movies/new")
                        .param("title", "")
                        .param("genre", "")
                        .param("releaseYear", "1999")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("newMovie"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMovie_ValidId_ShouldRedirectToMovieDetailsIfErrorOccurs() throws Exception {
        MovieDTO saved = movieService.addMovie(testMovie);
        saved.setReviews(null);
        mockMvc.perform(post("/movies/delete/" + saved.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies/" + saved.getId()));
    }


}
