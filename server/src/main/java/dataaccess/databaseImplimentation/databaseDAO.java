package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.DAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;

public class databaseDAO implements DAO {

    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    // create the database
    public databaseDAO() throws ResponseException, DataAccessException {
        createDatabase();
        authDAO = new databaseauthdao();
        userDAO = new DatabaseUserDAO();
        gameDAO = new DatabaseGameDAO();
    }

    @Override
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public GameDAO getGameDAO() {
        return gameDAO;
    }

    private void createDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
    }
}
