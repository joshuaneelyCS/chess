package dataaccess;
import model.UserData;

import java.util.List;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData data) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
}