package dataaccess.memoryImplimentation;
import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryUserDAO extends MemoryDAO implements UserDAO {

    HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("Username already exists");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) {
        try {
            return users.get(username);
        } catch (NullPointerException e) {
            throw e;
        }
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public List<UserData> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
