package client;

import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import server.ServerFacade;
import client.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class GameClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private final NotificationHandler notificationHandler;
    private String token;
    private String playerColor;
    private WebSocketFacade ws;
    private int gameID;

    public GameClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    @Override
    public String help() {
        return """
                Options:
                Redraw chess board: "redraw"
                Show all legal moves: "legal"
                Make a move: "move"
                Leave: "leave"
                Resign: "resign"
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
                case "redraw" -> drawBoard();
                case "legal" -> drawLegalMoves();
                case "move" -> makeMove();
                case "leave" -> leave();
                case "resign" -> resign();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String resign() {
        return "";
    }

    private String leave() {
        return "";
    }

    private String makeMove() {
        return "";
    }

    private String drawLegalMoves() {
        return "";
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
        this.playerColor = playerColor;
    }

    public String drawBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(gameID, playerColor, board);
        return "";
    }
}
