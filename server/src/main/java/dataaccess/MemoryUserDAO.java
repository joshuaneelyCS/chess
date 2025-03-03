package dataaccess;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

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
}
