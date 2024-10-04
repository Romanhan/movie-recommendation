package romanhan.movie_recommendation.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
