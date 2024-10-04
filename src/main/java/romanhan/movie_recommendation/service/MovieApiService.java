package romanhan.movie_recommendation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.dto.MovieSearchResponse;

import java.util.List;

@Service
public class MovieApiService {

    private final WebClient webClient;
    private final String apiKey;

    public MovieApiService(@Value("${tmdb.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .build();
    }

    public List<MovieDto> searchMovies(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(MovieSearchResponse.class)
                .map(MovieSearchResponse::getResults)
                .block();
    }

}
