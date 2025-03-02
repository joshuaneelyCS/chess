package service;

import org.eclipse.jetty.server.Authentication;

public class UserService {

    record RegisterRequest(String username, String password, String email) { }

    record LoginRequest(String username, String password) { }

    record LoginResult(String token, String username) { }

    public String register(RegisterRequest request) {
        return "";
    }

    public String login(LoginRequest request) {
        return "";
    }

    public void logout(String token) {

    }
}
