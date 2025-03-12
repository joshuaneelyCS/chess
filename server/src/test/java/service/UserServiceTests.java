package service;

import dataaccess.DataAccessException;
import dataaccess.databaseImplementation.DatabaseDAO;
import dataaccess.interfaces.DAO;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private GameService gameService;
    private UserService userService;
    private DAO dao;

    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new DatabaseDAO();
        userService = new UserService(dao.getAuthDAO(), dao.getUserDAO());
        gameService = new GameService(dao.getAuthDAO(), dao.getGameDAO(), dao.getUserDAO());
        gameService.clearDatabase();
    }

    //  Positive Tests

    @Test
    @DisplayName("Register User Successfully")
    public void testRegisterUserSuccess() throws DataAccessException, IncorrectPasswordException {
        UserService.RegisterRequest request = new UserService.RegisterRequest("user1", "password123", "user1@email.com");
        UserService.RegisterResult result = userService.register(request);

        assertNotNull(result, "Register result should not be null");
        assertNotNull(result.authToken(), "Auth token should be generated");
        assertEquals("user1", result.username(), "Username should match the request");
    }

    @Test
    @DisplayName("Login User Successfully")
    public void testLoginUserSuccess() throws DataAccessException, IncorrectPasswordException, UserNotFoundException {
        // Register first
        UserService.RegisterRequest registerRequest = new UserService.RegisterRequest("user2", "password456", "user2@email.com");
        userService.register(registerRequest);

        // Login with correct credentials
        UserService.LoginRequest loginRequest = new UserService.LoginRequest("user2", "password456");
        UserService.LoginResult loginResult = userService.login(loginRequest);

        assertNotNull(loginResult, "Login result should not be null");
        assertNotNull(loginResult.authToken(), "Auth token should be generated");
        assertEquals("user2", loginResult.username(), "Username should match the login request");
    }

    @Test
    @DisplayName("Logout Successfully")
    public void testLogoutSuccess() throws DataAccessException, IncorrectPasswordException {

        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("user3", "password789", "user3@email.com");
        UserService.RegisterResult result = userService.register(request);

        // Ensure token exists before logout
        assertNotNull(dao.getAuthDAO().getAuth(result.authToken()), "Auth should exist before logout");

        // Logout
        userService.logout(result.authToken());

        // Ensure token is removed
        assertThrows(DataAccessException.class, () -> {
            dao.getAuthDAO().getAuth(result.authToken());
        }, "Fetching the token after logout should throw DataAccessException");
    }

    // Negative Tests

    @Test
    @DisplayName("Register User with Missing Fields (Fail)")
    public void testRegisterUserMissingFields() {
        UserService.RegisterRequest badRequest = new UserService.RegisterRequest(null, "password123", "email@test.com");

        assertThrows(IncorrectPasswordException.class, () -> {
            userService.register(badRequest);
        }, "Should throw IncorrectPasswordException when username is null");
    }

    @Test
    @DisplayName("Login with Incorrect Password (Fail)")
    public void testLoginIncorrectPassword() throws DataAccessException, IncorrectPasswordException {
        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("user4", "correctpassword", "user4@email.com");
        userService.register(request);

        // Attempt to login with wrong password
        UserService.LoginRequest loginRequest = new UserService.LoginRequest("user4", "wrongpassword");

        assertThrows(IncorrectPasswordException.class, () -> {
            userService.login(loginRequest);
        }, "Should throw IncorrectPasswordException when password is incorrect");
    }

    @Test
    @DisplayName("Login Non-Existent User (Fail)")
    public void testLoginNonExistentUser() {
        UserService.LoginRequest loginRequest = new UserService.LoginRequest("nonexistent", "password");

        assertThrows(UserNotFoundException.class, () -> {
            userService.login(loginRequest);
        }, "Should throw UserNotFoundException when user does not exist");
    }

    @Test
    @DisplayName("Logout with Invalid Token (Fail)")
    public void testLogoutInvalidToken() {
        assertThrows(DataAccessException.class, () -> {
            userService.logout("invalid-token");
        }, "Should throw DataAccessException when token does not exist");
    }
}


