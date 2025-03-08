package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.ResponseException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.DAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import com.google.gson.Gson;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.sql.SQLException;

public class DatabaseDAO implements DAO {

    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    // create the database
    public DatabaseDAO() throws ResponseException, DataAccessException {
        createDatabase();
        authDAO = new DatabaseAuthDAO();
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
