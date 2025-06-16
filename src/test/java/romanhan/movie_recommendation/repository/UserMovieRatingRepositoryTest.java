package romanhan.movie_recommendation.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import romanhan.movie_recommendation.entity.User;
import romanhan.movie_recommendation.entity.UserMovieRating;

@DataJpaTest
public class UserMovieRatingRepositoryTest {

    @Autowired
    private UserMovieRatingRepository userMovieRatingRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByMovieId() {
        // Given
        Long movieId = 1L;
        User user = new User();
        user.setUsername("name");
        user.setPassword("pass");
        user.setEmail("mail@gmail.com");
        user = userRepository.save(user);

        UserMovieRating rating = new UserMovieRating();
        rating.setMovieId(movieId);
        rating.setUser(user);
        rating.setRating(5);
        rating.setRatedAt(LocalDateTime.now());
        userMovieRatingRepository.save(rating);

        // When
        List<UserMovieRating> foundRatings = userMovieRatingRepository.findByMovieId(movieId);

        // Then
        assertNotNull(foundRatings);
        assertEquals(movieId, foundRatings.get(0).getMovieId());
    }

    @Test
    void testFindByUserAndMovieId() {
        // Given
        Long movieId = 1L;
        User user = new User();
        user.setId(1L);
        user.setUsername("name");
        user.setPassword("pass");
        user.setEmail("mail@gmail.com");
        user = userRepository.save(user);

        UserMovieRating rating = new UserMovieRating();
        rating.setMovieId(movieId);
        rating.setUser(user);
        rating.setRating(4);
        rating.setRatedAt(LocalDateTime.now());
        userMovieRatingRepository.save(rating);

        // When
        Optional<UserMovieRating> foundResult = userMovieRatingRepository.findByUserAndMovieId(user, movieId);

        // Then
        assertTrue(foundResult.isPresent());
        assertEquals(movieId, foundResult.get().getMovieId());
        assertEquals(user.getId(), foundResult.get().getUser().getId());
        assertEquals(4, foundResult.get().getRating());
    }

    @AfterEach
    void tearDown() {
        userMovieRatingRepository.deleteAll();
    }
}
