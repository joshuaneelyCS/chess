package client;

import chess.*;
import client.websocket.NotificationHandler;
import server.ServerFacade;
import client.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    private ChessGame clientGame;

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
                Make a move: "move" <square of piece [Format: <Letter><Number>]> <destination square [Format: <Letter><Number>]>
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
        ws.resignGame(token, gameID);
        return "You have resigned the game. Your opponent wins.";
    }

    private String leave() throws Exception {
        ws.leaveGame(token, gameID);
        return String.format("You left the game\n");
    }

    private String makeMove(String... params) {

        int[] start = parseMove(params[0]);
        int[] end = parseMove(params[1]);
        ChessPiece.PieceType pieceType;

        if (params.length > 2) {
            pieceType = ChessPiece.PieceType.valueOf(params[2]);
        } else {
            pieceType = null;
        }

        ChessMove move = new ChessMove(new ChessPosition(start[0], start[1]), new ChessPosition(end[0], end[1]), pieceType);

        // Update the local game. If invalid move, return error
        try {
            clientGame.makeMove(move);
            ws.makeMove(move, token, gameID);
            return "";
        } catch (InvalidMoveException e) {
            return (e.getMessage());
        }
    }

    private int[] parseMove(String square) {
        int[] move = new int[2];
        // Convert the first character (A–H) to a number 0–7
        move[1] = (square.toUpperCase().charAt(0) - 'A') + 1;
        // Convert the second character (assumed to be 1–8) to 0–7
        move[0] = Integer.parseInt(square.substring(1)) ;
        return move;
    }

    private String drawLegalMoves(String... params) {
        // get its valid moves
        int[] square = parseMove(params[0]);
        Collection<ChessMove> moves = clientGame.validMoves(new ChessPosition(square[0], square[1]));

        // redraw the board with options to distinguish valid moves
        ChessBoardUI.drawBoard(gameID, playerColor, clientGame.getBoard(), moves);
        return "";
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void setState(State state) {

    }

    public void setGameInfo(int gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public void loadLocalGame(ChessGame game) {
        clientGame = game;
        drawBoard();
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
        ChessBoardUI.drawBoard(gameID, playerColor, clientGame.getBoard(), null);
        return "";
    }
}
