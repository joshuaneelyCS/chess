package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private ChessMove move;
    private String username;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, String username, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }
    //execute the function
}
