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
    private int gameID;
    private String playerColor = "WHITE";

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
                Join a game: "joinGame" <ID> [WHITE|BLACK] 
                Observe a game: "observe" <ID>
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
                case "observe" -> observeGame(params);
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
            for (int i = 1; i <= games.length; i++) {
                System.out.println(
                        "ID: " + i
                                + " NAME: " + games[i-1].getGameName()
                                + " WHITE: " + games[i-1].getWhiteUsername()
                                + " BLACK: " + games[i-1].getBlackUsername() + "\n");
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
                // Allows 'white' and 'WHITE'
                params[1] = params[1].toUpperCase();
                playerColor = params[1];

                // find Game ID
                GameData[] games = server.listGames(token);
                try {
                    if (Integer.parseInt(params[0])-1 > games.length) {
                        return String.format("Game does not exist. Please create a new game", params[0]);
                    }
                } catch (NumberFormatException ex) {
                    return String.format("Invalid Game ID. Please try again", params[0]);
                }

                var game = games[Integer.parseInt(params[0])-1];
                gameID = game.getGameID();

                server.joinGame(this.token, params[1], gameID);
                state = State.IN_GAME;
                return String.format("Successfully joined game %s as %s", game.getGameName(), params[1]);
            } catch (Exception ex) {
                throw new Exception("Could not join game. Color already taken");
            }
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    private String observeGame(String... params) throws Exception {
        if (params.length == 1) {
            try {
                // TODO observe vs play game
                // find Game ID
                GameData[] games = server.listGames(token);

                try {
                    if (Integer.parseInt(params[0])-1 > games.length || games.length == 0) {
                        return String.format("Game does not exist", params[0]);
                    }
                } catch (NumberFormatException ex) {
                    return String.format("Invalid Game ID. Please try again", params[0]);
                }

                var game = games[Integer.parseInt(params[0])-1];
                gameID = game.getGameID();
                playerColor = "WHITE";

                state = State.IN_GAME;
                return String.format("Observing game %s", game.getGameName());
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
        throw new Exception("Expected: <ID>");
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }
}
