package romanhan.movie_recommendation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import romanhan.movie_recommendation.dto.MovieDto;
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
    void testRateMovie() {

    }

    @Test
    void testMovie() {

    }
}
