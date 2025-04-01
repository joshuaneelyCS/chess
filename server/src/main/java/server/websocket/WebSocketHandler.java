package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Timer;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session);
            case MAKE_MOVE -> makeMove(command.getAuthToken());
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getAuthToken());
        }
    }

    // This function broadcasts to users joined on a game
    private void connect(String authToken, Session session) throws IOException {
        connections.add(authToken, session);
        var message = String.format("%s joined the game", authToken);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(authToken, serverMessage);
    }

    private void makeMove(String authToken) throws IOException {
        connections.remove(authToken);
        var message = String.format("%s moved", authToken);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(authToken, serverMessage);
    }

   private void leave(String authToken) throws IOException {
       var message = String.format("%s left the game.", authToken);
       var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
       connections.broadcast(authToken, serverMessage);
       connections.remove(authToken);
   }

    private void resign(String authToken) throws IOException {
        var message = String.format("%s resigned. {PLAYER} won the game", authToken);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
        connections.remove(authToken);
    }
}
