package romanhan.movie_recommendation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.service.MovieApiService;

@Controller
public class MovieController {
    private final MovieApiService movieApiService;

    public MovieController(MovieApiService movieApiService) {
        this.movieApiService = movieApiService;
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
    public String movie(@PathVariable Long id, Model model) {
        MovieDto movie = movieApiService.getMovie(id);
        model.addAttribute("movie", movie);
        return "movie-details";
    }
}
