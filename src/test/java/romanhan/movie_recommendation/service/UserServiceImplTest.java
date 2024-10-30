package romanhan.movie_recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import romanhan.movie_recommendation.entity.User;

@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Test
    void testRegisterUser() {
        // Given
        String username = "testUser";
        String email = "test@email.com";
        String password = "testPas123";

        // When
        User registeredUser = userService.registerUser(username, email, password);

        // Then
        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());
        assertEquals(username, registeredUser.getUsername());
    }

    @Test
    void testFindByUsername() {
        // Given
        String username = "findByName";
        String email = "find@mail.com";
        userService.registerUser(username, email, "passwoed");

        // When
        User foundUser = userService.findByUsername(username);

        // Then
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    void testExistsByEmail() {
        // Given
        String username = "existsEmail";
        String email = "exists@mail.com";
        userService.registerUser(username, email, "password");

        // Then
        assertTrue(userService.existsByEmail(email));
        assertFalse(userService.existsByEmail("notExisten@mail.com"));
    }

    @Test
    void testExistsByUserName() {
        // Given
        String username = "existsUsername";
        String email = "existsU@mail.com";
        userService.registerUser(username, email, "password");

        // Then
        assertTrue(userService.existsByUserName(username));
        assertFalse(userService.existsByUserName("notName"));
    }

}
