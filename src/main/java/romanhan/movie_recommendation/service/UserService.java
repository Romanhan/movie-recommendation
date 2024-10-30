package romanhan.movie_recommendation.service;

import romanhan.movie_recommendation.entity.User;

public interface UserService {
    User registerUser(String name, String email, String password);

    User findByUsername(String name);

    boolean existsByUserName(String name);

    boolean existsByEmail(String email);
}
