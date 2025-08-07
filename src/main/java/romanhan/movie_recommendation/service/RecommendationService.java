package romanhan.movie_recommendation.service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<MovieDto> findRecommendedMovies(Map<String, Double> getPreferences, List<Long> ratedMovies, int limit) {
        List<MovieDto> candidateMovies = getCandidateMovies(getPreferences);

        return candidateMovies.stream()
        .filter(movie -> !ratedMovies.contains(movie.getId()))
        .limit(limit)
        .collect(Collectors.toList());
    }

    private List<MovieDto> getCandidateMovies(Map<String, Double> genrePreference) {
        Set<MovieDto> allCandidates = new HashSet<>();
        
        for (String genre : genrePreference.keySet()) {
            try {
                List<MovieDto> genreMovies = movieApiService.searchMovies(genre);
                allCandidates.addAll(genreMovies);
            } catch (Exception e) {
                // TODO. Handle exception, add logger
            }
        }

        allCandidates.addAll(movieApiService.getTrendingMovies());

        return new ArrayList<>(allCandidates);
    }
}
