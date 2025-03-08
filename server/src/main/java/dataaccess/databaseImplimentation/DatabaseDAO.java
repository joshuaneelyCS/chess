package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;

import java.sql.SQLException;

public class DatabaseDAO {
    // create the database
    public DatabaseDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              PRIMARY KEY (`username`),
            ) 
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
