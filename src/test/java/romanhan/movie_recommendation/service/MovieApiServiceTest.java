package romanhan.movie_recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import romanhan.movie_recommendation.dto.MovieDto;

@SpringBootTest
public class MovieApiServiceTest {

    @Autowired
    private MovieApiService movieApiService;

    @Test
    void canSearchMovies() {
        List<MovieDto> movies = movieApiService.searchMovies("Batman");
        assertFalse(movies.isEmpty());
        assertTrue(movies.get(0).getTitle().contains("Batman"));
    }

    @Test
    void testGetMovieId() {
        MovieDto movie = movieApiService.getMovie(550L);
        assertNotNull(movie);
        assertEquals("Fight Club", movie.getTitle());
    }

    @Test
    void testGetMoviesByGenre() {
        // When
        List<MovieDto> moviesWithGenre = movieApiService.getMoviesByGenre("Action");

        // Then
        assertFalse(moviesWithGenre.isEmpty(), "Movies list should not be empty");
        assertNotNull(moviesWithGenre.get(0).getTitle(), "Movies should have titles");
        assertEquals(moviesWithGenre.get(0).getGenres().get(0).getName(), "Action",
                "First movie should have 'Action' genre");
    }

    @Test
    void testGetMoviesByGenre_InvalidGenre() {
        // When
        List<MovieDto> moviesWithInvalidGenre = movieApiService.getMoviesByGenre("InvalidGenre");
        List<MovieDto> trendingMovies = movieApiService.getTrendingMovies();

        // Then
        assertEquals(trendingMovies.size(), moviesWithInvalidGenre.size(),
                "Fake genre should return trending movies as fallback");
        assertEquals(trendingMovies.get(0).getId(), moviesWithInvalidGenre.get(0).getId(),
                "Should return exact same movies as trending");
    }

}
