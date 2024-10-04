package romanhan.movie_recommendation.dto;

import lombok.Data;

@Data
public class MovieDto {

    private Long id;
    private String title;
    private String posterPath;
    private String overview;

}
