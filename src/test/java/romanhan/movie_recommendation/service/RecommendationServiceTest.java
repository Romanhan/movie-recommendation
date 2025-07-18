package romanhan.movie_recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.entity.UserMovieRating;
import romanhan.movie_recommendation.repository.UserMovieRatingRepository;

public class RecommendationServiceTest {

   private UserMovieRatingRepository userMovieRatingRepository;
   private MovieApiService movieApiService;
   private RecommendationService recommendationService;

   @BeforeEach
   public void setUp() {
       userMovieRatingRepository = mock(UserMovieRatingRepository.class);
       movieApiService = mock(MovieApiService.class);
       recommendationService = new RecommendationService(userMovieRatingRepository, movieApiService);
   }

    @Test
    void analyzeGenrePreferences() {
        // Given
        UserMovieRating rating = new UserMovieRating();
        rating.setMovieId(1L);
        rating.setRating(8.0);        

        MovieDto movie = new MovieDto();
        movie.setId(1L);
        movie.setGenres(List.of(new MovieDto.Genre(1L, "Action"), new MovieDto.Genre(2L, "Adventure")));

        when(movieApiService.getMovie(1L)).thenReturn(movie);

        // When
        Map<String, Double> result = recommendationService.analyzeGenrePreferences(List.of(rating));

        // Then
        assertEquals(2, result.size());
        assertEquals(8.0, result.get("Action"));
        assertEquals(8.0, result.get("Adventure"));

    }
   
}
