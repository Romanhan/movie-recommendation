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
    void findRecommendedMovies_ShouldSortByScore() {
        // Given
        Map<String, Double> genrePreferences = Map.of("Action", 16.0, "Adventure", 5.0); // Action preferred more
        List<Long> ratedMovies = List.of();
        int limit = 3;

        MovieDto movie1 = new MovieDto();
        movie1.setId(1L);
        movie1.setTitle("Action Movie - Low Rating");
        movie1.setRating(6.0); // Low TMDB rating
        movie1.setGenres(List.of(new MovieDto.Genre(1L, "Action")));

        MovieDto movie2 = new MovieDto();
        movie2.setId(2L);
        movie2.setTitle("Adventure Movie - High Rating");
        movie2.setRating(9.0); // High TMDB rating
        movie2.setGenres(List.of(new MovieDto.Genre(2L, "Adventure")));

        MovieDto movie3 = new MovieDto();
        movie3.setId(3L);
        movie3.setTitle("Action Adventure - Medium Rating");
        movie3.setRating(7.5); // Medium TMDB rating
        movie3.setGenres(List.of(new MovieDto.Genre(1L, "Action"), new MovieDto.Genre(2L, "Adventure")));

        when(movieApiService.getMoviesByGenre("Action")).thenReturn(List.of(movie1, movie3));
        when(movieApiService.getMoviesByGenre("Adventure")).thenReturn(List.of(movie2, movie3));
        when(movieApiService.getTrendingMovies()).thenReturn(List.of());

        // When
        List<MovieDto> result = recommendationService.findRecommendedMovies(genrePreferences, ratedMovies, limit);

        // Then
        assertEquals(3, result.size());

        // Movie 3 should be first: (16.0 + 5.0) + (7.5 * 0.5) = 24.75
        assertEquals("Action Adventure - Medium Rating", result.get(0).getTitle());

        // Movie 1 should be second: 16.0 + (6.0 * 0.5) = 19.0
        assertEquals("Action Movie - Low Rating", result.get(1).getTitle());

        // Movie 2 should be third: 5.0 + (9.0 * 0.5) = 9.5
        assertEquals("Adventure Movie - High Rating", result.get(2).getTitle());
    }

    @Test
    void findRecommendedMovies_MovieWithoutGenres_ShouldStillGetTMDBBonus() {
        // Given
        Map<String, Double> genrePreferences = Map.of("Action", 10.0);
        List<Long> ratedMovies = List.of();
        int limit = 2;

        MovieDto movieNoGenres = new MovieDto();
        movieNoGenres.setId(1L);
        movieNoGenres.setTitle("No Genres Movie");
        movieNoGenres.setRating(9.5);
        movieNoGenres.setGenres(List.of()); // No genres

        MovieDto movieWithGenres = new MovieDto();
        movieWithGenres.setId(2L);
        movieWithGenres.setTitle("Action Movie");
        movieWithGenres.setRating(6.0);
        movieWithGenres.setGenres(List.of(new MovieDto.Genre(1L, "Action")));

        when(movieApiService.getMoviesByGenre("Action")).thenReturn(List.of(movieWithGenres));
        when(movieApiService.getTrendingMovies()).thenReturn(List.of(movieNoGenres));

        // When
        List<MovieDto> result = recommendationService.findRecommendedMovies(genrePreferences, ratedMovies, limit);

        // Then
        assertEquals(2, result.size());

        // Action movie should be first: 10.0 + (6.0 * 0.5) = 13.0
        assertEquals("Action Movie", result.get(0).getTitle());

        // No-genres movie should be second: 0.0 + (9.5 * 0.5) = 4.75
        assertEquals("No Genres Movie", result.get(1).getTitle());
    }
}
