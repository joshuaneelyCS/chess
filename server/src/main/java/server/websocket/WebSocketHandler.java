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
                makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getMove(), session);
            }
            case LEAVE -> leave(command.getAuthToken(), command.getGameID());
            case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println("onError: " + error.getMessage());
    }

    // This function broadcasts to users joined on a game
    private void connect(String authToken, int gameID, Session session) throws IOException {

        if (!validGame(gameID, session)) {
            throw new IOException("Invalid game ID");
        }

        validateAuth(authToken, session);

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

    private void validateAuth(String authToken, Session session) throws IOException {
        try {
            gameService.confirmAuth(authToken);
        } catch (DataAccessException e) {
            var errorMessage = new ErrorMessage("Auth token is invalid");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Invalid Auth Token");
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {

        // validateGame(gameID);
        if (!isOpen(gameID)) {
            var errorMessage = new ErrorMessage("This game is closed. No more moves can be made!");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Game closed!");
        }

        validateAuth(authToken, session);

        var username = getUsername(authToken);
        String role = getPlayerRole(username, gameID);
        ChessGame.TeamColor teamTurn;

        // Gets the team color
        if (role.equals("WHITE")) {
            teamTurn = ChessGame.TeamColor.WHITE;
        } else if (role.equals("BLACK")) {
            teamTurn = ChessGame.TeamColor.BLACK;
        } else {
            var errorMessage = new ErrorMessage("You are observing the game. No actions can be made");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Observer tried to make a move!");
        }

        ChessGame game = getGame(gameID);

        // Ensures that it is the player's turn
        if (game.getTeamTurn() != teamTurn) {
            var errorMessage = new ErrorMessage("It is not you turn to move. Please wait and try again.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Player tried to move out of turn!");
        }

        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());

        if (piece.getTeamColor() != teamTurn) {
            var errorMessage = new ErrorMessage("This is not your piece. Please select a " + teamTurn.toString() + " piece.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Player tried to move an opponent's piece!");
        }

        // Updates the game in the database
        try {
            game.makeMove(move);
            gameService.setGame(gameID, game);

        } catch (InvalidMoveException e) {
            var errorMessage = new ErrorMessage("Invalid move. Please try again.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Player tried an invalid move!");
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

        checkStatus(authToken, gameID, game, teamTurn == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, teamTurn);
    }

    private void checkStatus(String authToken, int gameID, ChessGame game, ChessGame.TeamColor opponentTeam,
                             ChessGame.TeamColor teamTurn) throws IOException {
        NotificationMessage notificationMessage;
        String message;
        if (game.isInCheckmate(opponentTeam)) {
            message = opponentTeam.toString() + " is in checkmate! " + teamTurn.toString() + "wins!";
            notificationMessage = new NotificationMessage(message);
            connections.broadcast(authToken, notificationMessage, false);
            terminateGame(authToken, gameID);
        } else if (game.isInCheck(opponentTeam)) {
            message = opponentTeam.toString() + " is in check!";
            notificationMessage = new NotificationMessage(message);
            connections.broadcast(authToken, notificationMessage, false);
        }
    }

    private String parsePosition(ChessPosition position) {
        char columnChar = (char) ('A' + position.getColumn() - 1);
        int rowNumber = position.getRow();
        return columnChar + Integer.toString(rowNumber);
    }

   private void leave(String authToken, int gameID) throws IOException {

        String username = getUsername(authToken);

       try {
           String playerColor = getPlayerRole(getUsername(authToken), gameID);
           if (!playerColor.equals("an observer")) {
               gameService.removePlayerFromGame(playerColor, gameID);
           }
       } catch (DataAccessException e) {
           throw new IOException(e);
       }

       var message = String.format("%s left the game.", username);
       var serverMessage = new NotificationMessage(message);
       connections.broadcast(authToken, serverMessage, true);
       connections.remove(authToken);


   }

    private void resign(String authToken, int gameID, Session session) throws IOException {

        // validateGame(gameID);
        if (!isOpen(gameID)) {
            var errorMessage = new ErrorMessage("This game is closed. No more actions can be made");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Game closed!");
        }



        // validateGame(gameID);
        String role = getPlayerRole(getUsername(authToken), gameID);

        if (role.equals("WHITE")) {
            role = "BLACK";
        } else if (role.equals("BLACK")) {
            role = "WHITE";
        } else if (role.equals("an observer")) {
            var errorMessage = new ErrorMessage("You are observing the game. No actions can be made");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            throw new IOException("Observer tried to resign the game!");
        }

        var message = String.format("%s resigned. %s won the game", getUsername(authToken), role);
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(authToken, serverMessage, false);
        terminateGame(authToken, gameID);
    }

    private boolean isOpen(int gameID) throws IOException {
        var games = connections.games.get(gameID);
        if (games != null) {
            for (var game : games) {
                return !game.isLocked();
            }
        }
        return true;
    }

    private void terminateGame(String authToken, int gameID) throws IOException {

        var games = connections.games.get(gameID);
        for (var game : games) {
            game.close();
        }

    }


    private String getUsername(String authToken) {
        try {
            return gameService.getUsername(authToken);
        } catch (DataAccessException e) {
            return "";
        }
    }

    private boolean validGame(int gameID, Session session) throws IOException {
        try {
            if (!gameService.validGame(gameID)) {
                var errorMessage = new ErrorMessage("This game does not exist!");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return false;
            }
            return true;
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
