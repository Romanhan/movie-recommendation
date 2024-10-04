package romanhan.movie_recommendation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
