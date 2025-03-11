package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseGameDAO implements GameDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `game_id` varchar(256) NOT NULL,
              `white_username` varchar(256),
              `black_username` varchar(256),
              `game_name` varchar(256),
              `chess_game` JSON NOT NULL,
              PRIMARY KEY (`game_id`)
            ) 
            """
    };

    public DatabaseGameDAO() throws ResponseException, DataAccessException {
        DatabaseManager.createTables(createStatements);
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        var statement = "SELECT * FROM games";
        var result = DatabaseManager.retrieveData(statement);
        try {
            return DatabaseHandler.GameDataHandler.resultSetToGameDataList(result);
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve game data from database");
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (game_id, white_username, black_username, game_name, chess_game) VALUES (?, ?, ?, ?, ?)";
        DatabaseManager.executeUpdate(
                statement,
                game.getGameID(),
                game.getWhiteUsername(),
                game.getBlackUsername(),
                game.getGameName(),
                DatabaseHandler.GameDataHandler.convertGameToString(game.getGame()));
        return game.getGameID();
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE game_id = ?";
        var result = DatabaseManager.retrieveData(statement, id);
        try {
            return DatabaseHandler.GameDataHandler.resultSetToGameData(result);
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve game data from database");
        }
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        var statement = "DELETE FROM games";
        DatabaseManager.executeUpdate(statement);
    }
}
