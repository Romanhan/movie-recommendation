package romanhan.movie_recommendation.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class UserTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");

        // When
        User savedUser = entityManager.persistAndFlush(user);
        User foundUser = entityManager.find(User.class, savedUser.getId());

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getPassword()).isEqualTo("password123");
        assertThat(foundUser.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void shouldEnforceUniqueUsernameConstraint() {
        // Given
        User user1 = new User();
        user1.setUsername("duplicate");
        user1.setPassword("password123");
        user1.setEmail("user1@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("duplicate");
        user2.setPassword("password456");
        user2.setEmail("user2@example.com");

        // When & Then
        assertThrows(Exception.class, () -> entityManager.persistAndFlush(user2),
                "Expected unique constraint violation for username");
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password123");
        user1.setEmail("duplicate@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password456");
        user2.setEmail("duplicate@example.com");

        // When & Then
        assertThrows(Exception.class, () -> entityManager.persistAndFlush(user2),
                "Expected unique constraint violation for email");
    }

    @Test
    void shouldEnforceNonNullableUsername() {
        // Given
        User user = new User();
        user.setPassword("password123");
        user.setEmail("testuser@example.com");

        // When & Then
        assertThrows(Exception.class, () -> entityManager.persistAndFlush(user),
                "Expected non-nullable constraint violation for username");
    }

    @Test
    void shouldEnforceNonNullablePassword() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        // When & Then
        assertThrows(Exception.class, () -> entityManager.persistAndFlush(user),
                "Expected non-nullable constraint violation for password");
    }

    @Test
    void shouldEnforceNonNullableEmail() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        // When & Then
        assertThrows(Exception.class, () -> entityManager.persistAndFlush(user),
                "Expected non-nullable constraint violation for email");
    }

    @Test
    void shouldUpdateUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        savedUser.setEmail("newemail@example.com");
        entityManager.persistAndFlush(savedUser);
        User updatedUser = entityManager.find(User.class, savedUser.getId());

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        entityManager.remove(savedUser);
        entityManager.flush();
        User foundUser = entityManager.find(User.class, savedUser.getId());

        // Then
        assertThat(foundUser).isNull();
    }
}