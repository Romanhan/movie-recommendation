package romanhan.movie_recommendation.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class UserMovieRatingTest {

    @Test
    void testGettersAndSetters() {
        // Given
        UserMovieRating rating = new UserMovieRating();
        User user = Mockito.mock(User.class);
        long id = 1L;
        Long movieId = 123L;
        double rate = 4.5;
        LocalDateTime ratedAt = LocalDateTime.now();

        // When
        rating.setId(id);
        rating.setUser(user);
        rating.setMovieId(movieId);
        rating.setRating(rate);
        rating.setRatedAt(ratedAt);

        // Then
        assertEquals(id, rating.getId());
        assertEquals(user, rating.getUser());
        assertEquals(movieId, rating.getMovieId());
        assertEquals(rate, rating.getRating());
        assertEquals(ratedAt, rating.getRatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User user = Mockito.mock(User.class);
        LocalDateTime now = LocalDateTime.now();

        // When
        UserMovieRating rating1 = new UserMovieRating();
        rating1.setId(1L);
        rating1.setUser(user);
        rating1.setMovieId(123L);
        rating1.setRating(4.5);
        rating1.setRatedAt(now);

        UserMovieRating rating2 = new UserMovieRating();
        rating2.setId(1L);
        rating2.setUser(user);
        rating2.setMovieId(123L);
        rating2.setRating(4.5);
        rating2.setRatedAt(now);

        // Then
        assertEquals(rating1, rating2);
        assertEquals(rating1.hashCode(), rating2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        User user = Mockito.mock(User.class);
        UserMovieRating rating = new UserMovieRating();
        rating.setId(1L);
        rating.setUser(user);
        rating.setMovieId(123L);
        rating.setRating(4.5);
        rating.setRatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

        // When and Then
        String str = rating.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("movieId=123"));
        assertTrue(str.contains("rating=4.5"));
        assertTrue(str.contains("ratedAt=2024-01-01T12:00"));
    }
}