package romanhan.movie_recommendation.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import romanhan.movie_recommendation.entity.User;
import romanhan.movie_recommendation.entity.UserMovieRating;
import romanhan.movie_recommendation.repository.UserMovieRatingRepository;

@Service
@Transactional
public class MovieRatingService {
    private final UserMovieRatingRepository ratingRepository;
    private final UserService userService;

    public MovieRatingService(UserMovieRatingRepository ratingRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
    }

    public UserMovieRating rateMovie(String username, Long movieId, double rating) {
        User user = userService.findByUsername(username);

        Optional<UserMovieRating> existingRating = ratingRepository.findByUserAndMovieId(user, movieId);

        if (existingRating.isPresent()) {
            UserMovieRating userRating = existingRating.get();
            userRating.setRating(rating);
            userRating.setRatedAt(LocalDateTime.now());
            return ratingRepository.save(userRating);
        } else {
            UserMovieRating newRating = new UserMovieRating();
            newRating.setUser(user);
            newRating.setMovieId(movieId);
            newRating.setRating(rating);
            newRating.setRatedAt(LocalDateTime.now());
            return ratingRepository.save(newRating);
        }
    }

    public Optional<UserMovieRating> getUserRating(String username, Long movieId) {
        User user = userService.findByUsername(username);
        return ratingRepository.findByUserAndMovieId(user, movieId);
    }
}
