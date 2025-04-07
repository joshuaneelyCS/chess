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
    private ChessGame localGame;
    private String playerColor;
    private WebSocketFacade ws;
    private int gameID;
    private String token;

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
                Show all legal moves: "legal" <square of piece>
                Make a move: "move" <square of piece> <destination square>
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
                case "legal" -> drawLegalMoves(params);
                case "move" -> makeMove(params);
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

    private String leave() throws Exception {
        ws.leaveGame(token, gameID);
        return String.format("You left the game\n");
    }

    private String makeMove(String... params) {
        // Check if legal move
        // Update the local game
        // Send the game to the websocket
        return "";
    }

    private String drawLegalMoves(String... params) {
        // get the piece of the local game
        // get its valid moves
        // redraw the board with options to distinguish valid moves
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

    public void setToken(String token) {
        this.token = token;
    }

    public void beginSession() throws Exception {
        ws = new WebSocketFacade(serverUrl,  notificationHandler);
        if (gameID == 0) {
            throw new Exception("You need to specify a game ID");
        }

        ws.joinGame(token, gameID);
    }

    public String drawBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(gameID, playerColor, board);
        return "";
    }
}
