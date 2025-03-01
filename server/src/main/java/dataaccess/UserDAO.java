package dataaccess;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    boolean createUser(UserData data) throws DataAccessException;
    boolean deleteAllUsers() throws DataAccessException;
}