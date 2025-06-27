package romanhan.movie_recommendation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import romanhan.movie_recommendation.entity.User;
import romanhan.movie_recommendation.entity.UserMovieRating;
import romanhan.movie_recommendation.repository.UserMovieRatingRepository;

class MovieRatingServiceTest {

    private UserMovieRatingRepository ratingRepository;
    private UserService userService;
    private MovieRatingService movieRatingService;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(UserMovieRatingRepository.class);
        userService = mock(UserService.class);
        movieRatingService = new MovieRatingService(ratingRepository, userService);
    }

    @Test
    void rateMovie_shouldUpdateExistingRating() {
        String username = "testuser";
        Long movieId = 1L;
        double newRating = 4.5;

        User user = new User();
        UserMovieRating existingRating = new UserMovieRating();
        existingRating.setUser(user);
        existingRating.setMovieId(movieId);
        existingRating.setRating(3.0);

        when(userService.findByUsername(username)).thenReturn(user);
        when(ratingRepository.findByUserAndMovieId(user, movieId)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(UserMovieRating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserMovieRating result = movieRatingService.rateMovie(username, movieId, newRating);

        assertEquals(newRating, result.getRating());
        assertEquals(user, result.getUser());
        assertEquals(movieId, result.getMovieId());
        assertNotNull(result.getRatedAt());
        verify(ratingRepository).save(existingRating);
    }

    @Test
    void rateMovie_shouldCreateNewRatingIfNotExists() {
        String username = "testuser";
        Long movieId = 2L;
        double rating = 5.0;

        User user = new User();

        when(userService.findByUsername(username)).thenReturn(user);
        when(ratingRepository.findByUserAndMovieId(user, movieId)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(UserMovieRating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserMovieRating result = movieRatingService.rateMovie(username, movieId, rating);

        assertEquals(rating, result.getRating());
        assertEquals(user, result.getUser());
        assertEquals(movieId, result.getMovieId());
        assertNotNull(result.getRatedAt());
        verify(ratingRepository).save(any(UserMovieRating.class));
    }

    @Test
    void getUserRating_shouldReturnRatingIfExists() {
        String username = "testuser";
        Long movieId = 3L;

        User user = new User();
        UserMovieRating userMovieRating = new UserMovieRating();

        when(userService.findByUsername(username)).thenReturn(user);
        when(ratingRepository.findByUserAndMovieId(user, movieId)).thenReturn(Optional.of(userMovieRating));

        Optional<UserMovieRating> result = movieRatingService.getUserRating(username, movieId);

        assertTrue(result.isPresent());
        assertEquals(userMovieRating, result.get());
    }

    @Test
    void getUserRating_shouldReturnEmptyIfNotExists() {
        String username = "testuser";
        Long movieId = 4L;

        User user = new User();

        when(userService.findByUsername(username)).thenReturn(user);
        when(ratingRepository.findByUserAndMovieId(user, movieId)).thenReturn(Optional.empty());

        Optional<UserMovieRating> result = movieRatingService.getUserRating(username, movieId);

        assertFalse(result.isPresent());
    }
}