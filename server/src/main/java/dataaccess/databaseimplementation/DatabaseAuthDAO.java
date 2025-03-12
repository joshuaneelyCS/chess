package dataaccess.databaseimplementation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.sql.SQLException;
import java.util.List;

public class DatabaseAuthDAO implements AuthDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) 
            """
    };

    public DatabaseAuthDAO() throws ResponseException, DataAccessException {
        DatabaseManager.createTables(createStatements);
    }
    // connect to the database

    @Override
    public void createAuth(AuthData auth) throws ResponseException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, auth.getAuthToken(), auth.getUsername());
    }

    @Override
    public void removeAuth(String token) throws DataAccessException {
        if (getAuth(token) == null) {
            throw new DataAccessException("Token not found");
        }
        var statement = "DELETE FROM auth WHERE authToken = ?";
        DatabaseManager.executeUpdate(statement, token);
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException{
        var statement = "SELECT * FROM auth WHERE authToken = ?";
        var result = DatabaseManager.retrieveData(statement, token);
        try {
            return DatabaseHandler.AuthDataHandler.resultSetToAuthData(result);
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve auth data from database");
        }
    }

    @Override
    public void deleteAllAuth() throws DataAccessException {
        var statement = "DELETE FROM auth";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public List<AuthData> getAllAuth() throws DataAccessException {
        var statement = "SELECT * FROM auth";
        var result = DatabaseManager.retrieveData(statement);
        try {
            return DatabaseHandler.AuthDataHandler.resultSetToAuthDataList(result);
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve auth data from database");
        }
    }






}
