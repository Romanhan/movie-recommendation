package romanhan.movie_recommendation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import romanhan.movie_recommendation.entity.UserMovieRating;

public interface UserMovieRatingRepository extends JpaRepository<UserMovieRating, Long> {
    List<UserMovieRating> findByUserId(Long userId);

    Optional<UserMovieRating> findByUserIdAndMovieId(Long userId, Long movieId);

}
