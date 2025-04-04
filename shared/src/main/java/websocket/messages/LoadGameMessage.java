package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;
    String message;

    public LoadGameMessage(ServerMessageType type, ChessGame game, String message) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

}
