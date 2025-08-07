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

    @Test
    void findRecommendedMovies() {
        // Given
        Map<String, Double> genrePreferences = Map.of("Action", 10.0, "Adventure", 5.0);
        List<Long> ratedMovies = List.of(1L, 2L);
        int limit = 3;
        
        MovieDto movie1 = new MovieDto();
        movie1.setId(3L);
        movie1.setTitle("Movie 3");
        movie1.setGenres(List.of(new MovieDto.Genre(1L, "Action"), new MovieDto.Genre(2L, "Adventure")));

        MovieDto movie2 = new MovieDto();
        movie2.setId(4L);
        movie2.setTitle("Movie 4");
        movie2.setGenres(List.of(new MovieDto.Genre(1L, "Action")));

        when(movieApiService.searchMovies("Action")).thenReturn(List.of(movie1, movie2));
        when(movieApiService.searchMovies("Adventure")).thenReturn(List.of(movie1));

        when(movieApiService.getTrendingMovies()).thenReturn(List.of());

        // When
        List<MovieDto> recommendedMovies = recommendationService.findRecommendedMovies(genrePreferences, ratedMovies, limit);

        // Then
        assertEquals(2, recommendedMovies.size());
        assertEquals("Movie 3", recommendedMovies.get(0).getTitle());
        assertEquals("Movie 4", recommendedMovies.get(1).getTitle());
    }
   
}
