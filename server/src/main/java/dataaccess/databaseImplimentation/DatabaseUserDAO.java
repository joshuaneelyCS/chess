package dataaccess.databaseImplimentation;

import com.google.gson.Gson;
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
        return null;
    }

    @Override
    public void createUser(UserData data) throws ResponseException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        DatabaseManager.executeUpdate(statement, data.getUsername(), data.getPassword(), data.getEmail());
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {

    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        return List.of();
    }


}
