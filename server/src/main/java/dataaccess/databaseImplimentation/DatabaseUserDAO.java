package dataaccess.databaseImplimentation;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.util.List;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {

    }

    @Override
    public void deleteAllUsers() throws DataAccessException {

    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        return List.of();
    }
}
