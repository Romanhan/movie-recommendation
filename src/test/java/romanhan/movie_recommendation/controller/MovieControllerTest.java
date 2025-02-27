package romanhan.movie_recommendation.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.entity.UserMovieRating;
import romanhan.movie_recommendation.service.MovieApiService;
import romanhan.movie_recommendation.service.MovieRatingService;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieApiService movieApiService;

    @MockBean
    private MovieRatingService movieRatingService;

    @Test
    @WithMockUser
    void shouldReturnHomePageWithTrendingMovies() throws Exception {
        MovieDto movie = new MovieDto();
        movie.setId(1L);
        movie.setTitle("Movie Title");
        movie.setReleaseDate("2023-10-15");
        movie.setGenres(List.of(new MovieDto.Genre(1L, "Action"), new MovieDto.Genre(2L, "Adventure")));

        List<MovieDto> trendingMovies = List.of(movie);
        when(movieApiService.getTrendingMovies()).thenReturn(trendingMovies);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("trendingMovies", trendingMovies));
    }

    @Test
    @WithMockUser

    void shouldReturnSearchResult() throws Exception {
        MovieDto movie = new MovieDto();
        movie.setId(3L);
        movie.setTitle("Movie Title2");
        movie.setReleaseDate("2023-10-16");
        movie.setGenres(List.of(new MovieDto.Genre(4L, "Action"), new MovieDto.Genre(5L, "Adventure")));

        List<MovieDto> moviesList = List.of(movie);
        when(movieApiService.searchMovies("test")).thenReturn(moviesList);

        mockMvc.perform(get("/search").param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("movies", moviesList));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldRedirectAfterRatingMovie() throws Exception {
        Long movieId = 7L;
        int rating = 5;

        mockMvc.perform(post("/movie/{id}/rate", movieId).param("rating", String.valueOf(rating))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movieId));

        verify(movieRatingService).rateMovie("testUser", movieId, rating);
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldReturnMovieDetails() throws Exception {
        Long movieId = 1L;
        MovieDto movie = new MovieDto();
        movie.setId(movieId);
        movie.setTitle("Movie Title");
        movie.setReleaseDate("2023-10-15");
        movie.setGenres(List.of(new MovieDto.Genre(1L, "Action"), new MovieDto.Genre(2L, "Adventure")));

        UserMovieRating userMovieRating = new UserMovieRating();
        userMovieRating.setRating(4.0);

        when(movieApiService.getMovie(movieId)).thenReturn(movie);
        when(movieRatingService.getUserRating("testUser", movieId)).thenReturn(Optional.of(userMovieRating));

        mockMvc.perform(get("/movie/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(view().name("movie-details"))
                .andExpect(model().attribute("movie", movie))
                .andExpect(model().attribute("userRating", 4.0));
    }
}
