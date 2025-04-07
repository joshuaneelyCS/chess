package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
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

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session);
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
                makeMove(moveCommand.getAuthToken(), moveCommand.getUsername(), moveCommand.getMove());
            }
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getAuthToken());
        }
    }

    // This function broadcasts to users joined on a game
    private void connect(String authToken, int gameID, Session session) throws IOException {
        connections.add(gameID, authToken, session);

        ChessGame game = new ChessGame();
        // access the username via UserDAO

        var clientMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(clientMessage));

        String message = "User joined the game";
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
    }

    private void makeMove(String authToken, String username, ChessMove move) throws IOException {
        ChessGame game = new ChessGame();
        String message = username + "moved" + move.toString();
        var serverMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        // connections.broadcast(authToken, serverMessage);
    }

   private void leave(String authToken) throws IOException {
       var message = String.format("%s left the game.", authToken);
       var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
       connections.broadcast(authToken, serverMessage);
       connections.remove(authToken);
   }

    private void resign(String authToken) throws IOException {
        var message = String.format("%s resigned. {PLAYER} won the game", authToken);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        // connections.broadcast(authToken, serverMessage);
        connections.remove(authToken);
    }
}
