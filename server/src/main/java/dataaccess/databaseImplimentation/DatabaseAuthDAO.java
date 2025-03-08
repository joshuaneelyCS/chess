package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseAuthDAO extends DatabaseDAO implements AuthDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL
              PRIMARY KEY (`authToken`),
            ) 
            """
    };

    public DatabaseAuthDAO() throws ResponseException, DataAccessException {
        super.createTables(createStatements);
    }
    // connect to the database

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

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

    }

    @Override
    public List<AuthData> getAllAuth() throws DataAccessException {
        return List.of();
    }






}
