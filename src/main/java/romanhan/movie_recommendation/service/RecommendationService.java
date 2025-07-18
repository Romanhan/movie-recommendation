package romanhan.movie_recommendation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.entity.UserMovieRating;
import romanhan.movie_recommendation.repository.UserMovieRatingRepository;

@Service
public class RecommendationService {
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final MovieApiService movieApiService;

    private static final double LIKED_RATING_THRESHOLD = 7.0;

    public RecommendationService(UserMovieRatingRepository userMovieRatingRepository, MovieApiService movieApiService) {
        this.userMovieRatingRepository = userMovieRatingRepository;
        this.movieApiService = movieApiService;
    }

    public Map<String, Double> analyzeGenrePreferences(List<UserMovieRating> userMovieRatings) {
        Map<String, Double> genrePreferences = new HashMap<>();
        
        for (UserMovieRating rating : userMovieRatings) {
            MovieDto movie = movieApiService.getMovie(rating.getMovieId());

            if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                for (MovieDto.Genre genre : movie.getGenres()) {
                    String genreName = genre.getName();
                    genrePreferences.put(genreName, genrePreferences.getOrDefault(genreName, 0.0) + rating.getRating());
                }
            }
        }

        return genrePreferences;
    }
}
