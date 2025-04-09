package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private static final Gson GSON = new Gson();

    private final ConnectionManager connections = new ConnectionManager();

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
                makeMove(moveCommand.getAuthToken(), moveCommand.getUsername(), moveCommand.getMove());
            }
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getAuthToken());
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println("onError: " + error.getMessage());
    }

    // This function broadcasts to users joined on a game
    private void connect(String authToken, int gameID, Session session) throws IOException {
        connections.add(gameID, authToken, session);
        System.out.println("\n\nConnections: " + connections);
        ChessGame game = new ChessGame();
        // access the username via UserDAO

        var clientMessage = new LoadGameMessage(game);
        session.getRemote().sendString(new Gson().toJson(clientMessage));

        String message = "User joined the game";
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(authToken, serverMessage);
    }

    private void makeMove(String authToken, String username, ChessMove move) throws IOException {
        ChessGame game = new ChessGame();
        String message = username + "moved" + move.toString();
        var serverMessage = new LoadGameMessage(game);
        // connections.broadcast(authToken, serverMessage);
    }

   private void leave(String authToken) throws IOException {
       var message = String.format("%s left the game.", authToken);
       var serverMessage = new NotificationMessage(message);
       connections.broadcast(authToken, serverMessage);
       connections.remove(authToken);
   }

    private void resign(String authToken) throws IOException {
        var message = String.format("%s resigned. {PLAYER} won the game", authToken);
        var serverMessage = new NotificationMessage(message);
        // connections.broadcast(authToken, serverMessage);
        connections.remove(authToken);
    }
}
