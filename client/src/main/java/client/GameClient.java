package client;

import chess.ChessBoard;
import server.ServerFacade;

import java.util.Arrays;

public class GameClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String token;
    private int gameID;

    public GameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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

    public void setGame(int gameID, String playerColor) {
        this.gameID = gameID;
        drawBoard(gameID, "WHITE");
        drawBoard(gameID, "BLACK");
    }

    public void drawBoard(int gameID, String playerColor) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(gameID, playerColor, board);
    }
}
