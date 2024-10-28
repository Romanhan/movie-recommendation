package romanhan.movie_recommendation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import romanhan.movie_recommendation.dto.GenreListResponse;
import romanhan.movie_recommendation.dto.MovieDto;
import romanhan.movie_recommendation.dto.MovieSearchResponse;

@Service
public class MovieApiService {

	private final WebClient webClient;
	private final String apiKey;
	private List<MovieDto.Genre> allGenres = new ArrayList<>();

	public MovieApiService(@Value("${tmdb.api.key}") String apiKey) {
		this.apiKey = apiKey;
		this.webClient = WebClient.builder()
				.baseUrl("https://api.themoviedb.org/3")
				.build();
	}

	@PostConstruct
	public void initializeGenres() {
		GenreListResponse response = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/genre/movie/list")
						.queryParam("api_key", apiKey)
						.build())
				.retrieve()
				.bodyToMono(GenreListResponse.class)
				.block();

		if (response != null && response.getGenres() != null) {
			this.allGenres = response.getGenres();
		}
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

		assignGenresToMovies(movies);

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

	public List<MovieDto> getTrendingMovies() {
		List<MovieDto> movies = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/trending/movie/week")
						.queryParam("api_key", apiKey)
						.build())
				.retrieve()
				.bodyToMono(MovieSearchResponse.class)
				.map(MovieSearchResponse::getResults)
				.block();

		assignGenresToMovies(movies);

		return movies.stream()
				.limit(10)
				.collect(Collectors.toList());
	}

	private void assignGenresToMovies(List<MovieDto> movies) {
		for (MovieDto movie : movies) {
			if (movie.getGenreIds() != null) {
				movie.setGenres(
						allGenres.stream()
								.filter(genre -> movie.getGenreIds().contains(genre.getId()))
								.collect(Collectors.toList()));
			}
		}
	}

}
