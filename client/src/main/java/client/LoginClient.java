package client;
import java.util.Arrays;

import model.AuthData;
import server.ServerFacade;

public class LoginClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String token;

    public LoginClient(String serverUrl) {
        // I don't really do anything with the url
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public String help() {
        return """
                Options:
                Login as an existing user: "login" <USERNAME> <PASSWORD> 
                Register a new user: "register" <USERNAME> <PASSWORD> <EMAIL>
                Exit the program: "quit"
                Print this message: "help"
                """;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    public String getAuthToken() {
        return token;
    }

    public String register(String... params) throws Exception {
        if (params.length == 3) {
            try {
                AuthData authData = server.register(params[0], params[1], params[2]);
                token = authData.getAuthToken();
                state = State.LOGGED_IN;
                return String.format("Successfully registered. User is logged in");
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws Exception {
        if (params.length == 2) {
            try {
                AuthData authData = server.login(params[0], params[1]);
                token = authData.getAuthToken();
                state = State.LOGGED_IN;
                return String.format("Successfully registered. User is logged in");
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }


}
