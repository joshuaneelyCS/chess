package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.awt.image.RescaleOp;
import java.sql.Connection;
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

    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAllAuth() throws DataAccessException {
        var statement = "DELETE FROM auth";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public List<AuthData> getAllAuth() throws DataAccessException {
        return List.of();
    }






}
