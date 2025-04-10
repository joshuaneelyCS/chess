package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;

import java.io.IOException;
import java.util.List;

@WebSocket
public class WebSocketHandler {

    private static final Gson GSON = new Gson();
    private final GameService gameService;
    private final UserService userService;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println("Message received from client: " + message);
        switch (command.getCommandType()) {
            case CONNECT -> {
                // Re-parse as MakeMove to access the move field
                ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                connect(connectCommand.getAuthToken(), connectCommand.getGameID(), session);
            }
            case MAKE_MOVE -> {
                // Re-parse as MakeMove to access the move field
                MakeMoveCommand moveCommand = GSON.fromJson(message, MakeMoveCommand.class);
                makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getMove());
            }
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getAuthToken(), command.getGameID());
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println("onError: " + error.getMessage());
    }

    // This function broadcasts to users joined on a game
    private void connect(String authToken, int gameID, Session session) throws IOException {

        validateGame(gameID);
        var username = getUsername(authToken);
        String role = getPlayerRole(username, gameID);

        connections.add(gameID, authToken, session);
        System.out.println("\nConnections: " + connections);
        ChessGame game = getGame(gameID);

        var clientMessage = new LoadGameMessage(game);
        session.getRemote().sendString(new Gson().toJson(clientMessage));

        var message = String.format("%s joined the game as %s.", username, role);
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(authToken, serverMessage, true);
    }

    private void makeMove(String authToken, int gameID, ChessMove move) throws IOException {

        validateGame(gameID);
        var username = getUsername(authToken);
        String role = getPlayerRole(username, gameID);
        ChessGame.TeamColor teamTurn;

        // Gets the team color
        if (role.equals("WHITE")) {
            teamTurn = ChessGame.TeamColor.WHITE;
        } else if (role.equals("BLACK")) {
            teamTurn = ChessGame.TeamColor.BLACK;
        } else {
            throw new IOException("You are not allowed to make a move to. Observe only.");
        }

        ChessGame game = getGame(gameID);

        // Ensures that it is the player's turn
        if (game.getTeamTurn() != teamTurn) {
            throw new IOException("It is not you turn to move. Please wait and try again.");
        }

        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());

        if (piece.getTeamColor() != teamTurn) {
            throw new IOException("This is not your piece. Please select a " + teamTurn.toString() + " piece.");
        }

        // Updates the game in the database
        try {
            game.makeMove(move);
            gameService.setGame(gameID, game);

        } catch (InvalidMoveException e) {
            throw new IOException("Invalid move. Please try again.");
        } catch (DataAccessException e) {
            throw new IOException(e);
        }

        var gameMessage = new LoadGameMessage(game);
        connections.broadcast(authToken, gameMessage, false);

        String message = username + " moved " +
                piece.getPieceType().toString() + " " +
                parsePosition(move.getStartPosition()) + " to " + parsePosition(move.getEndPosition());

        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(authToken, notificationMessage, true);

        checkStatus(authToken, game, teamTurn == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, teamTurn);
    }

    private void checkStatus(String authToken, ChessGame game, ChessGame.TeamColor OpponentTeam, ChessGame.TeamColor teamTurn) throws IOException {
        NotificationMessage notificationMessage;
        String message;
        if (game.isInCheckmate(OpponentTeam)) {
            message = OpponentTeam.toString() + " is in checkmate! " + teamTurn.toString() + "wins!";
            notificationMessage = new NotificationMessage(message);
            connections.broadcast(authToken, notificationMessage, false);
            connections.broadcast(authToken, new EndGameMessage(), false);
        } else if (game.isInCheck(OpponentTeam)) {
            message = OpponentTeam.toString() + " is in check!";
            notificationMessage = new NotificationMessage(message);
            connections.broadcast(authToken, notificationMessage, false);
        }
    }

    private String parsePosition(ChessPosition position) {
        char columnChar = (char) ('A' + position.getColumn() - 1);
        int rowNumber = position.getRow();
        return columnChar + Integer.toString(rowNumber);
    }

   private void leave(String authToken) throws IOException {
       var message = String.format("%s left the game.", getUsername(authToken));
       var serverMessage = new NotificationMessage(message);
       connections.broadcast(authToken, serverMessage, true);
       connections.remove(authToken);
   }

    private void resign(String authToken, int gameID) throws IOException {
        validateGame(gameID);
        String role = getPlayerRole(getUsername(authToken), gameID);

        if (role.equals("WHITE")) {
            role = "BLACK";
        } else if (role.equals("BLACK")) {
            role = "WHITE";
        }

        var message = String.format("%s resigned. %s won the game", getUsername(authToken), role);
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(authToken, serverMessage, true);
        connections.broadcast(authToken, new EndGameMessage(), false);
        gameService.removeGame(gameID);
        connections.remove(authToken);
    }



    private String getUsername(String authToken) {
        try {
            return gameService.getUsername(authToken);
        } catch (DataAccessException e) {
            return "Unknown User";
        }
    }

    private void validateGame(int gameID) throws IOException {
        try {
            if (!gameService.validGame(gameID)) {
                throw new IOException("Error: Game does not exist");
            }
        } catch (DataAccessException e) {
            throw new IOException("Server error: " + e.getMessage());
        }
    }

    private String getPlayerRole(String username, int gameID) throws IOException {
        try {
            String playerColor = gameService.getPlayerColor(username, gameID);
            if (playerColor == null) {
                return "an observer";
            }
            return playerColor;
        } catch (DataAccessException e) {
            throw new IOException("Server error: " + e.getMessage());
        }
    }

    private ChessGame getGame(int gameID) throws IOException {
        try {
            return gameService.getGame(gameID);
        } catch (DataAccessException e) {
            throw new IOException("Server error: " + e.getMessage());
        }
    }
}
