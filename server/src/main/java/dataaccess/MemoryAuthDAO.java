package dataaccess;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public void removeAuth(String token) {

    }

    @Override
    public AuthData getAuth(String token) {
        return null;
    }

    @Override
    public void deleteAllAuth() {

    }
}
