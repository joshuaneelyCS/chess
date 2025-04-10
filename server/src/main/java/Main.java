import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import server.Server;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        try {
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.out.println("Error creating database");
        }

        var server = new Server();
        server.run(8081);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}

