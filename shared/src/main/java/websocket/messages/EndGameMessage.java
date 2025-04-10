package websocket.messages;

public class EndGameMessage extends ServerMessage {

    public EndGameMessage() {
        super(ServerMessageType.END_GAME);
    }
}
