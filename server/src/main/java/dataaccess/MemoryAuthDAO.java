package dataaccess;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    HashMap<String, AuthData> signedIn = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        signedIn.put(auth.getUsername(), auth);
    }

    @Override
    public void removeAuth(String token) {
        signedIn.remove(token);
    }

    @Override
    public AuthData getAuth(String token) {
        return signedIn.get(token);
    }

    @Override
    public void deleteAllAuth() {
        signedIn.clear();
    }
}
