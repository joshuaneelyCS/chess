package websocket.messages;

import websocket.commands.UserGameCommand;

public class LoadGame extends ServerMessage {

    public LoadGame(ServerMessageType type) {
        super(type);
    }


}
