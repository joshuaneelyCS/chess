package client;

import model.AuthData;
import model.GameData;
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
                Join a game: "joinGame" <PLAYER_COLOR> <GAME_ID>
                Logout: "logout"
                Print this message: "help"
                """;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "listGames" -> listGames();
                case "createGame" -> createGame(params);
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

    private String listGames() throws Exception {
        try {
            GameData[] games = server.listGames(token);
            System.out.println("\nGames:");
            for (GameData game: games) {
                System.out.println(
                        "ID: " + game.getGameID()
                                + " NAME: " + game.getGameName()
                                + " WHITE: " + game.getWhiteUsername()
                                + " BLACK: " + game.getBlackUsername() + "\n");
            }
            return String.format("Successfully listed Games");
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private String createGame(String... params) throws Exception {
        if (params.length == 1) {
            try {
                server.createGame(token, params[0]);
                return String.format("Created game %s", params[0]);
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
        throw new Exception("Expected: <GAME_NAME>");
    }

    private String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            try {
                params[0] = params[0].toUpperCase();
                server.joinGame(this.token, params[0], Integer.parseInt(params[1]));
                state = State.IN_GAME;
                return String.format("Successfully joined game %s as %s", params[1], params[0]);
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
        throw new Exception("Expected: <PLAYER_COLOR> <GAME_ID>");
    }
}
