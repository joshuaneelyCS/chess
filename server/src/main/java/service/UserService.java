package service;

import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import spark.Response;

import java.util.UUID;

public class UserService {

    public record RegisterRequest(String username, String password, String email) { }

    public record LoginRequest(String username, String password) { }

    public record LoginResult(String authToken, String username) { }

    public record RegisterResult(String authToken, String username) { }

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException, IncorrectPasswordException, ResponseException {

        // if fields are empty
        if (request.username == null || request.password == null) {
            throw new IncorrectPasswordException("");
        }
        String hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt());
        // user is successfully created
        userDAO.createUser(new UserData(request.username, hashedPassword, request.email));
        // user token is stored in auth data
        String token = generateToken();
        AuthData authData = new AuthData(token, request.username);
        authDAO.createAuth(authData);
        // return success
        return new RegisterResult(token, authData.getUsername());

    }

    public LoginResult login(LoginRequest request) throws UserNotFoundException, IncorrectPasswordException, DataAccessException {

        UserData user = userDAO.getUser(request.username);
        if (user == null) {
            throw new UserNotFoundException("Error: User not found");
        }

        if (!BCrypt.checkpw(request.password, user.getPassword())) {
            System.out.println("Here");
            throw new IncorrectPasswordException("Error: Passwords do not match");
        }
        // user token is stored in auth data
        String token = generateToken();
        AuthData authData = new AuthData(token, request.username);
        authDAO.createAuth(authData);

        return new LoginResult(token, authData.getUsername());

    }

    public void logout(String token) throws DataAccessException {
        authDAO.removeAuth(token);
    }
}
