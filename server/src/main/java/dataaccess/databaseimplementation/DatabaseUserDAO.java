package dataaccess.databaseimplementation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.sql.SQLException;
import java.util.List;

public class DatabaseUserDAO implements UserDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              PRIMARY KEY (`username`)
            ) 
            """
    };

    public DatabaseUserDAO () throws DataAccessException {
        DatabaseManager.createTables(createStatements);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT * FROM users WHERE username = ?";
        var result = DatabaseManager.retrieveData(statement, username);
        try {
            return DatabaseHandler.UserDataHandler.resultSetToUserData(result);
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve user data from database");
        }
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        try {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            DatabaseManager.executeUpdate(statement, data.getUsername(), data.getPassword(), data.getEmail());
        } catch (ResponseException e) {
            throw new DataAccessException("Could not create user data");
        }
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        var statement = "DELETE FROM users";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        var statement = "SELECT * FROM users";
        var result = DatabaseManager.retrieveData(statement);
        try {
            return DatabaseHandler.UserDataHandler.resultSetToUserDataList(result);
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve user data from database");
        }
    }


}
