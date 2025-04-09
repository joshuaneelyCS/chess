package websocket.messages;

public class NotificationMessage extends ServerMessage {

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        super.message = message;
    }

    public String getMessage() {
        return super.getMessage();
    }

}
