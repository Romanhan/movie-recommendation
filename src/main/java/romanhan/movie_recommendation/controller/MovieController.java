package romanhan.movie_recommendation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String home() {
        return "home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("pageTitle", "Search Results");

        if (query != null && !query.isEmpty()) {
            List<MovieDto> movies = movieApiService.searchMovies(query);
            model.addAttribute("movies", movies);
        }
        return "search";
    }
}