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
    public void removeAuth(String token) throws DataAccessException{
        if (signedIn.containsKey(token)) {
            signedIn.remove(token);
        } else {
            throw new DataAccessException("Error: No token found!");
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        if (signedIn.containsKey(token)) {
            return signedIn.get(token);
        } else {
            throw new DataAccessException("Error: No token found!");
        }

    }

    @Override
    public void deleteAllAuth() {
        signedIn.clear();
    }
}
