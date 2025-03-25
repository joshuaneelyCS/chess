package client;

import server.ServerFacade;

import java.util.Arrays;

public class GameClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String token;
    private final int gameID;

    public GameClient(String serverUrl, int gameID) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameID = gameID;
        drawBoard(gameID);
    }

    @Override
    public String help() {
        return """
                Options:
                Quit game: "quit"
                Show this message: "help"
                """;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void setState(State state) {

    }

    public void drawBoard(int gameID) {
        System.out.println("Drawing board");
    }
}
