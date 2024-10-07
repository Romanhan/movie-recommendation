package romanhan.movie_recommendation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.dto.MovieSearchResponse;

import java.util.List;

@Service
public class MovieApiService {

	private static final Logger logger = LoggerFactory.getLogger(MovieApiService.class);

	private final WebClient webClient;
	private final String apiKey;

	public MovieApiService(@Value("${tmdb.api.key}") String apiKey) {
		this.apiKey = apiKey;
		this.webClient = WebClient.builder()
				.baseUrl("https://api.themoviedb.org/3")
				.build();
	}

	public List<MovieDto> searchMovies(String query) {
		List<MovieDto> movies = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/search/movie")
						.queryParam("api_key", apiKey)
						.queryParam("query", query)
						.build())
				.retrieve()
				.bodyToMono(MovieSearchResponse.class)
				.map(MovieSearchResponse::getResults)
				.block();

		for (MovieDto movie : movies) {
			MovieDto fullMovieDetails = getMovie(movie.getId()); // Fetch full movie details (including genres)
			movie.setGenres(fullMovieDetails.getGenres()); // Set genres from the detailed response
		}

		// for (MovieDto movie : movies) {
		// logger.info("Movie Title: {}, Raiting: {}", movie.getTitle(),
		// movie.getRaiting());
		// }

		return movies;
	}

	public MovieDto getMovie(Long movieId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/movie/{movieId}")
						.queryParam("api_key", apiKey)
						.build(movieId))
				.retrieve()
				.bodyToMono(MovieDto.class)
				.block();
	}

}
