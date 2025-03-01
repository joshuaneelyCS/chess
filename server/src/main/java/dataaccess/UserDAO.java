package dataaccess;

public interface UserDAO {
    User getUser();
    boolean createUser();
    boolean deleteAllUsers();
}