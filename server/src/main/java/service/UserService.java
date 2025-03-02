package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {

    record RegisterRequest(String username, String password, String email) { }

    record LoginRequest(String username, String password) { }

    record LoginResult(String token, String username) { }

    record RegisterResult(String token, String username) { }

    private final MemoryAuthDAO authDAO;
    private final MemoryUserDAO userDAO;

    public UserService(MemoryAuthDAO authDAO, MemoryUserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest request) {

        // if user already exists
        if (userDAO.getUser(request.username) != null) {
            throw new UserAlreadyExistsException("Error: The user already exists"); // Error: The user already exists
        }
        // user is successfully created
        userDAO.createUser(new UserData(request.username, request.password, request.email));
        // user token is stored in auth data
        String token = generateToken();
        AuthData authData = new AuthData(token, request.username);
        authDAO.createAuth(authData);
        // return success
        return new RegisterResult(token, authData.getUsername());
    }

    public LoginResult login(LoginRequest request) {
        UserData user = userDAO.getUser(request.username);
        if (user == null) {
            throw new UserNotFoundException("Error: User not found");
        }
        if (user.getPassword() != request.password) {
            throw new IncorrectPassword("Error: Passwords do not match");
        }
        // user token is stored in auth data
        String token = generateToken();
        AuthData authData = new AuthData(token, request.username);
        authDAO.createAuth(authData);

        return new LoginResult(token, authData.getUsername());

    }

    public void logout(String token) {
        authDAO.removeAuth(token);
    }
}
