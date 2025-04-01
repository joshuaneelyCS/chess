package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private static final Gson GSON = new Gson();

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session);
            case MAKE_MOVE -> {
                // Re-parse as MakeMove to access the move field
                MakeMoveCommand moveCommand = GSON.fromJson(message, MakeMoveCommand.class);
                makeMove(moveCommand.getAuthToken(), moveCommand.getMove());
            }
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getAuthToken());
        }
    }

    // This function broadcasts to users joined on a game
    private void connect(String authToken, Session session) throws IOException {
        connections.add(authToken, session);
        ChessGame game = new ChessGame();
        var serverMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(authToken, serverMessage);
    }

    private void makeMove(String authToken, ChessMove move) throws IOException {
        connections.remove(authToken);
        ChessGame game = new ChessGame();
        var serverMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(authToken, serverMessage);
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
        connections.broadcast(authToken, serverMessage);
        connections.remove(authToken);
    }
}
