package dataaccess;

public class MemoryUserDAO implements UserDAO {

    @Override
    public boolean createUser() {
        return false;
    }

    @Override
    public int getUser() {
        return 1;
    }

    @Override
    public boolean deleteAllUsers() {
        return null;
    }
}
