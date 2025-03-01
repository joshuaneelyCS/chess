package service;

import org.eclipse.jetty.server.Authentication;

public class UserService {

    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
    }

    public static class LoginRequest {
        private String username;
        private String password;
    }

    public String register(RegisterRequest request) {
        return "";
    }

    public String login(LoginRequest request) {
        return "";
    }

    public void logout() {

    }
}
