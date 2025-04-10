package dataaccess.databaseimplementation;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.GameDAO;
import model.GameAlreadyTakenException;
import model.GameData;
import model.InvalidColorException;

import java.sql.SQLException;
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
    public void joinGame(int id, String playerColor, String username) throws DataAccessException {
        if (playerColor.equals("WHITE")) {
            playerColor = "white_username";
        } else if (playerColor.equals("BLACK")) {
            playerColor = "black_username";
        } else {
            throw new InvalidColorException("Invalid player color");
        }

        String checkStatement = "SELECT " + playerColor + " FROM games WHERE game_id = ?";

        try (var resultSet = DatabaseManager.retrieveData(checkStatement, id)) {
            if (resultSet.next()) {
                String existingUser = resultSet.getString(playerColor);
                if (existingUser != null && !existingUser.isEmpty()) {
                    throw new GameAlreadyTakenException("The selected color is already taken by another player.");
                }
            } else {
                throw new DataAccessException("Game does not exist");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing game data");
        }

        var statement = "UPDATE games SET " + playerColor + " = ? WHERE game_id = ?";
        DatabaseManager.executeUpdate(statement, username, id);
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
    public void setGame(int gameID, ChessGame game) throws DataAccessException {
        var statement = """
        UPDATE games
        SET chess_game = ?
        WHERE game_id = ?
        """;

        try {
            DatabaseManager.executeUpdate(
                    statement,
                    DatabaseHandler.GameDataHandler.convertGameToString(game),
                    gameID
            );
        } catch (Exception e) {
            throw new DataAccessException("Could not set game in database");
        }
    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {
        var statement = """
            DELETE FROM games
            WHERE game_id = ?
            """;

        try {
            DatabaseManager.executeUpdate(statement, gameID);
        } catch (Exception e) {
            throw new DataAccessException("Could not remove game from database");
        }
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        var statement = "DELETE FROM games";
        DatabaseManager.executeUpdate(statement);
    }
}
