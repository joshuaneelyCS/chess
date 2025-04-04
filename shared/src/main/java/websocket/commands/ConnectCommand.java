package websocket.commands;

public class ConnectCommand extends UserGameCommand {

    String message;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String message) {
        super(commandType, authToken, gameID);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
