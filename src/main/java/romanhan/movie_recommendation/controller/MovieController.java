package romanhan.movie_recommendation.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.service.MovieApiService;
import romanhan.movie_recommendation.service.MovieRatingService;

@Controller
public class MovieController {
    private final MovieApiService movieApiService;
    private final MovieRatingService movieRatingService;

    public MovieController(MovieApiService movieApiService, MovieRatingService movieRatingService) {
        this.movieApiService = movieApiService;
        this.movieRatingService = movieRatingService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<MovieDto> trendingMovies = movieApiService.getTrendingMovies();
        model.addAttribute("trendingMovies", trendingMovies);
        return "home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("pageTitle", "Search Results");

        if (query != null && !query.isEmpty()) {
            List<MovieDto> movies = movieApiService.searchMovies(query);
            model.addAttribute("movies", movies);
            model.addAttribute("searchTitle", "Result for: " + query);
        }
        return "search";
    }

    @GetMapping("/movie/{id}")
    public String movie(@PathVariable Long id, Model model, Authentication authentication) {
        MovieDto movie = movieApiService.getMovie(id);
        model.addAttribute("movie", movie);

        if (authentication != null) {
            movieRatingService.getUserRating(authentication.getName(), id)
                    .ifPresent(userRating -> model.addAttribute("userRating", userRating.getRating()));
        }
        return "movie-details";
    }

    @PostMapping("/movie/{id}/rate")
    public String rateMovie(@PathVariable Long id, @RequestParam int rating, Authentication authetication) {
        if (authetication != null) {
            movieRatingService.rateMovie(authetication.getName(), id, rating);
        }
        return "redirect:/movie/" + id;
    }
}
