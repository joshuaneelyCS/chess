package dataaccess.databaseImplimentation;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.util.List;

public class DatabaseGameDAO extends DatabaseDAO implements GameDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `game_id` varchar(256) NOT NULL,
              `white_username` varchar(256),
              `black_username` varchar(256),
              `game_name` varchar(256),
              `chess_game` JSON NOT NULL,
              PRIMARY KEY (`authToken`),
            ) 
            """
    };

    public DatabaseGameDAO() throws ResponseException, DataAccessException {
        super.createTables(createStatements);
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAllGames() throws DataAccessException {

    }
}
