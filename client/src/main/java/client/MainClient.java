package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.List;

public class MainClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.OUT_GAME;
    private String token;

    public MainClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String help() {
        return """
                Options:
                List all games: "listGames"
                Create a game: "createGame" <GAME_NAME>
                Join a game: "joinGame" <GAME_ID>
                Logout: "logout"
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
                case "joinGame" -> joinGame(params);
                case "logout" -> "logout";
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

    private String joinGame(String... params) {
        return "";
    }
}
