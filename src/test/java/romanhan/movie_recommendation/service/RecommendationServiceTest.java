package romanhan.movie_recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.entity.User;
import romanhan.movie_recommendation.entity.UserMovieRating;
import romanhan.movie_recommendation.repository.UserMovieRatingRepository;

public class RecommendationServiceTest {

    private UserMovieRatingRepository userMovieRatingRepository;
    private MovieApiService movieApiService;
    private RecommendationService recommendationService;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userMovieRatingRepository = mock(UserMovieRatingRepository.class);
        movieApiService = mock(MovieApiService.class);
        userService = mock(UserService.class);
        recommendationService = new RecommendationService(userMovieRatingRepository, movieApiService, userService);
    }

    @Test
    void getRecommendationsForUser_NewUser_ShouldReturnTrendingMovies() {
        // Given
        String username = "newuser";
        User user = new User();
        user.setEmail(username);

        when(userService.findByUsername(username)).thenReturn(user);
        when(userMovieRatingRepository.findByUser(user)).thenReturn(List.of());

        MovieDto trendingMovie = new MovieDto();
        trendingMovie.setId(1L);
        trendingMovie.setTitle("Trending Movie");
        when(movieApiService.getTrendingMovies()).thenReturn(List.of(trendingMovie));

        // When
        List<MovieDto> result = recommendationService.getRecommendationsForUser(username, 5);

        // Then
        assertEquals(1, result.size());
        assertEquals("Trending Movie", result.get(0).getTitle());
    }

    @Test
    void getRecommendationsForUser_ExistingUser_ShouldReturnPersonalizedRecommendation() {
        // Given
        String username = "existinguser";
        User user = new User();
        user.setUsername(username);

        UserMovieRating rating = new UserMovieRating();
        rating.setMovieId(100L);
        rating.setRating(8.0);

        MovieDto ratedMovie = new MovieDto();
        ratedMovie.setId(100L);
        ratedMovie.setGenres(List.of(new MovieDto.Genre(1L, "Action")));

        MovieDto recommendedMovie = new MovieDto();
        recommendedMovie.setId(200L);
        recommendedMovie.setTitle("Recommended Movie");

        when(userService.findByUsername(username)).thenReturn(user);
        when(userMovieRatingRepository.findByUser(user)).thenReturn(List.of(rating));
        when(movieApiService.getMovie(100L)).thenReturn(ratedMovie);
        when(movieApiService.getMoviesByGenre("Action")).thenReturn(List.of(recommendedMovie));
        when(movieApiService.getTrendingMovies()).thenReturn(List.of());

        // When
        List<MovieDto> result = recommendationService.getRecommendationsForUser(username, 5);

        // Then
        assertFalse(result.isEmpty());
        assertEquals("Recommended Movie", result.get(0).getTitle());
        assertTrue(result.stream().noneMatch(movie -> movie.getId().equals(100L)));
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
        assertEquals(3.0, result.get("Action"));
        assertEquals(3.0, result.get("Adventure"));
    }

    @Test
    void analyzeGenrePreferences_ShouldOnlyCountHighRatedMoviesAndWeightByScore() {
        // Given
        UserMovieRating highActionRating = new UserMovieRating();
        highActionRating.setMovieId(1L);
        highActionRating.setRating(8.0); // Should contribute 3.0 weight

        UserMovieRating veryHighActionRating = new UserMovieRating();
        veryHighActionRating.setMovieId(2L);
        veryHighActionRating.setRating(10.0); // Should contribute 5.0 weight

        UserMovieRating lowHorrorRating = new UserMovieRating();
        lowHorrorRating.setMovieId(3L);
        lowHorrorRating.setRating(3.0); // Should be ignored

        MovieDto actionMovie1 = new MovieDto();
        actionMovie1.setGenres(List.of(new MovieDto.Genre(1L, "Action")));

        MovieDto actionMovie2 = new MovieDto();
        actionMovie2.setGenres(List.of(new MovieDto.Genre(1L, "Action")));

        MovieDto horrorMovie = new MovieDto();
        horrorMovie.setGenres(List.of(new MovieDto.Genre(2L, "Horror")));

        when(movieApiService.getMovie(1L)).thenReturn(actionMovie1);
        when(movieApiService.getMovie(2L)).thenReturn(actionMovie2);
        when(movieApiService.getMovie(3L)).thenReturn(horrorMovie);

        // When
        Map<String, Double> result = recommendationService.analyzeGenrePreferences(
                List.of(highActionRating, veryHighActionRating, lowHorrorRating));

        // Then
        assertEquals(8.0, result.get("Action")); // 3.0 + 5.0 = 8.0
        assertFalse(result.containsKey("Horror")); // Low rating ignored
    }

    @Test
    void findRecommendedMovies() {
        // Given
        Map<String, Double> genrePreferences = Map.of("Action", 10.0, "Adventure", 6.0);
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

        when(movieApiService.getMoviesByGenre("Action")).thenReturn(List.of(movie1, movie2));
        when(movieApiService.getMoviesByGenre("Adventure")).thenReturn(List.of(movie1));

        when(movieApiService.getTrendingMovies()).thenReturn(List.of());

        // When
        List<MovieDto> recommendedMovies = recommendationService.findRecommendedMovies(genrePreferences, ratedMovies,
                limit);

        // Then
        assertEquals(2, recommendedMovies.size());
        assertEquals("Movie 3", recommendedMovies.get(0).getTitle());
        assertEquals("Movie 4", recommendedMovies.get(1).getTitle());
    }

}
