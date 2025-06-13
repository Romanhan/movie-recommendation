package romanhan.movie_recommendation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import romanhan.movie_recommendation.entity.User;
import romanhan.movie_recommendation.service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserService userService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authController = new AuthController(userService);
    }

    @Test
    void showRegistrationForm_addsUserToModelAndReturnsRegisterView() {
        Model model = mock(Model.class);

        String view = authController.showRegisrationForm(model);

        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("register", view);
    }

    @Test
    void registerUser_successfulRegistration_redirectsToLoginWithSuccessMessage() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = authController.registerUser(user, redirectAttributes);

        verify(userService).registerUser("testuser", "test@example.com", "password");
        verify(redirectAttributes).addFlashAttribute("success", "Registration successful! Please login.");
        assertEquals("redirect:/login", view);
    }

    @Test
    void registerUser_registrationThrowsException_redirectsToRegisterWithErrorMessage() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        doThrow(new IllegalArgumentException("User already exists")).when(userService)
                .registerUser(anyString(), anyString(), anyString());

        String view = authController.registerUser(user, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("error", "User already exists");
        assertEquals("redirect:/register", view);
    }

    @Test
    void showLoginForm_returnsLoginView() {
        String view = authController.showLoginForm();
        assertEquals("login", view);
    }
}