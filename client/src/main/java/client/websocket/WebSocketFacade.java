package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {

                    // Get the type
                    JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                    String type = jsonObject.get("serverMessageType").getAsString();

                    // Create a message depending on the type
                    ServerMessage serverMessage;
                    switch (ServerMessage.ServerMessageType.valueOf(type)) {
                        case NOTIFICATION -> serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                        case ERROR -> serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                        case LOAD_GAME -> serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        default -> throw new IllegalArgumentException("Unknown server message type: " + type);
                    }

                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception("Error: Check catch in WebSocketFacade");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String token, int gameID) {
        try {
            var command = new ConnectCommand(token, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error sending connect command", ex);
        }
    }

    public void makeMove(ChessMove move, String token, int gameID) {
        try {
            var command = new MakeMoveCommand(token, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error sending connect command", ex);
        }
    }

    public void leaveGame(String token, int gameID) {
        try {
            var command = new LeaveCommand(LEAVE, token, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Check catch in WebSocketFacade");
        }
    }

    public void resignGame(String token, int gameID) {
        try {
            var command = new ResignCommand(token, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Check catch in WebSocketFacade");
        }
    }

}
