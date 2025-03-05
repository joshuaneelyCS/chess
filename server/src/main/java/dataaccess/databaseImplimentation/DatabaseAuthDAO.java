package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.sql.Connection;
import java.util.List;

public class DatabaseAuthDAO extends DatabaseDAO implements AuthDAO {

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
